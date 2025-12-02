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
import org.vignanuniversity.vignanlibrary.PYQPAdapter;
import org.vignanuniversity.vignanlibrary.Models.PYQDepartment;
import org.vignanuniversity.vignanlibrary.Models.PYQPListItem;
import org.vignanuniversity.vignanlibrary.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class PYQPFragment extends Fragment {

    private static final String API_URL =
            "http://192.168.10.25/jspapi/Vignan_Library_app/pyqsAPI.jsp?action=read";

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private PYQPAdapter adapter;

    private final List<PYQDepartment> allDepartments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pyqp, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerPyqp);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PYQPAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        initSearch();
        loadDepartments();

        return view;
    }

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

    private void loadDepartments() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);

                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream())
                    );

                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        json.append(line);
                    }

                    br.close();
                    conn.disconnect();

                    JSONObject root = new JSONObject(json.toString());
                    JSONArray arr = root.getJSONArray("data");

                    allDepartments.clear();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        PYQDepartment dept = new PYQDepartment();
                        dept.id = obj.getInt("id");
                        dept.deptName = obj.getString("dept_name");
                        dept.urlId = obj.getString("url_id");
                        dept.section = obj.getString("section");

                        allDepartments.add(dept);
                    }

                    if (isAdded()) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                applyFilter(""); // show all initially
                            }
                        });
                    }

                } catch (final Exception e) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(),
                                        "Failed to load departments",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void applyFilter(String query) {
        String q = query.toLowerCase(Locale.getDefault());

        List<PYQDepartment> filtered = new ArrayList<>();
        for (PYQDepartment d : allDepartments) {
            if (q.isEmpty() ||
                    d.deptName.toLowerCase(Locale.getDefault()).contains(q)) {
                filtered.add(d);
            }
        }

        List<PYQPListItem> sectioned = buildSectionedList(filtered);
        adapter.setItems(sectioned);
    }

    private List<PYQPListItem> buildSectionedList(List<PYQDepartment> depts) {
        List<PYQPListItem> result = new ArrayList<>();

        // group by section
        Map<String, List<PYQDepartment>> map = new LinkedHashMap<>();

        for (PYQDepartment d : depts) {
            List<PYQDepartment> list = map.get(d.section);
            if (list == null) {
                list = new ArrayList<>();
                map.put(d.section, list);
            }
            list.add(d);
        }

        // maintain fixed section order
        String[] order = new String[]{
                "engineering",
                "computer_science",
                "business",
                "law",
                "science",
                "pharmacy"
        };

        for (String sec : order) {
            List<PYQDepartment> list = map.get(sec);
            if (list == null || list.isEmpty()) continue;

            // header label
            String headerTitle = getSectionTitle(sec);
            result.add(PYQPListItem.createHeader(headerTitle));

            // sort by name
            Collections.sort(list, new Comparator<PYQDepartment>() {
                @Override
                public int compare(PYQDepartment o1, PYQDepartment o2) {
                    return o1.deptName.compareToIgnoreCase(o2.deptName);
                }
            });

            for (PYQDepartment d : list) {
                result.add(PYQPListItem.createDepartment(d));
            }
        }

        return result;
    }

    private String getSectionTitle(String sectionKey) {
        if ("engineering".equals(sectionKey)) return "Engineering";
        if ("computer_science".equals(sectionKey)) return "Computer Science & IT";
        if ("business".equals(sectionKey)) return "Business & Management";
        if ("law".equals(sectionKey)) return "Law";
        if ("science".equals(sectionKey)) return "Science";
        if ("pharmacy".equals(sectionKey)) return "Pharmacy";
        return sectionKey;
    }
}