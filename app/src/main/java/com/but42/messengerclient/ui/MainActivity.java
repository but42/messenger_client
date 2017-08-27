package com.but42.messengerclient.ui;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.but42.messengerclient.R;
import com.but42.messengerclient.service.ApiService;
import com.but42.messengerclient.service.ServiceReceiver;
import com.but42.messengerclient.service.SocketService;
import com.but42.messengerclient.service.repositories.MessageRepository;
import com.but42.messengerclient.service.repositories.UserRepository;
import com.but42.messengerclient.service.user_message.User;
import com.but42.messengerclient.service.user_message.UserMessage;
import com.but42.messengerclient.service.user_message.UserType;
import com.but42.messengerclient.ui.module.MainActivityPresenter;

import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";

    private String mText;
    private MainActivityPresenter mPresenter;

    @BindView(R.id.message_container) LinearLayout mMessageContainer;
    @BindView(R.id.editText) EditText mEditText;
    @BindView(R.id.count_users_text) TextView mTextViewCountUsers;
    @BindView(R.id.scrollView) ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ApiService service = new ApiService();
        ServiceReceiver receiver = new ServiceReceiver(service);
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra(SocketService.EXTRA_RECEIVER, receiver);
        startService(intent);
        mPresenter = new MainActivityPresenter(new MessageRepository(service), new UserRepository(service));
        mPresenter.setMainActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyUI();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.dispose();
    }

    @OnClick(R.id.button)
    void onClick() {
        UserMessage message = new UserMessage(new User(User.getOwnerName(), UserType.OWNER), mText, new Date());
        mPresenter.addMessage(message);
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
        for (User user : mPresenter.getAllUser()) {
            stringBuilder.append(user.getName()).append("\n");
        }
        builder.setMessage(stringBuilder.toString()).show();
    }

    private void notifyUI() {
        notifyUsers();
        notifyMessages();
    }

    public void notifyUsers() {
        mTextViewCountUsers.setText(String.format(Locale.getDefault(),
                "Число пользователей: %d",
                mPresenter.getAllUser().size()));
    }

    public void notifyMessages() {
        mMessageContainer.removeAllViews();
        for (UserMessage message : mPresenter.getMessages())
            mMessageContainer.addView(message.getView(this));
        mScrollView.post(() -> mScrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
