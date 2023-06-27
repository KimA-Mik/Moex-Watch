package ru.kima.moex

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import kotlin.math.absoluteValue
import kotlin.random.Random


private const val PRIMARY_CHANNEL_ID = "PRIMARY"

class NotificationController private constructor(val context: Context) {
    private var builder: NotificationCompat.Builder
    private var intentBuilder: NavDeepLinkBuilder

    init {
        createNotificationChannel(context)
        builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_price_change)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        intentBuilder = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.securityDetailsFragment)
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

    fun sendPriceChangedNotification(secId: String, percent: Double, curPrice: Double) {
        val bundle = bundleOf(Pair("SecurityId", secId))
        intentBuilder.setArguments(bundle)
        val provider = ResourceProvider.getInstance()

        val body = if (percent > 0.0)
            provider.getString(R.string.notification_price_increased_body, secId, percent, curPrice)
        else
            provider.getString(R.string.notification_price_decreased_body, secId, percent.absoluteValue, curPrice)

        sendNotification(
            provider.getString(R.string.notification_price_changed_title),
            body,
            intentBuilder.createPendingIntent()
        )

    }

    fun sendNotification(title: String, text: String, intent: PendingIntent? = null) {
        builder.setContentTitle(title)
        builder.setContentText(text)
        builder.setContentIntent(intent)

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