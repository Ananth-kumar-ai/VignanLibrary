package org.vignanuniversity.vignanlibrary.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.vignanuniversity.vignanlibrary.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    // Declare CardViews
    private CardView headerCard, totalBooksCard, eBooksCard, audioBooksCard;
    private CardView eSoundsCard, thesisCard, bookVolumesCard;
    private CardView resourcesCard, repository1Card, repository2Card;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate fragment_home.xml (your layout file)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize CardViews
        initializeViews(view);

        // Set Click Listeners
        setClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        // Initialize all CardViews with their respective IDs from the XML
        headerCard = view.findViewById(R.id.headerCard);
        totalBooksCard = view.findViewById(R.id.totalBooksCard);
        eBooksCard = view.findViewById(R.id.eBooksCard);
        audioBooksCard = view.findViewById(R.id.audioBooksCard);
        eSoundsCard = view.findViewById(R.id.eSoundsCard);
        thesisCard = view.findViewById(R.id.thesisCard);
        bookVolumesCard = view.findViewById(R.id.bookVolumesCard);
        resourcesCard = view.findViewById(R.id.resourcesCard);
        repository1Card = view.findViewById(R.id.repository1Card);
        repository2Card = view.findViewById(R.id.repository2Card);
    }

    private void setClickListeners() {
        // Set click listeners for all CardViews with null checks
        if (headerCard != null) {
            headerCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Add Photo clicked", Toast.LENGTH_SHORT).show();
                // Add photo selection logic here
            });
        }

        if (totalBooksCard != null) {
            totalBooksCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Total Books: 15,247", Toast.LENGTH_SHORT).show();
                // Navigate to total books screen
                navigateToTotalBooks();
            });
        }

        if (eBooksCard != null) {
            eBooksCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "E-Books: 8,392", Toast.LENGTH_SHORT).show();
                // Navigate to e-books screen
                navigateToEBooks();
            });
        }

        if (audioBooksCard != null) {
            audioBooksCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Audio Books: 1,247", Toast.LENGTH_SHORT).show();
                // Navigate to audio books screen
                navigateToAudioBooks();
            });
        }

        if (eSoundsCard != null) {
            eSoundsCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "E-Sounds: 3,456", Toast.LENGTH_SHORT).show();
                // Navigate to e-sounds screen
                navigateToESounds();
            });
        }

        if (thesisCard != null) {
            thesisCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Thesis Count: 892", Toast.LENGTH_SHORT).show();
                // Navigate to thesis screen
                navigateToThesis();
            });
        }

        if (bookVolumesCard != null) {
            bookVolumesCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Book Volumes: 24,156", Toast.LENGTH_SHORT).show();
                // Navigate to book volumes screen
                navigateToBookVolumes();
            });
        }

        if (resourcesCard != null) {
            resourcesCard.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Resources: 156", Toast.LENGTH_SHORT).show();
                // Navigate to resources screen
                navigateToResources();
            });
        }

        if (repository1Card != null) {
            repository1Card.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Repository 1: 4,823", Toast.LENGTH_SHORT).show();
                // Navigate to repository 1 screen
                navigateToRepository1();
            });
        }

        if (repository2Card != null) {
            repository2Card.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Repository 2: 2,967", Toast.LENGTH_SHORT).show();
                // Navigate to repository 2 screen
                navigateToRepository2();
            });
        }
    }

    // Navigation methods - implement these based on your app's navigation structure
    private void navigateToTotalBooks() {
        // Example: Navigate to TotalBooksFragment or Activity
        // You can implement navigation using FragmentManager or Navigation Component
    }

    private void navigateToEBooks() {
        // Example: Navigate to EBooksFragment or Activity
    }

    private void navigateToAudioBooks() {
        // Example: Navigate to AudioBooksFragment or Activity
    }

    private void navigateToESounds() {
        // Example: Navigate to ESoundsFragment or Activity
    }

    private void navigateToThesis() {
        // Example: Navigate to ThesisFragment or Activity
    }

    private void navigateToBookVolumes() {
        // Example: Navigate to BookVolumesFragment or Activity
    }

    private void navigateToResources() {
        // Example: Navigate to ResourcesFragment or Activity
    }

    private void navigateToRepository1() {
        // Example: Navigate to Repository1Fragment or Activity
    }

    private void navigateToRepository2() {
        // Example: Navigate to Repository2Fragment or Activity
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data if needed when fragment becomes visible
        refreshDashboardData();
    }

    private void refreshDashboardData() {
        // Method to refresh dashboard statistics
        // You can call your API or database methods here
    }
}