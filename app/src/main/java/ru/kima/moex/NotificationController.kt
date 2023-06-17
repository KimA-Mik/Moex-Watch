package ru.kima.moex

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlin.random.Random

private const val PRIMARY_CHANNEL_ID = "PRIMARY"

class NotificationController private constructor(val context: Context) {
    private var builder: NotificationCompat.Builder

    init {
        createNotificationChannel(context)
        builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_price_change)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val provider = ResourceProvider.getInstance()
            val name = provider.getString(R.string.channel_name)
            val descriptionText = provider.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(PRIMARY_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationManager =
                getSystemService(context, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(title: String, text: String) {
        builder.setContentTitle(title)
        builder.setContentText(text)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(Random.nextInt(), builder.build())
        }

    }


    companion object {
        private var manager: NotificationController? = null

        fun initialize(context: Context) {
            manager = NotificationController(context)
        }

        fun getInstance(): NotificationController {
            if (manager == null) {
                throw Exception("Uninitialized resource provider usage")
            }
            return manager as NotificationController
        }
    }
}