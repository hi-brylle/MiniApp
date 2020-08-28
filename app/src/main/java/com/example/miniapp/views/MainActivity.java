package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.miniapp.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewTaskActivity();
            }
        });
    }

    private void NewTaskActivity(){
        Intent intent = new Intent(MainActivity.this, NewTask.class);
        startActivity(intent);
    }



}
