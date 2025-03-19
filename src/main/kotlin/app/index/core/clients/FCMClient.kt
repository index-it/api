package app.index.core.clients

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.FcmOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
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
                .setNotification(Notification.builder().setTitle("Task reminder").setBody("Handle this in app").build())
                .putData("type", "task-reminder")
                .putData("task-id", taskId.toString())
                .putData("task-name", taskName)
                .setFcmOptions(FcmOptions.withAnalyticsLabel(taskReminderAnalyticsLabel))
                .setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
                .setApnsConfig(ApnsConfig.builder().putHeader("priority", "high").build())
                .build()

        firebaseMessaging.sendEachForMulticast(message)

        logger.debug { "Sent task reminder notification" }
    }
}
