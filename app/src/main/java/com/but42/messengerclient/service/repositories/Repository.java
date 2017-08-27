package com.but42.messengerclient.service.repositories;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public interface Repository<T> {
    void add(T item);
    List<T> getAll();
    Flowable<T> getFlowable();
}
