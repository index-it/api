package app.index.core.clients

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {  }

/**
 * Firebase cloud messaging client
 */
@Single(createdAtStart = true)
class FCMClient {
    private val firebaseOptions =
        FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .build()

    private val firebaseApp = FirebaseApp.initializeApp(firebaseOptions)
    private val firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)

    private val taskReminderAnalyticsLabel = "task-reminder"

    private val taskReminderNotificationCategory = "task-reminder"

    fun sendTaskReminderNotification(
        taskId: IxId<TaskData>,
        taskName: String,
        registrationToken: List<String>,
    ) {
        if (registrationToken.isEmpty()) {
            return
        }

        val message =
            MulticastMessage.builder()
                .addAllTokens(registrationToken)
                .setNotification(Notification.builder().setTitle("Index").setBody(taskName).build())
                .putData("type", "task-reminder")
                .putData("task-id", taskId.toString())
                .putData("task-name", taskName)
                .setFcmOptions(FcmOptions.withAnalyticsLabel(taskReminderAnalyticsLabel))
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setCollapseKey(taskId.toString())
                        .build()
                )
                .setApnsConfig(
                    ApnsConfig.builder().setAps(
                        Aps.builder()
                            .putCustomData("interruption-level", "time-sensitive")
                            .setCategory(taskReminderNotificationCategory)
                            .build()
                    ).build()
                )
                .build()

        firebaseMessaging.sendEachForMulticast(message)

        logger.debug { "Sent task reminder notification" }
    }
}
