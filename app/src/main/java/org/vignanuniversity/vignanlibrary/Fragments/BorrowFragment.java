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
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private LinearLayout emptyStateContainer;
    private TextView emptyStateTitle, emptyStateMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_borrow, container, false);

        recyclerView = root.findViewById(R.id.recyclerBorrowedBooks);
        btnCurrentlyBorrowed = root.findViewById(R.id.btnCurrentlyBorrowed);
        btnTransactionHistory = root.findViewById(R.id.btnTransactionHistory);

        emptyStateContainer = root.findViewById(R.id.emptyStateContainer);
        emptyStateTitle = root.findViewById(R.id.emptyStateTitle);
        emptyStateMessage = root.findViewById(R.id.emptyStateMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BorrowedBookAdapter(getContext(), displayedBooks, this::showPopup);
        recyclerView.setAdapter(adapter);

        btnCurrentlyBorrowed.setOnClickListener(v -> {
            showingCurrentlyBorrowed = true;
            updateButtonStyles();
            filterBooks();
        });
        btnTransactionHistory.setOnClickListener(v -> {
            showingCurrentlyBorrowed = false;
            updateButtonStyles();
            filterBooks();
        });

        queue = Volley.newRequestQueue(requireContext());
        loadBorrowedBooks();

        updateButtonStyles();
        return root;
    }

    private void updateButtonStyles() {
        if (btnCurrentlyBorrowed == null || btnTransactionHistory == null) return;

        if (showingCurrentlyBorrowed) {
            btnCurrentlyBorrowed.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#c5c035")));
            btnCurrentlyBorrowed.setTextColor(getResources().getColor(android.R.color.white, null));
            btnTransactionHistory.setBackgroundTintList(
                    getResources().getColorStateList(android.R.color.darker_gray, null));
            btnTransactionHistory.setTextColor(getResources().getColor(android.R.color.black, null));
        } else {
            btnTransactionHistory.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#c5c035")));
            btnTransactionHistory.setTextColor(getResources().getColor(android.R.color.white, null));
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
                if (!isReturned) displayedBooks.add(book);
            } else {
                if (isReturned) displayedBooks.add(book);
            }
        }

        safeUi(() -> {
            adapter.notifyDataSetChanged();
            updateEmptyStateVisibility();
        });
    }

    private boolean isBookReturned(BorrowedBook book) {
        if (book == null) return false;
        if (book.dateOfReturn == null ||
                book.dateOfReturn.equals("00/00/0000") ||
                book.dateOfReturn.equals("--") ||
                book.dateOfReturn.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void loadBorrowedBooks() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("pref", 0);
        String regno = prefs.getString("regno", "");
        if (regno == null || regno.isEmpty()) {
            Log.e(TAG, "Regno is empty — cannot fetch borrowed books.");
            safeUi(this::filterBooks);
            return;
        }

        String url = "http://160.187.169.13/Vignan_Library_app/student_info.jsp?regno=" + regno;
        Log.d(TAG, "Fetching borrowed books from: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        JSONArray data = responseObj.optJSONArray("data");

                        allBooks.clear();
                        if (data != null) {
                            if (data.length() == 0) {
                                safeUi(this::filterBooks);
                            } else {
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
                            }
                        } else {
                            safeUi(this::filterBooks);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing student details JSON", e);
                        safeUi(this::filterBooks);
                    }
                }),
                error -> {
                    Log.e(TAG, "Error fetching student details: ", error);
                    safeUi(this::filterBooks);
                });

        queue.add(request);
    }

    private void fetchBookMeta(String accno, String dateOfIssue, String dateOfReturn) {
        String url = "http://160.187.169.13/Vignan_Library_app/book_details.jsp?accno=" + accno;
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

                            safeUi(() -> {
                                for (BorrowedBook existing : allBooks) {
                                    if (existing.accno.equals(book.accno)) return;
                                }
                                allBooks.add(book);
                                filterBooks();
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

    private void updateEmptyStateVisibility() {
        if (!isAdded() || getActivity() == null) return;

        if (displayedBooks.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);

            if (showingCurrentlyBorrowed) {
                emptyStateTitle.setText("No books currently borrowed");
                emptyStateMessage.setText("You don't have any books borrowed right now. 📚\n\nVisit the catalog to borrow books or check back later.");
            } else {
                emptyStateTitle.setText("No transaction history");
                emptyStateMessage.setText("You have no past borrow/return transactions. 🌟\n\nOnce you return borrowed books, they will appear here.");
            }
        } else {
            emptyStateContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

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
