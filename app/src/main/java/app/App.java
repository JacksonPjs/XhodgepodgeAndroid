package app;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/2/27.
 */

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context=getApplicationContext();

    }
    public static Context getAppContext(){
        return App.context;
    };
}
