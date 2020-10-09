package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.miniapp.helper_classes.PWHash;

public class LoginDBManager extends DBManager implements ILoginDBManager {
    public LoginDBManager(DatabaseConfiguration config){
        super();
        this.config = config;
        dbToUseOrMake = "users_login";
    }

    @Override
    public void register(String email, String password) {
        MutableDocument doc = new MutableDocument();
        doc.setString("email", email);
        doc.setString("hash", PWHash.hash(password));

        try {
            currentDatabase.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEmailRegistered(String email) {
       EmailRegisteredRunnable emailRegisteredRunnable =  new EmailRegisteredRunnable(email);
       Thread emailThread = new Thread(emailRegisteredRunnable);
       emailThread.start();
        try {
            emailThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return emailRegisteredRunnable.isRegistered();
    }

    @Override
    public boolean verifyCredentials(String email, String password) {
        VerifyPasswordRunnable verifyPasswordRunnable = new VerifyPasswordRunnable(email, password);
        Thread passwordThread = new Thread(verifyPasswordRunnable);
        passwordThread.start();
        try {
            passwordThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return verifyPasswordRunnable.isCorrect();
    }

    private boolean OnThreadIsEmailRegistered(String email) {
        boolean isEmailRegistered = false;

        Query emailQuery = QueryBuilder.select(SelectResult.property("email"))
                .from(DataSource.database(currentDatabase))
                .where(Expression.property("email").equalTo(Expression.string(email)));

        exitLabel:
        try {
            ResultSet results = emailQuery.execute();

            // if app has no users yet
            if (results == null){
                break exitLabel;
            }

            for(Result result: results){
                Log.v("MY TAG", "email found: " + result.getString("email"));
                if (email.equals(result.getString("email"))){
                    isEmailRegistered = true;
                }
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


        return isEmailRegistered;
    }

    // TODO: should be done server-side
    private boolean onThreadVerifyPassword(String email, String password) {
        boolean isPasswordCorrect = false;
        Query hashQuery = QueryBuilder.select(SelectResult.property("email"), SelectResult.property("hash"))
                        .from(DataSource.database(currentDatabase))
                        .where(Expression.property("email").equalTo(Expression.string(email))
                                .add(Expression.property("hash").equalTo(Expression.string(PWHash.hash(password)))));

        try {
            ResultSet resultSet = hashQuery.execute();
            Log.v("MY TAG", "matched users");
            for(Result result : resultSet){
               Log.v("MY TAG", "email: " + result.getString("email"));
               Log.v("MY TAG", "hash: " + result.getString("hash"));
               if(email.equals(result.getString("email")) && PWHash.hash(password).equals(result.getString("hash"))){
                   isPasswordCorrect = true;
               }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return isPasswordCorrect;
    }

    private class EmailRegisteredRunnable implements Runnable {
        private String email;
        private boolean isEmailRegistered;
        public EmailRegisteredRunnable(String email){
            this.email = email;
        }

        @Override
        public void run() {
            isEmailRegistered = OnThreadIsEmailRegistered(email);
        }

        // call only after run()
        boolean isRegistered(){
            return isEmailRegistered;
        }
    }

    private class VerifyPasswordRunnable implements Runnable {
        private String email;
        private String password;
        private boolean isPasswordCorrect;
        public VerifyPasswordRunnable(String email, String password){
            this.email = email;
            this.password = password;
        }

        @Override
        public void run() {
            isPasswordCorrect = onThreadVerifyPassword(email, password);
        }

        // call only after run()
        boolean isCorrect(){
            return isPasswordCorrect;
        }
    }
}
