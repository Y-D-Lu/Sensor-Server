package cn.arsenals.sos.core;

import android.app.Activity;

import cn.arsenals.sos.util.SosLog;

public class ActivitySensor {
    private static final String TAG = "ActivitySensor";

    public ActivitySensor(){
    }

    public void inActivityOperation(Activity activity, int type) {
        SosLog.e(TAG, "do in-activity operation, activity : " + activity + " type : " + type);
        switch (type) {
            default:
                SosLog.e(TAG, "no specific type " + type + " for activity " + activity);
                break;
        }
    }
}