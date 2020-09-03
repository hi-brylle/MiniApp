package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;

import java.util.HashMap;

public class DBManager implements IDBManager {
    private Database currentDatabase;   //open one database per session (yeah?)
    private String DBToUseOrMake;       //DB name to use or make for current session

    private DBManager(String dbName, DatabaseConfiguration config){
        DBToUseOrMake = dbName;

        try {
            currentDatabase = new Database(DBToUseOrMake, config);
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
            currentDatabase.save(doc);
            Log.v("MY TAG", "Success I guess?");
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void read() {

    }

    public void update(String docID, Boolean markDone, Boolean markInProgress) {
        Document immutableDoc = currentDatabase.getDocument(docID);
        MutableDocument mutableDoc = immutableDoc.toMutable();
        mutableDoc.setBoolean("isDone", markDone);
        mutableDoc.setBoolean("isInProgress", markInProgress);

        try {
            currentDatabase.save(mutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void delete(String docID) {
        Document immutableDoc = currentDatabase.getDocument(docID);

        try {
            currentDatabase.delete(immutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(HashMap<String, String> kvPairs) {
        
    }
}
