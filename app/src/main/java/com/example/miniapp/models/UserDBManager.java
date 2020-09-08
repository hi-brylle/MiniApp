package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
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

    public void create(String task, Date dateCreated, Date dateStart){
        MutableDocument doc = new MutableDocument();
        doc.setString("task", task);
        doc.setString("dateCreated", String.valueOf(dateCreated));
        doc.setString("dateStart", String.valueOf(dateStart));
        doc.setString("isDone", String.valueOf(false));
        doc.setString("isInProgress", String.valueOf(false));

        try {
            currentDatabase.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Task> readAll(){
        ReadAllRunnable readAllRunnable = new ReadAllRunnable();
        Thread readAllThread = new Thread(readAllRunnable);
        readAllThread.start();
        // TODO: make UI put up a loading screen, or use a live query, or something
        try {
            readAllThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return readAllRunnable.getTasks();
    }

    private ArrayList<Task> onThreadReadAll(){
        ArrayList<Task> tasks = new ArrayList<>();
        Query allQuery = QueryBuilder.select(SelectResult.all())
                            .from(DataSource.database(currentDatabase));

        exitLabel:
        try {
            ResultSet results = allQuery.execute();

            if (results == null){
                Log.v("MY TAG", "results are null ffs");
                break exitLabel;
            }

            for(Result result: results){
                Dictionary all = result.getDictionary(currentDatabase.getName());
                String task = all.getString("task");
                String dateCreated = all.getString("dateCreated");
                String dateStart = all.getString("dateStart");

                // TODO: parse strings back as dates, or change how they are inputted to the DB
                Date dummyDates = Calendar.getInstance().getTime();
                tasks.add(new Task(task, dummyDates, dummyDates));
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    private class ReadAllRunnable implements Runnable {
        ArrayList<Task> tasks;

        ReadAllRunnable(){
            tasks = new ArrayList<>();
        }

        @Override
        public void run() {
            tasks = onThreadReadAll();
        }

        // call only after run
        ArrayList<Task> getTasks(){
            return tasks;
        }
    }


}
