package com.example.eden.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eden.model.User;
import com.example.eden.model.UserAuthRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {
    private UserAuthRepository userAuthRepository;
    public LiveData<User> authenticatedUserLiveData;
    public LiveData<User> createdUserLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        userAuthRepository = new UserAuthRepository();
    }

    public void signInWithGoogle(AuthCredential authCredential) {
        authenticatedUserLiveData = userAuthRepository.firebaseSignInWithGoogle(authCredential);
    }

    public void createUser(User authenticatedUser) {
        createdUserLiveData = userAuthRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }

    public void signInWithEmailProvider(FirebaseUser firebaseUser) {
        authenticatedUserLiveData = userAuthRepository.firebaseSignInWithEmailProvider(firebaseUser);
    }
}
