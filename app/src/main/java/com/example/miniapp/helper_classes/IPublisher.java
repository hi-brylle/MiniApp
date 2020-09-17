package com.example.miniapp.helper_classes;

import com.example.miniapp.models.Task;

import java.util.HashMap;

public interface IPublisher {
    void addSub(ISubscriber subscriber);
    void removeSub(ISubscriber subscriber);
    void notifySubs(Task t);
    void notifySubs(HashMap<String, Object> alarmPair);
    void notifySubs(int loginStatus);
}
