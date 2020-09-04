package com.example.miniapp.models;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableDocument;

import java.util.HashMap;
import java.util.Map;

public class LoginDBManager implements IDBManager {
    private Database currentDatabase;   // open one database per session (yeah?)
    private String DBToUseOrMake;
    private DatabaseConfiguration config;

    public LoginDBManager(DatabaseConfiguration config){
        // this is the fixed name that identifies the database for login credentials
        DBToUseOrMake = "users_login";
        this.config = config;
    }

    @Override
    public void openDB() {
        // open database or create it if it doesn't exist
        try {
            currentDatabase = new Database(DBToUseOrMake, config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeDB() {
        try {
            currentDatabase.close();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(HashMap<String, String> kvPairs) {

    }

    @Override
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
}
