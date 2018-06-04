package com.nnk.rechargeplatform.utils;

import android.util.Log;

import com.nnk.rechargeplatform.BuildConfig;

public class Logg {
    private static final String TAG = "nnk";
    private static final boolean debug_enable = BuildConfig.DEBUG;

    public static void d(String log) {
        if (debug_enable)
            Log.d(TAG, log);
    }

    public static void d(int log) {
        if (debug_enable)
            Log.d(TAG, log + "");
    }

    public static void i(String log) {
        if (debug_enable)
            Log.i(TAG, log);
    }

    public static void i(int log) {
        if (debug_enable)
            Log.i(TAG, log + "");
    }

    public static void e(String log) {
        if (debug_enable)
            Log.e(TAG, log);
    }

    public static void e(int log) {
        if (debug_enable)
            Log.e(TAG, log + "");
    }

    public static void d(String tag, String log) {
        if (debug_enable)
            Log.d(tag, log);
    }
}
