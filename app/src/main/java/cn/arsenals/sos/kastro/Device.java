package cn.arsenals.sos.kastro;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.IDisplayManager;
import android.os.Build;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IRotationWatcher;
import android.view.IWindowManager;

import cn.arsenals.sos.BuildConfig;
import cn.arsenals.sos.util.SosLog;

public final class Device {
    private static final String TAG = "Device";

    public interface RotationListener {
        void onRotationChanged(int rotation);
    }

    private ScreenInfo screenInfo;
    private RotationListener rotationListener;

    public Device(Options options) {
        screenInfo = computeScreenInfo(options.getCrop(), options.getMaxSize());
        registerRotationWatcher(new IRotationWatcher.Stub() {
            @Override
            public void onRotationChanged(int rotation) throws RemoteException {
                synchronized (Device.this) {
                    screenInfo = screenInfo.withRotation(rotation);

                    // notify
                    if (rotationListener != null) {
                        rotationListener.onRotationChanged(rotation);
                    }
                }
            }
        });
    }

    public synchronized ScreenInfo getScreenInfo() {
        return screenInfo;
    }

    private ScreenInfo computeScreenInfo(Rect crop, int maxSize) {
        try {
            DisplayInfo displayInfo = IDisplayManager.Stub.asInterface(ServiceManager.getService("display"))
                    .getDisplayInfo(Display.DEFAULT_DISPLAY);
            boolean rotated = (displayInfo.rotation & 1) != 0;
            Rect contentRect = new Rect(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
            if (crop != null) {
                if (rotated) {
                    // the crop (provided by the user) is expressed in the natural orientation
                    crop = flipRect(crop);
                }
                if (!contentRect.intersect(crop)) {
                    // intersect() changes contentRect so that it is intersected with crop
                    SosLog.w(TAG, "Crop rectangle (" + formatCrop(crop) + ") does not intersect device screen (" + formatCrop(contentRect) + ")");
                    contentRect = new Rect(); // empty
                }
            }

            Rect videoRect = computeVideoRect(contentRect.width(), contentRect.height(), maxSize);
            return new ScreenInfo(contentRect, videoRect, rotated);
        } catch (RemoteException e) {
            SosLog.e(TAG, "RemoteException : " + e);
            throw new AssertionError(e);
        }
    }

    private static String formatCrop(Rect rect) {
        return rect.width() + ":" + rect.height() + ":" + rect.left + ":" + rect.top;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Rect computeVideoRect(int w, int h, int maxSize) {
        // Compute the video size and the padding of the content inside this video.
        // Principle:
        // - scale down the great side of the screen to maxSize (if necessary);
        // - scale down the other side so that the aspect ratio is preserved;
        // - round this value to the nearest multiple of 8 (H.264 only accepts multiples of 8)
        w &= ~7; // in case it's not a multiple of 8
        h &= ~7;
        if (maxSize > 0) {
            if (BuildConfig.DEBUG && maxSize % 8 != 0) {
                throw new AssertionError("Max size must be a multiple of 8");
            }
            boolean portrait = h > w;
            int major = portrait ? h : w;
            int minor = portrait ? w : h;
            if (major > maxSize) {
                int minorExact = minor * maxSize / major;
                // +4 to round the value to the nearest multiple of 8
                minor = (minorExact + 4) & ~7;
                major = maxSize;
            }
            w = portrait ? minor : major;
            h = portrait ? major : minor;
        }
        return new Rect(0, 0, w, h);
    }

    public Point getPhysicalPoint(Position position) {
        // it hides the field on purpose, to read it with a lock
        @SuppressWarnings("checkstyle:HiddenField")
        ScreenInfo screenInfo = getScreenInfo(); // read with synchronization
        Rect videoSize = screenInfo.getVideoRect();
        Rect clientVideoSize = position.getScreenRect();
        if (!videoSize.equals(clientVideoSize)) {
            // The client sends a click relative to a video with wrong dimensions,
            // the device may have been rotated since the event was generated, so ignore the event
            return null;
        }
        Rect contentRect = screenInfo.getContentRect();
        Point point = position.getPoint();
        int scaledX = contentRect.left + point.x * contentRect.width() / videoSize.width();
        int scaledY = contentRect.top + point.y * contentRect.height() / videoSize.height();
        return new Point(scaledX, scaledY);
    }

    public static String getDeviceName() {
        return Build.MODEL;
    }

    public boolean isScreenOn() {
        try {
            return IPowerManager.Stub.asInterface(ServiceManager.getService("power")).isInteractive();
        } catch (RemoteException e) {
            SosLog.e(TAG, "RemoteException : " + e);
            throw new AssertionError(e);
        }
    }

    public void registerRotationWatcher(IRotationWatcher rotationWatcher) {
        try {
            IWindowManager.Stub.asInterface(ServiceManager.getService("window")).watchRotation(rotationWatcher, Display.DEFAULT_DISPLAY);
        } catch (RemoteException e) {
            SosLog.e(TAG, "RemoteException : " + e);
            throw new AssertionError(e);
        }
    }

    public synchronized void setRotationListener(RotationListener rotationListener) {
        this.rotationListener = rotationListener;
    }

    static Rect flipRect(Rect crop) {
        return new Rect(crop.top, crop.left, crop.bottom, crop.right);
    }
}
