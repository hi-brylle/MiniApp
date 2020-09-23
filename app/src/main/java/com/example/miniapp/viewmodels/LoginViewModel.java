package com.example.miniapp.viewmodels;

import android.util.Log;

import com.example.miniapp.helper_classes.IPublisher;
import com.example.miniapp.helper_classes.ISubscriber;
import com.example.miniapp.models.ILoginDBManager;

public class LoginViewModel implements IViewModel, IPublisher<Integer> {
    private ILoginDBManager dbManager;
    private ISubscriber<Integer> loginView;

    public LoginViewModel(ISubscriber<Integer> loginView, ILoginDBManager dbManager){
        this.dbManager = dbManager;

        // publish changes to MainActivity (login)
        this.addSub(loginView);
    }

    @Override
    public void openDB() {
        dbManager.openDB();
    }

    @Override
    public void closeDB() {
        dbManager.closeDB();
    }

    public void verify(String email, String password) {
        boolean isEmailRegistered = dbManager.isEmailRegistered(email);
        Boolean isPasswordCorrect = null;
        Log.v("MY TAG", "email reg: " + isEmailRegistered);
        if (isEmailRegistered){
            isPasswordCorrect = dbManager.verifyCredentials(email, password);
            Log.v("MY TAG", "password correct: " + isPasswordCorrect);
        }

        // the following integers are used for the login status
        // 0: email is not registered
        // 1: email is registered, password is correct
        // -1: email is registered, password is incorrect
        if (!isEmailRegistered) {
            notifySubs(0);
        } else {
            if (isPasswordCorrect) {
                notifySubs(1);
            } else {
                notifySubs(-1);
            }
        }
    }

    public void register(String email, String password) {
        dbManager.register(email, password);
    }

    @Override
    public void addSub(ISubscriber<Integer> subscriber) {
        loginView = subscriber;
    }

    @Override
    public void removeSub(ISubscriber<Integer> subscriber) {
    }

    @Override
    public void notifySubs(Integer loginStatus) {
        loginView.update(loginStatus);
    }

}
