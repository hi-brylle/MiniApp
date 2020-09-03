package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DBManager implements IDBManager {
    private Database currentDatabase;   // open one database per session (yeah?)
    private String DBToUseOrMake;       // DB name to use or make for current session

    private DBManager(String dbName, DatabaseConfiguration config){
        // DBToUseOrMake can either be a name for a shared database of login credentials
        // or email names for user-specific data
        DBToUseOrMake = dbName;

        // open database or create it if it doesn't exist
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
        // no need for custom ID cuz we're querying them all later anyway (?)
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

    @Override
    public JSONArray getAll() {
        Query query = QueryBuilder.select(SelectResult.all()).from(DataSource.database(currentDatabase));

        try {
            ResultSet results = query.execute();
            return new JSONArray(results);
        } catch (CouchbaseLiteException | JSONException e) {
            e.printStackTrace();
        }

        return null;

    }
}
