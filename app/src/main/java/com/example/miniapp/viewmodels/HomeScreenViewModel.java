package com.example.miniapp.viewmodels;

import com.example.miniapp.helper_classes.CustomAdapter;
import com.example.miniapp.models.UserDBManager;

import java.util.Observable;

public class HomeScreenViewModel extends Observable implements IViewModel {
    private UserDBManager dbManager;

    public HomeScreenViewModel(UserDBManager dbM){
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

    public void listen(CustomAdapter customAdapter){
        dbManager.listenForDBChanges(customAdapter);
    }
}
