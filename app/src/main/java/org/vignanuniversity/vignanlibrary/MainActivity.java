package org.vignanuniversity.vignanlibrary;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.vignanuniversity.vignanlibrary.R;

import org.vignanuniversity.vignanlibrary.Fragments.BorrowFragment;
import org.vignanuniversity.vignanlibrary.Fragments.CatalogFragment;
import org.vignanuniversity.vignanlibrary.Fragments.HomeFragment;
import org.vignanuniversity.vignanlibrary.ProfileFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange_toolbar));
        }


        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Vignan Library");
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String title = "";
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                title = "Home";
            } else if (id == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
                title = "Catalog";
            } else if (id == R.id.nav_borrow) {
                selectedFragment = new BorrowFragment();
                title = "Borrow";
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                title = "Profile";
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
            }
            return true;
        });
    }
}