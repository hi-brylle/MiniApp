package com.example.miniapp.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.R;
import com.example.miniapp.helper_classes.CustomAdapter;
import com.example.miniapp.helper_classes.SharedPrefUtils;
import com.example.miniapp.helper_classes.TestService;
import com.example.miniapp.models.UserDBManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeScreen extends AppCompatActivity implements CustomAdapter.onImageClickedListener, CustomAdapter.onItemLongClickedListener {
    private CustomAdapter customAdapter;
    FloatingActionButton fabNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        final String dbName = getIntent().getStringExtra(getString(R.string.userEmailExtra));
        UserDBManager sharedDBManager = new UserDBManager(dbName, new DatabaseConfiguration(getApplicationContext()));

        // implement once again when connection to server is established
        // HomeScreenViewModel homeScreenViewModel = new HomeScreenViewModel(sharedDBManager);

        FrameLayout frameLayoutContainer = findViewById(R.id.fragment_container);

        customAdapter = new CustomAdapter(sharedDBManager, this, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView recViewTaskList = findViewById(R.id.recycler_view_task_list);
        recViewTaskList.setLayoutManager(linearLayoutManager);
        recViewTaskList.setAdapter(customAdapter);

        Button buttonLogout = findViewById(R.id.button_logout);
        fabNewTask = findViewById(R.id.fab_new_task);

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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStackImmediate();
            fabNewTask.setEnabled(true);
            fabNewTask.setVisibility(View.VISIBLE);
        } else {
            exitApp();
        }

    }

    private void exitApp(){
        Intent intent = new Intent(HomeScreen.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    public void openFragment(String stringUri){
        ImageFragment fragment = ImageFragment.newInstance(stringUri);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragment_container, fragment, "BLANK_FRAGMENT").commit();
    }

    @Override
    public void onImageClicked(String stringUri) {
        openFragment(stringUri);
        fabNewTask.setEnabled(false);
        fabNewTask.setVisibility(View.GONE);
    }

    @Override
    public void onItemLongClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete task?")
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                })
                .setPositiveButton("Yes", (dialog, id) -> {
                    Toast.makeText(this, "(can't delete yet, our db is local)", Toast.LENGTH_SHORT).show();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}