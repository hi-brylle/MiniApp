package com.example.miniapp.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.helper_classes.CustomAdapter;
import com.example.miniapp.helper_classes.ISubscriber;
import com.example.miniapp.helper_classes.SharedPrefUtils;
import com.example.miniapp.helper_classes.TestService;
import com.example.miniapp.models.UserDBManager;
import com.example.miniapp.viewmodels.HomeScreenViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.HashMap;

public class HomeScreen extends AppCompatActivity implements ISubscriber<HashMap<String, Object>> {
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        final String dbName = getIntent().getStringExtra(getString(R.string.userEmailExtra));
        UserDBManager sharedDBManager = new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext()));

        HomeScreenViewModel homeScreenViewModel = new HomeScreenViewModel(this, sharedDBManager);

        customAdapter = new CustomAdapter(sharedDBManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView recViewTaskList = findViewById(R.id.recycler_view_task_list);
        recViewTaskList.setLayoutManager(linearLayoutManager);
        recViewTaskList.setAdapter(customAdapter);

        Button buttonLogout = findViewById(R.id.button_logout);
        FloatingActionButton fabNewTask = findViewById(R.id.fab_new_task);

        fabNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, NewTask.class);
                intent.putExtra(getString(R.string.userEmailExtra), dbName);
                startActivity(intent);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout? All alarms under your account will be cancelled.")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(getApplicationContext());
                        sharedPrefUtils.clearLogin();

                        Intent stopServiceIntent = new Intent(getApplicationContext(), TestService.class);
                        stopService(stopServiceIntent);

                        exitApp();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        customAdapter.openDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // updates items to be shown in the RecyclerView
        customAdapter.updateList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customAdapter.closeDB();
    }

    @Override
    public void onBackPressed() {
        Log.v("MY TAG", "SHOULD BE EXITING NOW");
        exitApp();
    }

    @Override
    public void update(HashMap<String, Object> alarmPair) {
        Date dateStart = (Date) alarmPair.get("dateStart");
        String task = (String) alarmPair.get("task");
        Log.v("MY TAG", "date start: " + dateStart);
        Log.v("MY TAG", "task: " + task);

        assert dateStart != null;
        long unixTimestamp = dateStart.getTime();
        // notification ID identifies the pending intent
        int notificationID = (int) (unixTimestamp / 1000);

        Intent alarmServiceIntent = new Intent(getApplicationContext(), TestService.class);
        alarmServiceIntent.putExtra("task", task);
        alarmServiceIntent.putExtra("unixTimestamp", unixTimestamp);
        alarmServiceIntent.putExtra("notificationID", notificationID);
        startService(alarmServiceIntent);
    }

    private void exitApp(){
        Intent intent = new Intent(HomeScreen.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }
}