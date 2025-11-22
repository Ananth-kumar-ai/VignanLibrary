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
    private TextView engineeringHeader, computerScienceHeader, businessHeader,
            lawHeader, scienceHeader, pharmacyHeader;

    private static class Department {
        String name;
        String urlId;
        String searchKeywords;
        String section;
        String xmlId;

        Department(String name, String urlId, String searchKeywords, String section, String xmlId) {
            this.name = name;
            this.urlId = urlId;
            this.searchKeywords = searchKeywords != null ? searchKeywords.toLowerCase() : "";
            this.section = section;
            this.xmlId = xmlId;
        }
    }

    private static class DeptCard {
        Department department;
        CardView card;
        DeptCard(Department d, CardView c) { department = d; card = c; }
    }

    private final Department[] departments = {
            new Department("AGRICULTURE ENGINEERING", "2889", "agriculture engineering agri", "engineering", "cardAgriculture"),
            new Department("AUTOMOBILE ENGINEERING", "2790", "automobile auto mobile car", "engineering", "cardAutomobile"),
            new Department("BIO INFORMATICS ENGINEERING", "1457", "bio informatics bioinformatics", "engineering", "cardBioInformatics"),
            new Department("BIO MEDICAL ENGINEERING", "1662", "bio medical biomedical", "engineering", "cardBioMedical"),
            new Department("BIO TECHNOLOGY ENGINEERING", "1890", "bio technology biotechnology", "engineering", "cardBioTechnology"),
            new Department("CHEMICAL ENGINEERING", "2447", "chemical chemistry", "engineering", "cardChemical"),
            new Department("CIVIL ENGINEERING", "2301", "civil construction", "engineering", "cardCivil"),
            new Department("ELECTRICAL & ELECTRONICS ENGINEERING", "205", "electrical electronics eee", "engineering", "cardElectrical"),
            new Department("ELECTRONICS & COMMUNICATION ENGINEERING", "204", "electronics communication ece", "engineering", "cardElectronics"),
            new Department("FOOD TECHNOLOGY ENGINEERING", "2297", "food technology", "engineering", "cardFoodTech"),
            new Department("INFORMATION TECHNOLOGY ENGINEERING", "2544", "information technology it", "engineering", "cardIT"),
            new Department("MECHANICAL ENGINEERING", "1996", "mechanical mech", "engineering", "cardMechanical"),
            new Department("PETROLEUM ENGINEERING", "2695", "petroleum oil", "engineering", "cardPetroleum"),
            new Department("ROBOTICS & AUTOMATION ENGINEERING", "6521", "robotics automation robot", "engineering", "cardRobotics"),
            new Department("TEXTILE ENGINEERING", "2166", "textile cloth fabric", "engineering", "cardTextile"),
            new Department("COMPUTER SCIENCE ENGINEERING", "202", "computer science cse cs", "computer_science", "cardCSE"),
            new Department("CSE - DATA SCIENCE", "6615", "data science cse ds", "computer_science", "cardCSEDataScience"),
            new Department("CSE - AI & ML", "4297", "artificial intelligence machine learning ai ml cse", "computer_science", "cardCSEAIML"),
            new Department("CSE - CS", "4374", "computer science cse cs", "computer_science", "cardCSECS"),
            new Department("CSE - CS - BS", "4375", "computer science cse cs bs", "computer_science", "cardCSECSBS"),
            new Department("BCA", "5615", "bca computer applications", "computer_science", "cardBCA"),
            new Department("MCA", "5161", "mca computer applications master", "computer_science", "cardMCA"),
            new Department("BBA", "5528", "bba business administration bachelor", "business", "cardBBA"),
            new Department("MBA", "5094", "mba business administration master", "business", "cardMBA"),
            new Department("BA - LLB", "5299", "ba llb law bachelor arts", "law", "cardBALLB"),
            new Department("BBA - LLB", "5470", "bba llb law business administration", "law", "cardBBALLB"),
            new Department("BSC", "5927", "bsc science bachelor", "science", "cardBSC"),
            new Department("AGRICULTURE - BSC", "5738", "agriculture bsc science", "science", "cardAgricultureBSC"),
            new Department("MSC - CHEMISTRY", "5213", "msc chemistry master science", "science", "cardMSCChemistry"),
            new Department("B - PHARMACY", "3515", "pharmacy pharmaceutical bachelor", "pharmacy", "cardBPharmacy")
    };

    private final List<DeptCard> deptCardList = new ArrayList<>();

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
    }

    private void setupDepartmentCards(View view) {
        deptCardList.clear();
        for (Department dept : departments) {
            if (dept.xmlId == null || dept.xmlId.trim().isEmpty()) continue;
            int id = getResources().getIdentifier(dept.xmlId, "id", requireContext().getPackageName());
            if (id == 0) continue;
            View found = view.findViewById(id);
            if (found instanceof CardView) {
                CardView card = (CardView) found;
                deptCardList.add(new DeptCard(dept, card));
                card.setOnClickListener(v -> openDepartmentUrl(dept));
            }
        }
    }

    private void setupSearch() {
        if (searchEditText == null) return;
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDepartments(s.toString());
            }
        });
    }

    private void filterDepartments(String query) {
        String searchQuery = (query == null) ? "" : query.toLowerCase().trim();

        boolean hasEngineering = false, hasCS = false, hasBusiness = false, hasLaw = false, hasScience = false, hasPharmacy = false;

        for (DeptCard dc : deptCardList) {
            if (dc == null || dc.card == null || dc.department == null) continue;
            Department dept = dc.department;
            CardView card = dc.card;
            boolean match;
            if (searchQuery.isEmpty()) {
                match = true;
            } else {
                match = dept.searchKeywords.contains(searchQuery) || dept.name.toLowerCase().contains(searchQuery);
            }
            card.setVisibility(match ? View.VISIBLE : View.GONE);
            if (match) {
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

        if (engineeringHeader != null) engineeringHeader.setVisibility(hasEngineering ? View.VISIBLE : View.GONE);
        if (computerScienceHeader != null) computerScienceHeader.setVisibility(hasCS ? View.VISIBLE : View.GONE);
        if (businessHeader != null) businessHeader.setVisibility(hasBusiness ? View.VISIBLE : View.GONE);
        if (lawHeader != null) lawHeader.setVisibility(hasLaw ? View.VISIBLE : View.GONE);
        if (scienceHeader != null) scienceHeader.setVisibility(hasScience ? View.VISIBLE : View.GONE);
        if (pharmacyHeader != null) pharmacyHeader.setVisibility(hasPharmacy ? View.VISIBLE : View.GONE);
    }

    private void openDepartmentUrl(Department department) {
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
    }
}
