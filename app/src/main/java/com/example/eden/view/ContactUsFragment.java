package com.example.eden.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.eden.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactUsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactUsFragment extends Fragment {
    private static final String TAG = "ContactUsFragment";
    private Button sendEmailButton;
    private TextInputLayout subjectInputLayout;
    private TextInputLayout emailBodyInputLayout;
    private EditText subjectEditText;
    private EditText emailBodyEditText;
    private String subject;
    private String emailBody;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactUsFragment newInstance(String param1, String param2) {
        ContactUsFragment fragment = new ContactUsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLayoutViews(view);
        initOnClickListeners();
        initTextChangedListener();
    }

    private void initLayoutViews(View view) {
        sendEmailButton = view.findViewById(R.id.send_email);
        subjectInputLayout = view.findViewById(R.id.email_subject);
        emailBodyInputLayout = view.findViewById(R.id.email_body);
        subjectEditText = subjectInputLayout.getEditText();
        emailBodyEditText = emailBodyInputLayout.getEditText();
    }

    private void initOnClickListeners() {
        sendEmailButton.setOnClickListener(v -> sendEmail());
    }

    private void initTextChangedListener() {
        emailBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailBodyInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }/*

    private void validateSubject(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            emailBodyInputLayout.setError(null);
        } else {
            emailBodyInputLayout.setError("Email body is empty. Please enter message first.");
        }
    }*/


    private void sendEmail() {
        getDataFromForm();

        if (emailBody != null && !emailBody.isEmpty()) {
            initEmailIntent();
        }
    }

    private void initEmailIntent() {
        String edenEmail = "bareengbiz@gmail.com";
        // create email app picker intent
        Intent emailChooser = new Intent(Intent.ACTION_SENDTO);
        emailChooser.setData(Uri.parse("mailto:"));

        // create email intent
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{edenEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        emailIntent.setSelector(emailChooser);
        Log.d(TAG, "sendEmail: " + subject + "\n" + emailBody);
        if (emailIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Drop us an email"));
        }
    }

    private void getDataFromForm() {
        subject = subjectEditText.getText().toString().trim();
        emailBody = emailBodyEditText.getText().toString().trim();

        if (emailBody.isEmpty()) {
            emailBodyInputLayout.setError("Please enter message body first.");
        }
    }

}