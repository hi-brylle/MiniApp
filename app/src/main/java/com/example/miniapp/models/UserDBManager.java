package com.example.miniapp.models;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableDocument;

import java.util.HashMap;
import java.util.Map;

public class UserDBManager {
    private Database currentDatabase;   // open one database per session (yeah?)
    private String DBToUseOrMake;       // DB name to use or make for current session
    private DatabaseConfiguration config;

    public UserDBManager(String dbName, DatabaseConfiguration config){
        // DBToUseOrMake can either be a name for a shared database of login credentials
        // or email names for user-specific data
        DBToUseOrMake = dbName;
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
        MutableDocument doc = new MutableDocument();

        for(Map.Entry<String, String> kvPair : kvPairs.entrySet()){
            // need to parse these values from String to either Date or Boolean later when getting them
            doc.setValue(kvPair.getKey(), kvPair.getValue());
        }

        try {
            currentDatabase.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }



}
