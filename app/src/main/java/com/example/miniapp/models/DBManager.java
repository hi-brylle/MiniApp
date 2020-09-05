package com.example.miniapp.models;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;

public abstract class DBManager {
    protected  Database currentDatabase;   // open one database per session (yeah?)
    protected String dbToUseOrMake;       // DB name to use or make for current session
    protected DatabaseConfiguration config;

    public DBManager() {

    }

    public DBManager(String dbName, DatabaseConfiguration config){
        dbToUseOrMake = dbName;
        this.config = config;
    }

    public void openDB() {
        // open database or create it if it doesn't exist
        try {
            currentDatabase = new Database(dbToUseOrMake, config);
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
}
