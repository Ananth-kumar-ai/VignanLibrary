package org.vignanuniversity.vignanlibrary.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.vignanuniversity.vignanlibrary.R;

import java.util.ArrayList;
import java.util.List;

public class PYQPFragment extends Fragment {

    private static final String BASE_URL = "http://160.187.169.16:8080/jspui/handle/123456789/";

    private EditText searchEditText;

    private List<CardView> allCards;
    private List<Department> allDepartments;

    private TextView engineeringHeader, computerScienceHeader, businessHeader,
            lawHeader, scienceHeader, pharmacyHeader;

    private static class Department {
        String name;
        String urlId;
        String searchKeywords;
        String section;

        Department(String name, String urlId, String searchKeywords, String section) {
            this.name = name;
            this.urlId = urlId;
            this.searchKeywords = searchKeywords != null ? searchKeywords.toLowerCase() : "";
            this.section = section;
        }
    }

    private final Department[] departments = {
            new Department("AGRICULTURE ENGINEERING", "2889", "agriculture engineering agri", "engineering"),
            new Department("AUTOMOBILE ENGINEERING", "2790", "automobile auto mobile car", "engineering"),
            new Department("BIO INFORMATICS ENGINEERING", "1457", "bio informatics bioinformatics", "engineering"),
            new Department("BIO MEDICAL ENGINEERING", "1662", "bio medical biomedical", "engineering"),
            new Department("BIO TECHNOLOGY ENGINEERING", "1890", "bio technology biotechnology", "engineering"),
            new Department("CHEMICAL ENGINEERING", "2447", "chemical chemistry", "engineering"),
            new Department("CIVIL ENGINEERING", "2301", "civil construction", "engineering"),
            new Department("ELECTRICAL & ELECTRONICS ENGINEERING", "205", "electrical electronics eee", "engineering"),
            new Department("ELECTRONICS & COMMUNICATION ENGINEERING", "204", "electronics communication ece", "engineering"),
            new Department("FOOD TECHNOLOGY ENGINEERING", "2297", "food technology", "engineering"),
            new Department("INFORMATION TECHNOLOGY ENGINEERING", "2544", "information technology it", "engineering"),
            new Department("MECHANICAL ENGINEERING", "1996", "mechanical mech", "engineering"),
            new Department("PETROLEUM ENGINEERING", "2695", "petroleum oil", "engineering"),
            new Department("ROBOTICS & AUTOMATION ENGINEERING", "6521", "robotics automation robot", "engineering"),
            new Department("TEXTILE ENGINEERING", "2166", "textile cloth fabric", "engineering"),
            new Department("COMPUTER SCIENCE ENGINEERING", "202", "computer science cse cs", "computer_science"),
            new Department("CSE - DATA SCIENCE", "6615", "data science cse ds", "computer_science"),
            new Department("CSE - AI & ML", "4297", "artificial intelligence machine learning ai ml cse", "computer_science"),
            new Department("CSE - CS", "4374", "computer science cse cs", "computer_science"),
            new Department("CSE - CS - BS", "4375", "computer science cse cs bs", "computer_science"),
            new Department("BCA", "5615", "bca computer applications", "computer_science"),
            new Department("MCA", "5161", "mca computer applications master", "computer_science"),
            new Department("BBA", "5528", "bba business administration bachelor", "business"),
            new Department("MBA", "5094", "mba business administration master", "business"),
            new Department("BA - LLB", "5299", "ba llb law bachelor arts", "law"),
            new Department("BBA - LLB", "5470", "bba llb law business administration", "law"),
            new Department("BSC", "5927", "bsc science bachelor", "science"),
            new Department("AGRICULTURE - BSC", "5738", "agriculture bsc science", "science"),
            new Department("MSC - CHEMISTRY", "5213", "msc chemistry master science", "science"),
            new Department("B - PHARMACY", "3515", "pharmacy pharmaceutical bachelor", "pharmacy")
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pyqp, container, false);
        initializeViews(view);
        setupDepartmentCards(view);
        setupSearch();
        return view;
    }

    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);

        engineeringHeader = view.findViewById(R.id.engineeringHeader);
        computerScienceHeader = view.findViewById(R.id.computerScienceHeader);
        businessHeader = view.findViewById(R.id.businessHeader);
        lawHeader = view.findViewById(R.id.lawHeader);
        scienceHeader = view.findViewById(R.id.scienceHeader);
        pharmacyHeader = view.findViewById(R.id.pharmacyHeader);

        allCards = new ArrayList<>();
        allDepartments = new ArrayList<>();
        for (Department dept : departments) {
            allDepartments.add(dept);
        }
    }

    private void setupDepartmentCards(View view) {
        // Loop through departments and find card by ID safely
        for (int i = 0; i < departments.length; i++) {
            Department dept = departments[i];
            int cardId = getResources().getIdentifier("card" + dept.name.replaceAll("[^A-Za-z0-9]", ""), "id", requireContext().getPackageName());
            if (cardId != 0) {
                CardView card = view.findViewById(cardId);
                if (card != null) {
                    allCards.add(card);
                    int finalI = i;
                    card.setOnClickListener(v -> openDepartmentUrl(finalI));
                }
            }
        }
    }

    private void setupSearch() {
        if (searchEditText == null) return;

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDepartments(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterDepartments(String query) {
        String searchQuery = query.toLowerCase().trim();

        // Hide all cards first
        for (CardView card : allCards) {
            if (card != null) card.setVisibility(View.GONE);
        }

        boolean hasEngineering = false, hasCS = false, hasBusiness = false, hasLaw = false, hasScience = false, hasPharmacy = false;

        if (!searchQuery.isEmpty()) {
            for (int i = 0; i < allDepartments.size() && i < allCards.size(); i++) {
                Department dept = allDepartments.get(i);
                CardView card = allCards.get(i);
                if (dept != null && card != null &&
                        (dept.searchKeywords.contains(searchQuery) || dept.name.toLowerCase().contains(searchQuery))) {
                    card.setVisibility(View.VISIBLE);
                    switch (dept.section) {
                        case "engineering": hasEngineering = true; break;
                        case "computer_science": hasCS = true; break;
                        case "business": hasBusiness = true; break;
                        case "law": hasLaw = true; break;
                        case "science": hasScience = true; break;
                        case "pharmacy": hasPharmacy = true; break;
                    }
                }
            }
        } else {
            for (CardView card : allCards) {
                if (card != null) card.setVisibility(View.VISIBLE);
            }
            hasEngineering = hasCS = hasBusiness = hasLaw = hasScience = hasPharmacy = true;
        }

        // Update header visibility safely
        if (engineeringHeader != null) engineeringHeader.setVisibility(hasEngineering ? View.VISIBLE : View.GONE);
        if (computerScienceHeader != null) computerScienceHeader.setVisibility(hasCS ? View.VISIBLE : View.GONE);
        if (businessHeader != null) businessHeader.setVisibility(hasBusiness ? View.VISIBLE : View.GONE);
        if (lawHeader != null) lawHeader.setVisibility(hasLaw ? View.VISIBLE : View.GONE);
        if (scienceHeader != null) scienceHeader.setVisibility(hasScience ? View.VISIBLE : View.GONE);
        if (pharmacyHeader != null) pharmacyHeader.setVisibility(hasPharmacy ? View.VISIBLE : View.GONE);
    }

    private void openDepartmentUrl(int departmentIndex) {
        if (departmentIndex < 0 || departmentIndex >= departments.length) return;

        try {
            Department department = departments[departmentIndex];
            if (department == null) return;

            String url = BASE_URL + department.urlId;
            Uri uri = Uri.parse(url);
            if (uri.getScheme() == null) uri = Uri.parse("http://" + url);

            if (isAdded()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(Intent.createChooser(intent, "Open with"));
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "No browser found to open the link", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            if (isAdded()) {
                Toast.makeText(requireContext(),
                        "Error opening question papers", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
