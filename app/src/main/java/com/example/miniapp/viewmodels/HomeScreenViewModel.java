package com.example.miniapp.viewmodels;

import android.util.Log;

import com.example.miniapp.models.IUserDBManager;
import com.example.miniapp.models.Task;
import com.example.miniapp.models.UserDBManager;

import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class HomeScreenViewModel extends Observable implements IViewModel, Observer {
    private UserDBManager dbManager;

    public HomeScreenViewModel(UserDBManager dbManager){
        this.dbManager = dbManager;
        dbManager.addObserver(this);
    }

    @Override
    public void openDB() {
        dbManager.openDB();
    }

    @Override
    public void closeDB() {
        dbManager.closeDB();
    }

    public void filterActiveTasks(){
//        userDBManager.listenForChanges();
    }

    @Override
    public void update(Observable observable, Object o) {
        Task justAdded = (Task) o;
        Date now = Calendar.getInstance().getTime();
        if (justAdded.getDateStart().after(now)){
            Log.v("MY TAG", "active added: " + justAdded.getTask());
            String task = justAdded.getTask();
            Date dateStart = justAdded.getDateStart();
            HashMap<String, Object> alarmPair = new HashMap<>();
            alarmPair.put("task", task);
            alarmPair.put("dateStart", dateStart);
            setChanged();
            notifyObservers(alarmPair);
        }
    }
}
