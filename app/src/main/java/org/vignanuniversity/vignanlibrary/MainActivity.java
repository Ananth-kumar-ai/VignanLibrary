package org.vignanuniversity.vignanlibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.vignanuniversity.vignanlibrary.Fragments.BorrowFragment;
import org.vignanuniversity.vignanlibrary.Fragments.CatalogFragment;
import org.vignanuniversity.vignanlibrary.Fragments.HomeFragment;
import org.vignanuniversity.vignanlibrary.LoginSignup.AboutUsActivity;
import org.vignanuniversity.vignanlibrary.LoginSignup.Change_pasword;
import org.vignanuniversity.vignanlibrary.LoginSignup.Feedback;
import org.vignanuniversity.vignanlibrary.LoginSignup.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private static final String SELECTED_TAB_KEY = "selected_tab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Safe status bar color (works across Android versions) - use theme color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_teal));
        }

        // ✅ Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vignan Library");
        }

        // ✅ Setup Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Restore last selected tab or default to Home
        int selectedTabId = savedInstanceState != null
                ? savedInstanceState.getInt(SELECTED_TAB_KEY, R.id.nav_home)
                : R.id.nav_home;

        bottomNavigationView.setSelectedItemId(selectedTabId);
        loadFragment(getFragmentForId(selectedTabId));

        // ✅ Handle navigation selection safely
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            loadFragment(getFragmentForId(id));
            return true;
        });
    }

    private Fragment getFragmentForId(int id) {
        if (id == R.id.nav_home) {
            return new HomeFragment();
        } else if (id == R.id.nav_catalog) {
            return new CatalogFragment();
        } else if (id == R.id.nav_borrow) {
            return new BorrowFragment();
        } else if (id == R.id.nav_profile) {
            return new ProfileFragment();
        }
        return new HomeFragment();
    }

    private void loadFragment(Fragment fragment) {
        if (fragment == null) return;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss(); // ✅ Prevent crash during fast tab switching
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bottomNavigationView != null) {
            outState.putInt(SELECTED_TAB_KEY, bottomNavigationView.getSelectedItemId());
        }
    }

    // ✅ Inflate top-right menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // ✅ Handle toolbar menu clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_change_password) {
            startActivity(new Intent(getApplicationContext(), Change_pasword.class));
            return true;
        } else if (id == R.id.menu_logout) {
            handleLogout();
            return true;
        } else if (id == R.id.menu_about_us) {
            startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
            return true;
        }
        else if (id == R.id.menu_feedback) {  // 👈 add this block
            startActivity(new Intent(getApplicationContext(), Feedback.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ✅ Fully safe logout
    private void handleLogout() {
        clearPreferences("pref");
        clearPreferences("LOGIN_PREFS");
        clearPreferences("CACHE_PREFS");

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearPreferences(String name) {
        SharedPreferences preferences = getSharedPreferences(name, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}
