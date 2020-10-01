package com.example.miniapp.viewmodels;

import android.annotation.SuppressLint;

import com.example.miniapp.models.IUserDBManager;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class NewTaskViewModel {
    private IUserDBManager dbManager;

    public NewTaskViewModel(IUserDBManager dbManager){
        this.dbManager = dbManager;
    }

    public static String  dateRepresentation(int year, int month, int day) {
        // needs localization
        String monthName = new DateFormatSymbols().getMonths()[month];

        return day + " " + monthName + " " + year;
    }

    @SuppressLint("DefaultLocale")
    public static String timeRepresentation(int hr, int min) {
        int hour = hr % 12;
        if (hour == 0) hour = 12;

        return String.format("%02d:%02d %s", hour, min, hr < 12 ? "am" : "pm");

    }

    public void openDB(){
        dbManager.openDB();
    }

    public void closeDB(){
        dbManager.closeDB();
    }

    public void submit(String task, Date created, Date start, String imageURIString, String addressString){
        dbManager.create(task, created, start, imageURIString, addressString);
    }

    public boolean isValid(Date dateTimeSelected) {
        Date now = Calendar.getInstance().getTime();
        return dateTimeSelected.after(now);
    }
}
