package com.example.miniapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.miniapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeScreen extends AppCompatActivity {
    private RecyclerView recViewTaskList;
    private RecyclerView.Adapter recViewAdapter;
    private RecyclerView.LayoutManager recViewLayoutManager;

    private Button buttonMenu;
    private FloatingActionButton fabAddNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        recViewTaskList = findViewById(R.id.recycler_view_task_list);
        recViewLayoutManager = new LinearLayoutManager(this);
        //recViewAdapter =
        recViewTaskList.setAdapter(recViewAdapter);//not initialized

    }
}