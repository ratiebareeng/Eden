package com.example.eden.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkRequest;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eden.ConnectionStateMonitor;
import com.example.eden.R;
import com.example.eden.model.User;
import com.example.eden.viewmodel.AuthViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private NavigationView navigationView;
    private LinearLayout userDetailsLinearLayout;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private AuthViewModel authViewModel;

    private TextView userName;
    private TextView userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInternet();
        initFirebase();
        initLayoutViews();
        initToolbar();
        initNavController();
    }

    private void checkInternet() {
        ConnectionStateMonitor connectionStateMonitor = new ConnectionStateMonitor(getApplicationContext());
        connectionStateMonitor.observe(this, isConnectedToInternet -> {
            if (!isConnectedToInternet) {
                Snackbar.make(findViewById(android.R.id.content), "You are Offline. Some functionality will be unavailable.", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.primaryColor))
                        .setActionTextColor(getResources().getColor(R.color.secondaryColor))
                        .show();
                Log.d(TAG, "checkInternet: boolean " + isConnectedToInternet);
            }
        });

    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initLayoutViews() {

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        Log.d(TAG, "initLayoutViews: nav view initialized");
        View view = navigationView.inflateHeaderView(R.layout.layout_drawer_header);
        userDetailsLinearLayout = view.findViewById(R.id.user_details_root);
        userName = view.findViewById(R.id.user_name_text_view);
        userEmail = view.findViewById(R.id.user_email_text_view);

    }

    private void callUs() {
        Log.d(TAG, "callUs: ");
    }

    private void emailUs() {
        NavDeepLinkRequest emailRequest = NavDeepLinkRequest.Builder
                .fromAction(Intent.ACTION_SEND)
                .setAction(Intent.ACTION_SEND)
                .build();
        navController.navigate(emailRequest);

    }

    private void visitUs() {
        Log.d(TAG, "visitUs: ");
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavController() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(drawerLayout)
                .build();

        // setup toolbar with nav controller and configuration
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        // setup nav controller and nav view
        NavigationUI.setupWithNavController(navigationView, navController);
        /*
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        // IMPLEMENT TOOLBAR UP BUTTON
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userDetailsLinearLayout.setVisibility(View.VISIBLE);
            userName.setText(firebaseUser.getDisplayName());
            userEmail.setText(firebaseUser.getEmail());
            Log.d(TAG, "onAuthStateChanged: " + firebaseUser.getDisplayName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }
}