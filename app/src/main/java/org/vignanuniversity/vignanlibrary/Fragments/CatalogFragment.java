package org.vignanuniversity.vignanlibrary.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;
import org.vignanuniversity.vignanlibrary.R;

public class CatalogFragment extends Fragment {

    private CardView cardBooks, cardEbooks, cardPYQP;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        cardBooks = view.findViewById(R.id.cardBooks);
        cardEbooks = view.findViewById(R.id.cardEbooks);
        cardPYQP = view.findViewById(R.id.cardPYQP);
    }

    private void setupClickListeners() {
        cardBooks.setOnClickListener(v -> {
            animateCard(cardBooks);
            navigateToBooksFragment();
        });

        cardEbooks.setOnClickListener(v -> {
            animateCard(cardEbooks);
            openEBooksWebsite();
        });

        cardPYQP.setOnClickListener(v -> {
            animateCard(cardPYQP);
            navigateToPYQPFragment();
        });
    }

    private void animateCard(CardView card) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.95f,
                1.0f, 0.95f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(1);

        card.startAnimation(scaleAnimation);
    }

    private void navigateToBooksFragment() {
        BooksFragment booksFragment = new BooksFragment();

        // Pass API URL to BooksFragment
        Bundle args = new Bundle();
        args.putString("api_url", "http://192.168.10.25/jspapi/library/basedOnaccno.jsp");
        booksFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        transaction.replace(R.id.fragment_container, booksFragment);
        transaction.addToBackStack("BooksFragment");
        transaction.commit();
    }

    private void openEBooksWebsite() {
        try {
            String ebooksUrl = "http://192.168.10.34:8080/jspui/handle/123456789/46";
            if (isAdded()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ebooksUrl));
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(),
                            "No browser available to open E-Books.",
                            Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            // Handle case where no browser is available
            if (getContext() != null) {
                Toast.makeText(getContext(),
                        "Unable to open E-Books. Please check your internet connection.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToPYQPFragment() {
        PYQPFragment pyqpFragment = new PYQPFragment();

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        transaction.replace(R.id.fragment_container, pyqpFragment);
        transaction.addToBackStack("PYQPFragment");
        transaction.commit();
    }

    private void showComingSoonMessage(String feature) {
        if (isAdded()) {
            Toast.makeText(requireContext(),
                    feature + " coming soon!",
                    Toast.LENGTH_SHORT).show();
        }

    }
}