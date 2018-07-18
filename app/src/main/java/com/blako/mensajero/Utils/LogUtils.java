package com.blako.mensajero.Utils;

import android.util.Log;

import com.blako.mensajero.BuildConfig;

public class LogUtils {

    public static void debug(final String tag, String message) {
        if (BuildConfig.DEBUG && message != null) {
            Log.d(tag, message + "\n");
        }
    }

    public static void error(final String tag, String message) {
        if (BuildConfig.DEBUG && message != null) {
            Log.e(tag, message + "\n");
        }
    }
}
