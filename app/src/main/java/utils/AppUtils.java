package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jackson on 2018/1/2 0002.
 */

public class AppUtils {
    public static String getModifyTime(String format, long time) {
        SimpleDateFormat dateformat = null;
        if (format == null) {
            dateformat = new SimpleDateFormat("yyyy_MMdd_HH_mm_ss");
        } else {
            dateformat = new SimpleDateFormat(format);
            dateformat.setTimeZone(TimeZone.getTimeZone("GT+00:00"));
        }
        Date date = new Date(time);
//        return dateformat.format(time);
        return dateformat.format(date);
    }

}
