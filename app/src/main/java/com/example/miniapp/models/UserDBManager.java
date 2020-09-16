package com.example.miniapp.models;

import android.util.Log;
import android.widget.ArrayAdapter;

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

                    setChanged();
                    notifyObservers(t);
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
