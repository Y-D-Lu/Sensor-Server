package cn.arsenals.sos

import android.app.*
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.view.View
import cn.arsenals.sos.services.ActivityHookService
import cn.arsenals.sos.util.SosLog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.*
import cn.arsenals.sos.core.MagicDisplayMgr
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private var ahsMessenger: Messenger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindActivityHookService()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindActivityHookService()
    }

    private var serviceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            ahsMessenger = Messenger(iBinder)
            SosLog.d(TAG, "ServiceConnection.onServiceConnected")
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            ahsMessenger = null
            SosLog.d(TAG, "ServiceConnection.onServiceDisconnected")
        }
    }

    private fun bindActivityHookService() {
        val intent = Intent(this, ActivityHookService::class.java)
        if (ahsMessenger == null) {
            startService(intent)
        }
        bindService(intent, serviceConn, Context.BIND_AUTO_CREATE)
    }

    private fun unbindActivityHookService() {
        if (ahsMessenger != null) {
            unbindService(serviceConn)
        }
    }

    fun onMainBtn1Clicked(view: View) {
        SosLog.d(TAG, "onMainBtn1Clicked")
        showCurrentActivityAlert()
    }

    fun onMainBtn2Clicked(view: View) {
        SosLog.d(TAG, "onMainBtn2Clicked")
        val mDisplayManager = this.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val displays = mDisplayManager.displays
        for (display in displays){
            SosLog.i(TAG, "Display : $display")
        }
    }

    fun onMainBtn3Clicked(view: View) {
        SosLog.d(TAG, "onMainBtn3Clicked")
        val wxPkgName = "com.tencent.mm"
        val wxMainActivity = "com.tencent.mm.ui.LauncherUI"
        val intent = Intent()
        intent.component = ComponentName(wxPkgName, wxMainActivity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val activityOptions = ActivityOptions.makeBasic()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activityOptions.launchDisplayId = MagicDisplayMgr.displayId
        }
        startActivity(intent, activityOptions.toBundle())
    }

    fun onMainBtn4Clicked(view: View) {
        SosLog.d(TAG, "onMainBtn4Clicked")
        val launcherPkgName = SosConstants.SensorLauncher.PACKAGE_NAME
        val launcherMainActivity = SosConstants.SensorLauncher.LAUNCHER_CLASS_NAME
        val intent = Intent()
        intent.component = ComponentName(launcherPkgName, launcherMainActivity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val activityOptions = ActivityOptions.makeBasic()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activityOptions.launchDisplayId = MagicDisplayMgr.displayId
        }
        startActivity(intent, activityOptions.toBundle())
    }

    fun showCurrentActivityAlert(){
        toast("will show after 5s")
        val msg = Message()
        val bd = Bundle()
        bd.putString("type","onTextViewClicked")
        msg.what = 0
        msg.obj = bd
        ahsMessenger?.send(msg)
    }
}
