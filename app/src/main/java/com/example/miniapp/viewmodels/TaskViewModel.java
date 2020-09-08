package com.example.miniapp.viewmodels;

import com.example.miniapp.models.UserDBManager;
import com.example.miniapp.models.Task;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.Observable;

public class TaskViewModel extends Observable {
    private UserDBManager dbManager;

    public TaskViewModel(UserDBManager dbM){
        dbManager = dbM;
    }

    public static String  dateRepresentation(int year, int month, int day) {
        // needs localization
        String monthName = new DateFormatSymbols().getMonths()[month];

        return day + " " + monthName + " " + year;
    }

    public static String timeRepresentation(int hr, int min) {
        String hour = hr < 10 ? "0" + hr : String.valueOf(hr);
        String minute = min < 10 ? "0" + min : String.valueOf(min);
        String xm = hr < 12 ? "AM" : "PM";

        return hour + ":" + minute + " " + xm;
    }

    public void openDB(){
        dbManager.openDB();
    }

    public void closeDB(){
        dbManager.closeDB();
    }

    public void submit(String task, Date created, Date start){
        dbManager.create(task, created, start);
    }
}
