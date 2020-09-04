package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.HashMap;
import java.util.Map;

public class DBManager implements IDBManager {
    private Database currentDatabase;   // open one database per session (yeah?)
    private String DBToUseOrMake;       // DB name to use or make for current session
    private DatabaseConfiguration config;

    public DBManager(String dbName, DatabaseConfiguration config){
        // DBToUseOrMake can either be a name for a shared database of login credentials
        // or email names for user-specific data
        DBToUseOrMake = dbName;
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
    public void create(String email, String hash) {

    }

    public void read(String email, String password){
        Query query = QueryBuilder.select(SelectResult.property("email"), SelectResult.property("hash")).from(DataSource.database(currentDatabase));
        try {
            ResultSet resultsSet = query.execute();
            for(Result result : resultsSet){
                Log.v("MY TAG", "email: " + result.getString("email"));
                Log.v("MY TAG", "password: "+ result.getString("hash"));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

}
