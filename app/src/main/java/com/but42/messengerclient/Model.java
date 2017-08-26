package com.but42.messengerclient;

import com.but42.messengerclient.server_message.ServerMessage;
import com.but42.messengerclient.server_message.ServerMessageType;
import com.but42.messengerclient.user_message.UserMessage;
import com.but42.messengerclient.user_message.UserType;

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
import io.reactivex.annotations.NonNull;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class Model extends DisposableSubscriber<ServerMessage> implements FlowableOnSubscribe<String>, ObservableOnSubscribe<ServerMessage> {
    private static Model sModel;
    private Set<String> mUsers;
    private List<UserMessage> mMessages;
    private FlowableEmitter<String> mFlowableEmitter;
    private Flowable<String> mFlowable;
    private ObservableEmitter<ServerMessage> mObservableEmitter;
    private Observable<ServerMessage> mObservable;

    public static Model get() {
        if (sModel == null) {
            sModel = new Model();
        }
        return sModel;
    }

    public Flowable<String> getFlowable() {
        return mFlowable;
    }

    public Observable<ServerMessage> getObservable() {
        return mObservable;
    }

    private Model() {
        mUsers = new HashSet<>();
        mMessages = new ArrayList<>();
        mFlowable = Flowable.create(this, BackpressureStrategy.BUFFER);
        mObservable = Observable.create(this);
    }

    public void addUser(String newUser) {
        mUsers.add(newUser);
        mFlowableEmitter.onNext(newUser);
    }

    public void deleteUser(String user) {
        mUsers.remove(user);
        mFlowableEmitter.onNext(user);
    }

    public Set<String> getAllUser() {
        return Collections.unmodifiableSet(mUsers);
    }

    public void addMessage(UserMessage message) {
        mMessages.add(message);
        mFlowableEmitter.onNext(message.toString());
        if (message.getUser() == UserType.OWNER) {
            mObservableEmitter.onNext(new ServerMessage(ServerMessageType.TEXT, message.getText()));
        }
    }

    public List<UserMessage> getMessages() {
        return mMessages;
    }

    @Override
    public void onNext(ServerMessage serverMessage) {
        switch (serverMessage.getType()) {
            case TEXT:
                String[] split = serverMessage.getData().split(":");
                if (!split[0].equals(getUserName())) {
                    UserMessage message1 = new UserMessage(UserType.OTHER, serverMessage.getData(), new Date());
                    addMessage(message1);
                }
                break;
            case USER_ADDED:
                addUser(serverMessage.getData());
                break;
            case USER_REMOVED:
                deleteUser(serverMessage.getData());
                break;
        }
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }

    private String getUserName() {
        return "Ted";
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {
        mFlowableEmitter = e;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<ServerMessage> e) throws Exception {
        mObservableEmitter = e;
    }
}
