package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.Date;

public class UserDBManager extends DBManager {
    public UserDBManager(String dbName, DatabaseConfiguration config){
        super(dbName, config);
    }

    public void create(Task newTask) {
        MutableDocument doc = new MutableDocument();
        doc.setString("task", newTask.getTask());
        doc.setDate("dateCreated", newTask.getDateCreated());
        doc.setDate("dateStart", newTask.getDateStart());
        doc.setBoolean("isDone", newTask.getIsDone());
        doc.setBoolean("isInProgress", newTask.getIsInProgress());

        Log.v("MY TAG", "task inserted: " + newTask.getTask());
        Log.v("MY TAG", "date created inserted: " + newTask.getDateCreated());
        Log.v("MY TAG", "date start inserted: " + newTask.getDateStart());

        try {
            currentDatabase.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Task> readAll(){
        ArrayList<Task> tasks = new ArrayList<>();
        Query allQuery = QueryBuilder.select(SelectResult.all())
                            .from(DataSource.database(currentDatabase));

        exitLabel:
        try {
            ResultSet results = allQuery.execute();

            if (results == null){
                break exitLabel;
            }

            Log.v("MY TAG", "data from: " + currentDatabase.getName());
            for(Result result : results){
                // TODO: for some fucking reason, these assholes return null;
                //       opening the DB using debug tools outside AS shows it is not empty
                String task = result.getString("task");
                Date dateCreated = result.getDate("dateCreated");
                Date dateStart = result.getDate("dateStart");
                boolean isDone = result.getBoolean("isDone");
                boolean isInProgress = result.getBoolean("isInProgress");

                Log.v("MY TAG", "task: " + task);
                Log.v("MY TAG", "date created: " + dateCreated);
                Log.v("MY TAG", "date start: " + dateStart);

                Task newTask = new Task(task, dateCreated, dateStart);
                newTask.setDone(isDone);
                newTask.setInProgress(isInProgress);

                tasks.add(newTask);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return tasks;
    }



}
