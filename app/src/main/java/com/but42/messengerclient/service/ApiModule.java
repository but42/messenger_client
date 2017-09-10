package com.but42.messengerclient.service;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

@Module
public class ApiModule {

    @Singleton
    @Provides
    ApiService providesApiService(Context context) {
        return new ApiService(context);
    }
}
