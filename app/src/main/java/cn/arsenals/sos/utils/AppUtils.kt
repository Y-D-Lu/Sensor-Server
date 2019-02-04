package cn.arsenals.sos.utils

import android.app.ActivityManager
import android.app.Activity
import android.app.ActivityManagerNative
import android.content.Context
import android.os.Binder
import android.os.IBinder
import android.os.Parcel


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
    }
}
