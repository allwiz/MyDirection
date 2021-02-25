package net.allwiz.mydirection.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    public static String getLocalTime() {
        Date localDate = new Date();
        TIME_FORMAT.setTimeZone(TimeZone.getDefault());
        return TIME_FORMAT.format(localDate);
    }


    public static String convertTimeToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = sdf.parse(time);
            String s = DATE_FORMAT.format(date);
            return DATE_FORMAT.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
