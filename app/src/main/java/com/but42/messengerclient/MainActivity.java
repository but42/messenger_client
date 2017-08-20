package com.but42.messengerclient;

import android.os.AsyncTask;
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
    private Connection mConnection;
    private volatile boolean clientConnected = false;
    private String mText;
    private Model mModel = new Model();

    @BindView(R.id.message_container) LinearLayout mMessageContainer;
    @BindView(R.id.editText) EditText mEditText;
    @BindView(R.id.count_users_text) TextView mTextViewCountUsers;
    @BindView(R.id.scrollView) ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        new Thread(new Handshake()).start();
    }

    @OnClick(R.id.button)
    void onClick() {
        String text = "Вы: " + mText;
        UserMessage message = new UserMessage(UserType.OWNER, text, new Date());
        mMessageContainer.addView(message.getView(this));
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        new SendTask().execute(mText);
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

    private class Handshake implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress address = InetAddress.getByName("192.168.1.180");
                Socket socket = new Socket(address, 4444);
                mConnection = new Connection(socket);
                Log.i(TAG, "Создался сокет");
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }
    }

    private void clientHandshake() throws IOException, ClassNotFoundException {
        while (true) {
            ServerMessage message = mConnection.receive();
            switch (message.getType()) {
                case NAME_REQUEST:
                    mConnection.send(new ServerMessage(ServerMessageType.USER_NAME, getUserName()));
                    break;
                case NAME_ACCEPTED:
                    notifyConnectionStatusChanged(true);
                    return;
                default:
                    throw new IOException("Unexpected MessageType");
            }
        }
    }

    protected void clientMainLoop() throws IOException, ClassNotFoundException {
        while (true) {
            ServerMessage message = mConnection.receive();
            switch (message.getType()) {
                case TEXT:
                    processIncomingMessage(message.getData());
                    break;
                case USER_ADDED:
                    informAboutAddingNewUser(message.getData());
                    break;
                case USER_REMOVED:
                    informAboutDeletingNewUser(message.getData());
                    break;
                default:
                    throw new IOException("Unexpected MessageType");
            }
        }
    }

    private String getUserName() {
        return "Mikhail";
    }

    private void informAboutDeletingNewUser(String data) {
        mModel.deleteUser(data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewCountUsers.setText(String.format(Locale.getDefault(), "Число пользователей: %d", mModel.getAllUser().size()));
            }
        });
    }

    private void informAboutAddingNewUser(String data) {
        mModel.addUser(data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewCountUsers.setText(String.format(Locale.getDefault(), "Число пользователей: %d", mModel.getAllUser().size()));
            }
        });
    }

    private void processIncomingMessage(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] split = data.split(":");
                if (!split[0].equals(getUserName())) {
                    UserMessage message = new UserMessage(UserType.OTHER, data, new Date());
                    mMessageContainer.addView(message.getView(MainActivity.this));
                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }
        });
    }

    private void notifyConnectionStatusChanged(boolean b) {
        this.clientConnected = b;
    }

    private class SendTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... text) {
            try {
                mConnection.send(new ServerMessage(ServerMessageType.TEXT, text[0]));
                mText = "";
            } catch (IOException e) {
                Log.i(TAG, "Произошло исключение");
                clientConnected = false;
            }
            return null;
        }
    }
}
