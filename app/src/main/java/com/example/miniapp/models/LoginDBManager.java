package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.HashMap;

public class LoginDBManager {
    private Database currentDatabase;   // open one database per session (yeah?)
    // this is the fixed name that identifies the database for login credentials
    private static final String DBToUseOrMake = "users_login";
    private DatabaseConfiguration config;

    public LoginDBManager(DatabaseConfiguration config){
        this.config = config;
    }

    public void openDB() {
        // open database or create it if it doesn't exist
        try {
            currentDatabase = new Database(DBToUseOrMake, config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        try {
            currentDatabase.close();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void create(HashMap<String, String> kvPairs) {

    }

    public void create(String email, String hash) {
        MutableDocument doc = new MutableDocument();
        doc.setString("email", email);
        doc.setString("hash", hash);

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
            ResultSet resultSet = emailQuery.execute();

            // DB has no entries yet
            if (resultSet == null){
                break exitLabel;
            }

            for(Result result: resultSet){
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

    public boolean verify(String email, String hash) {
        boolean isPasswordCorrect = false;
        Query hashQuery = QueryBuilder.select(SelectResult.property("email"), SelectResult.property("hash"))
                        .from(DataSource.database(currentDatabase))
                        .where(Expression.property("email").equalTo(Expression.string(email))
                                .add(Expression.property("hash").equalTo(Expression.string(hash))));

        try {
            ResultSet resultSet = hashQuery.execute();
            Log.v("MY TAG", "matched users");
            for(Result result : resultSet){
               Log.v("MY TAG", "email: " + result.getString("email"));
               Log.v("MY TAG", "hash: " + result.getString("hash"));
               if(email.equals(result.getString("email")) && hash.equals(result.getString("hash"))){
                   isPasswordCorrect = true;
               }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return isPasswordCorrect;
    }


}
