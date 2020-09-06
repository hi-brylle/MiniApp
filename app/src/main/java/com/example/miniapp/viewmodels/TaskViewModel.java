package com.example.miniapp.viewmodels;

import com.example.miniapp.models.UserDBManager;
import com.example.miniapp.models.Task;

import java.util.Date;
import java.util.HashMap;
import java.util.Observable;

public class TaskViewModel extends Observable {
    private UserDBManager dbManager;

    public TaskViewModel(UserDBManager dbM){
        dbManager = dbM;
    }

    public void openDB(){
        dbManager.openDB();
    }

    public void closeDB(){
        dbManager.closeDB();
    }

    public void submit(String task, Date created, Date start){
        Task newTask = new Task(task, created, start);

        dbManager.create(newTask);
    }
}
