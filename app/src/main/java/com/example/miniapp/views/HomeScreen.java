package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.helper_classes.CustomAdapter;
import com.example.miniapp.models.UserDBManager;
import com.example.miniapp.viewmodels.HomeScreenViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Observable;
import java.util.Observer;

public class HomeScreen extends AppCompatActivity implements Observer {
    private RecyclerView recViewTaskList;
    private LinearLayoutManager linearLayoutManager;
    private CustomAdapter customAdapter;

    private Button buttonPopupMenu;
    private FloatingActionButton fabNewTask;

    private HomeScreenViewModel homeScreenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        final String dbName = getIntent().getStringExtra("userEmail");
        homeScreenViewModel = new HomeScreenViewModel(new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext())));
        homeScreenViewModel.addObserver(this);

        recViewTaskList = findViewById(R.id.recycler_view_task_list);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        customAdapter = new CustomAdapter(new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext())));
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recViewTaskList.setLayoutManager(linearLayoutManager);
        recViewTaskList.setAdapter(customAdapter);

        buttonPopupMenu = findViewById(R.id.button_popup_menu);
        fabNewTask = findViewById(R.id.fab_new_task);

        fabNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, NewTask.class);
                intent.putExtra("userEmail", dbName);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        homeScreenViewModel.openDB();
        customAdapter.updateList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        homeScreenViewModel.closeDB();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HomeScreen.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}