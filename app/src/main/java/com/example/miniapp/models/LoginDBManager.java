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
import com.example.miniapp.helper_classes.PasswordHash;

public class LoginDBManager extends DBManager {
    public LoginDBManager(DatabaseConfiguration config){
        super();
        this.config = config;
        dbToUseOrMake = "users_login";
    }

    public void create(String email, String password) {
        MutableDocument doc = new MutableDocument();
        doc.setString("email", email);
        doc.setString("hash", PasswordHash.hash(password));

        try {
            currentDatabase.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmailRegistered(String email) {
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
    public boolean verifyPassword(String email, String password) {
        boolean isPasswordCorrect = false;
        Query hashQuery = QueryBuilder.select(SelectResult.property("email"), SelectResult.property("hash"))
                        .from(DataSource.database(currentDatabase))
                        .where(Expression.property("email").equalTo(Expression.string(email))
                                .add(Expression.property("hash").equalTo(Expression.string(PasswordHash.hash(password)))));

        try {
            ResultSet resultSet = hashQuery.execute();
            Log.v("MY TAG", "matched users");
            for(Result result : resultSet){
               Log.v("MY TAG", "email: " + result.getString("email"));
               Log.v("MY TAG", "hash: " + result.getString("hash"));
               if(email.equals(result.getString("email")) && PasswordHash.hash(password).equals(result.getString("hash"))){
                   isPasswordCorrect = true;
               }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return isPasswordCorrect;
    }


}
