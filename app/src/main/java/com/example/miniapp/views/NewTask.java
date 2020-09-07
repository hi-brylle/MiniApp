package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.helper_classes.NotificationHelper;
import com.example.miniapp.models.UserDBManager;
import com.example.miniapp.viewmodels.TaskViewModel;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class NewTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, Observer, Validator.ValidationListener {

    private Validator validator;

    @NotEmpty
    private EditText editTextTask;

    @NotEmpty
    private EditText editTextSelectDate;

    @NotEmpty
    private EditText editTextSelectTime;

    private Button buttonSaveTask;

    private TaskViewModel taskViewModel;

    String task;
    Date dateStart;
    Date dateCreated;

    NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        validator = new Validator(this);
        validator.setValidationListener(this);

        editTextTask = findViewById(R.id.edit_text_task);
        editTextSelectDate = findViewById(R.id.edit_text_select_date);
        editTextSelectTime = findViewById(R.id.edit_text_select_time);
        buttonSaveTask = findViewById(R.id.button_save_task);

        String dbName = getIntent().getStringExtra("userEmail");
        taskViewModel = new TaskViewModel(new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext())));
        taskViewModel.addObserver(this);

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
                validator.validate();
            }
        });

        notificationHelper = new NotificationHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        taskViewModel.openDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        taskViewModel.closeDB();
    }

    @Override
    public void onValidationSucceeded() {
        saveTask();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for(ValidationError error : errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText){
                ((EditText) view).setError(message);
            } else{
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
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

    private void saveTask() {
        task = String.valueOf(editTextTask.getText());
        dateCreated = Calendar.getInstance().getTime();
        taskViewModel.submit(task, dateCreated, dateStart);

        Log.v("MY TAG", "Task: " + task);
        Log.v("MY TAG", "Created: " + dateCreated);
        Log.v("MY TAG", "Start: " + dateStart);

        Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();
        // TODO: Exit this Activity, go back to HomeScreen

        notificationHelper.sendNotification();
    }

    @Override
    public void update(Observable observable, Object o) {

    }


}