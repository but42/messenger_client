package com.but42.messengerclient.ui.module;

import com.but42.messengerclient.service.ApiService;
import com.but42.messengerclient.service.repositories.MessageRepository;
import com.but42.messengerclient.service.repositories.UserRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

@Module
public class MainActivityPresenterModule {

    @Provides
    MainActivityPresenter providesPresenter() {
        return new MainActivityPresenter(new MessageRepository(new ApiService()), new UserRepository(new ApiService()));
    }
}
