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
    private final Set<User> mUsers = new HashSet<>();
    private String mNewMessage;

    public void addUser(User newUser) {
        mUsers.add(newUser);
    }

    public void deleteUser(User user) {
        mUsers.remove(user);
    }

    public Set<User> getAllUser() {
        return Collections.unmodifiableSet(mUsers);
    }

    public String getNewMessage() {
        return mNewMessage;
    }

    public void setNewMessage(String newMessage) {
        mNewMessage = newMessage;
    }
}
