package com.but42.messengerclient.app;

import android.content.Context;

import com.but42.messengerclient.ui.MainActivity;
import com.but42.messengerclient.ui.Presenter.MainActivityComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * Created by but on 28.08.2017.
 */

@Module(subcomponents = {MainActivityComponent.class})
public class AppModule {
    private Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Singleton
    @Provides
    Context providesContext() {
        return mContext;
    }

    @Provides
    @IntoMap
    @ClassKey(MainActivity.class)
    ActivityComponentBuilder providesMainActivityBuilder(MainActivityComponent.Builder builder) {
        return builder;
    }
}
