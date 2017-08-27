package com.but42.messengerclient.service.repositories;

import com.but42.messengerclient.service.SocketService;
import com.but42.messengerclient.service.server_message.ServerMessageType;
import com.but42.messengerclient.service.user_message.User;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Mikhail Kuznetsov on 27.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class UserRepository implements Repository<User> {
    private List<User> mUsers;

    public UserRepository() {
        mUsers = new ArrayList<>();
    }

    @Override
    public void add(User item) {

    }

    @Override
    public List<User> getAll() {
        return mUsers;
    }

    @Override
    public Flowable<User> getFlowable() {
        return SocketService.getFlowable()
                .filter(serverMessage -> serverMessage.getType() == ServerMessageType.USER_ADDED
                        || serverMessage.getType() == ServerMessageType.USER_REMOVED)
                .flatMap(serverMessage -> Flowable.just(new User(serverMessage.getData(), serverMessage.getType())))
                .doOnNext(user -> {
                    switch (user.getServerType()) {
                        case USER_ADDED:
                            mUsers.add(user);
                            break;
                        case USER_REMOVED:
                            mUsers.remove(user);
                            break;
                    }
                });
    }
}
