package com.example.miniapp.models;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

public class Task {
    private String task;
    private Date dateCreated;
    private Date dateStart;
    private boolean isDone;
    private boolean isExpanded; // for use in the recycler view only; not stored in DB
    private ArrayList<URI> imageURIs;

    public Task(String task, Date created, Date start){
        this.task = task;
        this.dateCreated = created;
        this.dateStart = start;
        this.isDone = false;
    }

    public void setDone(boolean m){
        isDone = m;
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

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean b){
        isExpanded = b;
    }
}
