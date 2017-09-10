package com.but42.messengerclient.app;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by but on 30.08.2017.
 */

public class ComponentsHolder {
    private final Context mContext;

    private AppComponent mAppComponent;
    @Inject
    Map<Class<?>, Provider<ActivityComponentBuilder>> mBuilders;
    private Map<Class<?>, ActivityComponent> mComponents;

    public ComponentsHolder(Context context) {
        mContext = context;
    }

    void init() {
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(mContext)).build();
        mAppComponent.injectComponentHolder(this);
        mComponents = new HashMap<>();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public ActivityComponent getActivityComponent(Class<?> cls) {
        return getActivityComponent(cls, null);
    }

    public ActivityComponent getActivityComponent(Class<?> cls, ActivityModule module) {
        ActivityComponent component = mComponents.get(cls);
        if (component == null) {
            ActivityComponentBuilder builder = mBuilders.get(cls).get();
            if (module != null) {
                builder.module(module);
            }
            component = builder.build();
            mComponents.put(cls, component);
        }
        return component;
    }

    public void releaseActivityComponent(Class<?> cls) {
        mComponents.put(cls, null);
    }
}
