package com.example.miniapp.models;

public interface ILoginDBManager extends IDBManager {
    void register(String email, String password);
    boolean isEmailRegistered(String email);
    boolean verifyCredentials(String email, String password);
}
