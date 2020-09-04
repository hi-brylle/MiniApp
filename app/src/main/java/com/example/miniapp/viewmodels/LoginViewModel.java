package com.example.miniapp.viewmodels;

import com.example.miniapp.models.LoginDBManager;

import org.json.JSONException;

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

    // TODO: implement this!
    private String hash(String plainPassword){
        // hash code here...
        return plainPassword; // LOL
    }

    // TODO: implement the actual hashing and checking, do in separate thread
    private boolean isPasswordCorrect(String password, String storedHash){
        return password.equals(storedHash);
    }

    public void verify(String email, String password) throws JSONException {
        // TODO : HASH!
        boolean isEmailRegistered = dbManager.isEmailRegistered(email);
        // the following integers are used for the login status
        // 0: email is not registered
        // 1: email is registered, password is correct
        // -1: email is registered, password is incorrect
        int loginStatus = dbManager.verify(email, hash(password));
        boolean isPasswordCorrect = false;

        if (!isEmailRegistered) {
            setChanged();
            notifyObservers(0);
        } else {
            if (isPasswordCorrect) {
                setChanged();
                notifyObservers(1);
            } else {
                setChanged();
                notifyObservers(-1);
            }
        }
    }


    public void register(String email, String password) {
        // TODO: hash (and even salt) the password!
        dbManager.create(email, password);
    }
}
