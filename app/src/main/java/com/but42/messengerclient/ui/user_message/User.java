package com.but42.messengerclient.ui.user_message;

import android.support.annotation.DrawableRes;

import com.but42.messengerclient.service.server_message.ServerMessageType;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class User {
    private String mName;
    private UserType mUserType;
    private ServerMessageType mServerType;

    public User(String name, ServerMessageType serverType) {
        mName = name;
        mServerType = serverType;
    }

    public User(String name, UserType userType) {
        mName = name;
        mUserType = userType;
    }

    public String getName() {
        return mName;
    }

    public ServerMessageType getServerType() {
        return mServerType;
    }

    public UserType getUserType() {
        return mUserType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return mName != null ? mName.equals(user.mName) : user.mName == null;

    }

    @Override
    public int hashCode() {
        return mName != null ? mName.hashCode() : 0;
    }

    @DrawableRes
    public int getBackground() {
        return mUserType.getBackground();
    }

    public float getBias() {
        return mUserType.getBias();
    }

    public static String getOwnerName() {
        return "Ted";
    }
}
