package com.but42.messengerclient.ui.module;

import com.but42.messengerclient.service.repositories.MessageRepository;
import com.but42.messengerclient.service.repositories.UserRepository;
import com.but42.messengerclient.service.server_message.ServerMessage;
import com.but42.messengerclient.service.server_message.ServerMessageType;
import com.but42.messengerclient.service.user_message.User;
import com.but42.messengerclient.service.user_message.UserMessage;
import com.but42.messengerclient.service.user_message.UserType;
import com.but42.messengerclient.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class MainActivityPresenter  {
    private MainActivity mMainActivity;
    private MessageRepository mMessageRepository;
    private UserRepository mUserRepository;
    private CompositeDisposable mCompositeDisposable;

    public MainActivityPresenter(MessageRepository messageRepository, UserRepository userRepository) {
        mMessageRepository = messageRepository;
        mUserRepository = userRepository;
        mCompositeDisposable = new CompositeDisposable();
    }

    public void setMainActivity(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public void addMessage(UserMessage message) {
        mMessageRepository.add(message);
    }

    public List<User> getAllUser() {
        return mUserRepository.getAll();
    }

    public List<UserMessage> getMessages() {
        return mMessageRepository.getAll();
    }

    public void subscribe() {
        mCompositeDisposable.add(mMessageRepository.getFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> mMainActivity.notifyMessages()));
        mCompositeDisposable.add(mUserRepository.getFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> mMainActivity.notifyUsers()));
    }

    public void dispose() {
        mCompositeDisposable.dispose();
    }
}
