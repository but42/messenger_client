package com.but42.messengerclient.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class App extends Application {
    private ComponentsHolder mComponentsHolder;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponentsHolder = new ComponentsHolder(this);
        mComponentsHolder.init();
    }

    public static App getApp(Context context) {
        return (App)context.getApplicationContext();
    }

    public ComponentsHolder getComponentsHolder() {
        return mComponentsHolder;
    }
}
