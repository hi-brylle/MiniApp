package com.example.miniapp.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.models.LoginDBManager;
import com.example.miniapp.viewmodels.LoginViewModel;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener, Observer {

    // SharedPreferences used to track login status of users for auto-login checking
    private static final String LOGGED_IN_USERS = "loggedInUsers";
    private static final String LOGGED_IN_USERNAME = "loggedInUsername";
    private static SharedPreferences userLoginTrackerSharedPref;

    private Validator validator;
    @NotEmpty
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

        // TODO: check if user is logged in (if yes, go to HomeScreen; else, stay here)

        validator = new Validator(this);
        validator.setValidationListener(this);

        editTextEmail = findViewById(R.id.edit_text_email);
        ediTextPassword = findViewById(R.id.edit_text_password);
        buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignInGoogle = findViewById(R.id.button_sign_in_google);
        buttonSignInFacebook = findViewById(R.id.button_sign_in_facebook);

        loginViewModel = new LoginViewModel(new LoginDBManager(new DatabaseConfiguration(this.getApplicationContext())));
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
    protected void onStart() {
        super.onStart();
        loginViewModel.openDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginViewModel.closeDB();
    }

    @Override
    public void onValidationSucceeded() {
        loginViewModel.verify(String.valueOf(editTextEmail.getText()), String.valueOf(ediTextPassword.getText()));
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
        // the following integers are used for the login status
        // 0: email is not registered
        // 1: email is registered, password is correct
        // -1: email is registered, password is incorrect
        int loginStatus = (int) o;
        Log.v("MY TAG", "received status: " + loginStatus);
        handleLoginStatus(loginStatus);
    }

    private void handleLoginStatus(int loginStatus) {
        switch (loginStatus){
            // register email
            case 0:
                loginViewModel.register(String.valueOf(editTextEmail.getText()), String.valueOf(ediTextPassword.getText()));
                login();
                // TODO: set SharedPref to be logged in for this email
                break;

            // password correct
            case 1:
                login();
                // TODO: set SharedPref to be logged in for this email
                break;

            // password incorrect
            case -1:
                passwordEmailMismatchDialog();
                break;

            default:
                Log.v("MY TAG", "Error in login status for some reason");
        }
    }

    // TODO: make intent go to HomeScreen, in the mean time, skip right to NewTask
    private void login() {
        Intent intent = new Intent(MainActivity.this, NewTask.class);
        // userEmail shall also be the name of the user-specific database
        intent.putExtra("userEmail", String.valueOf(editTextEmail.getText()));
        startActivity(intent);
    }

    private void passwordEmailMismatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign In Failed")
                .setMessage("Your email or password is incorrect.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        editTextEmail.setText("");
                        ediTextPassword.setText("");
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
