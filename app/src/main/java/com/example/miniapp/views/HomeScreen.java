package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.example.miniapp.R;
import com.example.miniapp.helper_classes.CustomAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeScreen extends AppCompatActivity {
    private RecyclerView recViewTaskList;
    private RecyclerView.Adapter recViewAdapter;
    private RecyclerView.LayoutManager recViewLayoutManager;

    private Button buttonPopupMenu;
    private FloatingActionButton fabNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        recViewTaskList = findViewById(R.id.recycler_view_task_list);
        recViewAdapter = new CustomAdapter(null); //TODO: connect to DB via a viewmodel to get data
        recViewTaskList.setAdapter(recViewAdapter);
        recViewTaskList.setLayoutManager(new LinearLayoutManager(this));

        buttonPopupMenu = findViewById(R.id.button_popup_menu);
        fabNewTask = findViewById(R.id.fab_new_task);

    }
}