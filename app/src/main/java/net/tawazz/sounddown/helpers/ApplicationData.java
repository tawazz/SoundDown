package net.tawazz.sounddown.helpers;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * Created by tnyak on 1/05/2016.
 */
public class ApplicationData extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Iconify.with(new FontAwesomeModule());
    }

    public static Context getContext(){
        return context;
    }

    public static WebRequest getWebRequest(){

        return WebRequest.getInstance();
    }
}
