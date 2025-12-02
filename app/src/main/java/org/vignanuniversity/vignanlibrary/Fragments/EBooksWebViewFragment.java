package org.vignanuniversity.vignanlibrary.Fragments;

import android.graphics.Bitmap;
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
import android.widget.Toast;

import org.vignanuniversity.vignanlibrary.R;

public class EBooksWebViewFragment extends Fragment {

    private static final String ARG_URL = "web_url";
    private String webUrl;

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
            webUrl = getArguments().getString(ARG_URL, "");
        }

        if (webUrl == null || webUrl.isEmpty()) {
            Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
            return root;
        }

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "Failed to load page",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl(webUrl);

        return root;
    }
}
