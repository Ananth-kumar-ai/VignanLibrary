package org.vignanuniversity.vignanlibrary.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.vignanuniversity.vignanlibrary.EbooksAdapter;
import org.vignanuniversity.vignanlibrary.Models.EbookResource;
import org.vignanuniversity.vignanlibrary.Models.EbooksListItem;
import org.vignanuniversity.vignanlibrary.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class EbooksFragment extends Fragment {

    // Backend API
    private static final String API_URL =
            "http://192.168.10.25/jspapi/Vignan_Library_app/ebooksAPI.jsp?action=read";

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private EbooksAdapter adapter;

    private final List<EbookResource> allResources = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ebooks, container, false);

        searchEditText = view.findViewById(R.id.searchEbooks);
        recyclerView = view.findViewById(R.id.recyclerEbooks);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EbooksAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        initSearch();
        loadResources();

        return view;
    }

    // Real-time search listener
    private void initSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString().trim());
            }
        });
    }

    // Load data from backend API
    private void loadResources() {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

                StringBuilder json = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    json.append(line);
                }

                br.close();
                connection.disconnect();

                JSONObject obj = new JSONObject(json.toString());
                JSONArray arr = obj.getJSONArray("data");

                allResources.clear();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject r = arr.getJSONObject(i);

                    EbookResource item = new EbookResource();
                    item.id = r.getInt("id");
                    item.name = r.getString("resource_name");
                    item.url = r.getString("resource_url");
                    item.category = r.getString("category"); // EBOOK or EJOURNAL

                    allResources.add(item);
                }

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> applyFilter(""));
                }

            } catch (Exception e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(),
                                    "Failed to load E-Resources",
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            }
        }).start();
    }

    private void applyFilter(String query) {
        String q = query.toLowerCase(Locale.getDefault());

        List<EbookResource> filtered = new ArrayList<>();
        for (EbookResource r : allResources) {
            if (q.isEmpty() || r.name.toLowerCase(Locale.getDefault()).contains(q)) {
                filtered.add(r);
            }
        }

        List<EbooksListItem> items = buildSectionList(filtered);
        adapter.setItems(items);
    }

    private List<EbooksListItem> buildSectionList(List<EbookResource> list) {
        List<EbooksListItem> items = new ArrayList<>();

        // Grouping by category
        Map<String, List<EbookResource>> map = new LinkedHashMap<>();

        for (EbookResource r : list) {
            map.computeIfAbsent(r.category, k -> new ArrayList<>()).add(r);
        }

        // Fixed order
        String[] order = {"EBOOK", "EJOURNAL"};

        for (String category : order) {

            List<EbookResource> group = map.get(category);
            if (group == null || group.isEmpty()) continue;

            // Add header
            items.add(EbooksListItem.header(
                    category.equals("EBOOK") ? "📘 E-Books" : "📗 E-Journals"
            ));

            // Sort alphabetically
            group.sort(Comparator.comparing(a -> a.name));

            // Add items
            for (EbookResource r : group) {
                items.add(EbooksListItem.resource(r));
            }
        }

        return items;
    }
}
