package org.vignanuniversity.vignanlibrary.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import org.vignanuniversity.vignanlibrary.R;

public class HomeFragment extends Fragment {

    private HorizontalScrollView marqueeScroll;
    private LinearLayout marqueeContent;
    private Handler handler = new Handler();
    private int scrollSpeed = 5; // pixels per step
    private int delay = 30; // ms

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        marqueeScroll = view.findViewById(R.id.marqueeScroll);
        marqueeContent = view.findViewById(R.id.marqueeContent);

        startMarquee();

        return view;
    }

    private void startMarquee() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (marqueeScroll != null) {
                    int scrollX = marqueeScroll.getScrollX() + scrollSpeed;
                    int maxScroll = marqueeContent.getWidth() - marqueeScroll.getWidth();

                    if (scrollX >= maxScroll) {
                        // restart from beginning
                        scrollX = 0;
                    }

                    marqueeScroll.scrollTo(scrollX, 0);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}
