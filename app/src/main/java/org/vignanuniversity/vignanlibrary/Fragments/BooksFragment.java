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
    private List<Book> allBooks;
    private List<Book> filteredBooks;

    private String apiUrl;
    private RequestQueue requestQueue;
    private Handler searchHandler;
    private Runnable searchRunnable;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        // Get API URL from arguments
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

        // Empty state views
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
        allBooks = new ArrayList<>();
        filteredBooks = new ArrayList<>();

        bookAdapter = new BookAdapter(filteredBooks);
        recyclerViewBooks.setAdapter(bookAdapter);
    }

    private void setupSearch() {
        // Clear search button
        ivClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.clearFocus();
        });

        // Search functionality with debouncing
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                ivClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Schedule new search with delay
                searchRunnable = () -> filterBooks();
                searchHandler.postDelayed(searchRunnable, 300); // 300ms delay
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle search action from keyboard
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterBooks();
                return true;
            }
            return false;
        });
    }

    private void loadBooks() {
        showLoading(true);
        allBooks.clear();
        filteredBooks.clear();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);

                            String title = obj.optString("Title", "Unknown Title");
                            String publisher = obj.optString("Publisher", "Unknown Publisher");
                            String edition = obj.optString("Edition", "Unknown Edition");
                            String department = obj.optString("Department", "Unknown Department");

                            Book book = new Book(title, publisher, edition, department);
                            allBooks.add(book);
                        }

                        // Copy all books to filtered list initially
                        filteredBooks.addAll(allBooks);
                        bookAdapter.notifyDataSetChanged();
                        updateBookCount();
                        showLoading(false);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showError("Error parsing book data");
                        showLoading(false);
                    }
                },
                error -> {
                    showError("Failed to fetch books. Please check your connection.");
                    showLoading(false);
                });

        requestQueue.add(request);
    }

    private void filterBooks() {
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();

        filteredBooks.clear();

        if (searchQuery.isEmpty()) {
            // If search is empty, show all books
            filteredBooks.addAll(allBooks);
            tvSearchResults.setVisibility(View.GONE);
        } else {
            // Filter books based on search query
            for (Book book : allBooks) {
                if (book.getTitle().toLowerCase().contains(searchQuery) ||
                        book.getPublisher().toLowerCase().contains(searchQuery) ||
                        book.getDepartment().toLowerCase().contains(searchQuery) ||
                        book.getEdition().toLowerCase().contains(searchQuery)) {
                    filteredBooks.add(book);
                }
            }

            // Show search results count
            String resultText = filteredBooks.size() + " result" +
                    (filteredBooks.size() == 1 ? "" : "s") + " found for \"" + searchQuery + "\"";
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
        if (filteredBooks.isEmpty()) {
            recyclerViewBooks.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);

            String searchQuery = etSearch.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                // Search specific empty state
                tvEmptyStateIcon.setText("🔍");
                tvEmptyStateTitle.setText("No results found");
                tvEmptyStateMessage.setText("Try searching with different keywords or check your spelling");
            } else if (allBooks.isEmpty() && !isLoading) {
                // No books loaded empty state
                tvEmptyStateIcon.setText("📚");
                tvEmptyStateTitle.setText("No books available");
                tvEmptyStateMessage.setText("Books will appear here when loaded from the server");
            } else {
                // Default empty state
                tvEmptyStateIcon.setText("📚");
                tvEmptyStateTitle.setText("No books found");
                tvEmptyStateMessage.setText("Try searching with different keywords");
            }
        } else {
            recyclerViewBooks.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        isLoading = show;
        if (show) {
            layoutLoadingState.setVisibility(View.VISIBLE);
            recyclerViewBooks.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        } else {
            layoutLoadingState.setVisibility(View.GONE);
            updateEmptyState();
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}