package com.example.miniapp.models;

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

        try {
            currentDatabase.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Task> readAll(){
        ArrayList<Task> tasks = new ArrayList<>();
        Query allQuery = QueryBuilder.select(SelectResult.all()).from(DataSource.database(currentDatabase));

        ResultSet results;
        exitLabel:
        try {
            results = allQuery.execute();

            if (results == null){
                break exitLabel;
            }

            for(Result result : results){
                String task = result.getString("task");
                Date dateCreated = result.getDate("dateCreated");
                Date dateStart = result.getDate("dateStart");
                boolean isDone = result.getBoolean("isDone");
                boolean isInProgress = result.getBoolean("isInProgress");

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
