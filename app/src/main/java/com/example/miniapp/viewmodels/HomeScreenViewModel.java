package com.example.miniapp.viewmodels;

import com.example.miniapp.models.IUserDBManager;

public class HomeScreenViewModel {
     private IUserDBManager dbManager;

    public HomeScreenViewModel(IUserDBManager dbManager){
        this.dbManager = dbManager;
    }

}
