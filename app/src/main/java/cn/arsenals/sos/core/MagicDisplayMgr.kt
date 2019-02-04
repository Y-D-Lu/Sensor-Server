package cn.arsenals.sos.core

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.view.Surface
import cn.arsenals.sos.SOSApplication
import cn.arsenals.sos.SosConstants
import cn.arsenals.sos.util.SosLog

object MagicDisplayMgr {
    private const val TAG = "MagicDisplayMgr"
    var mMagicDisplay: VirtualDisplay? = null
    var displayId = 0;
    fun createMagicDisplay(width: Int, height: Int, densityDpi: Int, surface: Surface): VirtualDisplay {
        if (mMagicDisplay == null) {
            SosLog.d(TAG, "SOSApplication.context : " + SOSApplication.context)
            val mDisplayManager = SOSApplication.context?.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val magicDisplay = mDisplayManager.createVirtualDisplay(SosConstants.SOS_PACKAGE_NAME, width, height, densityDpi, surface, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC)
            mMagicDisplay = magicDisplay
            displayId = magicDisplay.display.displayId
            SosLog.d(TAG, "mMagicDisplay : $mMagicDisplay")
            return magicDisplay
        } else {
            SosLog.d(TAG, "mMagicDisplay : $mMagicDisplay")
            val magicDisplay = mMagicDisplay as VirtualDisplay
            magicDisplay.surface = surface
            magicDisplay.resize(width, height, densityDpi)
            SosLog.d(TAG, "magicDisplay : $magicDisplay")
            return magicDisplay
        }
    }

    fun getMagicDisplay(): VirtualDisplay? {
        SosLog.d(TAG, "mMagicDisplay : $mMagicDisplay")
        return mMagicDisplay
    }

    fun resetMagicDisplay() {
        val magicDisplay = mMagicDisplay as VirtualDisplay
        magicDisplay.surface = null
        magicDisplay.resize(1080, 1920, 320)
        SosLog.d(TAG, "magicDisplay : $magicDisplay")
    }

    fun destroyMagicDisplay() {
        mMagicDisplay?.release()
        mMagicDisplay = null
    }
}