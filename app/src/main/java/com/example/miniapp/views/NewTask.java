package com.example.miniapp.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.helper_classes.NotificationBroadcastReceiver;
import com.example.miniapp.models.UserDBManager;
import com.example.miniapp.viewmodels.TaskViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
                                                            TimePickerDialog.OnTimeSetListener,
                                                            Validator.ValidationListener,
                                                            PopupMenu.OnMenuItemClickListener {

    private Validator validator;
    @NotEmpty
    private EditText editTextTask;
    @NotEmpty
    private EditText editTextSelectDate;
    @NotEmpty
    private EditText editTextSelectTime;

    ImageButton imageButtonAddPhoto;

    private TaskViewModel taskViewModel;

    String task;
    Date dateStart;
    Date dateCreated;
    Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        validator = new Validator(this);
        validator.setValidationListener(this);

        editTextTask = findViewById(R.id.edit_text_task);
        editTextSelectDate = findViewById(R.id.edit_text_select_date);
        editTextSelectTime = findViewById(R.id.edit_text_select_time);
        Button buttonSaveTask = findViewById(R.id.button_save_task);
        imageButtonAddPhoto = findViewById(R.id.image_button_add_photo);

        String dbName = getIntent().getStringExtra(getString(R.string.userEmailExtra));
        taskViewModel = new TaskViewModel(new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext())));

        editTextSelectDate.setOnClickListener(view -> openDatePickerDialog());
        editTextSelectTime.setOnClickListener(view -> openTimePickerDialog());

        buttonSaveTask.setOnClickListener(view -> {
            if(taskViewModel.isValid(dateStart)){
                validator.validate();
            } else {
                editTextSelectTime.setError("Time cannot be from the past");
            }
        });

        imageButtonAddPhoto.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(NewTask.this, view);
            popupMenu.setOnMenuItemClickListener(NewTask.this);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.show();
        });

        // TODO: remove block later
        Button buttonTestAlarm = findViewById(R.id.button_test_alarm);
        buttonTestAlarm.setOnClickListener(view -> {
            wrappedAlarm(60, 1,"60s alive");
            wrappedAlarm(120, 2, "120s alive");
        });

        String API_KEY = getString(R.string.api_key);
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), API_KEY);
        }

        PlacesClient placesClient = Places.createClient(this);

    }

    // TODO: remove block later
    public void wrappedAlarm(int seconds, int notificationID, String task){
        Toast.makeText(NewTask.this, "Alarm Set!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NewTask.this, NotificationBroadcastReceiver.class);
        intent.putExtra("task", task);
        intent.putExtra("notificationID", notificationID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NewTask.this, notificationID, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long timeAtButtonClick = System.currentTimeMillis();
        long timeInMS = 1000 * seconds;
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + timeInMS, pendingIntent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.popup_item_take_photo:
                takePhoto();
                return true;
            case R.id.popup_item_choose_photo:
                choosePhoto();
                return true;
            default:
                return false;
        }
    }

    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageURI = null;
            if (file != null) {
                imageURI = Uri.fromFile(file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(timeStamp, ".jpg", storageDir);
    }

    private void choosePhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    imageURI = data.getData();
                    imageButtonAddPhoto.setImageURI(imageURI);
                }
                break;
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK){
                    if (imageURI != null){
                        imageButtonAddPhoto.setImageURI(imageURI);
                    }
                }
                break;
        }
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
    public void onBackPressed() {
        super.onBackPressed();
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
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE) + 5,
                false);

        timePickerDialog.show();
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

        // set date component of dateStart
        dateStart = calendar.getTime();

        editTextSelectDate.setText(TaskViewModel.dateRepresentation(i, i1, i2));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if (dateStart == null){
            dateStart = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, i);
        calendar.set(Calendar.MINUTE, i1);

        // set time component of dateStart
        dateStart = calendar.getTime();

        editTextSelectTime.setText(TaskViewModel.timeRepresentation(i, i1));
    }

    private void saveTask() {
        task = String.valueOf(editTextTask.getText());
        dateCreated = Calendar.getInstance().getTime();

        String imageURIString = imageURI == null ? "" : imageURI.toString();

        taskViewModel.submit(task, dateCreated, dateStart, imageURIString);

        Log.v("MY TAG", "Task: " + task);
        Log.v("MY TAG", "Created: " + dateCreated);
        Log.v("MY TAG", "Start: " + dateStart);
        Log.v("MY TAG", "URI: " + imageURI);

        Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();

        finish();
    }

}