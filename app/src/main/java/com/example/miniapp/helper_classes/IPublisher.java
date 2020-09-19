package com.example.miniapp.helper_classes;

public interface IPublisher<T> {
    void addSub(ISubscriber subscriber);
    void removeSub(ISubscriber subscriber);
    void notifySubs(T notifyInput);
}
