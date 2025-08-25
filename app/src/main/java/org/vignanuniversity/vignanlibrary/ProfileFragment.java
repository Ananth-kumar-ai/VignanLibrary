package org.vignanuniversity.vignanlibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vignanuniversity.vignanlibrary.LoginSignup.LoginActivity;
import org.vignanuniversity.vignanlibrary.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private static final String PERSONAL_DETAILS_URL_TEMPLATE = "http://160.187.169.12/jspapi/personal_details.jsp?regno=%s";
    private static final String STUDENT_PHOTO_URL_TEMPLATE = "http://160.187.169.14/jspapi/photos/%s.JPG";

    private FragmentProfileBinding binding;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String regno = sharedPreferences.getString("regno", null);

        if (regno != null && !regno.isEmpty()) {
            fetchStudentDetails(regno);
            loadStudentImage(regno);
        } else {
            Toast.makeText(getContext(), "Could not find student login information.", Toast.LENGTH_LONG).show();
            logoutUser(); // If regno is missing, logout to be safe
        }

        // ▼▼▼ ADDED LOGOUT BUTTON LISTENER ▼▼▼
        binding.logoutButton.setOnClickListener(v -> {
            logoutUser();
        });
    }

    // ▼▼▼ ADDED LOGOUT FUNCTION ▼▼▼
    private void logoutUser() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Removes all data (isLogin, regno, etc.)
        editor.apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear all previous activities from the stack
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish(); // Finish MainActivity
    }


    private void fetchStudentDetails(String regno) {
        String url = String.format(PERSONAL_DETAILS_URL_TEMPLATE, regno);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            JSONObject studentData = dataArray.getJSONObject(0);

                            String name = studentData.optString("name", "N/A");
                            String branch = studentData.optString("branch", "N/A");
                            String registrationNo = studentData.optString("regno", "N/A");

                            binding.tvStudentName.setText(name);
                            binding.tvBranch.setText(branch);
                            binding.tvRegno.setText(registrationNo);
                        }
                    } catch (JSONException e) {
                        Log.e("ProfileFragment", "JSON parsing error", e);
                        Toast.makeText(getContext(), "Failed to parse student data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ProfileFragment", "Volley error", error);
                    Toast.makeText(getContext(), "Network error fetching details.", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void loadStudentImage(String regno) {
        String imageUrl = String.format(STUDENT_PHOTO_URL_TEMPLATE, regno);

        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .error(R.drawable.ic_profile_placeholder) // Use a placeholder drawable
                .into(binding.profileImage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
        binding = null;
    }
}