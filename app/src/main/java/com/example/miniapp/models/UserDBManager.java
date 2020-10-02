package com.example.miniapp.models;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.miniapp.helper_classes.ISubscriber;

import java.util.Date;

public class UserDBManager extends DBManager implements IUserDBManager {
    Query changesQuery;
    ListenerToken listenerToken;
    public UserDBManager(String dbName, DatabaseConfiguration config){
        super(dbName, config);
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
    public void queueDelete(String task, Date dateCreated, Date dateStart) {
        Query docQuery = QueryBuilder.select(SelectResult.expression(Meta.id))
                                        .from(DataSource.database(currentDatabase))
                                        .where(Expression.property("task").equalTo(Expression.string(task))
                                            .add(Expression.property("dateCreated").equalTo(Expression.date(dateCreated)))
                                            .add(Expression.property("dateStart").equalTo(Expression.date(dateStart))));

        try {
            ResultSet resultSet = docQuery.execute();
            for(Result result : resultSet){
                String id = result.getString("id");
                MutableDocument mutableDocument  = currentDatabase.getDocument(id).toMutable();
                mutableDocument.setBoolean("isQueuedForDeletion", true);
                currentDatabase.save(mutableDocument);
            }
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
                t.addImageURI(imageURI);
                t.setAddress(address);

                Log.v("MY TAG", "Task Retrieved: " + task);
                Log.v("MY TAG", "Created Retrieved: " + dateCreated);
                Log.v("MY TAG", "Start Retrieved: " + dateStart);
                Log.v("MY TAG", "URI Retrieved: " + imageURI);
                Log.v("MY TAG", "Address Retrieved: " + address);

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
        super.openDB();
        changesQuery = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(currentDatabase));
    }

    @Override
    public void closeDB() {
        if (listenerToken != null) {
            changesQuery.removeChangeListener(listenerToken);
        }

        super.closeDB();
    }
}
