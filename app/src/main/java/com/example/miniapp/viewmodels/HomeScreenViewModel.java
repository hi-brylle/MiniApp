package com.example.miniapp.viewmodels;

import android.util.Log;

import com.example.miniapp.helper_classes.IPublisher;
import com.example.miniapp.helper_classes.ISubscriber;
import com.example.miniapp.models.IUserDBManager;
import com.example.miniapp.models.Task;
import com.example.miniapp.models.UserDBManager;

import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;

public class HomeScreenViewModel implements IViewModel, ISubscriber, IPublisher {
    private IUserDBManager dbManager;
    private ISubscriber homeScreenView;

    public HomeScreenViewModel(ISubscriber homeScreenView, IUserDBManager dbManager){
        this.dbManager = dbManager;
        // subscribe to changes in the DB
        this.dbManager.addSub(this);

        // publish changes to HomeScreenActivity
        this.addSub(homeScreenView);
    }

    @Override
    public void openDB() {
        dbManager.openDB();
    }

    @Override
    public void closeDB() {
        dbManager.closeDB();
    }

    @Override
    public void update(Task t) {
        Date now = Calendar.getInstance().getTime();
        if (t.getDateStart().after(now)){
            Log.v("MY TAG", "active added: " + t.getTask());
            String task = t.getTask();
            Date dateStart = t.getDateStart();
            HashMap<String, Object> alarmPair = new HashMap<>();
            alarmPair.put("task", task);
            alarmPair.put("dateStart", dateStart);

            notifySubs(alarmPair);
        }
    }

    @Override
    public void update(HashMap<String, Object> alarmPair) {

    }

    @Override
    public void update(int loginStatus) {

    }

    @Override
    public void addSub(ISubscriber subscriber) {
        homeScreenView = subscriber;
    }

    @Override
    public void removeSub(ISubscriber subscriber) {

    }

    @Override
    public void notifySubs(Task t) {

    }

    @Override
    public void notifySubs(HashMap<String, Object> alarmPair) {
        homeScreenView.update(alarmPair);
    }

    @Override
    public void notifySubs(int loginStatus) {

    }
}
