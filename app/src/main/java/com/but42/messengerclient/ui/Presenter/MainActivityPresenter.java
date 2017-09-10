package com.but42.messengerclient.ui.Presenter;

import android.util.Log;

import com.but42.messengerclient.service.repositories.MessageRepository;
import com.but42.messengerclient.service.repositories.UserRepository;
import com.but42.messengerclient.ui.user_message.User;
import com.but42.messengerclient.ui.user_message.UserMessage;
import com.but42.messengerclient.ui.MainActivity;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Mikhail Kuznetsov on 13.08.2017.
 *
 * @author Mikhail Kuznetsov
 */

public class MainActivityPresenter  {
    private MainActivity mMainActivity;
    private MessageRepository mMessageRepository;
    private UserRepository mUserRepository;
    private CompositeDisposable mCompositeDisposable;

    public MainActivityPresenter(MessageRepository messageRepository, UserRepository userRepository) {
        mMessageRepository = messageRepository;
        mUserRepository = userRepository;
    }

    public void setMainActivity(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public void addMessage(UserMessage message) {
        mMessageRepository.add(message);
    }

    public List<User> getAllUser() {
        return mUserRepository.getAll();
    }

    public List<UserMessage> getMessages() {
        return mMessageRepository.getAll();
    }

    public void subscribe() {
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(mMessageRepository.getFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> mMainActivity.notifyMessages()));
        mCompositeDisposable.add(mUserRepository.getFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> mMainActivity.notifyUsers()));
    }

    public void dispose() {
        mCompositeDisposable.dispose();
    }
}
