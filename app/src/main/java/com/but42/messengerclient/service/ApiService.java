package com.but42.messengerclient.service;

import android.content.Context;
import android.content.Intent;

import com.but42.messengerclient.service.server_message.Connection;
import com.but42.messengerclient.service.server_message.ServerMessage;
import com.but42.messengerclient.service.server_message.ServerMessageType;
import com.but42.messengerclient.ui.user_message.UserMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class ApiService implements FlowableOnSubscribe<ServerMessage>, OnReceiveMessageCallback {
    private static final String TAG = ApiService.class.getSimpleName();
    private ConnectableFlowable<ServerMessage> mFlowable;
    private List<FlowableEmitter<ServerMessage>> mEmitters;

    public ApiService(Context context) {
        mFlowable = Flowable.create(this, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).publish();
        mFlowable.connect();
        mEmitters = new ArrayList<>();
        ServiceReceiver receiver = new ServiceReceiver(this);
        Intent intent = new Intent(context, SocketService.class);
        intent.putExtra(SocketService.EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }

    public Flowable<ServerMessage> getFlowable() {
        return mFlowable;
    }

    public void send(final UserMessage message) {
        new Thread(() -> {
            try {
                Connection.getConnection()
                        .send(new ServerMessage(ServerMessageType.TEXT, message.getText()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void subscribe(@NonNull final FlowableEmitter<ServerMessage> e) throws Exception {
        e.setDisposable(new Disposable() {
            @Override
            public void dispose() {
                mEmitters.remove(e);
            }

            @Override
            public boolean isDisposed() {
                return mEmitters.contains(e);
            }
        });
        mEmitters.add(e);
    }

    @Override
    public void onReceiveMessage(ServerMessage serverMessage) {
        for (FlowableEmitter<ServerMessage> emitter : mEmitters) emitter.onNext(serverMessage);
    }
}
