package com.dieti.dietiestates25.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dieti.dietiestates25.MainActivity
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.data.remote.FcmTokenRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Controlla se il messaggio contiene dati payload
        remoteMessage.notification?.let {
            val title = it.title ?: "DietiEstates"
            val body = it.body ?: "Nuova notifica"
            sendNotification(title, body)
        }
    }

    private fun sendRegistrationToServer(token: String) {
        val userId = SessionManager.getUserId(applicationContext)
        val authToken = SessionManager.getAuthToken(applicationContext)

        if (userId != null && authToken != null) {
            RetrofitClient.authToken = authToken
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.notificationService.updateFcmToken(FcmTokenRequest(token))
                    Log.d("FCM", "Token inviato al server con successo")
                } catch (e: Exception) {
                    Log.e("FCM", "Errore invio token al server", e)
                }
            }
        } else {
            getSharedPreferences("FCM_PREFS", Context.MODE_PRIVATE)
                .edit()
                .putString("pending_token", token)
                .apply()
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "DietiEstatesChannel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Uso ic_launcher_foreground come fallback sicuro se ic_notification manca
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifiche DietiEstates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}