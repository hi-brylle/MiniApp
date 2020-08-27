package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.miniapp.viewmodels.TaskViewModel;

import java.util.Calendar;

public class NewTask extends AppCompatActivity {

    private EditText editTextTask;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button buttonSaveTask;

    private Calendar calendar;

    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

    }

    @Override
    protected void onStart() {
        super.onStart();

        editTextTask = findViewById(R.id.edit_text_task);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        buttonSaveTask = findViewById(R.id.button_save_task);

        //prevent user from selecting past date
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        //show current time to be 5 minutes from now
        timePicker.setCurrentHour(timePicker.getCurrentHour());
        timePicker.setCurrentMinute(timePicker.getCurrentMinute() + 5);

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

        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClick();
            }
        });

    }

    private void onSaveButtonClick() {
        Toast.makeText(this, "Hey, setup the DB first!", Toast.LENGTH_SHORT).show();
    }


}