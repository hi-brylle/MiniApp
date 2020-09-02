package com.example.miniapp.viewmodels;

import com.example.miniapp.models.Task;

import java.util.Date;
import java.util.Observable;

public class TaskViewModel extends Observable {
    public TaskViewModel(){
    }

    public void submit(String task, Date created, Date start){
        Task newTask = new Task(task, created, start);
        this.setChanged();
        notifyObservers("Should be putting this in the DB now");
        //put the thing in the DB
    }
}
