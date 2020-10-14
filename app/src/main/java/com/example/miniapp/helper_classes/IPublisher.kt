package com.example.miniapp.helper_classes

interface IPublisher<T> {
    fun addSub(subscriber: ISubscriber<T>?)
    fun notifySubs(notifyInput: T)
}