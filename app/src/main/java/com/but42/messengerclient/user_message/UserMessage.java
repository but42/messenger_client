package com.but42.messengerclient.user_message;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.but42.messengerclient.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class UserMessage {
    private UserType mUser;
    private String mText;
    private Date mDate;

    public UserMessage(UserType user, String text, Date date) {
        mUser = user;
        mText = text;
        mDate = date;
    }

    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_message, null);
        ConstraintLayout layout = (ConstraintLayout) view;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.setHorizontalBias(R.id.message_layout, mUser.getBias());
        constraintSet.applyTo(layout);
        ConstraintLayout messageLayout = view.findViewById(R.id.message_layout);
        messageLayout.setBackground(context.getResources().getDrawable(mUser.getBackground()));
        TextView message = view.findViewById(R.id.message_text);
        String text = mText;
        if (mUser == UserType.OWNER) text = "Вы: " + mText;
        message.setText(text);
        TextView time = view.findViewById(R.id.message_time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        time.setText(format.format(mDate));
        return view;
    }

    public UserType getUser() {
        return mUser;
    }

    public String getText() {
        return mText;
    }
}
