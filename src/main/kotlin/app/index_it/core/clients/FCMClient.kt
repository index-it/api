package app.index_it.core.clients

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

/**
 * Firebase cloud messaging client
 */
object FCMClient {
    private val firebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build()

    private val firebaseApp = FirebaseApp.initializeApp(firebaseOptions)


    fun sendNotificationToDevices(message: String, vararg registrationId: String) {
        for (id in registrationId) {
            TODO()
        }
    }
}