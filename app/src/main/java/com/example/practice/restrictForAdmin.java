package com.example.practice;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class restrictForAdmin extends DialogFragment {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private restrictForAdminListener mListener;

    public interface restrictForAdminListener {
        void onLoginClick(String username, String password);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            mListener = (restrictForAdminListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement restrictForAdminListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_restrict_for_admin, null);

        mUsernameEditText = view.findViewById(R.id.username_edittext);
        mPasswordEditText = view.findViewById(R.id.password_edittext);

        builder.setView(view)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = mUsernameEditText.getText().toString();
                        String password = mPasswordEditText.getText().toString();

                        // Check if the username and password are correct
                        if (username.equals("admin") && password.equals("1234")) {
                            Intent intent = new Intent(getActivity(), Admin_sign_up.class);
                            startActivity(intent);
                            dialogInterface.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restrictForAdmin.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
