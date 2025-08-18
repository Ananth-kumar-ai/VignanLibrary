package org.vignanuniversity.vignanlibrary.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import org.vignanuniversity.vignanlibrary.LoginSignup.LoginActivity;
import org.vignanuniversity.vignanlibrary.R;

public class ProfileFragment extends Fragment {

    TextView tvName, tvRegno, tvEmail, tvPhone;
    ShapeableImageView profileImage;
    MaterialButton btnLogout;
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Init views
        tvName = view.findViewById(R.id.tv_name);
        tvRegno = view.findViewById(R.id.tv_regno);
        tvEmail = view.findViewById(R.id.tv_email);

        profileImage = view.findViewById(R.id.profile_image);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Load user data from SharedPreferences
        preferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        String regNo = preferences.getString("regno", "N/A");
        String name = preferences.getString("name", "Student Name");
        String email = preferences.getString("email", "example@vignan.ac.in");
        String phone = preferences.getString("phone", "+91 XXXXX XXXXX");

        tvName.setText(name);
        tvRegno.setText("Reg No: " + regNo);
        tvEmail.setText(email);

        // Logout logic
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
