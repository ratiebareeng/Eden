package com.example.eden.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eden.ConnectionStateMonitor;
import com.example.eden.R;
import com.example.eden.model.User;
import com.example.eden.viewmodel.AuthViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthFragment extends Fragment {
    private static final String TAG = "AuthFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RC_GOOGLE_SIGN_IN = 11;
    private static final int RC_EMAIL_SIGN_IN = 32;

    // Layout Views
    private FrameLayout root;
    private Button googleSignInButton;
    private Button emailSignInButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleSignInClient googleSignInClient;
    public static AuthViewModel authViewModel;
    private NavController navController;
    private List<AuthUI.IdpConfig> emailProvider;

    private boolean isConnected;
    private int primaryColorResource;
    private int secondaryColorResource;

    public AuthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AuthFragment newInstance(String param1, String param2) {
        AuthFragment fragment = new AuthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // hide action bar
        initResources();
        initAuthViewModel();
        initGoogleSignInClient();
        initEmailSignIn();
    }

    private void updateUiForConnectionStatus() {
            Log.d(TAG, "initOnclickListeners: " + isConnected);
            final Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "You are offline. Please connect to the internet to sign in.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setBackgroundTint(primaryColorResource).setActionTextColor(secondaryColorResource);
            snackbar.setAction("OK", confirmView -> snackbar.dismiss());
            snackbar.show();
    }

    private void initResources() {
        primaryColorResource = getResources().getColor(R.color.primaryColor);
        secondaryColorResource = getResources().getColor(R.color.secondaryColor);
    }

    private void checkIsConnected() {
        ConnectionStateMonitor connectionStateMonitor = new ConnectionStateMonitor(requireContext());
        connectionStateMonitor.observe(getViewLifecycleOwner(), isConnectedToNet -> {
            isConnected = isConnectedToNet;
            if (isConnectedToNet) {
                googleSignInButton.setEnabled(true);
                emailSignInButton.setEnabled(true);
            } else {
                updateUiForConnectionStatus();
                googleSignInButton.setEnabled(false);
                emailSignInButton.setEnabled(false);
            }
        });
    }

    private void initEmailSignIn() {
        emailProvider = Collections.singletonList(
                new AuthUI.IdpConfig.EmailBuilder().build());
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLayoutViews(view);
        checkIsConnected();
        initNavController(view);
    }

    private void initNavController(View view) {
        navController = Navigation.findNavController(view);
    }

    private void initLayoutViews(View view) {
        root = view.findViewById(R.id.sign_in_root);
        googleSignInButton = view.findViewById(R.id.google_sign_in_button);
        emailSignInButton = view.findViewById(R.id.email_sign_in_button);

        initOnClickListeners();
    }

    private void initOnClickListeners() {
        googleSignInButton.setOnClickListener(v -> googleSignIn());
        emailSignInButton.setOnClickListener(v -> emailSignIn());
    }

    private void googleSignIn() {
        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void emailSignIn() {
        startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(emailProvider)
        .setLogo(R.drawable.ic_baseline_delete_sweep_24)
        .setTheme(R.style.AppTheme)
        .build(), RC_EMAIL_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Error getting google account: ", e);
                Toast.makeText(getActivity(), "Error Signing In" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == RC_EMAIL_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser != null;
                Log.d(TAG, "check email user: " + firebaseUser.getDisplayName());
                authViewModel.signInWithEmailProvider(firebaseUser);
            } else {
                if (idpResponse == null) {
                    Log.d(TAG, "user cancelled login flow ");
                    Toast.makeText(getActivity(), "Email login cancelled", Toast.LENGTH_SHORT).show();
                } else if (idpResponse.getError() != null) {
                    Log.e(TAG, "Email Login Error: ", idpResponse.getError());
                    Log.d(TAG, "getErrorCode(): " + idpResponse.getError().getErrorCode());
                    /*if (idpResponse.getError().getErrorCode() == 7) {
                        Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        }
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(authCredential);
    }

    private void signInWithGoogleAuthCredential(AuthCredential authCredential) {
        authViewModel.signInWithGoogle(authCredential);
        authViewModel.authenticatedUserLiveData.observe(getViewLifecycleOwner(), authenticatedUser -> {
            if (authenticatedUser.isNew()) {
                createNewUser(authenticatedUser);
            } else {
                goToMainActivity();
            }
        });
    }

    private void createNewUser(User authenticatedUser) {
        authViewModel.createUser(authenticatedUser);
        authViewModel.createdUserLiveData.observe(getViewLifecycleOwner(), user -> {
            if (user.isCreated()) {
                Snackbar.make(root, "Welcome " + user.getName(), Snackbar.LENGTH_LONG);
            }
            goToMainActivity();
        });
    }

    private void goToMainActivity() {
        navController.navigate(R.id.action_signInFragment_to_homeFragment);
    }

    private void logErrorMessage(String message) {
        Log.d(TAG, "Error getting Google Account: \n" + message);
        Snackbar.make(root, message, Snackbar.LENGTH_LONG);
    }
}