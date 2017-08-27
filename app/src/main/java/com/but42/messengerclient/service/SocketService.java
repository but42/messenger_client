package com.but42.messengerclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.but42.messengerclient.service.user_message.User;
import com.but42.messengerclient.service.user_message.UserMessage;
import com.but42.messengerclient.service.server_message.Connection;
import com.but42.messengerclient.service.server_message.ServerMessage;
import com.but42.messengerclient.service.server_message.ServerMessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by but on 26.08.2017.
 */

public class SocketService extends Service implements FlowableOnSubscribe<ServerMessage> {
    private static final String TAG = SocketService.class.getSimpleName();
    private static final int CONNECTION = 100;
    private static final int SEND = 101;
    private static final int STOP = 102;
    private static final String SERVER_MESSAGE = "SERVER_MESSAGE";

    private static SocketService sService;
    private Handler mServiceHandler;
    private HandlerThread mMainLoopThread;
    private Connection mConnection;
    private List<FlowableEmitter<ServerMessage>> mEmitters = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceHandlerThread");
        thread.start();
        Looper looper = thread.getLooper();
        mServiceHandler = new ServiceHandler(looper);
        mEmitters = sService.mEmitters;
        sService = this;
    }

    public static Flowable<ServerMessage> getFlowable() {
        if (sService == null) sService = new SocketService();
        return Flowable.create(sService, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io());
    }

    public static void send(UserMessage message) {
        Message msg = sService.mServiceHandler.obtainMessage(SEND);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SocketService.SERVER_MESSAGE,
                new ServerMessage(ServerMessageType.TEXT, message.getText()));
        msg.setData(bundle);
        sService.mServiceHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message message = mServiceHandler.obtainMessage(CONNECTION);
        mServiceHandler.sendMessage(message);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
        mEmitters.forEach(FlowableEmitter::onComplete);
    }

    private void close() {
        if (!mMainLoopThread.isInterrupted()) {
            mMainLoopThread.interrupt();
        }
        try {
            mConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<ServerMessage> e) throws Exception {
        mEmitters.add(e);
    }

    private class ServiceHandler extends Handler {
        private ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTION:
                    mMainLoopThread = new MainLoopThread();
                    mMainLoopThread.start();
                    break;
                case SEND:
                    ServerMessage message = msg.getData().getParcelable(SERVER_MESSAGE);
                    new Thread(() -> {
                        try {
                            mConnection.send(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    break;
                case STOP:
                    close();
                    stopSelf();
                    break;
            }
        }
    }

    private class MainLoopThread extends HandlerThread {

        private MainLoopThread() {
            super("MainLoopThread");
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    InetAddress address = InetAddress.getByName("192.168.1.180");
                    Socket socket = new Socket(address, 4444);
                    mConnection = new Connection(socket);
                    Log.i(TAG, "Создался сокет");
                    clientHandshake();
                    clientMainLoop();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clientHandshake() throws IOException, ClassNotFoundException {
        while (true) {
            ServerMessage message = mConnection.receive();
            switch (message.getType()) {
                case NAME_REQUEST:
                    Message msg = mServiceHandler.obtainMessage(SEND);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(SERVER_MESSAGE, new ServerMessage(ServerMessageType.USER_NAME, User.getOwnerName()));
                    msg.setData(bundle);
                    mServiceHandler.sendMessage(msg);
                    break;
                case NAME_ACCEPTED:
                    Log.d(TAG, "NAME_ACCEPTED");
                    return;
                default:
                    throw new IOException("Unexpected MessageType");
            }
        }
    }

    protected void clientMainLoop() throws IOException, ClassNotFoundException {
        while (true) {
            ServerMessage message = mConnection.receive();
            for (FlowableEmitter<ServerMessage> emitter : mEmitters) emitter.onNext(message);
        }
    }
}
