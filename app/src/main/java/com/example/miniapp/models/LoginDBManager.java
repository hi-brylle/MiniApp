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

    public int verify(String email, String hash) {
        Query emailQuery = QueryBuilder.select(SelectResult.property("email"), SelectResult.property("hash"))
                        .from(DataSource.database(currentDatabase))
                        .where(Expression.property("email").equalTo(Expression.string(email)));

        label:
        try {
            ResultSet resultSet = emailQuery.execute();
            if (resultSet == null){
                // register email
                break label;
            }
            Log.v("MY TAG", "matched users");
            for(Result result : resultSet){
               Log.v("MY TAG", "email: " + result.getString("email"));
               Log.v("MY TAG", "hash: " + result.getString("hash"));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        // return 0 when email is not yet registered
        return 0;
    }

    public boolean isEmailRegistered(String email) {
        return false;
    }
}
