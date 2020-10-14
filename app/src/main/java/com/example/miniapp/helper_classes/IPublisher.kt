package com.example.miniapp.helper_classes;

public interface IPublisher<T> {
    void addSub(ISubscriber<T> subscriber);
    void removeSub(ISubscriber<T> subscriber);
    void notifySubs(T notifyInput);
}
