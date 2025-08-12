package org.vignanuniversity.vignanlibrary.LoginSignup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Request_Location {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private final Context context;
    private final Activity activity;

    public Request_Location(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    // Request Location Permission
    public void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getConnectedWifiSSID();  // If already granted, fetch SSID
        }
    }

    // Handle Permission Result and return if permission was granted
    public boolean onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getConnectedWifiSSID();
                return true;  // Permission granted
            } else {
                Toast.makeText(context, "Location permission is required to get Wi-Fi SSID",
                        Toast.LENGTH_LONG).show();
                return false;  // Permission denied
            }
        }
        return false;
    }

    // Fetch the connected Wi-Fi SSID
    public void getConnectedWifiSSID() {
        WifiManager wifiManager =
                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) {
            Toast.makeText(context, "Wi-Fi Manager not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(context, "Wi-Fi is disabled. Please enable it.", Toast.LENGTH_SHORT).show();
            return;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();

        if (ssid != null) {
            ssid = ssid.replace("\"", "");  // Remove quotes from SSID
            Log.d("Request_Location", "Connected to Wi-Fi: " + ssid);
//            Toast.makeText(context, "Connected to: " + ssid, Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(context, "Not connected to any Wi-Fi", Toast.LENGTH_SHORT).show();
        }
    }
}
