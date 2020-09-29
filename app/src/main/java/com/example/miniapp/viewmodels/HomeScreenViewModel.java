package com.example.miniapp.viewmodels;

import com.example.miniapp.models.IUserDBManager;
import com.example.miniapp.models.Task;

public class HomeScreenViewModel {
     private IUserDBManager dbManager;

    public HomeScreenViewModel(IUserDBManager dbManager){
        this.dbManager = dbManager;
    }

    public void queueForDeletion(Task toDelete){
        // verify
        if(toDelete.isQueuedForDeletion()){
            dbManager.queueDelete(toDelete.getTask(), toDelete.getDateCreated(), toDelete.getDateStart());
        }
    }

}
