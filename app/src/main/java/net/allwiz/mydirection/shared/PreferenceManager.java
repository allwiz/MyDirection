package net.allwiz.mydirection.shared;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String TAG = PreferenceManager.class.getSimpleName();

    private static final String MY_DIRECTION_PREFERENCE = "MY_DIRECTION_PREFERENCE";
    private static final String SHORT_CUTS = "SHORT_CUTS";

    public static void setShortCut(Context context, boolean shortcut) {
        SharedPreferences s = context.getSharedPreferences(MY_DIRECTION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = s.edit();
        e.putBoolean(SHORT_CUTS, shortcut);
        e.commit();
    }

    public static boolean isShortCut(Context context) {
        SharedPreferences s = context.getSharedPreferences(MY_DIRECTION_PREFERENCE, Context.MODE_PRIVATE);
        return s.getBoolean(SHORT_CUTS, false);
    }
}
