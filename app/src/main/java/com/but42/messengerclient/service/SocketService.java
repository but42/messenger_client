package com.but42.messengerclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.but42.messengerclient.ui.user_message.User;
import com.but42.messengerclient.service.server_message.Connection;
import com.but42.messengerclient.service.server_message.ServerMessage;
import com.but42.messengerclient.service.server_message.ServerMessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by but on 26.08.2017.
 */

public class SocketService extends Service {
    private static final String TAG = SocketService.class.getSimpleName();
    private static final int CONNECTION = 100;
    private static final int STOP = 102;
    public static final String EXTRA_RECEIVER = "EXTRA_RECEIVER";

    private Handler mServiceHandler;
    private ResultReceiver mReceiver;
    private HandlerThread mMainLoopThread;
    private Connection mConnection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        HandlerThread thread = new HandlerThread("ServiceHandlerThread");
        thread.start();
        Looper looper = thread.getLooper();
        mServiceHandler = new ServiceHandler(looper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Message message = mServiceHandler.obtainMessage(CONNECTION);
        mServiceHandler.sendMessage(message);
        mReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
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
                case STOP:
                    close();
                    stopSelf();
                    break;
            }
        }
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

    public class MainLoopThread extends HandlerThread {

        public MainLoopThread() {
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
                    while (true) {
                        ServerMessage message = mConnection.receive();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ServiceReceiver.SERVER_MESSAGE, message);
                        mReceiver.send(ServiceReceiver.MESSAGE, bundle);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        private void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                ServerMessage message = mConnection.receive();
                switch (message.getType()) {
                    case NAME_REQUEST:
                        try {
                            mConnection.send(new ServerMessage(ServerMessageType.USER_NAME, User.getOwnerName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case NAME_ACCEPTED:
                        Log.d(TAG, "NAME_ACCEPTED");
                        return;
                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }
    }
}
