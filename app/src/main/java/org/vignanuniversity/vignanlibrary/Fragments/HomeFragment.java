package org.vignanuniversity.vignanlibrary.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vignanuniversity.vignanlibrary.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String COUNT_API_URL = "http://192.168.10.25/jspapi/Vignan_Library_app/count.jsp";
    private static final String UCOUNT_API_URL = "http://192.168.10.25/jspapi/Vignan_Library_app/ucount.jsp";
    private static final String LIBRARY_INFO_URL = "https://vignan.ac.in/newvignan/";

    private TextView totalBooksCount, notIssuedBooksCount, issuedBooksCount;
    private RequestQueue requestQueue;
    private boolean isAlertShowing = false;

    // ✅ Static flag — ensures overdue alert shows only once per app launch
    private static boolean hasShownOverdueAlert = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        requestQueue = Volley.newRequestQueue(requireContext());
        initializeViews(view);
        setClickListeners(view);
        fetchDashboardCounts();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // ✅ Show overdue alert only once after app opens
        if (!hasShownOverdueAlert) {
            hasShownOverdueAlert = true;
            checkForOverdueBooks();
        }
    }

    private void initializeViews(View view) {
        totalBooksCount = view.findViewById(R.id.totalBooksCount);
        notIssuedBooksCount = view.findViewById(R.id.notIssuedBooksCount);
        issuedBooksCount = view.findViewById(R.id.issuedBooksCount);
    }

    private void fetchDashboardCounts() {
        totalBooksCount.setText("Loading...");
        notIssuedBooksCount.setText("Loading...");
        issuedBooksCount.setText("Loading...");

        StringRequest totalRequest = new StringRequest(Request.Method.GET, COUNT_API_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String totalBooks = jsonObject.getString("totalBooks");
                        if (totalBooksCount != null) totalBooksCount.setText(totalBooks);
                    } catch (JSONException e) {
                        if (totalBooksCount != null) totalBooksCount.setText("Error");
                    }
                },
                error -> {
                    if (totalBooksCount != null) totalBooksCount.setText("Error");
                });

        StringRequest issuedRequest = new StringRequest(Request.Method.GET, UCOUNT_API_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String notIssuedBooks = jsonObject.getString("notIssuedBooks");
                        String issuedBooks = jsonObject.getString("issuedBooks");
                        if (notIssuedBooksCount != null) notIssuedBooksCount.setText(notIssuedBooks);
                        if (issuedBooksCount != null) issuedBooksCount.setText(issuedBooks);
                    } catch (JSONException e) {
                        if (notIssuedBooksCount != null) notIssuedBooksCount.setText("Error");
                        if (issuedBooksCount != null) issuedBooksCount.setText("Error");
                    }
                },
                error -> {
                    if (notIssuedBooksCount != null) notIssuedBooksCount.setText("Error");
                    if (issuedBooksCount != null) issuedBooksCount.setText("Error");
                });

        requestQueue.add(totalRequest);
        requestQueue.add(issuedRequest);
    }

    private void checkForOverdueBooks() {
        if (isAlertShowing) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("pref", 0);
        String regno = prefs.getString("regno", "");
        if (regno.isEmpty()) return;

        String url = "http://192.168.10.25/jspapi/Vignan_Library_app/student_info.jsp?regno=" + regno;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        JSONArray data = responseObj.optJSONArray("data");
                        List<JSONObject> overdueItems = new ArrayList<>();
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.optJSONObject(i);
                                if (item == null) continue;
                                String dateOfReturn = item.optString("dateofreturn", "").trim();
                                if (dateOfReturn.equals("0000/00/00") || dateOfReturn.equals("00/00/0000")) {
                                    if (isBookOverdue(item.optString("dateofissue"))) {
                                        overdueItems.add(item);
                                    }
                                }
                            }
                        }
                        if (!overdueItems.isEmpty()) {
                            fetchOverdueBookTitles(overdueItems);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error checking for overdue books", e);
                    }
                }), error -> Log.e(TAG, "Failed to fetch student details for alert", error));
        requestQueue.add(request);
    }

    private boolean isBookOverdue(String dateOfIssueStr) {
        if (dateOfIssueStr == null || dateOfIssueStr.trim().isEmpty()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        try {
            Date issueDate = sdf.parse(dateOfIssueStr.trim());
            if (issueDate == null) return false;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(issueDate);
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            Date dueDate = calendar.getTime();

            return dueDate.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private void fetchOverdueBookTitles(List<JSONObject> overdueItems) {
        AtomicInteger counter = new AtomicInteger(overdueItems.size());
        List<String> overdueBookTitles = new ArrayList<>();

        for (JSONObject item : overdueItems) {
            String accno = item.optString("accno");
            String url = "http://192.168.10.25/jspapi/library/basedOnaccno.jsp?accno=" + accno;
            StringRequest titleRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray dataArr = obj.optJSONArray("data");
                            if (dataArr != null && dataArr.length() > 0) {
                                String title = dataArr.getJSONObject(0).optString("Title", "Unknown Title");
                                synchronized (overdueBookTitles) {
                                    overdueBookTitles.add(title);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing book title", e);
                        } finally {
                            if (counter.decrementAndGet() == 0) {
                                showOverdueBooksPopup(overdueBookTitles);
                            }
                        }
                    },
                    error -> {
                        Log.e(TAG, "Error fetching book title", error);
                        if (counter.decrementAndGet() == 0) {
                            showOverdueBooksPopup(overdueBookTitles);
                        }
                    });
            requestQueue.add(titleRequest);
        }
    }

    private void showOverdueBooksPopup(List<String> bookTitles) {
        if (isAdded() && getActivity() != null && !bookTitles.isEmpty() && !isAlertShowing) {
            isAlertShowing = true;
            StringBuilder message = new StringBuilder("The following books have crossed the 14-day return deadline:\n");
            for (String title : bookTitles) {
                message.append("\n• ").append(title);
            }
            getActivity().runOnUiThread(() -> new AlertDialog.Builder(requireContext())
                    .setTitle("🚨 Overdue Books Alert")
                    .setMessage(message.toString())
                    .setPositiveButton("OK", null)
                    .setOnDismissListener(dialog -> isAlertShowing = false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show());
        }
    }

    private void setClickListeners(View view) {
        CardView moreInfoCard = view.findViewById(R.id.moreInfoCard);
        if (moreInfoCard != null) {
            moreInfoCard.setOnClickListener(v -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LIBRARY_INFO_URL));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Unable to open website", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
