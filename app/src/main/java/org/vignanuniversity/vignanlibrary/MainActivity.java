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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_change_password) {
            // Handle Change Password
            startActivity(new Intent(getApplicationContext(), Change_pasword.class));
            return true;
        }
        else if (id == R.id.menu_logout) {
            // Handle Logout
            handleLogout();
            return true;
        } else if (id == R.id.menu_about_us) {
            startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        // Clear the main SharedPreferences used by ProfileFragment
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Clear LOGIN_PREFS if you're using it elsewhere
        SharedPreferences loginPrefs = getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPrefs.edit();
        loginEditor.clear();
        loginEditor.apply();

        // Clear CACHE_PREFS
        SharedPreferences cachePrefs = getSharedPreferences("CACHE_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor cacheEditor = cachePrefs.edit();
        cacheEditor.clear();
        cacheEditor.apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}