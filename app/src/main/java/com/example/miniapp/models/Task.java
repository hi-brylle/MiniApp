package com.example.miniapp.models;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

public class Task {
    private String task;
    private Date created;
    private Date start;
    private Boolean isDone;
    private Boolean isInProgress;
    private ArrayList<URI> imageURIs;

    public Task(String task, Date created, Date start){
        this.task = task;
        this.created = created;
        this.start = start;
        this.isDone = false;
        this.isInProgress = false;
    }

    public void markInProgress(){
        isInProgress = true;
    }

    public void markDone(){
        isDone = true;
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
        return created;
    }

    public Date getDateStart() {
        return start;
    }

    public boolean getIsMarkDone() {
        return isDone;
    }

    public boolean getIsInProgress() {
        return isInProgress;
    }
}
