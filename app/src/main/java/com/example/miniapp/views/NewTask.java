package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.miniapp.R;

public class NewTask extends AppCompatActivity {

    private EditText editTextTask;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button buttonSaveTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
    }
}