package com.example.miniapp.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.helper_classes.ISubscriber;
import com.example.miniapp.helper_classes.SharedPrefUtils;
import com.example.miniapp.helper_classes.AlarmService;
import com.example.miniapp.models.LoginDBManager;
import com.example.miniapp.viewmodels.LoginViewModel;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener, ISubscriber<Integer> {

    SharedPrefUtils sharedPrefUtils;

    private Validator validator;
    @NotEmpty
    @Email(message = "Invalid Email")
    private EditText editTextEmail;
    @Password(scheme = Password.Scheme.ANY, message = "Password must be at least 6 characters")
    private EditText editTextPassword;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefUtils = new SharedPrefUtils(getApplicationContext());

        validator = new Validator(this);
        validator.setValidationListener(this);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        Button buttonSignIn = findViewById(R.id.button_sign_in);

        loginViewModel = new LoginViewModel(this, new LoginDBManager(new DatabaseConfiguration(getApplicationContext())));

        buttonSignIn.setOnClickListener(view -> validator.validate());
    }

    private void checkSharedPrefs() {
        if (sharedPrefUtils.isUserLoggedOut()){
            Log.v("MY TAG", "previous user logged out.");
        } else {
            Log.v("MY TAG", "auto login");
            login(sharedPrefUtils.getEmailFromSP());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginViewModel.openDB();
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
        attemptSync();
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

    private void login(String userEmailExtra) {
        // start alarm service prior to login
        startServiceForUser();

        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        // userEmail shall also be the name of the user-specific database
        intent.putExtra(getString(R.string.userEmailExtra), userEmailExtra);
        startActivity(intent);
    }

    private void setAlwaysLoggedIn(){
        sharedPrefUtils.recordUserLogin(String.valueOf(editTextEmail.getText()), String.valueOf(editTextPassword.getText()));
    }

    private void startServiceForUser() {
        Intent alarmServiceIntent = new Intent(MainActivity.this, AlarmService.class);
        startService(alarmServiceIntent);
    }

    private void passwordEmailMismatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign In Failed")
                .setMessage("Your password is incorrect.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    // we don't need to clear out the email for extra security
                    // I mean, this is just a task notifier app
                    // editTextEmail.setText("");
                    editTextPassword.setText("");
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void update(Integer loginStatus) {
        handleLoginStatus(loginStatus);
    }

    private void attemptSync(){
        BroadcastReceiver wifiStateReceiver =  new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        // TODO: add service for replicator of db for user login credentials
                        Toast.makeText(context, "Wifi is Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        // do nothing
                        break;

                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }
}
