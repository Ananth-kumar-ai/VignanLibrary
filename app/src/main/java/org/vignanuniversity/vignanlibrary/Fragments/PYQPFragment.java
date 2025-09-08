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
import android.widget.LinearLayout;
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

    private static final String BASE_URL = "http://192.168.10.34:8080/jspui/handle/123456789/";

    private EditText searchEditText;

    // Section headers
    private TextView engineeringHeader;
    private TextView computerScienceHeader;
    private TextView businessHeader;
    private TextView lawHeader;
    private TextView scienceHeader;
    private TextView pharmacyHeader;

    // Department data with names and URL IDs
    private static class Department {
        String name;
        String urlId;
        String searchKeywords;
        String section; // Added section categorization

        Department(String name, String urlId, String searchKeywords, String section) {
            this.name = name;
            this.urlId = urlId;
            this.searchKeywords = searchKeywords.toLowerCase();
            this.section = section;
        }
    }

    // Department mapping with updated URLs and sections
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

    private List<CardView> allCards;
    private List<Department> allDepartments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pyqp, container, false);

        initializeViews(view);
        setupDepartmentCards(view);
        setupSearch();

        return view;
    }

    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);

        // Initialize section headers - you'll need to add IDs to these TextView elements in your XML
        engineeringHeader = view.findViewById(R.id.engineeringHeader);
        computerScienceHeader = view.findViewById(R.id.computerScienceHeader);
        businessHeader = view.findViewById(R.id.businessHeader);
        lawHeader = view.findViewById(R.id.lawHeader);
        scienceHeader = view.findViewById(R.id.scienceHeader);
        pharmacyHeader = view.findViewById(R.id.pharmacyHeader);

        allCards = new ArrayList<>();
        allDepartments = new ArrayList<>();

        // Convert array to list for easier searching
        for (Department dept : departments) {
            allDepartments.add(dept);
        }
    }

    private void setupDepartmentCards(View view) {
        // Engineering Cards
        CardView cardAgriculture = view.findViewById(R.id.cardAgriculture);
        CardView cardAutomobile = view.findViewById(R.id.cardAutomobile);
        CardView cardBioInformatics = view.findViewById(R.id.cardBioInformatics);
        CardView cardBioMedical = view.findViewById(R.id.cardBioMedical);
        CardView cardBioTechnology = view.findViewById(R.id.cardBioTechnology);
        CardView cardChemical = view.findViewById(R.id.cardChemical);
        CardView cardCivil = view.findViewById(R.id.cardCivil);
        CardView cardElectrical = view.findViewById(R.id.cardElectrical);
        CardView cardElectronics = view.findViewById(R.id.cardElectronics);
        CardView cardFoodTech = view.findViewById(R.id.cardFoodTech);
        CardView cardIT = view.findViewById(R.id.cardIT);
        CardView cardMechanical = view.findViewById(R.id.cardMechanical);
        CardView cardPetroleum = view.findViewById(R.id.cardPetroleum);
        CardView cardRobotics = view.findViewById(R.id.cardRobotics);
        CardView cardTextile = view.findViewById(R.id.cardTextile);

        // Computer Science Cards
        CardView cardCSE = view.findViewById(R.id.cardCSE);
        CardView cardCSEDataScience = view.findViewById(R.id.cardCSEDataScience);
        CardView cardCSEAIML = view.findViewById(R.id.cardCSEAIML);
        CardView cardCSECS = view.findViewById(R.id.cardCSECS);
        CardView cardCSECSBS = view.findViewById(R.id.cardCSECSBS);
        CardView cardBCA = view.findViewById(R.id.cardBCA);
        CardView cardMCA = view.findViewById(R.id.cardMCA);

        // Business Cards
        CardView cardBBA = view.findViewById(R.id.cardBBA);
        CardView cardMBA = view.findViewById(R.id.cardMBA);

        // Law Cards
        CardView cardBALLB = view.findViewById(R.id.cardBALLB);
        CardView cardBBALLB = view.findViewById(R.id.cardBBALLB);

        // Science Cards
        CardView cardBSC = view.findViewById(R.id.cardBSC);
        CardView cardAgricultureBSC = view.findViewById(R.id.cardAgricultureBSC);
        CardView cardMSCChemistry = view.findViewById(R.id.cardMSCChemistry);

        // Pharmacy Cards
        CardView cardBPharmacy = view.findViewById(R.id.cardBPharmacy);

        // Add all cards to list for search functionality
        allCards.add(cardAgriculture);
        allCards.add(cardAutomobile);
        allCards.add(cardBioInformatics);
        allCards.add(cardBioMedical);
        allCards.add(cardBioTechnology);
        allCards.add(cardChemical);
        allCards.add(cardCivil);
        allCards.add(cardElectrical);
        allCards.add(cardElectronics);
        allCards.add(cardFoodTech);
        allCards.add(cardIT);
        allCards.add(cardMechanical);
        allCards.add(cardPetroleum);
        allCards.add(cardRobotics);
        allCards.add(cardTextile);
        allCards.add(cardCSE);
        allCards.add(cardCSEDataScience);
        allCards.add(cardCSEAIML);
        allCards.add(cardCSECS);
        allCards.add(cardCSECSBS);
        allCards.add(cardBCA);
        allCards.add(cardMCA);
        allCards.add(cardBBA);
        allCards.add(cardMBA);
        allCards.add(cardBALLB);
        allCards.add(cardBBALLB);
        allCards.add(cardBSC);
        allCards.add(cardAgricultureBSC);
        allCards.add(cardMSCChemistry);
        allCards.add(cardBPharmacy);

        // Set click listeners
        cardAgriculture.setOnClickListener(v -> openDepartmentUrl(0));
        cardAutomobile.setOnClickListener(v -> openDepartmentUrl(1));
        cardBioInformatics.setOnClickListener(v -> openDepartmentUrl(2));
        cardBioMedical.setOnClickListener(v -> openDepartmentUrl(3));
        cardBioTechnology.setOnClickListener(v -> openDepartmentUrl(4));
        cardChemical.setOnClickListener(v -> openDepartmentUrl(5));
        cardCivil.setOnClickListener(v -> openDepartmentUrl(6));
        cardElectrical.setOnClickListener(v -> openDepartmentUrl(7));
        cardElectronics.setOnClickListener(v -> openDepartmentUrl(8));
        cardFoodTech.setOnClickListener(v -> openDepartmentUrl(9));
        cardIT.setOnClickListener(v -> openDepartmentUrl(10));
        cardMechanical.setOnClickListener(v -> openDepartmentUrl(11));
        cardPetroleum.setOnClickListener(v -> openDepartmentUrl(12));
        cardRobotics.setOnClickListener(v -> openDepartmentUrl(13));
        cardTextile.setOnClickListener(v -> openDepartmentUrl(14));
        cardCSE.setOnClickListener(v -> openDepartmentUrl(15));
        cardCSEDataScience.setOnClickListener(v -> openDepartmentUrl(16));
        cardCSEAIML.setOnClickListener(v -> openDepartmentUrl(17));
        cardCSECS.setOnClickListener(v -> openDepartmentUrl(18));
        cardCSECSBS.setOnClickListener(v -> openDepartmentUrl(19));
        cardBCA.setOnClickListener(v -> openDepartmentUrl(20));
        cardMCA.setOnClickListener(v -> openDepartmentUrl(21));
        cardBBA.setOnClickListener(v -> openDepartmentUrl(22));
        cardMBA.setOnClickListener(v -> openDepartmentUrl(23));
        cardBALLB.setOnClickListener(v -> openDepartmentUrl(24));
        cardBBALLB.setOnClickListener(v -> openDepartmentUrl(25));
        cardBSC.setOnClickListener(v -> openDepartmentUrl(26));
        cardAgricultureBSC.setOnClickListener(v -> openDepartmentUrl(27));
        cardMSCChemistry.setOnClickListener(v -> openDepartmentUrl(28));
        cardBPharmacy.setOnClickListener(v -> openDepartmentUrl(29));
    }

    private void setupSearch() {
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

        if (searchQuery.isEmpty()) {
            // Show all cards and headers when search is empty
            for (CardView card : allCards) {
                card.setVisibility(View.VISIBLE);
            }
            showAllHeaders();
            return;
        }

        // Hide all cards first
        for (CardView card : allCards) {
            card.setVisibility(View.GONE);
        }

        // Track which sections have visible cards
        boolean hasEngineeringResults = false;
        boolean hasComputerScienceResults = false;
        boolean hasBusinessResults = false;
        boolean hasLawResults = false;
        boolean hasScienceResults = false;
        boolean hasPharmacyResults = false;

        // Show matching cards and track sections
        for (int i = 0; i < allDepartments.size(); i++) {
            Department dept = allDepartments.get(i);
            if (dept.searchKeywords.contains(searchQuery) ||
                    dept.name.toLowerCase().contains(searchQuery)) {
                if (i < allCards.size()) {
                    allCards.get(i).setVisibility(View.VISIBLE);

                    // Track which section has results
                    switch (dept.section) {
                        case "engineering":
                            hasEngineeringResults = true;
                            break;
                        case "computer_science":
                            hasComputerScienceResults = true;
                            break;
                        case "business":
                            hasBusinessResults = true;
                            break;
                        case "law":
                            hasLawResults = true;
                            break;
                        case "science":
                            hasScienceResults = true;
                            break;
                        case "pharmacy":
                            hasPharmacyResults = true;
                            break;
                    }
                }
            }
        }

        // Show/hide section headers based on results
        updateHeaderVisibility(hasEngineeringResults, hasComputerScienceResults,
                hasBusinessResults, hasLawResults, hasScienceResults, hasPharmacyResults);
    }

    private void showAllHeaders() {
        if (engineeringHeader != null) engineeringHeader.setVisibility(View.VISIBLE);
        if (computerScienceHeader != null) computerScienceHeader.setVisibility(View.VISIBLE);
        if (businessHeader != null) businessHeader.setVisibility(View.VISIBLE);
        if (lawHeader != null) lawHeader.setVisibility(View.VISIBLE);
        if (scienceHeader != null) scienceHeader.setVisibility(View.VISIBLE);
        if (pharmacyHeader != null) pharmacyHeader.setVisibility(View.VISIBLE);
    }

    private void updateHeaderVisibility(boolean hasEngineering, boolean hasComputerScience,
                                        boolean hasBusiness, boolean hasLaw,
                                        boolean hasScience, boolean hasPharmacy) {
        if (engineeringHeader != null) {
            engineeringHeader.setVisibility(hasEngineering ? View.VISIBLE : View.GONE);
        }
        if (computerScienceHeader != null) {
            computerScienceHeader.setVisibility(hasComputerScience ? View.VISIBLE : View.GONE);
        }
        if (businessHeader != null) {
            businessHeader.setVisibility(hasBusiness ? View.VISIBLE : View.GONE);
        }
        if (lawHeader != null) {
            lawHeader.setVisibility(hasLaw ? View.VISIBLE : View.GONE);
        }
        if (scienceHeader != null) {
            scienceHeader.setVisibility(hasScience ? View.VISIBLE : View.GONE);
        }
        if (pharmacyHeader != null) {
            pharmacyHeader.setVisibility(hasPharmacy ? View.VISIBLE : View.GONE);
        }
    }

    private void openDepartmentUrl(int departmentIndex) {
        try {
            if (departmentIndex >= 0 && departmentIndex < departments.length) {
                Department department = departments[departmentIndex];
                String url = BASE_URL + department.urlId;

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                // Check if there's an app that can handle this intent
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Show error message if no browser is available
                    Toast.makeText(getContext(), "No browser found to open the link", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            // Handle any errors
            String deptName = departmentIndex < departments.length ? departments[departmentIndex].name : "Department";
            Toast.makeText(getContext(), "Error opening " + deptName + " question papers", Toast.LENGTH_SHORT).show();
        }
    }
}