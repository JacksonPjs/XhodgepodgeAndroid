package ui.widget.camera.view.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {
    private static final String TAG="DATEUTILS";
    private static final String date_format = "yyyyMMddHHmmss";
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();

    public static DateFormat getDateFormat(){
        DateFormat df = threadLocal.get();
        if(df==null){
            df = new SimpleDateFormat(date_format);
            threadLocal.set(df);
        }
        return df;
    }

    public static String formatDate(Date date) throws ParseException {
        return getDateFormat().format(date);
    }

    public static Date parse(String strDate) throws ParseException {
        return getDateFormat().parse(strDate);
    }

    public static String getTimestamp(){
        try {
            return formatDate(new Date());
        }catch (Exception e){
            Log.e(TAG,"get timestamp exception:"+e.getMessage());
            e.printStackTrace();
            return "";
        }

    }
}
