package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDBManager extends DBManager {
    public UserDBManager(String dbName, DatabaseConfiguration config){
        super(dbName, config);
    }

    public void create(String task, Date dateCreated, Date dateStart){
        MutableDocument doc = new MutableDocument();
        doc.setString("task", task);
        doc.setString("dateCreated", String.valueOf(dateCreated));
        doc.setString("dateStart", String.valueOf(dateStart));
        doc.setString("isDone", String.valueOf(false));
        doc.setString("isInProgress", String.valueOf(false));

        Log.v("mUserDBManager.create", "task inserted: " + task);
        Log.v("mUserDBManager.create", "date created inserted: " + dateCreated);
        Log.v("mUserDBManager.create", "date start inserted: " + dateStart);

        try {
            currentDatabase.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Task> readAll(){
        ArrayList<Task> tasks = new ArrayList<>();
        Query allQuery = QueryBuilder.select(SelectResult.all())
                            .from(DataSource.database(currentDatabase))
                            .where(Expression.property("task").equalTo(Expression.string("u1 t1"))
                                    .add(Expression.property("task").equalTo(Expression.string("u1 t2"))));

        exitLabel:
        try {
            ResultSet results = allQuery.execute();

            if (results == null){
                Log.v("MY TAG", "results are null ffs");
                break exitLabel;
            }

            Log.v("mUserDBManager.readAll", "data from: " + currentDatabase.getName());
            List<Result> resultList = results.allResults();
            for(Result result : resultList){
                Log.v("MY TAG", "has task? " + result.contains("task"));
                Log.v("MY TAG", "has created? " + result.contains("dateCreated"));
                Log.v("MY TAG", "has start? " + result.contains("dateStart"));
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return tasks;
    }


}
