package com.example.miniapp.models;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.SelectResult;
import com.example.miniapp.helper_classes.ISubscriber;
import com.example.miniapp.helper_classes.Logger;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class UserDBManager implements IUserDBManager {
    ArrayList<ISubscriber> listeners;
    protected Database currentDatabase;    // open one database per session (yeah?)
    protected String dbToUseOrMake;         // DB name to use or make for current session
    protected DatabaseConfiguration config;

    Query changesQuery;
    ListenerToken listenerToken;

    public UserDBManager(String dbName, DatabaseConfiguration config){
        dbToUseOrMake = dbName;
        this.config = config;
    }

    @Override
    public void create(String task, Date dateCreated, Date dateStart, String imageURIString, String address){
        MutableDocument doc = new MutableDocument();
        doc.setString("task", task);
        doc.setDate("dateCreated", dateCreated);
        doc.setDate("dateStart", dateStart);
        doc.setBoolean("isDone", false);
        doc.setString("imageURI", imageURIString);
        doc.setString("address", address);

        try {
            currentDatabase.save(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void listenForChanges(){
        listenerToken = changesQuery.addChangeListener(change -> {
            for (Result result : change.getResults()) {
                Dictionary all = result.getDictionary(currentDatabase.getName());
                String task = all.getString("task");
                Date dateCreated = all.getDate("dateCreated");
                Date dateStart = all.getDate("dateStart");
                boolean isDone = all.getBoolean("isDone");
                String imageURI = all.getString("imageURI");
                String address = all.getString("address");

                Task t = new Task(task, dateCreated, dateStart);

                // TODO: change setDone based on date and time
                t.setDone(isDone);
                t.setImageURI(imageURI);
                t.setAddress(address);

                Logger.log("Task Retrieved: " + task);
                Logger.log("Created Retrieved: " + dateCreated);
                Logger.log("Start Retrieved: " + dateStart);
                Logger.log("URI Retrieved: " + imageURI);
                Logger.log("Address Retrieved: " + address);

                notifySubs(t);


            }
        });

        try {
            changesQuery.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifySubs(Task t) {
        for(ISubscriber subscriber : listeners){
            subscriber.update(t);
        }
    }

    @Override
    public void openDB() {

        try {
            currentDatabase = new Database(dbToUseOrMake, config);
            Logger.log("opened " + currentDatabase.getName());

            changesQuery = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(currentDatabase));
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeDB() {
        if (listenerToken != null) {
            changesQuery.removeChangeListener(listenerToken);
        }

        try {
            currentDatabase.close();
            Logger.log("closed " + currentDatabase.getName());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSub(@Nullable ISubscriber<Task> subscriber) {
        if (listeners == null){
            listeners = new ArrayList<>();
        }

        listeners.add(subscriber);
    }
}
