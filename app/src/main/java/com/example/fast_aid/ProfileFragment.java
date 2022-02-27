package com.example.fast_aid;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.UserProfileChangeRequest;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().findViewById(R.id.btnDrawer).setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Removing hamburger icon
        getActivity().findViewById(R.id.btnDrawer).setVisibility(View.GONE);

        String name = ApplicationClass.user.getDisplayName() == null ? "" : ApplicationClass.user.getDisplayName(),
                phNum = ApplicationClass.user.getPhoneNumber();
        EditText etName = view.findViewById(R.id.etName);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        Button btnUpdate = view.findViewById(R.id.btnUpdate), btnCancel = view.findViewById(R.id.btnCancel);
        etName.setText(name);
        tvPhone.setText(phNum);

        etName.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                btnUpdate.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnUpdate.setOnClickListener(b -> {
                    String uName = etName.getText().toString();
                    if (!name.equals(uName)) {

                        ApplicationClass.user.updateProfile(
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(uName)
                                        .build())
                                .addOnSuccessListener(t -> {
                                    Toast.makeText(getActivity(), "Name updated.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(t -> {
                                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    etName.setText(name);
                                });
                    }
                    btnUpdate.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    v.clearFocus();
                });
            } else {
                btnUpdate.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }


        });
        btnCancel.setOnClickListener(b -> {
            etName.clearFocus();
            btnUpdate.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        });

    }


}


