package com.example.miniapp.viewmodels;

import com.example.miniapp.models.DBManager;
import com.example.miniapp.models.Task;

import java.util.Date;
import java.util.Observable;

public class TaskViewModel extends Observable {
    private DBManager dbManager;

    public TaskViewModel(DBManager dbM){
        dbManager = dbM;
    }

    public void submit(String task, Date created, Date start){
        Task newTask = new Task(task, created, start);
        dbManager.create(newTask);
    }
}
