package app.index.shared.core.clients

import app.index.shared.core.logic.ObjectMapper
import app.index.shared.core.data.models.analytics.AnalyticsEvent
import app.index.shared.core.data.models.analytics.AnalyticsEventType
import app.index.shared.core.di.IClosableComponent
import app.index.shared.core.config.BigQueryConfig
import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.InsertAllRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger { }

/**
 * BigQuery client used mainly to pushAnalyticsEvents
 *
 * @see pushAnalyticsEvent
 */
@Single(createdAtStart = true)
class BigQueryClient(
    private val objectMapper: ObjectMapper
) : IClosableComponent {
    private val bigQueryClient = run {
        if (!BigQueryConfig.enabled) {
            null
        } else {
            try {
                BigQueryOptions.getDefaultInstance().service
            } catch (e: Exception) {
                log.error(e) { "Failed creating BigQuery client" }
                null
            }
        }
    }

    private val eventsPool: MutableList<AnalyticsEvent.BigQueryConsumableAnalyticsEvent> = Collections.synchronizedList(ArrayList())
    private val executor = Executors.newScheduledThreadPool(BigQueryConfig.poolSize)

    init {
        executor.scheduleAtFixedRate(
            this::pushAnalyticsEvents,
            BigQueryConfig.delay,
            BigQueryConfig.interval,
            TimeUnit.SECONDS
        )
    }

    /**
     * Pushes an analytics event
     *
     * If [app.index.shared.core.config.BigQueryConfig.enabled] is false, this will log the event in the console,
     * otherwise the event will be added to a pool that gets pushed following a schedule
     */
    fun pushAnalyticsEvent(event: AnalyticsEvent.BigQueryConsumableAnalyticsEvent) {
        if (bigQueryClient != null) {
            eventsPool.add(event)
            if (eventsPool.size >= BigQueryConfig.maxEventsEntry) {
                executor.submit { pushAnalyticsEvents() }
            }
        } else {
            val simulatedEvent = objectMapper.encode(event)
            log.info { "Simulating event dispatch since bigquery is turned off!\n$simulatedEvent" }
        }
    }

    private fun pushAnalyticsEvents() {
        if (bigQueryClient == null || eventsPool.isEmpty()) {
            log.debug { "bigquery enabled: ${BigQueryConfig.enabled} - no events to dispatch: ${eventsPool.isEmpty()}" }
            return
        }

        val events: List<AnalyticsEvent.BigQueryConsumableAnalyticsEvent> = ArrayList(eventsPool)
        eventsPool.clear()

        try {
            events
                .groupBy({ event ->
                    AnalyticsEventType.getForData(event.data).also {
                        if (it == null)
                            log.warn { "unmapped analytics event data type: ${event.data::class}" }
                    }
                }, AnalyticsEvent.BigQueryConsumableAnalyticsEvent::data)
                .filterKeys { it != null }
                .forEach { (type, listOfData) ->
                    // asserting type is ugly, but unfortunately it doesn't recognize we filtered null types
                    val builder = InsertAllRequest.newBuilder(BigQueryConfig.datasetName, type!!.name)

                    listOfData
                        .map { data -> objectMapper.encodeToMap(data, omitClassDiscriminator = true) }
                        .forEach { serializedData -> builder.addRow(serializedData) }

                    val response = bigQueryClient.insertAll(builder.build())
                    if (response.hasErrors()) {
                        response.insertErrors.values.forEach { errors ->
                            log.error { "Failed while inserting analytics event in bigquery: $errors" }
                        }
                    } else {
                        log.debug { "Successfully inserted analytics event to bigquery" }
                    }
                }
        } catch (e: Exception) {
            log.error { e }
        }
    }

    override suspend fun close() {
        executor.submit { pushAnalyticsEvents() }
        executor.shutdown()
    }
}