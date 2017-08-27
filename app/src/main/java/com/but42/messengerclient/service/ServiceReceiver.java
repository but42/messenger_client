package com.but42.messengerclient.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.but42.messengerclient.service.server_message.ServerMessage;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class ServiceReceiver extends ResultReceiver {
    public static final int MESSAGE = 100;
    public static final String SERVER_MESSAGE = "SERVER_MESSAGE";

    private OnReceiveMessageCallback mCallback;

    public ServiceReceiver(OnReceiveMessageCallback callback) {
        super(new Handler());
        mCallback = callback;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == MESSAGE) {
            ServerMessage message = resultData.getParcelable(SERVER_MESSAGE);
            mCallback.onReceiveMessage(message);
        }
    }
}
