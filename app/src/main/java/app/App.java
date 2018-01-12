package app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/2/27.
 */

public class App extends Application {
    private static Context context;
    private static Activity CURRENT_ACTIVITY;
    private static App INSTANCE;


    public static App getInstance() {
        return INSTANCE;
    }

    public static void setCurrentActivity(Activity activity) {
        CURRENT_ACTIVITY = activity;
    }

    public static Activity getCurrentActivity() {
        return CURRENT_ACTIVITY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.context=getApplicationContext();
        INSTANCE=this;

    }
    public static Context getAppContext(){
        return App.context;
    };
}
