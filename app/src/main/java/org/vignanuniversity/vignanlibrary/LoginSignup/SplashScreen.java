package org.vignanuniversity.vignanlibrary.LoginSignup;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import org.vignanuniversity.vignanlibrary.R;

public class SplashScreen extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    private static final String ORG_BASE_URL = "http://192.168.10.18/";
    private static final String PUBLIC_BASE_URL = "http://14.139.85.171/";
    public static String baseURL;
    private Request_Location requestLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        lottieAnimationView = findViewById(R.id.begin);

        requestLocation = new Request_Location(this, this);

//        if (isLocationPermissionGranted()) {
        startLottieAndProceed();
//        } else {
//            requestLocation.requestLocationPermission();
//        }
//
//        baseURL = detectNetworkAndSetBaseUrl(this);

    }

    private void startLottieAndProceed() {
        lottieAnimationView.setSpeed(1.5f); // play 1.5x faster
        lottieAnimationView.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);
    }

    private String detectNetworkAndSetBaseUrl(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return PUBLIC_BASE_URL;

        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) return PUBLIC_BASE_URL;

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (capabilities == null) return PUBLIC_BASE_URL;

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            // Check if connected to organization's Wi-Fi
            return internetIsConnected() ? ORG_BASE_URL : PUBLIC_BASE_URL;
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return PUBLIC_BASE_URL;
        }

        return PUBLIC_BASE_URL;
    }
    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 192.168.10.1";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionGranted = requestLocation.onRequestPermissionsResult(requestCode, grantResults);
        if (permissionGranted) {
            startLottieAndProceed();
        } else {
            Toast.makeText(this, "Permission denied. Cannot proceed.", Toast.LENGTH_LONG).show();
        }
    }

}