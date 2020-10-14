package com.example.miniapp.models;

import com.example.miniapp.helper_classes.IPublisher;

import java.util.Date;

public interface IUserDBManager extends IDBManager {
    void create(String task, Date dateCreated, Date dateStart, String imageURIString, String addressString);
}
