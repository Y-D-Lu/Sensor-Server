package cn.arsenals.sos.services

import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.os.*
import android.view.WindowManager
import cn.arsenals.sos.SosConstants
import cn.arsenals.sos.kastro.Server
import cn.arsenals.sos.util.SosLog
import cn.arsenals.sos.utils.AppUtils
import java.util.*

class ActivityHookService : Service() {
    companion object {
        private const val TAG = "ActivityHookService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val args = intent?.getStringArrayExtra(SosConstants.Broadcast.ARGS)
        if(args != null){
            SosLog.d(TAG, "args : " + Arrays.toString(args))
            Thread(Runnable {
                kotlin.run {
                    Server.main(args)
                }
            }).start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        val messenger = Messenger(mHandler)
        return messenger.binder
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            SosLog.d(TAG, msg.toString())
            when (msg.what) {
                0 -> {
                    showCurrentActivity()
                }
                else -> {
                    SosLog.w(TAG, "Message invalid!")
                }
            }
        }
    }

    fun showCurrentActivity() {
        Thread(Runnable {
            kotlin.run {
                try {
                    Thread.sleep(5000)
                    val currentActivity = AppUtils.getCurrentActivityName(this)
                    SosLog.d(TAG, "currentActivity is $currentActivity")
                    Looper.prepare()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("showCurrentActivity")
                    builder.setMessage("currentActivity is $currentActivity")
                    builder.setPositiveButton("ok") { _, _ ->
                        SosLog.d(TAG, "onOkClicked")
                    }
                    builder.setCancelable(false)
                    val dialog = builder.create()
                    dialog?.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                    dialog.show()
                    Looper.loop()
                } catch (e: InterruptedException) {
                    SosLog.e(TAG, e.toString())
                }
            }
        }).start()
    }
}