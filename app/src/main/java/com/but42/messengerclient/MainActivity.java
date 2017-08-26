package com.but42.messengerclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.but42.messengerclient.server_message.ServerMessage;
import com.but42.messengerclient.server_message.ServerMessageType;
import com.but42.messengerclient.user_message.UserMessage;
import com.but42.messengerclient.user_message.UserType;

import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";

    private String mText;
    private Disposable mSubscriber;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyChanges();
        mSubscriber = Model.get().getFlowable().subscribe(s -> notifyChanges());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSubscriber.dispose();
    }

    @OnClick(R.id.button)
    void onClick() {
        UserMessage message = new UserMessage(UserType.OWNER, mText, new Date());
        Model.get().addMessage(message);
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
        for (String string : Model.get().getAllUser()) {
            stringBuilder.append(string).append("\n");
        }
        builder.setMessage(stringBuilder.toString()).show();
    }

    public void notifyChanges() {
        runOnUiThread(() -> {
            mTextViewCountUsers.setText(String.format(Locale.getDefault(),
                    "Число пользователей: %d",
                    Model.get().getAllUser().size()));
            mMessageContainer.removeAllViews();
            for (UserMessage message : Model.get().getMessages())
                mMessageContainer.addView(message.getView(this));
            mScrollView.post(() -> mScrollView.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }
}
