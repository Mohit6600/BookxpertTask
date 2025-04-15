package com.example.bookxpert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFCMService"
        private const val CHANNEL_ID = "item_deletion_channel"
        const val NOTIFICATION_PREFERENCE_KEY = "item_deletion_notifications_enabled"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // ...
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleNotification(applicationContext, remoteMessage.data) // Use applicationContext here as well
        }
        // ...
    }

    fun handleNotification(context: Context, data: Map<String, String>) {
        val itemName = data["itemName"]
        val itemId = data["itemId"]

        if (itemName != null && itemId != null && areNotificationsEnabled(context)) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Item Deletion Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_delete)
                .setContentTitle("Item Deleted")
                .setContentText("Item '$itemName' (ID: $itemId) has been deleted.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(UUID.randomUUID().hashCode(), builder.build())
        }
    }

    private fun areNotificationsEnabled(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(NOTIFICATION_PREFERENCE_KEY, true)
    }
}