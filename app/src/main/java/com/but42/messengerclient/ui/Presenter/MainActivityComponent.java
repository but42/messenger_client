package com.but42.messengerclient.ui.Presenter;

import com.but42.messengerclient.ui.MainActivity;

import dagger.Subcomponent;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

@ActivityScope
@Subcomponent(modules = {MainActivityPresenterModule.class})
public interface MainActivityComponent {
    void injectMainActivity(MainActivity mainActivity);
}
