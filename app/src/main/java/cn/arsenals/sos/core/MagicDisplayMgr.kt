package cn.arsenals.sos.core

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.IDisplayManager
import android.hardware.display.VirtualDisplay
import android.os.ServiceManager
import android.view.Display
import android.view.Surface
import cn.arsenals.sos.SOSApplication
import cn.arsenals.sos.SosConstants
import cn.arsenals.sos.util.SosLog
import cn.arsenals.sos.utils.AppUtils

object MagicDisplayMgr {
    private const val TAG = "MagicDisplayMgr"
    var mMagicDisplay: VirtualDisplay? = null
    var displayId = Display.INVALID_DISPLAY;
    val displayInfo = IDisplayManager.Stub.asInterface(ServiceManager.getService("display"))
            .getDisplayInfo(Display.DEFAULT_DISPLAY)
    val physicalHeight = displayInfo.naturalHeight
    val physicalWidth = displayInfo.naturalWidth
    val physicalDpi = displayInfo.logicalDensityDpi
    fun createMagicDisplay(width: Int = physicalWidth,
                           height: Int = physicalHeight,
                           densityDpi: Int = physicalDpi,
                           surface: Surface = Surface(null)): VirtualDisplay {
        if (mMagicDisplay == null) {
            if (existMagicDisplay()) {
                SosLog.wtf(TAG, "FATAL!")
                throw AssertionError()
            }
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

    fun existMagicDisplay(): Boolean {
        if (getMagicDisplayId() < Display.DEFAULT_DISPLAY) {
            return false
        }
        return true
    }

    fun getMagicDisplay(): VirtualDisplay {
        SosLog.d(TAG, "getMagicDisplay : $mMagicDisplay")
        return mMagicDisplay ?: createMagicDisplay()
    }

    fun getMagicDisplayId(): Int {
        if (displayId < Display.DEFAULT_DISPLAY) {
            displayId = AppUtils.getMagicDisplayId(SOSApplication.context
                    ?: AppUtils.getSystemContext())
            SosLog.w(TAG, "find and set magicDisplayId : $displayId")
        }
        return displayId
    }

    fun resetMagicDisplay() {
        val magicDisplay = mMagicDisplay as VirtualDisplay
        magicDisplay.surface = null
        magicDisplay.resize(physicalWidth, physicalHeight, physicalDpi)
        SosLog.d(TAG, "reset MagicDisplay to default : $magicDisplay")
    }

    fun destroyMagicDisplay() {
        mMagicDisplay?.release()
        mMagicDisplay = null
    }
}
