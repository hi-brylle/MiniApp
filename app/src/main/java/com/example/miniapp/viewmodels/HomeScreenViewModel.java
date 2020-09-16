package com.example.miniapp.viewmodels;

import android.util.Log;

import com.example.miniapp.models.Task;
import com.example.miniapp.models.UserDBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.Observable;
import java.util.Observer;

public class HomeScreenViewModel extends Observable implements IViewModel, Observer {
    private UserDBManager userDBManager;
    private ArrayList<Task> taskList;
    private ArrayList<Task> activeTasks;

    public HomeScreenViewModel(UserDBManager userDBManager){
        this.userDBManager = userDBManager;
        userDBManager.addObserver(this);
        if (taskList == null){
            taskList = new ArrayList<>();
        }
        if (activeTasks == null){
            activeTasks = new ArrayList<>();
        }
    }

    @Override
    public void openDB() {
        userDBManager.openDB();
    }

    @Override
    public void closeDB() {
        userDBManager.closeDB();
    }

    public void filterActiveTasks(){
        activeTasks.clear();
        userDBManager.listenForChanges();
    }

    @Override
    public void update(Observable observable, Object o) {
        Task justAdded = (Task) o;
        Date now = Calendar.getInstance().getTime();
        if (justAdded.getDateStart().after(now)){
            activeTasks.add(justAdded);
            Log.v("MY TAG", "active added: " + justAdded.getTask());
        }
    }
}
