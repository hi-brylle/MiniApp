package com.example.miniapp.models;

import java.util.HashMap;

public interface IDBManager {
    void openDB();
    void closeDB();
    void create(HashMap<String, String> kvPairs);
    void create(String email, String hash);
}
