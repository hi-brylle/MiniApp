package com.example.miniapp.viewmodels;

import com.example.miniapp.models.UserDBManager;

import java.text.DateFormatSymbols;
import java.util.Calendar;
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
        String hour = hr > 12 ? String.valueOf(hr - 12) : String.valueOf(hr);
        String minute = min < 10 ? "0" + min : String.valueOf(min);

        return hour + ":" + minute + " ";
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

    public boolean isValid(Date dateTimeSelected) {
        Date now = Calendar.getInstance().getTime();
        return dateTimeSelected.after(now);
    }
}
