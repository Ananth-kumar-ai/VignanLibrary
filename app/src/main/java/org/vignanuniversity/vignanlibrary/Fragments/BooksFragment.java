package org.vignanuniversity.vignanlibrary.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vignanuniversity.vignanlibrary.Book;
import org.vignanuniversity.vignanlibrary.BookAdapter;
import org.vignanuniversity.vignanlibrary.R;
import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment {

    private ImageView ivBack, ivClearSearch;
    private EditText etSearch;
    private TextView tvBookCount, tvSearchResults;
    private TextView tvEmptyStateIcon, tvEmptyStateTitle, tvEmptyStateMessage;
    private RecyclerView recyclerViewBooks;
    private LinearLayout layoutEmptyState, layoutLoadingState;

    private BookAdapter bookAdapter;
    private final List<Book> allBooks = new ArrayList<>();
    private final List<Book> filteredBooks = new ArrayList<>();

    private String apiUrl;
    private RequestQueue requestQueue;
    private Handler searchHandler;
    private Runnable searchRunnable;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        if (getArguments() != null) {
            apiUrl = getArguments().getString("api_url", "");
        }

        initViews(view);
        setupBackButton();
        setupRecyclerView();
        setupSearch();

        requestQueue = Volley.newRequestQueue(requireContext());
        searchHandler = new Handler();

        loadBooks();

        return view;
    }

    private void initViews(View view) {
        ivBack = view.findViewById(R.id.ivBack);
        ivClearSearch = view.findViewById(R.id.ivClearSearch);
        etSearch = view.findViewById(R.id.etSearch);
        tvBookCount = view.findViewById(R.id.tvBookCount);
        tvSearchResults = view.findViewById(R.id.tvSearchResults);
        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        layoutLoadingState = view.findViewById(R.id.layoutLoadingState);
        tvEmptyStateIcon = view.findViewById(R.id.tvEmptyStateIcon);
        tvEmptyStateTitle = view.findViewById(R.id.tvEmptyStateTitle);
        tvEmptyStateMessage = view.findViewById(R.id.tvEmptyStateMessage);
    }

    private void setupBackButton() {
        ivBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        bookAdapter = new BookAdapter(filteredBooks);
        recyclerViewBooks.setAdapter(bookAdapter);
    }

    private void setupSearch() {
        ivClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.clearFocus();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = BooksFragment.this::filterBooks;
                searchHandler.postDelayed(searchRunnable, 300);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterBooks();
                return true;
            }
            return false;
        });
    }

    private void loadBooks() {
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            showError("Invalid API URL");
            updateEmptyState();
            return;
        }

        showLoading(true);
        allBooks.clear();
        filteredBooks.clear();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                response -> {
                    if (!isAdded()) return;

                    JSONArray dataArray = response.optJSONArray("data");
                    if (dataArray == null) dataArray = new JSONArray();

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject obj = dataArray.optJSONObject(i);
                        if (obj == null) continue;

                        String title = obj.optString("Title", "Unknown Title");
                        String publisher = obj.optString("Publisher", "Unknown Publisher");
                        String edition = obj.optString("Edition", "Unknown Edition");
                        String department = obj.optString("Department", "Unknown Department");

                        allBooks.add(new Book(title, publisher, edition, department));
                    }

                    filteredBooks.addAll(allBooks);
                    bookAdapter.notifyDataSetChanged();
                    updateBookCount();
                    showLoading(false);
                },
                error -> {
                    if (!isAdded()) return;
                    showError("Failed to load books. Please check your internet connection.");
                    showLoading(false);
                });

        // ✅ Prevent infinite waits
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000, // 5s timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }


    private void filterBooks() {
        if (!isAdded()) return;
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();
        filteredBooks.clear();

        if (searchQuery.isEmpty()) {
            filteredBooks.addAll(allBooks);
            tvSearchResults.setVisibility(View.GONE);
        } else {
            for (Book book : allBooks) {
                if ((book.getTitle() != null && book.getTitle().toLowerCase().contains(searchQuery)) ||
                        (book.getPublisher() != null && book.getPublisher().toLowerCase().contains(searchQuery)) ||
                        (book.getDepartment() != null && book.getDepartment().toLowerCase().contains(searchQuery)) ||
                        (book.getEdition() != null && book.getEdition().toLowerCase().contains(searchQuery))) {
                    filteredBooks.add(book);
                }
            }
            String resultText = filteredBooks.size() + " result" +
                    (filteredBooks.size() == 1 ? "" : "s") + " for \"" + searchQuery + "\"";
            tvSearchResults.setText(resultText);
            tvSearchResults.setVisibility(View.VISIBLE);
        }

        bookAdapter.notifyDataSetChanged();
        updateBookCount();
        updateEmptyState();
    }

    private void updateBookCount() {
        int count = filteredBooks.size();
        tvBookCount.setText(count + " book" + (count == 1 ? "" : "s"));
    }

    private void updateEmptyState() {
        if (!isAdded()) return;
        if (filteredBooks.isEmpty()) {
            recyclerViewBooks.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            String searchQuery = etSearch.getText().toString().trim();

            if (!searchQuery.isEmpty()) {
                tvEmptyStateIcon.setText("🔍");
                tvEmptyStateTitle.setText("No results found");
                tvEmptyStateMessage.setText("Try a different keyword or check spelling.");
            } else {
                tvEmptyStateIcon.setText("📚");
                tvEmptyStateTitle.setText("No books available");
                tvEmptyStateMessage.setText("Books will appear here when loaded.");
            }
        } else {
            recyclerViewBooks.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        isLoading = show;
        if (!isAdded()) return;
        layoutLoadingState.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            recyclerViewBooks.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        } else {
            updateEmptyState();
        }
    }

    private void showError(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) requestQueue.cancelAll(this);
        if (searchHandler != null && searchRunnable != null)
            searchHandler.removeCallbacks(searchRunnable);
    }
}
