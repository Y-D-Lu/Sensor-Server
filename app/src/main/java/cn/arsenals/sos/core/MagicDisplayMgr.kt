package cn.arsenals.sos.core

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.view.Surface
import cn.arsenals.sos.SOSApplication
import cn.arsenals.sos.SosConstants
import cn.arsenals.sos.util.SosLog
import cn.arsenals.sos.utils.AppUtils

object MagicDisplayMgr {
    private const val TAG = "MagicDisplayMgr"
    var mMagicDisplay: VirtualDisplay? = null
    var displayId = 0;
    fun createMagicDisplay(width: Int, height: Int, densityDpi: Int, surface: Surface): VirtualDisplay {
        if (mMagicDisplay == null) {
            SosLog.d(TAG, "mMagicDisplay == null, SOSApplication.context : " + SOSApplication.context)
            val mDisplayManager = SOSApplication.context?.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val magicDisplay = mDisplayManager.createVirtualDisplay(SosConstants.MagicDisplay.MAGIC_DISPLAY_NAME,
                    width, height, densityDpi, surface, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC)
            mMagicDisplay = magicDisplay
            displayId = magicDisplay.display.displayId
            SosLog.d(TAG, "createMagicDisplay : $mMagicDisplay")
            return magicDisplay
        } else {
            SosLog.d(TAG, "current mMagicDisplay : $mMagicDisplay")
            val magicDisplay = mMagicDisplay as VirtualDisplay
            magicDisplay.surface = surface
            magicDisplay.resize(width, height, densityDpi)
            SosLog.d(TAG, "recreateMagicDisplay : $magicDisplay")
            return magicDisplay
        }
    }

    fun getMagicDisplay(): VirtualDisplay? {
        SosLog.d(TAG, "getMagicDisplay : $mMagicDisplay")
        return mMagicDisplay
    }

    fun getMagicDisplayId(): Int {
        if (displayId <= 0) {
            displayId = AppUtils.getMagicDisplayId(SOSApplication.context
                    ?: AppUtils.getSystemContext())
            SosLog.w(TAG, "find and set magicDisplayId : $displayId")
            return displayId
        }
        return displayId
    }

    fun resetMagicDisplay() {
        val magicDisplay = mMagicDisplay as VirtualDisplay
        magicDisplay.surface = null
        magicDisplay.resize(SosConstants.MagicDisplay.WIDTH, SosConstants.MagicDisplay.HEIGHT, SosConstants.MagicDisplay.DPI)
        SosLog.d(TAG, "reset MagicDisplay to default : $magicDisplay")
    }

    fun destroyMagicDisplay() {
        mMagicDisplay?.release()
        mMagicDisplay = null
    }
}
