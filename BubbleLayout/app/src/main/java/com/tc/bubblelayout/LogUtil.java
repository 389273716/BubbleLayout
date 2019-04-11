package com.tc.bubblelayout;

import android.util.Log;

/**
 * author：   tc
 * date：      2019/3/14 & 15:40
 * version    1.0
 * description
 * modify by
 */
public class LogUtil {
    public static void v(String tag, String info) {
        Log.v(tag, info);
    }

    public static void d(String tag, String info) {
        Log.d(tag, info);
    }

    public static void w(String tag, String info) {
        Log.w(tag, info);
    }

    public static void i(String tag, String info) {
        Log.i(tag, info);
    }

    public static void e(String tag, String info) {
        Log.e(tag, info);
    }

    public static void e(String tag, String info, Throwable throwable) {
        Log.e(tag, info, throwable);
    }
}
