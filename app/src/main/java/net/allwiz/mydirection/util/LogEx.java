package net.allwiz.mydirection.util;

import android.util.Log;

import net.allwiz.mydirection.BuildConfig;

public class LogEx {
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            String caller = getCallerInfo();
            Log.d(tag, "[" + caller + "]  Debug : " + message);

        }
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            String caller = getCallerInfo();
            Log.e(tag, "[" + caller + "]  Error : " + message);

        }
    }

    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            String caller = getCallerInfo();
            Log.w(tag, "[" + caller + "]  Warning : " + message);

        }
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) {
            String caller = getCallerInfo();
            Log.i(tag, "[" + caller + "]  Info : " + message);

        }
    }

    public static void v(String tag, String message) {
        if (BuildConfig.DEBUG) {
            String caller = getCallerInfo();
            Log.v(tag, "[" + caller + "]  V : " + message);

        }
    }


    private static String getCallerInfo() {
        // w, d, i, v, e method caller
        StackTraceElement el = new Throwable().fillInStackTrace().getStackTrace()[2];
        // Class Shot Name
        String className = el.getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        String caller = className + "." + el.getMethodName() + "() Line " + el.getLineNumber();

        return caller;
    }
}
