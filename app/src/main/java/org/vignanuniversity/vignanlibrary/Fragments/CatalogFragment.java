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
import org.vignanuniversity.vignanlibrary.Fragments.EBooksWebViewFragment;

import org.vignanuniversity.vignanlibrary.R;
public class CatalogFragment extends Fragment {

    private CardView cardBooks, cardEbooks, cardPYQP, cardWebsiteLink;
    private static final String LIBRARY_PORTAL_URL = "http://160.187.169.16:8080/jspui/";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        cardBooks = view.findViewById(R.id.cardBooks);
        cardEbooks = view.findViewById(R.id.cardEbooks);
        cardPYQP = view.findViewById(R.id.cardPYQP);
        cardWebsiteLink = view.findViewById(R.id.cardWebsiteLink);
    }

    private void setupClickListeners() {

        // BOOKS
        cardBooks.setOnClickListener(v -> {
            animateCard(cardBooks);
            navigateToBooksFragment();
        });

        // E-BOOKS → Dynamic Fragment
        cardEbooks.setOnClickListener(v -> {
            animateCard(cardEbooks);
            navigateToEbooksFragment();
        });

        // PYQP
        cardPYQP.setOnClickListener(v -> {
            animateCard(cardPYQP);
            navigateToPYQPFragment();
        });

        // Portal
        cardWebsiteLink.setOnClickListener(v -> {
            animateCard(cardWebsiteLink);
            openPortalWebView();
        });
    }

    private void animateCard(CardView card) {
        ScaleAnimation animation = new ScaleAnimation(
                1.0f, 0.95f,
                1.0f, 0.95f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setDuration(100);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        card.startAnimation(animation);
    }

    private void navigateToBooksFragment() {
        BooksFragment fragment = new BooksFragment();

        Bundle args = new Bundle();
        args.putString("api_url", "http://160.187.169.13/Vignan_Library_app/book_details.jsp");
        fragment.setArguments(args);

        replaceFragment(fragment, "BooksFragment");
    }

    private void navigateToEbooksFragment() {
        EbooksFragment fragment = new EbooksFragment();
        replaceFragment(fragment, "EbooksFragment");
    }

    private void navigateToPYQPFragment() {
        PYQPFragment fragment = new PYQPFragment();
        replaceFragment(fragment, "PYQPFragment");
    }

    private void openPortalWebView() {
        EBooksWebViewFragment fragment =
                EBooksWebViewFragment.newInstance(LIBRARY_PORTAL_URL);

        replaceFragment(fragment, "Portal");
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}
