package com.example.miniapp.models;

import com.example.miniapp.helper_classes.IPublisher;

public interface IDBManager extends IPublisher {
    void openDB();
    void closeDB();
}
