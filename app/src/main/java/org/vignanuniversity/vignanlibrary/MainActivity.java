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
import org.vignanuniversity.vignanlibrary.LoginSignup.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#CC6442"));
        }

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Keep static title always
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vignan Library");
        }

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Handle bottom nav item clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
            } else if (id == R.id.nav_borrow) {
                selectedFragment = new BorrowFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
        return super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        // Clear SharedPreferences
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

        SharedPreferences loginPrefs = getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);
        loginPrefs.edit().clear().apply();

        SharedPreferences cachePrefs = getSharedPreferences("CACHE_PREFS", Context.MODE_PRIVATE);
        cachePrefs.edit().clear().apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
