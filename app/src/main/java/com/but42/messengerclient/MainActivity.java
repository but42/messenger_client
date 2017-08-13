package com.but42.messengerclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.but42.messengerclient.user_message.User;
import com.but42.messengerclient.user_message.UserMessage;
import com.but42.messengerclient.user_message.UserType;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.message_container) LinearLayout mMessageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        User owner = new User(UserType.OWNER);
        User ted = new User(UserType.OTHER, "Ted");
        User but = new User(UserType.OTHER, "but");

        mMessageContainer.addView(new UserMessage(owner, owner.getName(), new Date()).getView(this));
        mMessageContainer.addView(new UserMessage(ted, ted.getName(), new Date()).getView(this));
        mMessageContainer.addView(new UserMessage(but, but.getName(), new Date()).getView(this));
    }
}
