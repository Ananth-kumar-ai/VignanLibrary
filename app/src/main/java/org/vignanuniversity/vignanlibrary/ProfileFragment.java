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

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private static final String PERSONAL_DETAILS_URL_TEMPLATE =
            "http://160.187.169.12/jspapi/personal_details.jsp?regno=%s";
    private static final String STUDENT_PHOTO_URL_TEMPLATE =
            "http://160.187.169.14/jspapi/photos/%s.JPG";

    private static final String ACADEMIC_DETAILS_URL_TEMPLATE =
            "http://160.187.169.14/jspapi/personal_details.jsp?regno=%s";

    private FragmentProfileBinding binding;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());

        SharedPreferences sharedPreferences =
                requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String regno = sharedPreferences.getString("regno", null);

        if (regno != null && !regno.isEmpty()) {
            setLoadingStates();
            fetchStudentDetails(regno);
            loadStudentImage(regno);
        } else {
            Toast.makeText(getContext(),
                    "Could not find student login information.",
                    Toast.LENGTH_LONG).show();
            logoutUser();
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void setLoadingStates() {
        binding.tvStudentName.setText("Loading...");
        binding.tvRegno.setText("Loading...");
        binding.tvYear.setText("Loading...");
        binding.tvBranch.setText("Loading...");
        binding.tvDepartment.setText("Loading...");
        binding.tvEmail.setText("Loading...");
        binding.tvPhone.setText("Loading...");


    }

    private void fetchStudentDetails(String regno) {
        String url = String.format(PERSONAL_DETAILS_URL_TEMPLATE, regno);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    JSONObject studentData = dataArray.getJSONObject(0);

                                    String name = studentData.optString("name", "N/A");
                                    String branch = studentData.optString("branch", "N/A");
                                    String registrationNo = studentData.optString("regno", "N/A");

                                    // ✅ Fixed email & phone
                                    String department = studentData.optString("department", "School of Computing");
                                    String email = studentData.optString("studentemailid", generateEmail(registrationNo));
                                    String phone = studentData.optString("studentmobile", "+91 XXXXXXXXXX");

                                    // ✅ Get cyear if present, else fallback
                                    String cyear = studentData.optString("cyear", "");
                                    String year;
                                    if (!cyear.isEmpty()) {
                                        try {
                                            int y = Integer.parseInt(cyear);
                                            year = y + getYearSuffix(y) + " Year";
                                        } catch (NumberFormatException e) {
                                            year = "N/A";
                                        }
                                    } else {
                                        year = studentData.optString("year", determineYear(registrationNo));
                                    }

                                    String cgpa = studentData.optString("cgpa", "8.5");
                                    String credits = studentData.optString("credits", "120");
                                    String rank = studentData.optString("rank", "--");

                                    updateUIWithStudentData(
                                            name, branch, registrationNo, department,
                                            email, phone, year, cgpa, credits, rank
                                    );

                                } else {
                                    showErrorAndSetDefaults("No student data found.");
                                }
                            } catch (JSONException e) {
                                Log.e("ProfileFragment", "JSON parsing error", e);
                                showErrorAndSetDefaults("Failed to parse student data.");
                            }
                        },
                        error -> {
                            Log.e("ProfileFragment", "Volley error", error);
                            showErrorAndSetDefaults("Network error fetching details.");
                        });

        requestQueue.add(jsonObjectRequest);
    }

    private void updateUIWithStudentData(String name, String branch, String regno, String department,
                                         String email, String phone, String year, String cgpa,
                                         String credits, String rank) {

        binding.tvStudentName.setText(name);
        binding.tvRegno.setText(regno);
        binding.tvYear.setText(year);
        binding.tvBranch.setText(branch);
        binding.tvDepartment.setText(department);
        binding.tvEmail.setText(email);
        binding.tvPhone.setText(phone);



        Log.d("ProfileFragment", "UI updated successfully with student data");
    }

    private void showErrorAndSetDefaults(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

        binding.tvStudentName.setText("Student Name");
        binding.tvRegno.setText("Registration No.");
        binding.tvBranch.setText("Branch Not Available");
        binding.tvDepartment.setText("Department Not Available");
        binding.tvEmail.setText("Email Not Available");
        binding.tvPhone.setText("Phone Not Available");
        binding.tvYear.setText("Year");
    }

    private String generateEmail(String regno) {
        if (regno != null && !regno.isEmpty()) {
            return regno.toLowerCase() + "@vignan.ac.in";
        }
        return "email@vignan.ac.in";
    }

    private String determineYear(String regno) {
        if (regno != null && regno.length() >= 5) {
            try {
                String yearStr = regno.substring(3, 5);
                int admissionYear = Integer.parseInt(yearStr) + 2000;
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int academicYear = currentYear - admissionYear + 1;

                if (academicYear > 0 && academicYear <= 4) {
                    return academicYear + getYearSuffix(academicYear) + " Year";
                }
            } catch (Exception e) {
                Log.e("ProfileFragment",
                        "Error determining year from regno: " + regno, e);
            }
        }
        return "N/A";
    }

    private String getYearSuffix(int year) {
        switch (year) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    private void loadStudentImage(String regno) {
        String imageUrl = String.format(STUDENT_PHOTO_URL_TEMPLATE, regno);

        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(binding.profileImage);
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences =
                requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
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