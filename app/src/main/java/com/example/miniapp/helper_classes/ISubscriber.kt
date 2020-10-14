package com.example.miniapp.helper_classes

interface ISubscriber<T> {
    fun update(updateInput: T)
}