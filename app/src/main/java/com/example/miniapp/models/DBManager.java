package com.example.miniapp.models;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;

public class DBManager {
    private Database database;

    public DBManager(Context context){
        DatabaseConfiguration config = new DatabaseConfiguration(context.getApplicationContext());
        try {
            database = new Database("mini-app-db", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void create(Task task) {
        // docID shall be the Unix timestamp of the task on the date it was created,
        // I mean, that should be unique enough
        String docID = String.valueOf(task.getDateCreated().getTime() / 1000);
        MutableDocument doc = new MutableDocument(docID);

        doc.setString("task", task.getTask());
        doc.setDate("dateCreated", task.getDateCreated());
        doc.setDate("dateStart", task.getDateStart());
        doc.setBoolean("isDone", task.getIsMarkDone());
        doc.setBoolean("isInProgress", task.getIsInProgress());

        try {
            database.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void read() {

    }

    public void update(String docID, Boolean markDone, Boolean markInProgress) {
        Document immutableDoc = database.getDocument(docID);
        MutableDocument mutableDoc = immutableDoc.toMutable();
        mutableDoc.setBoolean("isDone", markDone);
        mutableDoc.setBoolean("isInProgress", markInProgress);

        try {
            database.save(mutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void delete(String docID) {
        Document immutableDoc = database.getDocument(docID);

        try {
            database.delete(immutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
