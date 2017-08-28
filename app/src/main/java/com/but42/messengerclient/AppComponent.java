package com.but42.messengerclient;

import com.but42.messengerclient.service.ApiModule;
import com.but42.messengerclient.service.repositories.RepositoryModule;
import com.but42.messengerclient.ui.Presenter.MainActivityComponent;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

@Singleton
@Component(modules = {AppModule.class, ApiModule.class, RepositoryModule.class})
public interface AppComponent {
    MainActivityComponent createMainActivityComponent();
}
