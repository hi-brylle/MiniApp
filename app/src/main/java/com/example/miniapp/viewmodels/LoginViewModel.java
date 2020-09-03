package com.example.miniapp.viewmodels;

import com.example.miniapp.models.DBManager;

import java.util.Observable;

public class LoginViewModel extends Observable implements IViewModel {
    private DBManager dbManager;

    public LoginViewModel(DBManager dbM){
        dbManager = dbM;
    }

    @Override
    public void openDB() {
        dbManager.openDB();
    }

    @Override
    public void closeDB() {
        dbManager.closeDB();
    }
}
