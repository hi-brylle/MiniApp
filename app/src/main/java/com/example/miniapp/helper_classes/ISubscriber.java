package com.example.miniapp.helper_classes;

import com.example.miniapp.models.Task;

import java.util.HashMap;

public interface ISubscriber {
    void update(Task t);
    void update(HashMap<String, Object> alarmPair);
    void update(int loginStatus);
}
