package com.but42.messengerclient.app;

/**
 * Created by but on 30.08.2017.
 */

public interface ActivityComponent<A> {
    void inject(A activity);
}
