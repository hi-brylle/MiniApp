package com.example.miniapp.models;

import java.util.Date;

public class Task {
    private String task;
    private Date dateCreated;
    private Date dateStart;
    private boolean isDone;
    private boolean isExpanded; // for use in the recycler view only; not stored in DB
    private String imageURI;
    private String address;

    public Task(String task, Date created, Date start){
        this.task = task;
        this.dateCreated = created;
        this.dateStart = start;
        this.isDone = false;
        imageURI = "";
        address = "";
    }

    public void setDone(boolean m){
        isDone = m;
    }

    public void setExpanded(boolean b){
        isExpanded = b;
    }

    protected void addImageURI(String imageURI){
        this.imageURI = imageURI;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getTask() {
        return task;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public String getImageURI(){
        return imageURI;
    }

    public String getAddress(){
        return address;
    }
}
