package cn.arsenals.sos.cast

import android.content.*
import cn.arsenals.sos.SosConstants
import cn.arsenals.sos.services.ActivityHookService
import cn.arsenals.sos.util.SosLog
import java.util.*

class CastBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "CastBroadcastReceiver"
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        val args = p1?.getStringArrayExtra(SosConstants.Broadcast.ARGS)
        SosLog.d(TAG, "args " + Arrays.toString(args))
        val intent = Intent(p0, ActivityHookService::class.java)
        intent.putExtra(SosConstants.Broadcast.ARGS, args)
        p0?.startService(intent)
    }
}
