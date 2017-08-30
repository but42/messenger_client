package com.but42.messengerclient.ui.Presenter;

import com.but42.messengerclient.app.ActivityModule;
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
public class MainActivityPresenterModule implements ActivityModule {

    @ActivityScope
    @Provides
    MainActivityPresenter providesPresenter(MessageRepository messageRepository, UserRepository userRepository) {
        return new MainActivityPresenter(messageRepository, userRepository);
    }
}
