package com.example.miniapp.models;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

public class Task {
    private String task;
    private Date dateCreated;
    private Date dateStart;
    private Boolean isDone;
    private Boolean isInProgress;
    private ArrayList<URI> imageURIs;

    public Task(String task, Date created, Date start){
        this.task = task;
        this.dateCreated = created;
        this.dateStart = start;
        this.isDone = false;
        this.isInProgress = false;
    }

    public void setDone(boolean m){
        isDone = m;
    }

    public void setInProgress(boolean p){
        isInProgress = p;
    }

    protected void addImageURI(URI imgURI){
        if (imageURIs == null){
            imageURIs = new ArrayList<URI>();
        }
        imageURIs.add(imgURI);
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

    public boolean getIsInProgress() {
        return isInProgress;
    }
}
