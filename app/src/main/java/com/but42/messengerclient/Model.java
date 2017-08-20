package com.but42.messengerclient;

import com.but42.messengerclient.user_message.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class Model {
    private final Set<String> mUsers = new HashSet<>();
    private String mNewMessage;

    public void addUser(String newUser) {
        mUsers.add(newUser);
    }

    public void deleteUser(String user) {
        mUsers.remove(user);
    }

    public Set<String> getAllUser() {
        return Collections.unmodifiableSet(mUsers);
    }

    public String getNewMessage() {
        return mNewMessage;
    }

    public void setNewMessage(String newMessage) {
        mNewMessage = newMessage;
    }
}
