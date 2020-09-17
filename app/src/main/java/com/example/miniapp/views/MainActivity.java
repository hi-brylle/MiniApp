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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener, Observer {

    // SharedPreferences used to track login status of users for auto-login checking
    private static final String LOGGED_IN_USERNAME = "loggedInUser";
    private static SharedPreferences userLoginTrackerSharedPref;
    private String emailFromSP;

    private Validator validator;
    @NotEmpty
    @Email(message = "Invalid Email")
    private EditText editTextEmail;
    @Password(scheme = Password.Scheme.ANY, message = "Password must be at least 6 characters")
    private EditText editTextPassword;
    private Button buttonSignIn;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validator = new Validator(this);
        validator.setValidationListener(this);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonSignIn = findViewById(R.id.button_sign_in);

        loginViewModel = new LoginViewModel(new LoginDBManager(new DatabaseConfiguration(getApplicationContext())));
        loginViewModel.addObserver(this);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }

    private void checkSharedPrefs() {
        userLoginTrackerSharedPref = getSharedPreferences(LOGGED_IN_USERNAME, MODE_PRIVATE);
        emailFromSP = userLoginTrackerSharedPref.getString("email", "");
        String password = userLoginTrackerSharedPref.getString("password", "");
        if (emailFromSP.equals("") || password.equals("")){
            // if sharedpref has been emptied (indicating logging out), continue this activity
            return;
        } else {
            Log.v("MY TAG", "auto login");
            login(emailFromSP);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginViewModel.openDB();
        Log.v("MY TAG", "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("MY TAG", "onResume called");
        // this closes the app when the Back button is pressed in Home Screen
        if (getIntent().getBooleanExtra("EXIT", false)){
            finish();
        } else{
            checkSharedPrefs();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginViewModel.closeDB();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        loginViewModel.verify(String.valueOf(editTextEmail.getText()), String.valueOf(editTextPassword.getText()));
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
        handleLoginStatus(loginStatus);
    }

    private void handleLoginStatus(int loginStatus) {
        switch (loginStatus){
            // register email
            case 0:
                loginViewModel.register(String.valueOf(editTextEmail.getText()), String.valueOf(editTextPassword.getText()));
                setAlwaysLoggedIn();
                login(String.valueOf(editTextEmail.getText()));
                break;

            // password correct
            case 1:
                setAlwaysLoggedIn();
                login(String.valueOf(editTextEmail.getText()));
                break;

            // password incorrect
            case -1:
                passwordEmailMismatchDialog();
                break;

            default:
                Log.v("MY TAG", "Error in login status for some reason");
        }
    }

    private void login(String userExtra) {
        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        // userEmail shall also be the name of the user-specific database
        intent.putExtra("userEmail", userExtra);
        startActivity(intent);
    }

    private void setAlwaysLoggedIn(){
        userLoginTrackerSharedPref = getSharedPreferences(LOGGED_IN_USERNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userLoginTrackerSharedPref.edit();
        editor.putString("email", String.valueOf(editTextEmail.getText()));
        editor.putString("password", String.valueOf(editTextPassword.getText())); // IS THIS SECURE?? NO, IT'S NOT
        editor.apply();
    }

    private void passwordEmailMismatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign In Failed")
                .setMessage("Your password is incorrect.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // we don't need to clear out the email for extra security
                        // I mean, this is just a task notifier app
                        // editTextEmail.setText("");
                        editTextPassword.setText("");
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
