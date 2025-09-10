package org.vignanuniversity.vignanlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
            Toast.makeText(this, "Change Password clicked", Toast.LENGTH_SHORT).show();
            // You can start a new activity for change password
            // Intent intent = new Intent(this, ChangePasswordActivity.class);
            // startActivity(intent);
            return true;

        } else if (id == R.id.menu_privacy_policy) {
            // Handle Privacy Policy
            Toast.makeText(this, "Privacy & Policy clicked", Toast.LENGTH_SHORT).show();
            // You can start a new activity or show a dialog
            // Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            // startActivity(intent);
            return true;

        } else if (id == R.id.menu_about_us) {
            // Handle About Us
            Toast.makeText(this, "About Us clicked", Toast.LENGTH_SHORT).show();
            // You can start a new activity or show a dialog
            showAboutUsDialog();
            return true;

        } else if (id == R.id.menu_feedback) {
            // Handle Feedback
            Toast.makeText(this, "Feedback clicked", Toast.LENGTH_SHORT).show();
            // You can start a new activity for feedback
            // Intent intent = new Intent(this, FeedbackActivity.class);
            // startActivity(intent);
            return true;

        } else if (id == R.id.menu_logout) {
            // Handle Logout
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutUsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Us")
                .setMessage("Vignan Library App\nVersion 1.0\n\nDeveloped for Vignan University\nLibrary Management System")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Handle logout logic here
                    // Clear user session, preferences, etc.
                    Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to login screen
                    // Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // startActivity(intent);
                    // finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}