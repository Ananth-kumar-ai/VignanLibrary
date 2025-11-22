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
import org.json.JSONObject;
import org.vignanuniversity.vignanlibrary.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String COUNT_API_URL = "http://160.187.169.13/Vignan_Library_app/count.jsp";
    private static final String UCOUNT_API_URL = "http://160.187.169.13/Vignan_Library_app/ucount.jsp";
    private static final String LIBRARY_INFO_URL = "https://vignan.ac.in/newvignan/";
    private TextView totalBooksCount, notIssuedBooksCount, issuedBooksCount;
    private RequestQueue requestQueue;
    private volatile boolean isAlertShowing = false;
    private static boolean hasShownOverdueAlert = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        requestQueue = Volley.newRequestQueue(requireContext());
        totalBooksCount = view.findViewById(R.id.totalBooksCount);
        notIssuedBooksCount = view.findViewById(R.id.notIssuedBooksCount);
        issuedBooksCount = view.findViewById(R.id.issuedBooksCount);
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
        fetchDashboardCounts();
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

    private void fetchDashboardCounts() {
        setTextSafely(totalBooksCount, "Loading...");
        setTextSafely(notIssuedBooksCount, "Loading...");
        setTextSafely(issuedBooksCount, "Loading...");

        StringRequest totalRequest = new StringRequest(Request.Method.GET, COUNT_API_URL,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        JSONArray data = root.optJSONArray("data");
                        String totalBooks = "";
                        if (data != null && data.length() > 0) {
                            totalBooks = data.getJSONObject(0).optString("totalBooks", "");
                        }
                        if (totalBooks.isEmpty()) totalBooks = root.optString("totalBooks", root.optString("total", ""));
                        if (!totalBooks.isEmpty()) setTextSafely(totalBooksCount, totalBooks);
                        else setTextSafely(totalBooksCount, "Error");
                    } catch (Exception e) {
                        setTextSafely(totalBooksCount, "Error");
                        Log.e(TAG, "COUNT parse error", e);
                    }
                },
                error -> {
                    setTextSafely(totalBooksCount, "Error");
                    Log.e(TAG, "COUNT request error", error);
                });
        totalRequest.setTag(TAG);

        StringRequest issuedRequest = new StringRequest(Request.Method.GET, UCOUNT_API_URL,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        JSONArray data = root.optJSONArray("data");
                        String notIssued = "";
                        String issued = "";
                        if (data != null && data.length() > 0) {
                            JSONObject o = data.getJSONObject(0);
                            notIssued = o.optString("notIssuedBooks", o.optString("notissued", ""));
                            issued = o.optString("issuedBooks", o.optString("issued", ""));
                        }
                        if (notIssued.isEmpty()) notIssued = root.optString("notIssuedBooks", root.optString("notissued", ""));
                        if (issued.isEmpty()) issued = root.optString("issuedBooks", root.optString("issued", ""));
                        if (!notIssued.isEmpty()) setTextSafely(notIssuedBooksCount, notIssued); else setTextSafely(notIssuedBooksCount, "Error");
                        if (!issued.isEmpty()) setTextSafely(issuedBooksCount, issued); else setTextSafely(issuedBooksCount, "Error");
                    } catch (Exception e) {
                        setTextSafely(notIssuedBooksCount, "Error");
                        setTextSafely(issuedBooksCount, "Error");
                        Log.e(TAG, "UCOUNT parse error", e);
                    }
                },
                error -> {
                    setTextSafely(notIssuedBooksCount, "Error");
                    setTextSafely(issuedBooksCount, "Error");
                    Log.e(TAG, "UCOUNT request error", error);
                });
        issuedRequest.setTag(TAG);

        requestQueue.add(totalRequest);
        requestQueue.add(issuedRequest);
    }

    private void checkForOverdueBooks() {
        if (isAlertShowing) return;
        SharedPreferences prefs = requireActivity().getSharedPreferences("pref", 0);
        String regno = prefs.getString("regno", "");
        if (regno == null || regno.trim().isEmpty()) return;
        String url = "http://192.168.10.25/jspapi/Vignan_Library_app/student_info.jsp?regno=" + regno;
        StringRequest request = new StringRequest(Request.Method.GET, url,
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
                                if (dateOfReturn.equals("0000/00/00") || dateOfReturn.equals("00/00/0000") || dateOfReturn.isEmpty()) {
                                    String doi = item.optString("dateofissue", item.optString("dateOfIssue", ""));
                                    if (isBookOverdue(doi)) overdueItems.add(item);
                                }
                            }
                        }
                        if (!overdueItems.isEmpty()) fetchOverdueBookTitles(overdueItems);
                    } catch (Exception e) {
                        Log.e(TAG, "student_info parse error", e);
                    }
                }), error -> Log.e(TAG, "student_info request error", error));
        request.setTag(TAG);
        requestQueue.add(request);
    }

    private boolean isBookOverdue(String dateOfIssueStr) {
        if (dateOfIssueStr == null || dateOfIssueStr.trim().isEmpty()) return false;
        String[] patterns = new String[]{"yyyy/MM/dd", "dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy", "MM/dd/yyyy"};
        Date issueDate = null;
        for (String p : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.US);
                sdf.setLenient(false);
                issueDate = sdf.parse(dateOfIssueStr.trim());
                if (issueDate != null) break;
            } catch (ParseException ignored) {}
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
            String accno = item.optString("accno", item.optString("AccNo", item.optString("Accno", "")));
            if (accno == null) accno = "";
            final String finalAccno = accno;
            String url = "http://160.187.169.13/Vignan_Library_app/book_details.jsp?accno=" + finalAccno;
            StringRequest titleRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            JSONObject root = new JSONObject(response);
                            JSONArray dataArr = root.optJSONArray("data");
                            String title = "";
                            if (dataArr != null && dataArr.length() > 0) {
                                title = dataArr.getJSONObject(0).optString("Title", dataArr.getJSONObject(0).optString("title", ""));
                            }
                            if (title.isEmpty()) title = root.optString("Title", root.optString("title", ""));
                            if (title.isEmpty() && response != null) title = response.trim();
                            if (!title.isEmpty()) {
                                synchronized (overdueBookTitles) { overdueBookTitles.add(title); }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "book_details parse error", e);
                        } finally {
                            if (counter.decrementAndGet() == 0) showOverdueBooksPopup(overdueBookTitles);
                        }
                    },
                    error -> {
                        if (counter.decrementAndGet() == 0) showOverdueBooksPopup(overdueBookTitles);
                        Log.e(TAG, "book_details request error", error);
                    });
            titleRequest.setTag(TAG);
            requestQueue.add(titleRequest);
        }
    }

    private void showOverdueBooksPopup(List<String> bookTitles) {
        if (!isAdded() || getActivity() == null || bookTitles == null || bookTitles.isEmpty() || isAlertShowing) return;
        isAlertShowing = true;
        StringBuilder message = new StringBuilder("The following books have crossed the 14-day return deadline:\n");
        for (String t : bookTitles) message.append("\n• ").append(t);
        getActivity().runOnUiThread(() -> new AlertDialog.Builder(requireContext())
                .setTitle("🚨 Overdue Books Alert")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .setOnDismissListener(dialog -> isAlertShowing = false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) requestQueue.cancelAll(TAG);
    }

    private void setTextSafely(TextView tv, String text) {
        if (tv == null) return;
        if (getActivity() == null) {
            tv.setText(text);
            return;
        }
        getActivity().runOnUiThread(() -> {
            try { tv.setText(text); } catch (Exception e) { Log.w(TAG, "setText failed", e); }
        });
    }
}
