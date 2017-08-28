package com.but42.messengerclient.service.repositories;

import com.but42.messengerclient.service.ApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by but on 28.08.2017.
 */

@Module
public class RepositoryModule {

    @Singleton
    @Provides
    MessageRepository providesMessageRepository(ApiService service) {
        return new MessageRepository(service);
    }

    @Singleton
    @Provides
    UserRepository providesUserRepository(ApiService service) {
        return new UserRepository(service);
    }
}
