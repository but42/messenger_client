package com.but42.messengerclient;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.but42.messengerclient.server_message.Connection;
import com.but42.messengerclient.server_message.ServerMessage;
import com.but42.messengerclient.server_message.ServerMessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by but on 26.08.2017.
 */

public class SocketService extends Service {
    private static final String TAG = SocketService.class.getSimpleName();
    private static final int CONNECTION = 100;
    public static final int SEND = 101;
    private static final int STOP = 102;
    public static final String SERVER_MESSAGE = "SERVER_MESSAGE";
    public static final String EXTRA_RECEIVER = "EXTRA_RECEIVER";

    private Messenger mMessenger;
    private Handler mServiceHandler;
    private HandlerThread mMainLoopThread;
    private Connection mConnection;
    private ResultReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceHandlerThread");
        thread.start();
        Looper looper = thread.getLooper();
        mServiceHandler = new ServiceHandler(looper);
        mMessenger = new Messenger(mServiceHandler);
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
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.DATA, message.getData());
            switch (message.getType()) {
                case TEXT:
                    mReceiver.send(MainActivity.NEW_MESSAGE, bundle);
                    break;
                case USER_ADDED:
                    mReceiver.send(MainActivity.ADD_NEW_USER, bundle);
                    break;
                case USER_REMOVED:
                    mReceiver.send(MainActivity.REMOVE_USER, bundle);
                    break;
                default:
                    throw new IOException("Unexpected MessageType");
            }
        }
    }

    private String getUserName() {
        return "Ted";
    }
}
