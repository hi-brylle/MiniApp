package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.miniapp.R;

public class HomeScreen extends AppCompatActivity {

    private ListView listViewTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();

        listViewTasks = findViewById(R.id.list_view_tasks);
    }
}