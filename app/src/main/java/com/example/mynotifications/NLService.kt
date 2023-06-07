package com.example.mynotifications

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NLService : NotificationListenerService() {

    private val TAG = this.javaClass.simpleName

    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */
    private object ApplicationPackageNames {
        const val FACEBOOK_PACK_NAME = "com.facebook.katana"
        const val FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca"
        const val WHATSAPP_PACK_NAME = "com.whatsapp"
        const val INSTAGRAM_PACK_NAME = "com.instagram.android"
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    object InterceptedNotificationCode {
        const val FACEBOOK_CODE = 1
        const val WHATSAPP_CODE = 2
        const val INSTAGRAM_CODE = 3
        const val OTHER_NOTIFICATIONS_CODE = 4 // We ignore all notification with code == 4
    }

    fun setListener(myListener: MyListener) {
        NLService.myListener = myListener
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notificationCode = matchNotificationCode(sbn)
        // We can read notification while posted.
        val title: String? = sbn.notification.extras.getString("android.title")
        val text: String? = sbn.notification.extras.getString("android.text")
        Log.d(TAG, "onNotificationPosted: $title")
        Log.d(TAG, "onNotificationPosted: $text")
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val currentDate = LocalDateTime.now().format(formatter)

        val newItem = NotificationData(
            notiPackage = sbn.packageName,
            notiTitle = title,
            notiContent = text,
            notiDate = currentDate.toString()
        )
        myListener.setValue(newItem)
        //output.addNewItem(newItem)

        //Toast.makeText(
        //    this, "Notification title is:" + title +
        //            " Notification text is:" + text, Toast.LENGTH_LONG
        //).show()

        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
            val intent = Intent("com.example.mynotifications")
            intent.putExtra("Notification Code", notificationCode)
            sendBroadcast(intent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val notificationCode: Int = matchNotificationCode(sbn)

        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
            val activeNotifications = this.activeNotifications
            if (activeNotifications != null && activeNotifications.isNotEmpty()) {
                for (i in activeNotifications.indices) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        val intent = Intent("com.example.mynotifications")
                        intent.putExtra("Notification Code", notificationCode)
                        sendBroadcast(intent)
                        break
                    }
                }
            }
        }
        /*Log.i(TAG, "********** onNOtificationRemoved")
        Log.i(TAG, "ID :" + sbn.id + "t" + sbn.notification.tickerText + "t" + sbn.packageName)
        val i = Intent("com.example.mynotifications.NOTIFICATION_LISTENER_EXAMPLE")
        i.putExtra("notification_event", "onNotificationRemoved :" + sbn.packageName + "n")
        sendBroadcast2(i)*/
    }

    private fun matchNotificationCode(sbn: StatusBarNotification): Int {
        return when (sbn.packageName) {
            ApplicationPackageNames.FACEBOOK_PACK_NAME, ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME -> {
                InterceptedNotificationCode.FACEBOOK_CODE
            }
            ApplicationPackageNames.INSTAGRAM_PACK_NAME -> {
                InterceptedNotificationCode.INSTAGRAM_CODE
            }
            ApplicationPackageNames.WHATSAPP_PACK_NAME -> {
                InterceptedNotificationCode.WHATSAPP_CODE
            }
            else -> {
                InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE
            }
        }
    }

    companion object {
        lateinit var myListener: MyListener
    }

}