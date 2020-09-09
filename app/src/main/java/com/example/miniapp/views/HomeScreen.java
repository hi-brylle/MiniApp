package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    private Button buttonLogout;
    private FloatingActionButton fabNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        final String dbName = getIntent().getStringExtra("userEmail");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        customAdapter = new CustomAdapter(new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext())));
        customAdapter.openDB();
        customAdapter.updateList();

        recViewTaskList = findViewById(R.id.recycler_view_task_list);
        recViewTaskList.setLayoutManager(linearLayoutManager);
        recViewTaskList.setAdapter(customAdapter);

        buttonLogout = findViewById(R.id.button_logout);
        fabNewTask = findViewById(R.id.fab_new_task);

        fabNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, NewTask.class);
                intent.putExtra("userEmail", dbName);
                startActivity(intent);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences userLoginTrackerSharedPref = getSharedPreferences("loggedInUser", MODE_PRIVATE);
                SharedPreferences.Editor editor = userLoginTrackerSharedPref.edit();
                editor.putString("email", "");
                editor.putString("password", "");
                editor.apply();

                exitApp();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        customAdapter.openDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customAdapter.closeDB();
    }

    @Override
    public void onBackPressed() {
        // TODO: FIX here, app not exiting for some reason
//        if(getIntent().getBooleanExtra("autoLogIn", false)){
//            exitApp();
//        }
        Log.v("MY TAG", "SHOULD BE EXITING NOW");
        exitApp();
    }

    private void exitApp(){
        Intent intent = new Intent(HomeScreen.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}