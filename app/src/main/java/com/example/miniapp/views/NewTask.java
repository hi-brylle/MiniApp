package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.miniapp.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class NewTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText editTextTask;
    private EditText editTextSelectDate;
    private EditText editTextSelectTime;
    private Button buttonSaveTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        editTextTask = findViewById(R.id.edit_text_task);
        editTextSelectDate = findViewById(R.id.edit_text_select_date);
        editTextSelectTime = findViewById(R.id.edit_text_select_time);
        buttonSaveTask = findViewById(R.id.button_save_task);
        buttonSaveTask.setEnabled(false);

        editTextTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0){
                    buttonSaveTask.setEnabled(false);
                } else {
                    buttonSaveTask.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                Toast.makeText(NewTask.this, "GRRRRR", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String month = new DateFormatSymbols().getMonths()[i1 - 1];
        String date = i2 + " " + month + " " + i;
        editTextSelectDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        String hour = i < 10 ? "0" + i : String.valueOf(i);
        String minute = i1 < 10 ? "0" + i1 : String.valueOf(i1);
        String xm = i < 12 ? "AM" : "PM";

        String time = hour + ":" + minute + " " + xm;
        editTextSelectTime.setText(time);
    }
}