package com.but42.messengerclient;

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

import com.but42.messengerclient.server_message.Connection;
import com.but42.messengerclient.server_message.ServerMessage;
import com.but42.messengerclient.server_message.ServerMessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by but on 26.08.2017.
 */

public class SocketService extends Service implements FlowableOnSubscribe<ServerMessage> {
    private static final String TAG = SocketService.class.getSimpleName();
    private static final int CONNECTION = 100;
    private static final int SEND = 101;
    private static final int STOP = 102;
    private static final String SERVER_MESSAGE = "SERVER_MESSAGE";

    private Handler mServiceHandler;
    private HandlerThread mMainLoopThread;
    private Connection mConnection;
    private FlowableEmitter<ServerMessage> mEmitter;
    private DisposableSubscriber<ServerMessage> m;
    private Disposable m2;

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
        Flowable<ServerMessage> flowable = Flowable.create(this, BackpressureStrategy.BUFFER);
        m = flowable.observeOn(Schedulers.io()).subscribeWith(Model.get());
        m2 = Model.get().getObservable().subscribe(message -> {
            Message msg = mServiceHandler.obtainMessage(SEND);
            Bundle bundle = new Bundle();
            bundle.putParcelable(SocketService.SERVER_MESSAGE, message);
            msg.setData(bundle);
            mServiceHandler.sendMessage(msg);
        });

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
        m.dispose();
        m2.dispose();
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
        mEmitter = e;
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
                    bundle.putParcelable(SERVER_MESSAGE, new ServerMessage(ServerMessageType.USER_NAME, getUserName()));
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
            mEmitter.onNext(message);
        }
    }

    private String getUserName() {
        return "Ted";
    }
}
