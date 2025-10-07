package org.vignanuniversity.vignanlibrary.Fragments;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import org.vignanuniversity.vignanlibrary.R;

public class EBooksWebViewFragment extends Fragment {

    private static final String ARG_URL = "ebooks_url";
    private String ebooksUrl;

    public static EBooksWebViewFragment newInstance(String url) {
        EBooksWebViewFragment fragment = new EBooksWebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_webview, container, false);

        WebView webView = root.findViewById(R.id.webView);
        ProgressBar progressBar = root.findViewById(R.id.progressBar);

        if (getArguments() != null) {
            ebooksUrl = getArguments().getString(ARG_URL);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.loadUrl(ebooksUrl);

        return root;
    }
}
