package com.but42.messengerclient.user_message;

import android.support.annotation.DrawableRes;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class User {
    private String mName;
    private UserType mType;

    public User(UserType type, String name) {
        mType = type;
        mName = name;
    }

    public User(UserType type) {
        mType = type;
        mName = "Вы";
    }

    public String getName() {
        return mName;
    }

    @DrawableRes
    public int getBackground() {
        return mType.getBackground();
    }

    public float getBias() {
        return mType.getBias();
    }
}
