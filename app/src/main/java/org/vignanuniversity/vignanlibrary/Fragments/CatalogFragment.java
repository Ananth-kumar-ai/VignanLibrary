package org.vignanuniversity.vignanlibrary.Fragments;
import android.content.ActivityNotFoundException;
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

    private CardView cardBooks, cardEbooks, cardPYQP, cardWebsiteLink;
    private static final String EBOOKS_URL = "http://160.187.169.16:8080/jspui/handle/123456789/46";
    private static final String LIBRARY_PORTAL_URL = "http://160.187.169.16:8080/jspui/";

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
        cardWebsiteLink = view.findViewById(R.id.cardWebsiteLink);
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

        cardWebsiteLink.setOnClickListener(v -> {
            animateCard(cardWebsiteLink);
            openLibraryPortal();
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
        args.putString("api_url", "http://160.187.169.14/jspapi/library/basedOnaccno.jsp");
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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(EBOOKS_URL));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            if (isAdded()) {
                Toast.makeText(requireContext(),
                        "No browser found. Opening inside app...",
                        Toast.LENGTH_SHORT).show();
                openEBooksInWebView();
            }
        } catch (Exception e) {
            if (isAdded()) {
                Toast.makeText(requireContext(),
                        "Unable to open in browser. Opening inside app...",
                        Toast.LENGTH_SHORT).show();
                openEBooksInWebView();
            }
        }
    }

    private void openEBooksInWebView() {
        EBooksWebViewFragment webViewFragment = EBooksWebViewFragment.newInstance(EBOOKS_URL);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, webViewFragment);
        transaction.addToBackStack("EBooksWebView");
        transaction.commit();
    }

    private void openLibraryPortal() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(LIBRARY_PORTAL_URL));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            if (isAdded()) {
                Toast.makeText(requireContext(),
                        "No browser found. Opening inside app...",
                        Toast.LENGTH_SHORT).show();
                openLibraryPortalInWebView();
            }
        } catch (Exception e) {
            if (isAdded()) {
                Toast.makeText(requireContext(),
                        "Unable to open in browser. Opening inside app...",
                        Toast.LENGTH_SHORT).show();
                openLibraryPortalInWebView();
            }
        }
    }

    private void openLibraryPortalInWebView() {
        EBooksWebViewFragment webViewFragment = EBooksWebViewFragment.newInstance(LIBRARY_PORTAL_URL);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, webViewFragment);
        transaction.addToBackStack("LibraryPortalWebView");
        transaction.commit();
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
}