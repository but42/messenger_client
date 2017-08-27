package com.but42.messengerclient;

import android.app.Application;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class App extends Application {
    private static AppComponent sComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sComponent = DaggerAppComponent.create();
    }

    public static AppComponent getComponent() {
        return sComponent;
    }
}
