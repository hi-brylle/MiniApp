package com.example.miniapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.models.DBManager;
import com.example.miniapp.viewmodels.LoginViewModel;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener, Observer {

    private static final String LOGGED_IN_USERS = "loggedInUsers";
    private static final String LOGGED_IN_USERNAME = "loggedInUsername";
    private static SharedPreferences userLoginTrackerSharedPref;

    private Validator validator;

    @NotEmpty(message = "This field is required")
    @Email(message = "Invalid Email")
    private EditText editTextEmail;

    @Password(min = 6, scheme = Password.Scheme.ANY, message = "Password must be at least 6 characters")
    private EditText ediTextPassword;

    private Button buttonSignIn;
    private Button buttonSignInGoogle;
    private Button buttonSignInFacebook;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if user is logged in (if yes, go to HomeScreen; else, stay here)

        validator = new Validator(this);
        validator.setValidationListener(this);

        editTextEmail = findViewById(R.id.edit_text_email);
        ediTextPassword = findViewById(R.id.edit_text_password);
        buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignInGoogle = findViewById(R.id.button_sign_in_google);
        buttonSignInFacebook = findViewById(R.id.button_sign_in_facebook);


        loginViewModel = new LoginViewModel(new DBManager("users_login", new DatabaseConfiguration(this.getApplicationContext())));
        loginViewModel.addObserver(this);


        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        buttonSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Activity for Google log in...", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSignInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Activity for FB log in...", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onValidationSucceeded() {
//        Toast.makeText(this, "Put creds in DB now", Toast.LENGTH_SHORT).show();
        Intent testIntent = new Intent(MainActivity.this, NewTask.class);
        startActivity(testIntent);
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

    @Override
    public void update(Observable observable, Object o) {

    }
}
