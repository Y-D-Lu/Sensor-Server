package cn.arsenals.sos.cast;

import android.graphics.Rect;

public final class ScreenInfo {
    private final Rect contentRect; // device size, possibly cropped
    private final Rect videoRect;
    private final boolean rotated;

    public ScreenInfo(Rect contentRect, Rect videoRect, boolean rotated) {
        this.contentRect = contentRect;
        this.videoRect = videoRect;
        this.rotated = rotated;
    }

    public Rect getContentRect() {
        return contentRect;
    }

    public Rect getVideoRect() {
        return videoRect;
    }

    public ScreenInfo withRotation(int rotation) {
        boolean newRotated = (rotation & 1) != 0;
        if (rotated == newRotated) {
            return this;
        }
        return new ScreenInfo(Device.flipRect(contentRect),
                new Rect(0, 0, videoRect.bottom, videoRect.right), newRotated);
    }
}
