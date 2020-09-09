package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.miniapp.helper_classes.CustomAdapter;

import java.util.ArrayList;
import java.util.Date;

public class UserDBManager extends DBManager {
    public UserDBManager(String dbName, DatabaseConfiguration config){
        super(dbName, config);
    }

    public void create(String task, Date dateCreated, Date dateStart){
        MutableDocument doc = new MutableDocument();
        doc.setString("task", task);
        doc.setDate("dateCreated", dateCreated);
        doc.setDate("dateStart", dateStart);
        doc.setBoolean("isDone", false);

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
                break exitLabel;
            }

            for(Result result: results){
                Dictionary all = result.getDictionary(currentDatabase.getName());
                String task = all.getString("task");
                Date dateCreated = all.getDate("dateCreated");
                Date dateStart = all.getDate("dateStart");
                boolean isDone = all.getBoolean("isDone");

                Task t = new Task(task, dateCreated, dateStart);
                t.setDone(isDone);
                tasks.add(t);
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

    public void listenForDBChanges(CustomAdapter customAdapter){
        customAdapter.tasksList.clear();
        Query changesQuery = QueryBuilder.select(SelectResult.all())
                        .from(DataSource.database(currentDatabase));

        ListenerToken listenerToken = changesQuery.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {
                for (Result result : change.getResults()) {
                    Log.v("MY TAG", "results: " + result.getKeys());

                    Dictionary all = result.getDictionary(currentDatabase.getName());
                    String task = all.getString("task");
                    Date dateCreated = all.getDate("dateCreated");
                    Date dateStart = all.getDate("dateStart");
                    boolean isDone = all.getBoolean("isDone");

                    Task t = new Task(task, dateCreated, dateStart);
                    t.setDone(isDone);

                    customAdapter.tasksList.add(t);

                    customAdapter.notifyDataSetChanged();


                }
            }
        });

        try {
            changesQuery.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

}
