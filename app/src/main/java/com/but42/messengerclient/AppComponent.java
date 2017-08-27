package com.but42.messengerclient;

import com.but42.messengerclient.service.ApiModule;
import com.but42.messengerclient.ui.Companent.MainActivityComponent;

import dagger.Component;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

@Component(modules = {ApiModule.class})
public interface AppComponent {
    MainActivityComponent createMainActivityComponent();
}
