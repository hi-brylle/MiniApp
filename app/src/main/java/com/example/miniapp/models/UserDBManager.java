package com.example.miniapp.models;

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
import com.couchbase.lite.SelectResult;
import com.example.miniapp.helper_classes.ISubscriber;

import java.util.Date;
import java.util.HashMap;

public class UserDBManager extends DBManager implements IUserDBManager {
    public UserDBManager(String dbName, DatabaseConfiguration config){
        super(dbName, config);
    }

    @Override
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

    @Override
    public void listenForChanges(){
        Query changesQuery = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(currentDatabase));

        ListenerToken listenerToken = changesQuery.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {
                for (Result result : change.getResults()) {
                    Dictionary all = result.getDictionary(currentDatabase.getName());
                    String task = all.getString("task");
                    Date dateCreated = all.getDate("dateCreated");
                    Date dateStart = all.getDate("dateStart");
                    boolean isDone = all.getBoolean("isDone");

                    Task t = new Task(task, dateCreated, dateStart);
                    t.setDone(isDone);

                    notifySubs(t);
                }
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
        // notify listeners (CustomAdapter and HomeScreenViewModel)
        for(ISubscriber subscriber : listeners){
            subscriber.update(t);
        }
    }

    @Override
    public void notifySubs(HashMap<String, Object> alarmPair) {

    }
}
