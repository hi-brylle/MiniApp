package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.miniapp.R;

public class NewTask extends AppCompatActivity {

    private EditText editTextTask;
    private EditText editTextSelectDate;
    private Button buttonSaveTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        editTextTask = findViewById(R.id.edit_text_task);

        editTextSelectDate = findViewById(R.id.edit_text_select_date);

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
                Toast.makeText(NewTask.this, "Show Date Picker Dialog now bro", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NewTask.this, "GRRRRR", Toast.LENGTH_SHORT).show();
            }
        });
    }
}