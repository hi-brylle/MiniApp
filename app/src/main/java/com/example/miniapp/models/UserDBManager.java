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
import java.util.Calendar;
import java.util.Date;

public class UserDBManager extends DBManager {
    public UserDBManager(String dbName, DatabaseConfiguration config){
        super(dbName, config);
    }

    public void create(Task newTask) {
        MutableDocument doc = new MutableDocument();
//        doc.setString("task", newTask.getTask());
//        doc.setDate("dateCreated", newTask.getDateCreated());
//        doc.setDate("dateStart", newTask.getDateStart());
//        doc.setBoolean("isDone", newTask.getIsDone());
//        doc.setBoolean("isInProgress", newTask.getIsInProgress());

        Log.v("mUserDBManager.create", "task inserted: " + newTask.getTask());
        Log.v("mUserDBManager.create", "date created inserted: " + newTask.getDateCreated());
        Log.v("mUserDBManager.create", "date start inserted: " + newTask.getDateStart());

        doc.setString("task", newTask.getTask());
        doc.setString("dateCreated", String.valueOf(newTask.getDateCreated()));
        doc.setString("dateStart", String.valueOf(newTask.getDateStart()));
        doc.setString("isDone", String.valueOf(newTask.getIsDone()));
        doc.setString("isInProgress", String.valueOf(newTask.getIsInProgress()));

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

//                String task = result.getString("task");
//                Date dateCreated = result.getDate("dateCreated");
//                Date dateStart = result.getDate("dateStart");
//                boolean isDone = result.getBoolean("isDone");
//                boolean isInProgress = result.getBoolean("isInProgress");

                // TODO: get back to work here
                String task = result.getString("task");
                String dateCreated = result.getString("dateCreated");
                String dateStart = result.getString("dateStart");
                String isDone = result.getString("isDone");
                String isInProgress = result.getString("isInProgress");

                Log.v("mUserDBManager.readAll", "task: " + task);
                Log.v("mUserDBManager.readAll", "date created: " + dateCreated);
                Log.v("mUserDBManager.readAll", "date start: " + dateStart);

                Date dummyDates = Calendar.getInstance().getTime();
                Task newTask = new Task(task, dummyDates, dummyDates);
//                newTask.setDone(isDone);
//                newTask.setInProgress(isInProgress);

                tasks.add(newTask);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return tasks;
    }



}
