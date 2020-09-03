package com.example.miniapp.viewmodels;

import android.text.Editable;

import com.example.miniapp.models.DBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Observable;

public class LoginViewModel extends Observable implements IViewModel {
    private DBManager dbManager;

    public LoginViewModel(DBManager dbM){
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
        JSONArray emailsAndHashes = dbManager.getAll();
        boolean isEmailRegistered = false;
        boolean isPasswordCorrect = false;

        // go through the iteration of email-hash pairs if dbManager returns non-null
        if (emailsAndHashes != null){
            for (int i = 0; i < emailsAndHashes.length(); i++) {
                JSONObject emailHashPair = emailsAndHashes.getJSONObject(i);
                String storedEmail = emailHashPair.getString("email");

                // if email exists in record, check for password match
                if (storedEmail.equals(email)) {
                    isEmailRegistered = true;
                    String storedHash = emailHashPair.getString("hash");
                    isPasswordCorrect = isPasswordCorrect(password, storedHash);
                    // TODO: wait for hash check thread execute
                    break;
                }
            }
        }

        // the following integers are used for the login status
        // 0: email is not registered
        // 1: email is registered, password is correct
        // -1: email is registered, password is incorrect
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
        HashMap<String, String> emailHashPair = new HashMap<>();
        emailHashPair.put("email", email);
        emailHashPair.put("hash", hash(password));

        dbManager.create(emailHashPair);
    }
}
