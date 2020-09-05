package com.example.miniapp.viewmodels;

import android.util.Log;

import com.example.miniapp.models.LoginDBManager;

import java.util.Observable;

public class LoginViewModel extends Observable implements IViewModel {
    private LoginDBManager dbManager;

    public LoginViewModel(LoginDBManager dbM){
        dbManager = dbM;
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
            isPasswordCorrect = dbManager.verifyPassword(email, password);
            Log.v("MY TAG", "password correct: " + isPasswordCorrect);
        }

        // the following integers are used for the login status
        // 0: email is not registered
        // 1: email is registered, password is correct
        // -1: email is registered, password is incorrect
        if (!isEmailRegistered) {
            setChanged();
            notifyObservers(0);
        } else {
            setChanged();
            if (isPasswordCorrect) {
                notifyObservers(1);
            } else {
                notifyObservers(-1);
            }
        }
    }


    public void register(String email, String password) {
        dbManager.create(email, password);
    }
}
