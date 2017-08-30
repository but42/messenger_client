package com.but42.messengerclient.app;

/**
 * Created by but on 30.08.2017.
 */

public interface ActivityComponentBuilder<C extends ActivityComponent, M extends ActivityModule> {
    C build();
    ActivityComponentBuilder<C, M> module(M module);
}
