package com.example.miniapp.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.helper_classes.ISubscriber;
import com.example.miniapp.helper_classes.Logger;
import com.example.miniapp.helper_classes.PWHash;
import com.example.miniapp.helper_classes.SecureSharedPref;
import com.example.miniapp.helper_classes.AlarmService;
import com.example.miniapp.helper_classes.TestWifiService;
import com.example.miniapp.helper_classes.UserDBListenerService;
import com.example.miniapp.models.DBManager;
import com.example.miniapp.viewmodels.LoginVM;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener, ISubscriber<Integer> {

    SecureSharedPref secureSharedPref;

    private Validator validator;
    @NotEmpty
    @Email(message = "Invalid Email")
    private EditText editTextEmail;
    @Password(scheme = Password.Scheme.ANY, message = "Password must be at least 6 characters")
    private EditText editTextPassword;

    private LoginVM loginVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        secureSharedPref = new SecureSharedPref(getApplicationContext());

        validator = new Validator(this);
        validator.setValidationListener(this);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        Button buttonSignIn = findViewById(R.id.button_sign_in);

        loginVM = new LoginVM(this, new DBManager(getString(R.string.users_login_db_name), new DatabaseConfiguration(getApplicationContext())));

        buttonSignIn.setOnClickListener(view -> validator.validate());
    }

    private void checkSharedPrefs() {
        if (secureSharedPref.isUserLoggedOut()){
            Logger.log("previous user logged out.");
        } else {
            Logger.log("auto login");
            login(secureSharedPref.getLoggedEmail());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        loginVM.verifyCredentials(String.valueOf(editTextEmail.getText()), String.valueOf(editTextPassword.getText()));
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

    private void handleLoginStatus(int loginStatus) {
        switch (loginStatus){
            // register email
            case 0:
                loginVM.register(String.valueOf(editTextEmail.getText()), String.valueOf(editTextPassword.getText()));
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
                Logger.log("Error in login status for some reason");
        }
    }

    private void login(String userEmailExtra) {
        startConnectivityService();

        startListenerService(userEmailExtra);

        // start alarm service prior to login
        startAlarmServiceForUser(userEmailExtra);

        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        // userEmail shall also be the name of the user-specific database
        intent.putExtra(getString(R.string.userEmailExtra), userEmailExtra);
        startActivity(intent);
    }

    private void setAlwaysLoggedIn(){
        secureSharedPref.recordLogin(String.valueOf(editTextEmail.getText()), PWHash.hash(String.valueOf(editTextPassword.getText())));
    }

    private void startConnectivityService() {
        Intent testWifiServiceIntent = new Intent(MainActivity.this, TestWifiService.class);
        startService(testWifiServiceIntent);
    }

    private void startListenerService(String userEmailExtra){
        Intent listenerServiceIntent = new Intent(MainActivity.this, UserDBListenerService.class);
        listenerServiceIntent.putExtra("email", userEmailExtra);
        listenerServiceIntent.setAction("INTENT_ACTION_DB_CHANGED");
        startService(listenerServiceIntent);
    }

    private void startAlarmServiceForUser(String userEmailExtra) {
        Intent alarmServiceIntent = new Intent(MainActivity.this, AlarmService.class);
        alarmServiceIntent.putExtra("email", userEmailExtra);
        startService(alarmServiceIntent);
    }

    private void passwordEmailMismatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign In Failed")
                .setMessage("Your password is incorrect.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    editTextPassword.setText("");
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void update(Integer loginStatus) {
        handleLoginStatus(loginStatus);
    }
}
