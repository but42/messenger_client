package com.but42.messengerclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.but42.messengerclient.server_message.Connection;
import com.but42.messengerclient.server_message.ServerMessage;
import com.but42.messengerclient.server_message.ServerMessageType;
import com.but42.messengerclient.user_message.UserMessage;
import com.but42.messengerclient.user_message.UserType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";
    public static final int ADD_NEW_USER = 50;
    public static final int REMOVE_USER = 51;
    public static final int NEW_MESSAGE = 52;
    public static final String DATA = "DATA";

    private Messenger mMessenger;
    private String mText;
    private Model mModel = new Model();
    private Receiver mReceiver;

    @BindView(R.id.message_container) LinearLayout mMessageContainer;
    @BindView(R.id.editText) EditText mEditText;
    @BindView(R.id.count_users_text) TextView mTextViewCountUsers;
    @BindView(R.id.scrollView) ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
        if (mReceiver == null) mReceiver = new Receiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra(SocketService.EXTRA_RECEIVER, mReceiver);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mServiceConnection);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMessenger = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMessenger = null;
        }
    };

    @OnClick(R.id.button)
    void onClick() {
        String text = "Вы: " + mText;
        UserMessage message = new UserMessage(UserType.OWNER, text, new Date());
        mMessageContainer.addView(message.getView(this));
        mScrollView.post(() -> mScrollView.fullScroll(ScrollView.FOCUS_DOWN));
        Message msg = new Message();
        msg.what = SocketService.SEND;
        Bundle bundle = new Bundle();
        bundle.putParcelable(SocketService.SERVER_MESSAGE, new ServerMessage(ServerMessageType.TEXT, mText));
        msg.setData(bundle);
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mText = "";
        mEditText.setText(mText);
    }

    @OnTextChanged(R.id.editText)
    void onTextChanged(CharSequence sequence) {
        mText = sequence.toString();
    }

    @OnClick(R.id.count_users_text)
    void onCountUsers() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : mModel.getAllUser()) {
            stringBuilder.append(string).append("\n");
        }
        builder.setMessage(stringBuilder.toString()).show();
    }

    private void informAboutDeletingNewUser(String data) {
        mModel.deleteUser(data);
        runOnUiThread(() ->
                mTextViewCountUsers.setText(String.format(Locale.getDefault(),
                        "Число пользователей: %d",
                        mModel.getAllUser().size())));
    }

    private void informAboutAddingNewUser(String data) {
        mModel.addUser(data);
        runOnUiThread(() ->
                mTextViewCountUsers.setText(String.format(Locale.getDefault(),
                        "Число пользователей: %d",
                        mModel.getAllUser().size())));
    }

    private void processIncomingMessage(final String data) {
        runOnUiThread(() -> {
            String[] split = data.split(":");
            if (!split[0].equals(getUserName())) {
                UserMessage message1 = new UserMessage(UserType.OTHER, data, new Date());
                mMessageContainer.addView(message1.getView(MainActivity.this));
                mScrollView.post(() -> mScrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }

    private String getUserName() {
        return "Ted";
    }

    private class Receiver extends ResultReceiver {

        private Receiver() {
            super(new Handler());
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String data = resultData.getString(DATA);
            switch (resultCode) {
                case ADD_NEW_USER:
                    informAboutAddingNewUser(data);
                    break;
                case REMOVE_USER:
                    informAboutDeletingNewUser(data);
                    break;
                case NEW_MESSAGE:
                    processIncomingMessage(data);
                    break;
            }
        }
    }
}
