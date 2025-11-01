package org.vignanuniversity.vignanlibrary.Fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.vignanuniversity.vignanlibrary.BorrowedBook;
import org.vignanuniversity.vignanlibrary.BorrowedBookAdapter;
import org.vignanuniversity.vignanlibrary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BorrowFragment extends Fragment {

    private RecyclerView recyclerView;
    private BorrowedBookAdapter adapter;
    private final List<BorrowedBook> allBooks = new ArrayList<>();
    private final List<BorrowedBook> displayedBooks = new ArrayList<>();
    private RequestQueue queue;
    private static final String TAG = "BorrowFragment";

    private Button btnCurrentlyBorrowed, btnTransactionHistory;
    private boolean showingCurrentlyBorrowed = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_borrow, container, false);

        // Initialize views
        recyclerView = root.findViewById(R.id.recyclerBorrowedBooks);
        btnCurrentlyBorrowed = root.findViewById(R.id.btnCurrentlyBorrowed);
        btnTransactionHistory = root.findViewById(R.id.btnTransactionHistory);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BorrowedBookAdapter(getContext(), displayedBooks, this::showPopup);
        recyclerView.setAdapter(adapter);

        // Set up button click listeners
        btnCurrentlyBorrowed.setOnClickListener(v -> showCurrentlyBorrowed());
        btnTransactionHistory.setOnClickListener(v -> showTransactionHistory());

        queue = Volley.newRequestQueue(requireContext());
        loadBorrowedBooks();

        return root;
    }

    private void showCurrentlyBorrowed() {
        showingCurrentlyBorrowed = true;
        updateButtonStyles();
        filterBooks();
    }

    private void showTransactionHistory() {
        showingCurrentlyBorrowed = false;
        updateButtonStyles();
        filterBooks();
    }

    private void updateButtonStyles() {
        if (showingCurrentlyBorrowed) {
            // Currently Borrowed - Active style
            btnCurrentlyBorrowed.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#c5c035")));
            btnCurrentlyBorrowed.setTextColor(getResources().getColor(android.R.color.white, null));

            // Transaction History - Inactive style
            btnTransactionHistory.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.darker_gray, null));
            btnTransactionHistory.setTextColor(getResources().getColor(android.R.color.black, null));
        } else {
            // Transaction History - Active style
            btnTransactionHistory.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#c5c035")));
            btnTransactionHistory.setTextColor(getResources().getColor(android.R.color.white, null));

            // Currently Borrowed - Inactive style
            btnCurrentlyBorrowed.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.darker_gray, null));
            btnCurrentlyBorrowed.setTextColor(getResources().getColor(android.R.color.black, null));
        }
    }

    private void filterBooks() {
        displayedBooks.clear();

        for (BorrowedBook book : allBooks) {
            boolean isReturned = isBookReturned(book);

            if (showingCurrentlyBorrowed) {
                // Show books that are NOT returned (dateOfReturn is 00/00/0000 or --)
                if (!isReturned) {
                    displayedBooks.add(book);
                }
            } else {
                // Show books that ARE returned
                if (isReturned) {
                    displayedBooks.add(book);
                }
            }
        }

        safeUi(() -> adapter.notifyDataSetChanged());
    }

    private boolean isBookReturned(BorrowedBook book) {
        if (book.dateOfReturn == null ||
                book.dateOfReturn.equals("00/00/0000") ||
                book.dateOfReturn.equals("--") ||
                book.dateOfReturn.trim().isEmpty()) {
            return false; // Not returned
        }
        return true; // Returned
    }

    private void loadBorrowedBooks() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("pref", 0);
        String regno = prefs.getString("regno", "");
        if (regno == null || regno.isEmpty()) {
            Log.e(TAG, "Regno is empty — cannot fetch borrowed books.");
            return;
        }

        String url = "http://192.168.10.25/jspapi/Vignan_Library_app/student_info.jsp?regno=" + regno;
        String url = "http://160.187.169.14/jspapi/Vignan_Library_app/student_info.jsp?regno=" + regno;
        Log.d(TAG, "Fetching borrowed books from: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        JSONArray data = responseObj.optJSONArray("data");

                        allBooks.clear();
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.optJSONObject(i);
                                if (item == null) continue;

                                String accno = item.optString("accno", "");
                                String dateOfIssue = item.optString("dateofissue", "--");
                                String dateOfReturn = item.optString("dateofreturn", "--");

                                if (!accno.isEmpty()) {
                                    fetchBookMeta(accno, dateOfIssue, dateOfReturn);
                                }
                            }
                        } else {
                            Log.w(TAG, "No data found in student details.");
                            safeUi(() -> {
                                filterBooks();
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing student details JSON", e);
                    }
                }),
                error -> Log.e(TAG, "Error fetching student details: ", error));

        queue.add(request);
    }

    private void fetchBookMeta(String accno, String dateOfIssue, String dateOfReturn) {
        String url = "http://192.168.10.25/jspapi/Vignan_Library_app/book_details.jsp?accno=" + accno;
        Log.d(TAG, "Fetching meta for accno: " + accno);

        StringRequest req = new StringRequest(Request.Method.GET, url,
                resp -> Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        JSONObject obj = new JSONObject(resp);
                        JSONArray dataArr = obj.optJSONArray("data");

                        if (dataArr != null && dataArr.length() > 0) {
                            JSONObject d = dataArr.optJSONObject(0);
                            if (d == null) return;

                            String title = d.optString("Title", "No Title");
                            String dept = d.optString("Department", "No Department");
                            String author = d.optString("Author", "No Author");
                            String publisher = d.optString("Publisher", "No Publisher");

                            BorrowedBook book = new BorrowedBook(title, dept, author, publisher, accno);
                            book.dateOfIssue = dateOfIssue;
                            book.dateOfReturn = dateOfReturn;

                            // Debug logging
                            Log.d(TAG, "========================================");
                            Log.d(TAG, "Book Title: " + title);
                            Log.d(TAG, "Date of Issue: [" + dateOfIssue + "]");
                            Log.d(TAG, "Date of Return: [" + dateOfReturn + "]");
                            Log.d(TAG, "========================================");

                            safeUi(() -> {
                                // Prevent duplicates
                                for (BorrowedBook existing : allBooks) {
                                    if (existing.accno.equals(book.accno)) return;
                                }
                                allBooks.add(book);
                                filterBooks(); // Re-filter to show in correct tab
                            });
                        } else {
                            Log.w(TAG, "No data for accno: " + accno);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing book meta for accno: " + accno, e);
                    }
                }),
                error -> Log.e(TAG, "Error fetching book meta for accno: " + accno, error));

        queue.add(req);
    }

    /** Safely run UI updates only if fragment is still active */
    private void safeUi(Runnable action) {
        if (isAdded() && getActivity() != null) {
            requireActivity().runOnUiThread(() -> {
                if (!isDetached() && !isRemoving()) {
                    try {
                        action.run();
                    } catch (Exception e) {
                        Log.e(TAG, "UI update error: ", e);
                    }
                }
            });
        }
    }

    private void showPopup(BorrowedBook book) {
        if (!isAdded() || getContext() == null) return;

        String dueDate = "--";
        try {
            if (book.dateOfIssue != null && !book.dateOfIssue.equals("--") && !book.dateOfIssue.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate issueDate = LocalDate.parse(book.dateOfIssue.trim(), formatter);
                LocalDate due = issueDate.plusDays(14);
                dueDate = due.format(formatter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating due date", e);
        }

        String msg = "\nDate of Issue: " + (book.dateOfIssue == null ? "--" : book.dateOfIssue) +
                "\nDate of Return: " + (book.dateOfReturn == null ? "--" : book.dateOfReturn) +
                "\nDue Date: " + dueDate;

        try {
            new AlertDialog.Builder(requireContext())
                    .setTitle(book.title != null ? book.title : "Book Details")
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing popup", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (queue != null) queue.cancelAll(TAG);
    }
}