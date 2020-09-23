package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.helper_classes.ISubscriber;

import java.util.ArrayList;

public abstract class DBManager implements IDBManager {
    ArrayList<ISubscriber> listeners;
    protected Database currentDatabase;    // open one database per session (yeah?)
    protected String dbToUseOrMake;         // DB name to use or make for current session
    protected DatabaseConfiguration config;

    public DBManager() {

    }

    public DBManager(String dbName, DatabaseConfiguration config){
        dbToUseOrMake = dbName;
        this.config = config;
    }

    @Override
    public void openDB() {
        // open database or create it if it doesn't exist
        try {
            currentDatabase = new Database(dbToUseOrMake, config);
            Log.v("MY TAG", "opened " + currentDatabase.getName());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeDB() {
        try {
            currentDatabase.close();
            Log.v("MY TAG", "closed " + currentDatabase.getName());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void addSub(ISubscriber subscriber){
        if (listeners == null){
            listeners = new ArrayList<>();
        }

        listeners.add(subscriber);
    }

    public void removeSub(ISubscriber subscriber){
        listeners.remove(subscriber);
    }
}
