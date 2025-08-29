package org.vignanuniversity.vignanlibrary.Fragments;

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
            showComingSoonMessage("E-Books");
        });

        cardPYQP.setOnClickListener(v -> {
            animateCard(cardPYQP);
            showComingSoonMessage("Previous Year Question Papers");
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
        transaction.replace(R.id.fragment_container, booksFragment); // Make sure this matches your container ID
        transaction.addToBackStack("BooksFragment");
        transaction.commit();
    }

    private void showComingSoonMessage(String feature) {
        if (getContext() != null) {
            Toast.makeText(getContext(),
                    feature + " coming soon!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}