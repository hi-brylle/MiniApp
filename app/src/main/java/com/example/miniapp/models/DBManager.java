package com.example.miniapp.models;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableDocument;

import java.util.Date;

public class DBManager implements IDBOperations{
    private Database database;

    public DBManager(Context context){
        DatabaseConfiguration config = new DatabaseConfiguration(context.getApplicationContext());
        try {
            database = new Database("mini-app-db", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public Database getDatabase() {
        return database;
    }

    @Override
    public void create(String task, Date created, Date start) {
        MutableDocument doc = new MutableDocument();
        doc.setString("task", task);
        doc.setDate("dateCreated", created);
        doc.setDate("dateStart", start);
        doc.setBoolean("isDone", false);
        doc.setBoolean("isInProgress", false);
    }

    @Override
    public void read() {

    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
