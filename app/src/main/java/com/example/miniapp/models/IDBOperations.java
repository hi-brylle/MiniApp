package com.example.miniapp.models;

import java.util.Date;

public interface IDBOperations {
    public void create(String task, Date created, Date start);
    public void read();
    public void update();
    public void delete();
}
