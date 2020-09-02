package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.miniapp.R;
import com.example.miniapp.models.DBManager;
import com.example.miniapp.viewmodels.TaskViewModel;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class NewTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, Observer {

    private EditText editTextTask;
    private EditText editTextSelectDate;
    private EditText editTextSelectTime;
    private Button buttonSaveTask;

    private TaskViewModel taskViewModel;

    String task;
    Date dateStart;
    Date dateCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        editTextTask = findViewById(R.id.edit_text_task);
        editTextSelectDate = findViewById(R.id.edit_text_select_date);
        editTextSelectTime = findViewById(R.id.edit_text_select_time);
        buttonSaveTask = findViewById(R.id.button_save_task);
        buttonSaveTask.setEnabled(false);

        // manual DI
        taskViewModel = new TaskViewModel(DBManager.getInstance(this));
        taskViewModel.addObserver(this);

        editTextTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0){
                    task = String.valueOf(editable);
                }
            }
        });

        editTextSelectDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveButtonEnable();
            }
        });

        editTextSelectTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveButtonEnable();
            }
        });

        editTextSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });

        editTextSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePickerDialog();
            }
        });

        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void openDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void openTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE) + 5,
                false);

        timePickerDialog.show();
    }

    String dateRepresentation(int year, int month, int day){
        // needs localization
        String monthName = new DateFormatSymbols().getMonths()[month];

        return day + " " + monthName + " " + year;
    }

    String timeRepresentation(int hr, int min){
        String hour = hr < 10 ? "0" + hr : String.valueOf(hr);
        String minute = min < 10 ? "0" + min : String.valueOf(min);
        String xm = hr < 12 ? "AM" : "PM";

        return hour + ":" + minute + " " + xm;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (dateStart == null){
            dateStart = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
        dateStart = calendar.getTime();

        editTextSelectDate.setText(dateRepresentation(i, i1, i2));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if (dateStart == null){
            dateStart = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, i);
        calendar.set(Calendar.MINUTE, i1);
        dateStart = calendar.getTime();

        editTextSelectTime.setText(timeRepresentation(i, i1));
    }

    private void saveButtonEnable(){
        if(editTextTask.getText().length() != 0 &&
            editTextSelectDate.getText().length() != 0 &&
            editTextSelectTime.getText().length() != 0){
            buttonSaveTask.setEnabled(true);
        } else{
            buttonSaveTask.setEnabled(false);
        }
    }

    private void saveTask() {
        dateCreated = Calendar.getInstance().getTime();
        Log.v("MY TAG", "Task: " + task);
        Log.v("MY TAG", "Created: " + dateCreated);
        Log.v("MY TAG", "Due: " + dateStart);
        taskViewModel.submit(task, dateCreated, dateStart);
        Toast.makeText(this, "Saved?", Toast.LENGTH_SHORT).show();
        // Exit this Activity, go back to HomeScreen
    }

    @Override
    public void update(Observable observable, Object o) {
        Toast.makeText(this, (CharSequence) o, Toast.LENGTH_SHORT).show();
    }
}