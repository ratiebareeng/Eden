package com.example.eden.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eden.ConnectionStateMonitor;
import com.example.eden.R;
import com.example.eden.viewmodel.AuthViewModel;
import com.firebase.ui.auth.AuthUI;
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
    private String edenCell = "77149948";
    private String edenEmail = "kutangajoni@gmail.com";

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

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavController() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.basketFragment, R.id.ordersFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        // setup toolbar with nav controller and configuration
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        // setup nav controller and nav view
        NavigationUI.setupWithNavController(navigationView, navController);
        /*
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.call:
                    call();
                    return true;
                case R.id.whatsapp:
                    whatsapp();
                    closeDrawer();
                    return true;
                case R.id.email:
                    email();
                    return true;
                case R.id.signOut:
                    signOutUser();
                    return true;
                default:
                    closeDrawer();
                    return NavigationUI.onNavDestinationSelected(item, navController);
            }
        });

    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void signOutUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error signing out", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "signOutUser failed: ", e);
                });
    }

    private void call() {
        Log.d(TAG, "callUs: ");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "77149948"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void whatsapp() {
        try {
            String whatsappUrl = "https://api.whatsapp.com/send?phone=267" + edenCell;
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setType("text/plain")
                    .setData(Uri.parse(whatsappUrl))
                    .setPackage("com.whatsapp"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp is not installed on this device", Toast.LENGTH_LONG).show();
        }
    }

    private void email() {
        try {
            String emailUrl = "mailto:" + edenEmail;
            startActivity(new Intent(Intent.ACTION_SENDTO).setData(Uri.parse(emailUrl)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Email app is not installed on this device", Toast.LENGTH_LONG).show();
        }
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
            navigationView.getMenu().findItem(R.id.signInFragment).setVisible(false);
            navigationView.getMenu().findItem(R.id.signOut).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.signInFragment).setVisible(true);
            navigationView.getMenu().findItem(R.id.signOut).setVisible(false);

            userDetailsLinearLayout.setVisibility(View.GONE);
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