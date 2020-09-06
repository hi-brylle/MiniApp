package com.example.miniapp.viewmodels;

import com.example.miniapp.models.UserDBManager;

import java.util.Observable;

public class HomeScreenViewModel extends Observable implements IViewModel {
    private UserDBManager dbManager;

    public HomeScreenViewModel(UserDBManager dbM){
        dbManager = dbM;
    }

    public UserDBManager getDB(){
        return dbManager;
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
