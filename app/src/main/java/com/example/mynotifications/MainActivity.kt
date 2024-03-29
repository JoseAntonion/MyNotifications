package com.example.mynotifications

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynotifications.Constantes.NOTIFICATION
import com.example.mynotifications.Constantes.NOTIFICATION_CHANNEL_ID
import com.example.mynotifications.Constantes.NOTIFICATION_ID
import com.example.mynotifications.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MyListener {

    private lateinit var notiAdapter: NotificationAdapter
    private lateinit var notiList: MutableList<NotificationData>
    private lateinit var imageChangeBroadcastReceiver: ImageChangeBroadcastReceiver
    private lateinit var bView: ActivityMainBinding

    private val enabledNotificationListeners = "enabled_notification_listeners"
    private val actionNotificationListenerSettings =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

    private var interceptedNotificationImageView: ImageView? = null
    private var enableNotificationListenerAlertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bView.root)

        // Para que el activity se pueda comunicar con el servicio de notificaciones
        NLService().setListener(this)

        bView.buttonAppFilter.setOnClickListener {
            startActivity(Intent(this, FilterActivity::class.java))
        }
        bView.notiRecycler.setHasFixedSize(true)
        bView.notiRecycler.layoutManager = LinearLayoutManager(this)
        notiList = mutableListOf(NotificationData("Primera", "Primera", "Primera"))
        notiAdapter = NotificationAdapter(notiList)
        bView.notiRecycler.adapter = notiAdapter

        if (!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog()
            enableNotificationListenerAlertDialog!!.show()
        }

        // Finally we register a receiver to tell the MainActivity when a notification has been received
        imageChangeBroadcastReceiver = ImageChangeBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.mynotifications")
        registerReceiver(imageChangeBroadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(imageChangeBroadcastReceiver)
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(
            contentResolver,
            enabledNotificationListeners
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun buildNotificationServiceAlertDialog(): AlertDialog? {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.notification_listener_service)
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation)
        alertDialogBuilder.setPositiveButton(
            R.string.yes
        ) { _, _ ->
            startActivity(
                Intent(
                    actionNotificationListenerSettings
                )
            )
        }
        alertDialogBuilder.setNegativeButton(
            R.string.no
        ) { _, _ ->
            // If you choose to not enable the notification listener
            // the app. will not work as expected
        }
        return alertDialogBuilder.create()
    }

    fun changeInterceptedNotificationImage(notificationCode: Int) {
        when (notificationCode) {
            NLService.InterceptedNotificationCode.FACEBOOK_CODE -> interceptedNotificationImageView!!.setImageResource(
                R.drawable.facebook_logo
            )
            NLService.InterceptedNotificationCode.INSTAGRAM_CODE -> interceptedNotificationImageView!!.setImageResource(
                R.drawable.instagram_logo
            )
            NLService.InterceptedNotificationCode.WHATSAPP_CODE -> interceptedNotificationImageView!!.setImageResource(
                R.drawable.whatsapp_logo
            )
            NLService.InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE -> interceptedNotificationImageView!!.setImageResource(
                R.drawable.other_notification_logo
            )
        }
    }

    class ImageChangeBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val receivedNotificationCode = intent.getIntExtra("Notification Code", -1)
            //changeInterceptedNotificationImage(receivedNotificationCode)
        }
    }

    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            StringBuilder().apply {
                append("Action: ${intent.action}\n")
                append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
                toString().also { log ->
                    Log.d("TAG", log)
                    Toast.makeText(context, log, Toast.LENGTH_LONG).show()
                }
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NOTIFICATION, Notification::class.java)
            } else {
                intent.getParcelableExtra(NOTIFICATION)!!
            }
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            notificationManager.createNotificationChannel(notificationChannel)
            val id = intent.getIntExtra(NOTIFICATION_ID, 0)
            notificationManager.notify(id, notification)
        }
    }

    override fun setValue(newItem: NotificationData) {
        notiList.add(0, newItem)
        notiAdapter.notifyItemInserted(0)
        bView.notiRecycler.scrollToPosition(0)
    }

}