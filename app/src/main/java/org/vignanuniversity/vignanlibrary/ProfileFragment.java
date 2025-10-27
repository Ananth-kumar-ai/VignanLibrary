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
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // Personal Details API
    private static final String PERSONAL_DETAILS_URL_TEMPLATE =
            "http://160.187.169.12/jspapi/personal_details.jsp?regno=%s";

    // Student Photo API
    private static final String STUDENT_PHOTO_URL_TEMPLATE =
            "http://160.187.169.14/jspapi/photos/%s.JPG";

    // Library APIs
    private static final String LIBRARY_STUDENT_DETAILS_URL =
            "http://192.168.10.25/jspapi/Vignan_Library_app/student_info.jsp?regno=%s";
    private static final String LIBRARY_BOOK_COUNT_URL =
            "http://192.168.10.25/jspapi/Vignan_Library_app/bcount.jsp?regno=%s";

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
            Log.d(TAG, "Loading profile for regno: " + regno);
            setLoadingStates();
            fetchStudentDetails(regno);
            loadStudentImage(regno);

            // IMPORTANT: Call fetchLibraryStatus FIRST to get both accid AND total books
            // Then call fetchBookCount separately if needed
            fetchLibraryStatusAndCount(regno);
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
        // Academic Information Loading States
        binding.tvStudentName.setText("Loading...");
        binding.tvRegno.setText("Loading...");
        binding.tvYear.setText("Loading...");
        binding.tvBranch.setText("Loading...");
        binding.tvDepartment.setText("Loading...");
        binding.tvEmail.setText("Loading...");
        binding.tvPhone.setText("Loading...");

        // Library Status Loading States
        binding.tvAccid.setText("Loading...");
        binding.tvTotalBooks.setText("Loading...");
        binding.tvBooksToReturn.setText("Loading...");
    }

    /**
     * Fetch student personal and academic details
     */
    private void fetchStudentDetails(String regno) {
        String url = String.format(PERSONAL_DETAILS_URL_TEMPLATE, regno);
        Log.d(TAG, "Fetching student details from: " + url);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                Log.d(TAG, "Student details response: " + response.toString());
                                JSONArray dataArray = response.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    JSONObject studentData = dataArray.getJSONObject(0);

                                    String name = studentData.optString("name", "N/A");
                                    String branch = studentData.optString("branch", "N/A");
                                    String registrationNo = studentData.optString("regno", "N/A");

                                    String department = studentData.optString("department", "School of Computing");
                                    String email = studentData.optString("studentemailid", generateEmail(registrationNo));
                                    String phone = studentData.optString("studentmobile", "+91 XXXXXXXXXX");

                                    // Determine year
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

                                    updateUIWithStudentData(
                                            name, branch, registrationNo, department,
                                            email, phone, year
                                    );

                                    Log.d(TAG, "Student details fetched successfully for: " + name);

                                } else {
                                    showErrorAndSetDefaults("No student data found.");
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON parsing error", e);
                                showErrorAndSetDefaults("Failed to parse student data.");
                            }
                        },
                        error -> {
                            Log.e(TAG, "Volley error fetching student details", error);
                            if (error.networkResponse != null) {
                                Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                            }
                            showErrorAndSetDefaults("Network error fetching details.");
                        });

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * COMBINED METHOD: Fetch library status, total books, and books to return
     * This method gets ALL the data from student_details API
     */
    /**
     * Fetch library status, total books, and count of books not yet returned
     */
    private void fetchLibraryStatusAndCount(String regno) {
        String url = String.format(LIBRARY_STUDENT_DETAILS_URL, regno);
        Log.d(TAG, "Fetching library data from: " + url);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            // Parse data off the UI thread
                            Executors.newSingleThreadExecutor().execute(() -> {
                                try {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    int totalBooks = dataArray.length();
                                    String accid = "N/A";
                                    int booksToReturn = 0;

                                    if (totalBooks > 0) {
                                        JSONObject firstRecord = dataArray.getJSONObject(0);
                                        accid = firstRecord.optString("accid", "N/A");

                                        for (int i = 0; i < totalBooks; i++) {
                                            JSONObject book = dataArray.getJSONObject(i);
                                            String dateOfReturn = book.optString("dateofreturn", "").trim();

                                            if (dateOfReturn.equals("0000/00/00") ||
                                                    dateOfReturn.equals("00/00/0000") ||
                                                    dateOfReturn.equals("0000/00/00")) {
                                                booksToReturn++;
                                            }
                                        }
                                    }

                                    String finalAccid = accid;
                                    int finalTotalBooks = totalBooks;
                                    int finalBooksToReturn = booksToReturn;

                                    // Post UI updates back to the main thread
                                    requireActivity().runOnUiThread(() -> {
                                        binding.tvAccid.setText(finalAccid);
                                        binding.tvTotalBooks.setText(String.valueOf(finalTotalBooks));
                                        binding.tvBooksToReturn.setText(String.valueOf(finalBooksToReturn));
                                    });

                                    Log.d(TAG, "Library Data: accid=" + accid +
                                            ", total=" + totalBooks +
                                            ", pending=" + booksToReturn);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON parse error", e);
                                    requireActivity().runOnUiThread(() -> {
                                        binding.tvAccid.setText("Error");
                                        binding.tvTotalBooks.setText("--");
                                        binding.tvBooksToReturn.setText("--");
                                    });
                                }
                            });
                        },
                        error -> {
                            Log.e(TAG, "Error fetching library data", error);
                            if (error.networkResponse != null) {
                                Log.e(TAG, "Library data error code: " + error.networkResponse.statusCode);
                            }
                            binding.tvAccid.setText("N/A");
                            binding.tvTotalBooks.setText("--");
                            binding.tvBooksToReturn.setText("--");
                            Toast.makeText(getContext(),
                                    "Could not fetch library data",
                                    Toast.LENGTH_SHORT).show();
                        });

        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Update UI with student academic data
     */
    private void updateUIWithStudentData(String name, String branch, String regno,
                                         String department, String email, String phone,
                                         String year) {
        binding.tvStudentName.setText(name);
        binding.tvRegno.setText(regno);
        binding.tvYear.setText(year);
        binding.tvBranch.setText(branch);
        binding.tvDepartment.setText(department);
        binding.tvEmail.setText(email);
        binding.tvPhone.setText(phone);

        Log.d(TAG, "UI updated with student data for: " + name);
    }

    /**
     * Show error and set default values
     */
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

    /**
     * Generate email from registration number
     */
    private String generateEmail(String regno) {
        if (regno != null && !regno.isEmpty()) {
            return regno.toLowerCase() + "@vignan.ac.in";
        }
        return "email@vignan.ac.in";
    }

    /**
     * Determine year from registration number
     */
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
                Log.e(TAG, "Error determining year from regno: " + regno, e);
            }
        }
        return "N/A";
    }

    /**
     * Get year suffix (st, nd, rd, th)
     */
    private String getYearSuffix(int year) {
        switch (year) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    /**
     * Load student profile image
     */
    private void loadStudentImage(String regno) {
        String imageUrl = String.format(STUDENT_PHOTO_URL_TEMPLATE, regno);

        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(binding.profileImage);

        Log.d(TAG, "Loading profile image from: " + imageUrl);
    }

    /**
     * Logout user and clear session
     */
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

        Log.d(TAG, "User logged out");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
        binding = null;
        Log.d(TAG, "ProfileFragment view destroyed");
    }
}