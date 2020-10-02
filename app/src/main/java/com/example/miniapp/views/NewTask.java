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
import com.example.miniapp.viewmodels.NewTaskViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    EditText editTextLocation;
    ImageButton imageButtonAddPhoto;

    private NewTaskViewModel newTaskViewModel;

    String task;
    Date dateStart;
    Date dateCreated;
    Uri imageURI = null;
    String completeAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        validator = new Validator(this);
        validator.setValidationListener(this);

        String apiKey = getString(R.string.api_key);

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);

        editTextTask = findViewById(R.id.edit_text_task);
        editTextSelectDate = findViewById(R.id.edit_text_select_date);
        editTextSelectTime = findViewById(R.id.edit_text_select_time);
        editTextLocation = findViewById(R.id.edit_text_location);
        Button buttonSaveTask = findViewById(R.id.button_save_task);
        imageButtonAddPhoto = findViewById(R.id.image_button_add_photo);

        String dbName = getIntent().getStringExtra(getString(R.string.userEmailExtra));
        newTaskViewModel = new NewTaskViewModel(new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext())));

        editTextSelectDate.setOnClickListener(view -> openDatePickerDialog());
        editTextSelectTime.setOnClickListener(view -> openTimePickerDialog());
        editTextLocation.setOnClickListener(view -> onSearchCalled());

        buttonSaveTask.setOnClickListener(view -> {
            if(newTaskViewModel.isValid(dateStart)){
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
    private final int AUTOCOMPLETE_REQUEST = 3;

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

    private void onSearchCalled() {
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("PH")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST);
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

            case AUTOCOMPLETE_REQUEST:
                if (resultCode == RESULT_OK){
                    assert data != null;
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    completeAddress = place.getName() + ", " + place.getAddress();
                    editTextLocation.setText(completeAddress);
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        newTaskViewModel.openDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        newTaskViewModel.closeDB();
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

        editTextSelectDate.setText(NewTaskViewModel.dateRepresentation(i, i1, i2));
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

        editTextSelectTime.setText(NewTaskViewModel.timeRepresentation(i, i1));
    }

    private void saveTask() {
        task = String.valueOf(editTextTask.getText());
        dateCreated = Calendar.getInstance().getTime();

        newTaskViewModel.submit(task, dateCreated, dateStart, imageURI, completeAddress);

        Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();

        finish();
    }

}