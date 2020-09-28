package com.example.miniapp.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.helper_classes.CustomAdapter;
import com.example.miniapp.helper_classes.SharedPrefUtils;
import com.example.miniapp.helper_classes.TestService;
import com.example.miniapp.models.UserDBManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeScreen extends AppCompatActivity implements CustomAdapter.onImageClickedListener {
    private CustomAdapter customAdapter;
    private FrameLayout frameLayoutContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        final String dbName = getIntent().getStringExtra(getString(R.string.userEmailExtra));
        UserDBManager sharedDBManager = new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext()));

        // implement once again when connection to server is established
        // HomeScreenViewModel homeScreenViewModel = new HomeScreenViewModel(sharedDBManager);

        frameLayoutContainer = findViewById(R.id.fragment_container);

        customAdapter = new CustomAdapter(sharedDBManager, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView recViewTaskList = findViewById(R.id.recycler_view_task_list);
        recViewTaskList.setLayoutManager(linearLayoutManager);
        recViewTaskList.setAdapter(customAdapter);

        Button buttonLogout = findViewById(R.id.button_logout);
        FloatingActionButton fabNewTask = findViewById(R.id.fab_new_task);

        fabNewTask.setOnClickListener(view -> {
            Intent intent = new Intent(HomeScreen.this, NewTask.class);
            intent.putExtra(getString(R.string.userEmailExtra), dbName);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(view -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout? All alarms under your account will be cancelled.")
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                })
                .setPositiveButton("Log out", (dialogInterface, i) -> {
                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(getApplicationContext());
                    sharedPrefUtils.clearLogin();

                    Intent stopServiceIntent = new Intent(getApplicationContext(), TestService.class);
                    stopService(stopServiceIntent);

                    exitApp();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        customAdapter.openDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // updates items to be shown in the RecyclerView
        customAdapter.updateList();
        customAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customAdapter.closeDB();
    }

    @Override
    public void onBackPressed() {
        Log.v("MY TAG", "SHOULD BE EXITING NOW");
        exitApp();
    }

    private void exitApp(){
        Intent intent = new Intent(HomeScreen.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    public void openFragment(String stringUri){
        BlankFragment fragment = BlankFragment.newInstance(stringUri);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragment_container, fragment, "BLANK_FRAGMENT").commit();
    }

    @Override
    public void onImageClicked(String stringUri) {
        openFragment(stringUri);
    }
}