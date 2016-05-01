package net.tawazz.sounddown.helpers;

import android.app.Application;
import android.content.Context;

/**
 * Created by tnyak on 1/05/2016.
 */
public class ApplicationData extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
