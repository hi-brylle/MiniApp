package com.example.miniapp.models;

import org.json.JSONArray;

import java.util.HashMap;

public interface IDBManager {
    void create(HashMap<String, String> kvPairs);
    JSONArray getAll();
}
