package org.vignanuniversity.vignanlibrary.Fragments;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class BorrowFragment extends Fragment {

    private RecyclerView recyclerView;
    private BorrowedBookAdapter adapter;
    private final List<BorrowedBook> bookList = new ArrayList<>();
    private RequestQueue queue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_borrow, container, false);
        recyclerView = root.findViewById(R.id.recyclerBorrowedBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BorrowedBookAdapter(getContext(), bookList, this::showPopup);
        recyclerView.setAdapter(adapter);
        queue = Volley.newRequestQueue(requireContext());
        loadBorrowedBooks();
        return root;
    }

    private void loadBorrowedBooks() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("pref", 0);
        String regno = prefs.getString("regno", "");
        if (regno == null || regno.isEmpty()) {
            Log.e("BorrowFragment", "regno is empty");
            return;
        }
        String url = "http://160.187.169.14//jspapi/library/student_details.jsp?regno=" + regno;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        JSONArray data = responseObj.optJSONArray("data");
                        bookList.clear();
                        if(data != null) {
                            for(int i=0; i<data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                String accno = item.optString("accno");
                                String dateOfIssue = item.optString("dateofissue");
                                String dateOfReturn = item.optString("dateofreturn");
                                fetchBookMeta(accno, dateOfIssue, dateOfReturn);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("BorrowFragment", "Error parsing student details: ", e);
                    }
                }, error -> Log.e("BorrowFragment", "Error fetching student details: ", error));
        queue.add(request);
    }

    private void fetchBookMeta(String accno, String dateOfIssue, String dateOfReturn) {
        String url = "http://160.187.169.14/jspapi/library/basedOnaccno.jsp?accno=" + accno;
        StringRequest req = new StringRequest(Request.Method.GET, url,
                resp -> {
                    try {
                        JSONObject obj = new JSONObject(resp);
                        JSONArray dataArr = obj.optJSONArray("data");
                        if (dataArr != null && dataArr.length() > 0) {
                            JSONObject d = dataArr.getJSONObject(0);
                            String title = d.optString("Title", "No Title");
                            String dept = d.optString("Department", "No Department");
                            String author = d.optString("Author", "No Author");

                            BorrowedBook book = new BorrowedBook(title, dept, author, accno);
                            book.dateOfIssue = dateOfIssue;
                            book.dateOfReturn = dateOfReturn;
                            bookList.add(book);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception ignored) { }
                }, error -> {
            Log.e("BorrowFragment", "Error fetching book meta: ", error);
        });
        queue.add(req);
    }

    private void showPopup(BorrowedBook book) {
        String msg = "Date of Issue: " + (book.dateOfIssue == null ? "--" : book.dateOfIssue) +
                "\nDate of Return: " + (book.dateOfReturn == null ? "--" : book.dateOfReturn);
        if (isAdded()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(book.title)
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}
