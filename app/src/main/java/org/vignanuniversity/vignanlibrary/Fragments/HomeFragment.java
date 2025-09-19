package org.vignanuniversity.vignanlibrary.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.vignanuniversity.vignanlibrary.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    // API URLs
    private static final String COUNT_API_URL = "http://192.168.10.25/jspapi/library/count.jsp";
    private static final String UCOUNT_API_URL = "http://192.168.10.25/jspapi/library/ucount.jsp";
    private static final String LIBRARY_INFO_URL = "https://vignan.ac.in/newvignan/library.php";

    // Declare CardViews - Now 8 cards total (header + 6 statistics + more info)
    private CardView headerCard, totalBooksCard, notIssuedBooksCard, issuedBooksCard;
    private CardView eBooksCard, onlineJournalsCard, printJournalsCard, moreInfoCard;

    // Declare TextViews for dynamic content
    private TextView totalBooksCount, notIssuedBooksCount, issuedBooksCount;

    // Volley RequestQueue
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate fragment_home.xml (your layout file)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(requireContext());

        // Initialize CardViews
        initializeViews(view);

        // Set Click Listeners
        setClickListeners();

        // Fetch data from APIs
        fetchTotalBooksCount();
        fetchIssuedBooksCount();

        return view;
    }

    private void initializeViews(View view) {
        // Initialize all 8 CardViews we need
        headerCard = view.findViewById(R.id.headerCard);
        totalBooksCard = view.findViewById(R.id.totalBooksCard);
        notIssuedBooksCard = view.findViewById(R.id.notIssuedBooksCard);
        issuedBooksCard = view.findViewById(R.id.issuedBooksCard);
        eBooksCard = view.findViewById(R.id.eBooksCard);
        onlineJournalsCard = view.findViewById(R.id.onlineJournalsCard);
        printJournalsCard = view.findViewById(R.id.printJournalsCard);
        moreInfoCard = view.findViewById(R.id.moreInfoCard);

        // Initialize TextViews for dynamic content (only for API-driven cards)
        if (totalBooksCard != null) {
            totalBooksCount = view.findViewById(R.id.totalBooksCount);
        }
        if (notIssuedBooksCard != null) {
            notIssuedBooksCount = view.findViewById(R.id.notIssuedBooksCount);
        }
        if (issuedBooksCard != null) {
            issuedBooksCount = view.findViewById(R.id.issuedBooksCount);
        }
    }

    private void fetchTotalBooksCount() {
        // Show loading state
        if (totalBooksCount != null) {
            totalBooksCount.setText("Loading...");
        }

        // Create StringRequest for total books count
        StringRequest stringRequest = new StringRequest(Request.Method.GET, COUNT_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse JSON response
                            JSONObject jsonObject = new JSONObject(response);

                            // Extract total books count - adjust key based on your API response
                            String totalBooks = jsonObject.getString("totalBooks");

                            // Update UI on main thread
                            if (getActivity() != null && totalBooksCount != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        totalBooksCount.setText(totalBooks);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handleApiError("Error parsing total books response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleApiError("Failed to fetch total books: " + error.getMessage());
                    }
                });

        // Add request to queue
        requestQueue.add(stringRequest);
    }

    private void fetchIssuedBooksCount() {
        // Show loading state
        if (notIssuedBooksCount != null) {
            notIssuedBooksCount.setText("Loading...");
        }
        if (issuedBooksCount != null) {
            issuedBooksCount.setText("Loading...");
        }

        // Create StringRequest for issued books count
        StringRequest stringRequest = new StringRequest(Request.Method.GET, UCOUNT_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse JSON response
                            JSONObject jsonObject = new JSONObject(response);

                            // Extract issued and not issued books count
                            String notIssuedBooks = jsonObject.getString("notIssuedBooks");
                            String issuedBooks = jsonObject.getString("issuedBooks");

                            // Update UI on main thread
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (notIssuedBooksCount != null) {
                                            notIssuedBooksCount.setText(notIssuedBooks);
                                        }
                                        if (issuedBooksCount != null) {
                                            issuedBooksCount.setText(issuedBooks);
                                        }
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handleApiError("Error parsing issued books response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleApiError("Failed to fetch issued books: " + error.getMessage());
                    }
                });

        // Add request to queue
        requestQueue.add(stringRequest);
    }

    private void handleApiError(String errorMessage) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Show error message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();

                    // Set error state for any TextViews still showing "Loading..."
                    if (totalBooksCount != null && totalBooksCount.getText().toString().equals("Loading...")) {
                        totalBooksCount.setText("Error");
                    }
                    if (notIssuedBooksCount != null && notIssuedBooksCount.getText().toString().equals("Loading...")) {
                        notIssuedBooksCount.setText("Error");
                    }
                    if (issuedBooksCount != null && issuedBooksCount.getText().toString().equals("Loading...")) {
                        issuedBooksCount.setText("Error");
                    }
                }
            });
        }
    }

    private void setClickListeners() {
        // Set click listeners for all 8 cards we have
        if (headerCard != null) {
            headerCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Add Photo clicked", Toast.LENGTH_SHORT).show();
                // Add photo selection logic here
            });
        }

        if (totalBooksCard != null) {
            totalBooksCard.setOnClickListener(v -> {
                String count = totalBooksCount != null ? totalBooksCount.getText().toString() : "Unknown";
                Toast.makeText(requireContext(), "Total Books: " + count, Toast.LENGTH_SHORT).show();
                // Navigate to total books screen
                navigateToTotalBooks();
            });
        }

        if (notIssuedBooksCard != null) {
            notIssuedBooksCard.setOnClickListener(v -> {
                String count = notIssuedBooksCount != null ? notIssuedBooksCount.getText().toString() : "Unknown";
                Toast.makeText(requireContext(), "Not Issued Books: " + count, Toast.LENGTH_SHORT).show();
                // Navigate to not issued books screen
                navigateToNotIssuedBooks();
            });
        }

        if (issuedBooksCard != null) {
            issuedBooksCard.setOnClickListener(v -> {
                String count = issuedBooksCount != null ? issuedBooksCount.getText().toString() : "Unknown";
                Toast.makeText(requireContext(), "Issued Books: " + count, Toast.LENGTH_SHORT).show();
                // Navigate to issued books screen
                navigateToIssuedBooks();
            });
        }

        if (eBooksCard != null) {
            eBooksCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "E-Books: 207,366", Toast.LENGTH_SHORT).show();
                // Navigate to e-books screen
                navigateToEBooks();
            });
        }

        if (onlineJournalsCard != null) {
            onlineJournalsCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Online Journals: 7,127", Toast.LENGTH_SHORT).show();
                // Navigate to online journals screen
                navigateToOnlineJournals();
            });
        }

        if (printJournalsCard != null) {
            printJournalsCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Print Journals: 261", Toast.LENGTH_SHORT).show();
                // Navigate to print journals screen
                navigateToPrintJournals();
            });
        }

        // NEW: More Information Card Click Listener
        if (moreInfoCard != null) {
            moreInfoCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Opening Library Website...", Toast.LENGTH_SHORT).show();
                openLibraryWebsite();
            });
        }
    }

    // NEW: Method to open library website
    private void openLibraryWebsite() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LIBRARY_INFO_URL));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Unable to open website", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Navigation methods - implement these based on your app's navigation structure
    private void navigateToTotalBooks() {
        // Example: Navigate to TotalBooksFragment or Activity
        // You can implement navigation using FragmentManager or Navigation Component
    }

    private void navigateToNotIssuedBooks() {
        // Example: Navigate to NotIssuedBooksFragment or Activity
    }

    private void navigateToIssuedBooks() {
        // Example: Navigate to IssuedBooksFragment or Activity
    }

    private void navigateToEBooks() {
        // Example: Navigate to EBooksFragment or Activity
    }

    private void navigateToOnlineJournals() {
        // Example: Navigate to OnlineJournalsFragment or Activity
    }

    private void navigateToPrintJournals() {
        // Example: Navigate to PrintJournalsFragment or Activity
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        refreshDashboardData();
    }

    private void refreshDashboardData() {
        // Method to refresh dashboard statistics
        // Fetch latest data from APIs
        fetchTotalBooksCount();
        fetchIssuedBooksCount();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel all pending requests when fragment is destroyed
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}