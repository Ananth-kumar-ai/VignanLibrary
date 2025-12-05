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
import android.widget.LinearLayout;
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
import org.json.JSONObject;
import org.vignanuniversity.vignanlibrary.R;

import java.text.ParseException;
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

    // API URLs - UPDATE THESE TO MATCH YOUR SERVER
    private static final String BOOK_STATS_API_URL = "http://192.168.10.25/jspapi/Vignan_Library_app/bookstatistics.jsp";
    private static final String LIBRARY_INFO_URL = "https://vignan.ac.in/newvignan/";
    private static final String ANNOUNCEMENTS_API_URL = "http://192.168.10.25/jspapi/Vignan_Library_app/announcements.jsp";
    private static final String STUDENT_INFO_API_URL = "http://192.168.10.25/jspapi/Vignan_Library_app/student_info.jsp";
    private static final String BOOK_DETAILS_API_URL = "http://192.168.10.25/jspapi/Vignan_Library_app/book_details.jsp";

    // CardViews for all 6 statistics
    private CardView card1, card2, card3, card4, card5, card6;

    // TextViews for announcements
    private TextView announcementsText;

    private RequestQueue requestQueue;
    private volatile boolean isAlertShowing = false;
    private static boolean hasShownOverdueAlert = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        requestQueue = Volley.newRequestQueue(requireContext());

        // Find all 6 CardViews
        card1 = view.findViewById(R.id.totalBooksCard);
        card2 = view.findViewById(R.id.notIssuedBooksCard);
        card3 = view.findViewById(R.id.issuedBooksCard);
        card4 = view.findViewById(R.id.eBooksCard);
        card5 = view.findViewById(R.id.onlineJournalsCard);
        card6 = view.findViewById(R.id.printJournalsCard);

        announcementsText = view.findViewById(R.id.announcementsText);

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

        // Fetch all data
        fetchBookStatistics();
        fetchAnnouncements();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasShownOverdueAlert) {
            hasShownOverdueAlert = true;
            checkForOverdueBooks();
        }
    }

    // ------------------ Fetch Book Statistics from API ------------------
    private void fetchBookStatistics() {
        // Set all cards to loading state
        setCardLoading(card1);
        setCardLoading(card2);
        setCardLoading(card3);
        setCardLoading(card4);
        setCardLoading(card5);
        setCardLoading(card6);

        String url = BOOK_STATS_API_URL + "?action=read";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        JSONArray data = root.optJSONArray("data");

                        if (data == null || data.length() == 0) {
                            setAllCardsError();
                            Log.e(TAG, "No data returned from bookstatistics API");
                            Toast.makeText(requireContext(), "No statistics data available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Array to store cards for easy access by index
                        CardView[] cards = {card1, card2, card3, card4, card5, card6};

                        // Parse and update each card based on database order (id 1-6)
                        for (int i = 0; i < data.length() && i < 6; i++) {
                            JSONObject item = data.getJSONObject(i);

                            int id = item.optInt("id", 0);
                            String title = item.optString("title", "").trim();
                            int noOfBooks = item.optInt("no_of_books", 0);

                            // Map database ID to card position (ID 1 → card1, ID 2 → card2, etc.)
                            int cardIndex = id - 1; // Convert ID (1-6) to array index (0-5)

                            if (cardIndex >= 0 && cardIndex < cards.length) {
                                updateCard(cards[cardIndex], title, noOfBooks);
                                Log.d(TAG, "Updated Card " + id + ": " + title + " = " + noOfBooks);
                            }
                        }

                        Log.d(TAG, "Successfully fetched and updated all book statistics");

                    } catch (Exception e) {
                        setAllCardsError();
                        Log.e(TAG, "Error parsing book statistics", e);
                        Toast.makeText(requireContext(), "Error loading statistics", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    setAllCardsError();
                    Log.e(TAG, "Error fetching book statistics", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status code: " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                }
        );

        request.setTag(TAG);
        requestQueue.add(request);
    }

    /**
     * Update a card with title and count from database
     */
    private void updateCard(CardView card, String title, int count) {
        if (card == null || getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            try {
                // Find the LinearLayout inside the CardView
                View child = card.getChildAt(0);
                if (child instanceof LinearLayout) {
                    LinearLayout layout = (LinearLayout) child;

                    TextView countTextView = null;
                    TextView labelTextView = null;

                    // Find the TextViews - count and label
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        View v = layout.getChildAt(i);
                        if (v instanceof TextView) {
                            TextView tv = (TextView) v;
                            String text = tv.getText().toString();

                            // Skip emoji TextView (first one)
                            if (text.matches(".*[📚✅📖💻🌐📰].*")) {
                                continue;
                            }

                            // Second TextView is the count
                            if (countTextView == null) {
                                countTextView = tv;
                            }
                            // Third TextView is the label
                            else if (labelTextView == null) {
                                labelTextView = tv;
                                break; // Found both, exit loop
                            }
                        }
                    }

                    // Update the TextViews
                    if (countTextView != null) {
                        countTextView.setText(formatNumber(count));
                    }

                    if (labelTextView != null) {
                        labelTextView.setText(title);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating card", e);
            }
        });
    }

    /**
     * Set a card to loading state
     */
    private void setCardLoading(CardView card) {
        if (card == null) return;
        updateCard(card, "Loading...", 0);
    }

    /**
     * Set all cards to error state
     */
    private void setAllCardsError() {
        CardView[] cards = {card1, card2, card3, card4, card5, card6};
        for (CardView card : cards) {
            if (card != null) {
                updateCard(card, "Error", 0);
            }
        }
    }

    /**
     * Format numbers with comma separators (e.g., 207366 -> 207,366)
     */
    private String formatNumber(int number) {
        return String.format(Locale.US, "%,d", number);
    }

    // ------------------ Fetch Announcements ------------------
    private void fetchAnnouncements() {
        if (announcementsText == null) return;

        setTextSafely(announcementsText, "Loading announcements...");

        String url = ANNOUNCEMENTS_API_URL + "?action=read";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        JSONArray data = root.optJSONArray("data");

                        if (data == null || data.length() == 0) {
                            String msg = root.optString("message", "No announcements available");
                            setTextSafely(announcementsText, msg);
                            return;
                        }

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            String text = obj.optString("text", "").trim();
                            if (text.isEmpty()) continue;

                            if (sb.length() > 0) sb.append("\n\n");
                            sb.append("• ").append(text);
                        }

                        if (sb.length() == 0) {
                            setTextSafely(announcementsText, "No announcements available");
                        } else {
                            setTextSafely(announcementsText, sb.toString());
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing announcements", e);
                        setTextSafely(announcementsText, "Failed to load announcements");
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching announcements", error);
                    setTextSafely(announcementsText, "Failed to load announcements");
                }
        );

        request.setTag(TAG);
        requestQueue.add(request);
    }

    // ------------------ Overdue Books Logic ------------------
    private void checkForOverdueBooks() {
        if (isAlertShowing) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("pref", 0);
        String regno = prefs.getString("regno", "");
        if (regno == null || regno.trim().isEmpty()) return;

        String url = STUDENT_INFO_API_URL + "?regno=" + regno;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        JSONArray data = root.optJSONArray("data");
                        List<JSONObject> overdueItems = new ArrayList<>();

                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.optJSONObject(i);
                                if (item == null) continue;

                                String dateOfReturn = item.optString("dateofreturn", "").trim();
                                if (dateOfReturn.equals("0000/00/00") ||
                                        dateOfReturn.equals("00/00/0000") ||
                                        dateOfReturn.isEmpty()) {

                                    String doi = item.optString("dateofissue",
                                            item.optString("dateOfIssue", ""));
                                    if (isBookOverdue(doi)) {
                                        overdueItems.add(item);
                                    }
                                }
                            }
                        }

                        if (!overdueItems.isEmpty()) {
                            fetchOverdueBookTitles(overdueItems);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing student info", e);
                    }
                }),
                error -> Log.e(TAG, "Error fetching student info", error)
        );

        request.setTag(TAG);
        requestQueue.add(request);
    }

    private boolean isBookOverdue(String dateOfIssueStr) {
        if (dateOfIssueStr == null || dateOfIssueStr.trim().isEmpty()) return false;

        String[] patterns = new String[]{
                "yyyy/MM/dd", "dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy", "MM/dd/yyyy"
        };

        Date issueDate = null;
        for (String p : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.US);
                sdf.setLenient(false);
                issueDate = sdf.parse(dateOfIssueStr.trim());
                if (issueDate != null) break;
            } catch (ParseException ignored) {
            }
        }

        if (issueDate == null) return false;

        Calendar c = Calendar.getInstance();
        c.setTime(issueDate);
        c.add(Calendar.DAY_OF_YEAR, 14);
        return c.getTime().before(new Date());
    }

    private void fetchOverdueBookTitles(List<JSONObject> overdueItems) {
        AtomicInteger counter = new AtomicInteger(overdueItems.size());
        List<String> overdueBookTitles = new ArrayList<>();

        for (JSONObject item : overdueItems) {
            String accno = item.optString("accno",
                    item.optString("AccNo",
                            item.optString("Accno", "")));
            if (accno == null) accno = "";
            final String finalAccno = accno;

            String url = BOOK_DETAILS_API_URL + "?accno=" + finalAccno;

            StringRequest titleRequest = new StringRequest(
                    Request.Method.GET,
                    url,
                    response -> {
                        try {
                            JSONObject root = new JSONObject(response);
                            JSONArray dataArr = root.optJSONArray("data");
                            String title = "";

                            if (dataArr != null && dataArr.length() > 0) {
                                title = dataArr.getJSONObject(0)
                                        .optString("Title",
                                                dataArr.getJSONObject(0).optString("title", ""));
                            }

                            if (title.isEmpty()) {
                                title = root.optString("Title", root.optString("title", ""));
                            }

                            if (title.isEmpty() && response != null) {
                                title = response.trim();
                            }

                            if (!title.isEmpty()) {
                                synchronized (overdueBookTitles) {
                                    overdueBookTitles.add(title);
                                }
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing book details", e);
                        } finally {
                            if (counter.decrementAndGet() == 0) {
                                showOverdueBooksPopup(overdueBookTitles);
                            }
                        }
                    },
                    error -> {
                        if (counter.decrementAndGet() == 0) {
                            showOverdueBooksPopup(overdueBookTitles);
                        }
                        Log.e(TAG, "Error fetching book details", error);
                    }
            );

            titleRequest.setTag(TAG);
            requestQueue.add(titleRequest);
        }
    }

    private void showOverdueBooksPopup(List<String> bookTitles) {
        if (!isAdded() || getActivity() == null || bookTitles == null ||
                bookTitles.isEmpty() || isAlertShowing) return;

        isAlertShowing = true;

        StringBuilder message = new StringBuilder(
                "The following books have crossed the 14-day return deadline:\n"
        );
        for (String t : bookTitles) {
            message.append("\n• ").append(t);
        }

        getActivity().runOnUiThread(() ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("🚨 Overdue Books Alert")
                        .setMessage(message.toString())
                        .setPositiveButton("OK", null)
                        .setOnDismissListener(dialog -> isAlertShowing = false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    private void setTextSafely(TextView tv, String text) {
        if (tv == null) return;

        if (getActivity() == null) {
            tv.setText(text);
            return;
        }

        getActivity().runOnUiThread(() -> {
            try {
                tv.setText(text);
            } catch (Exception e) {
                Log.w(TAG, "setText failed", e);
            }
        });
    }
}