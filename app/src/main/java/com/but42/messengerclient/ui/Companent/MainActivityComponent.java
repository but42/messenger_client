package com.but42.messengerclient.ui.Companent;

import com.but42.messengerclient.ui.MainActivity;
import com.but42.messengerclient.ui.module.MainActivityPresenterModule;

import dagger.Subcomponent;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

@Subcomponent(modules = {MainActivityPresenterModule.class})
public interface MainActivityComponent {
    void injectMainActivity(MainActivity mainActivity);
}
