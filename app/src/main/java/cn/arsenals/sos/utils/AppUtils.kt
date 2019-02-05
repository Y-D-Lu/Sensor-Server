package cn.arsenals.sos.utils

import android.app.ActivityManager
import android.app.Activity
import android.app.ActivityManagerNative
import android.app.ActivityThread
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.IBinder
import android.os.Parcel
import cn.arsenals.sos.SosConstants

class AppUtils {
    companion object {
        fun getCurrentPackageName(context: Context): String {
            val activityManager: ActivityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
            val taskInfo = activityManager.getRunningTasks(1);
            val componentInfo = taskInfo[0].topActivity
            return componentInfo.packageName
        }

        fun getCurrentActivityName(context: Context): String {
            val activityManager: ActivityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
            val taskInfo = activityManager.getRunningTasks(1);
            val componentInfo = taskInfo[0].topActivity
            return componentInfo.className
        }

        fun requestCurrentActivity(context: Context, type: Int) {
            val data = Parcel.obtain()
            data.writeString(AppUtils.getCurrentActivityName(context))
            data.writeInt(type)
            val reply = Parcel.obtain()
            val ret = ActivityManagerNative.getDefault().asBinder()
                    .transact(IBinder.REQ_CURRENT_ACTIVITY_TRANSACTION/*'`'.toInt() shl 24 or 1*/,
                            data, reply, 0)
        }

        fun getCurrentServerActivity() : Activity?{
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThread = ActivityThread.currentActivityThread()
            val activitiesField = activityThreadClass.getDeclaredField("mActivities")
            activitiesField.setAccessible(true)
            val activities = activitiesField.get(activityThread) as Map<*, *>
            for (activityRecord in activities.values) {
                if (activityRecord == null) {
                    return null
                }
                val activityRecordClass = activityRecord.javaClass
                val pausedField = activityRecordClass.getDeclaredField("paused")
                pausedField.isAccessible = true
                if (!pausedField.getBoolean(activityRecord)) {
                    val activityField = activityRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    val activity = activityField.get(activityRecord) as Activity
                    return activity
                }
            }
            return null
        }

        fun getMagicDisplayId(context: Context): Int {
            val displayManager: DisplayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            for (display in displayManager.displays) {
                if (display.name.equals(SosConstants.MagicDisplay.MAGIC_DISPLAY_NAME)) {
                    return display.displayId
                }
            }
            return 0
        }

        fun getSystemContext() : Context {
            return ActivityThread.systemMain().systemContext
        }
    }
}
