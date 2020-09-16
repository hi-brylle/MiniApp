package com.example.miniapp.viewmodels;

import android.util.Log;

import com.example.miniapp.models.Task;
import com.example.miniapp.models.UserDBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;

public class HomeScreenViewModel extends Observable implements IViewModel {
    private UserDBManager dbManager;
    private ArrayList<Task> filteredTasks;

    public HomeScreenViewModel(UserDBManager dbM){
        dbManager = dbM;
    }

    @Override
    public void openDB() {
        dbManager.openDB();
    }

    @Override
    public void closeDB() {
        dbManager.closeDB();
    }

    public void filterActiveTasks(ArrayList<Task> updatedTaskList){
        if (filteredTasks == null){
            filteredTasks = new ArrayList<>();
            filteredTasks.clear();
        }

        Date now = Calendar.getInstance().getTime();
        for(Task task : updatedTaskList){
            if (task.getDateStart().after(now)){
                filteredTasks.add(task);
            }
        }
    }



}
