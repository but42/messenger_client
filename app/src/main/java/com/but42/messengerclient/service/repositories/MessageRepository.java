package com.but42.messengerclient.service.repositories;

import com.but42.messengerclient.service.ApiService;
import com.but42.messengerclient.service.SocketService;
import com.but42.messengerclient.service.server_message.ServerMessageType;
import com.but42.messengerclient.service.user_message.UserMessage;
import com.but42.messengerclient.service.user_message.UserType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class MessageRepository implements Repository<UserMessage>, FlowableOnSubscribe<UserMessage> {
    private ApiService mService;
    private List<UserMessage> mMessages;
    private FlowableEmitter<UserMessage> mEmitter;
    private Flowable<UserMessage> mFlowable;

    public MessageRepository(ApiService service) {
        mService = service;
        mMessages = new ArrayList<>();
        mFlowable = mService.getFlowable()
                .filter(serverMessage -> serverMessage.getType() == ServerMessageType.TEXT)
                .flatMap(serverMessage -> Flowable.just(new UserMessage(serverMessage.getData(), new Date())))
                .filter(message -> message.getUserType() != UserType.OWNER)
                .mergeWith(Flowable.create(this, BackpressureStrategy.BUFFER));
        mFlowable.subscribe(message -> mMessages.add(message));
    }

    @Override
    public void add(UserMessage item) {
        mMessages.add(item);
        mService.send(item);
        mEmitter.onNext(item);
    }

    @Override
    public List<UserMessage> getAll() {
        return mMessages;
    }

    @Override
    public Flowable<UserMessage> getFlowable() {
        return mFlowable;
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<UserMessage> e) throws Exception {
        mEmitter = e;
    }
}
