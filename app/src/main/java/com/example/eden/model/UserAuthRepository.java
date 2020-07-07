package com.example.eden.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserAuthRepository {
    private static final String TAG = "UserAuthRepository";
    public static final String USERS_COLLECTION = "users";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestoreRoot = FirebaseFirestore.getInstance();
    private CollectionReference usersCollectionRef = firestoreRoot.collection(USERS_COLLECTION);
    private User user;

    public MutableLiveData<User> firebaseSignInWithEmailProvider(FirebaseUser firebaseUser) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        getUserFromFirebaseUser(firebaseUser);

        authenticatedUserMutableLiveData.setValue(user);
        createUserInFirestoreIfNotExists(user);
        Log.d(TAG, "email provider app user: " + user.getName());
        return authenticatedUserMutableLiveData;
    }

    private void getUserFromFirebaseUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            String id = firebaseUser.getUid();
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            // create a user from firebase user
            user = new User(id, name, email);
            Log.d(TAG, "check user: " + name);
        }

    }

    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential authCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();

        // sign in user
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean isNewUser = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getAdditionalUserInfo()).isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String id = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    // create a user from firebase user
                    User user = new User(id, name, email);
                    user.setNew(isNewUser);
                    // add user to user live data
                    authenticatedUserMutableLiveData.setValue(user);
                }
            } else {
                Log.e(TAG, "Sign in with google failed: \n", task.getException());
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Sign in with Google failed: \n", e));

        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> createUserInFirestoreIfNotExists(User authenticatedUser) {
        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();
        DocumentReference userDocRef = usersCollectionRef.document(authenticatedUser.getId());
        userDocRef.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot userDocument = userTask.getResult();
                assert userDocument != null;
                if (!userDocument.exists()) {
                    // add user document to firestore
                    userDocRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
                        if (userCreationTask.isSuccessful()) {
                            authenticatedUser.setCreated(true);
                            newUserMutableLiveData.setValue(authenticatedUser);
                        } else {
                            Log.e(TAG, "Error creating user: ", userCreationTask.getException());
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating user: ", e);
                    });
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error getting user document: ", e));

        return newUserMutableLiveData;
    }

}
