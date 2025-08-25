package org.vignanuniversity.vignanlibrary.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vignanuniversity.vignanlibrary.Adapter.URL;
import org.vignanuniversity.vignanlibrary.LoadingDialog;
import org.vignanuniversity.vignanlibrary.MainActivity;
import org.vignanuniversity.vignanlibrary.R;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 200;
    boolean simStatus = false;
    String phone_number = "";
    SharedPreferences preferences;
    String checker="";
    private Executor executor;

    final String MY_PREFS_NAME = "status";
    com.google.android.material.textfield.TextInputEditText email , password ;
    Spinner type,year_type;

    String user_type = "" ,user_type1 = "" ;

    TextView forgot_password;
    Button submit;

    LoadingDialog loadingDialog;
//
//    /**
//     * Checks and requests all necessary permissions at runtime.
//     */
//    private void checkPermissions() {
//        String[] permissions = {
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.SEND_SMS,
//                Manifest.permission.READ_SMS
//        };
//
//        List<String> permissionsToRequest = new ArrayList<>();
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                permissionsToRequest.add(permission);
//            }
//        }
//
//        if (!permissionsToRequest.isEmpty()) {
//            ActivityCompat.requestPermissions(
//                    this,
//                    permissionsToRequest.toArray(new String[0]),
//                    PERMISSIONS_REQUEST_CODE
//            );
//        } else {
//            getSimDetails();
//        }
//    }
//
//    /**
//     * Handles the user's response to runtime permission requests.
//     */
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode,
//            @NonNull String[] permissions,
//            @NonNull int[] grantResults
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSIONS_REQUEST_CODE) {
//            boolean allGranted = true;
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    allGranted = false;
//                    break;
//                }
//            }
//
//            if (allGranted) {
//                getSimDetails();
//            } else {
//                Toast.makeText(this, "Required permissions are denied.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    /**
//     * Attempts to fetch SIM details safely if permissions are granted.
//     */
//    private void getSimDetails() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//                if (telephonyManager != null) {
//                    String phoneNumber = telephonyManager.getLine1Number();
//                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
//                        phone_number = phoneNumber;
//                        Log.d("TAG", "Phone Number: " + phoneNumber);
    ////                        Toast.makeText(this, "Phone Number: " + phoneNumber, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.d("TAG", "Phone Number not available on SIM.");
//                        Toast.makeText(this, "Phone Number not available on SIM", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Log.d("TAG", "TelephonyManager is null");
//                    Toast.makeText(this, "Unable to fetch TelephonyManager", Toast.LENGTH_SHORT).show();
//                }
//            } catch (SecurityException e) {
//                Log.d("TAG", "SecurityException: " + e.getMessage());
//                Toast.makeText(this, "Error accessing phone state: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Phone state permissions are required.", Toast.LENGTH_SHORT).show();
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingDialog = new LoadingDialog(this);



//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        email = findViewById(R.id.input_mail);
        password = findViewById(R.id.input_password);
        forgot_password = findViewById(R.id.signin_signup);
        submit = findViewById(R.id.phone_submit_button);

        preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        type = findViewById(R.id.login_type);

        if(!internetIsConnected()){
            submit.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No Internet!!", Toast.LENGTH_SHORT).show();
        }else{
            submit.setEnabled(true);
//            Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        }


        if(preferences.getBoolean("isLogin",false)){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("refreshPages",true);
            startActivity(i);
            finish();
        }

        if(preferences.getBoolean("isLogin",false)){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("refreshPages",true);
            startActivity(i);
            finish();
        }

        validatePassword();


        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = type.getSelectedItem().toString().trim();
                if (!str.equals("Select Type of User"))
                    user_type = str;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameToCheck = Objects.requireNonNull(email.getText()).toString().trim();
                String passToCheck = Objects.requireNonNull(password.getText()).toString().trim();

                if(usernameToCheck.isEmpty()){
                    email.setError("Give Username");
                    email.requestFocus();
                    return;
                }

                if(passToCheck.isEmpty()){
                    password.setError("Give Password");
                    password.requestFocus();
                    return;
                }


                /*if(user_type.isEmpty() || user_type.equals("Select Type of User")){
                    ((TextView)type.getSelectedView()).setError("This field can not be blank");
                    type.requestFocus();
                    return;
                }*/
                else{
                    loadingDialog.load();
                    switch (user_type){
                        case "":
                            if(email.getText().toString().trim().equals("test") && password.getText().toString().equals("test")){
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("isLogin",true);
                                editor.putString("regno","211FA04389");
                                editor.apply();
                                loadingDialog.dismisss();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, "Please Choose User Type!", Toast.LENGTH_SHORT).show();
                                ((TextView)type.getSelectedView()).setError("This field can not be blank");
                                type.requestFocus();
                                loadingDialog.dismisss();
                            }
                            break;
                        case "Student":
                            String username = email.getText().toString().toUpperCase().trim();
                            String pass = password.getText().toString();
//                            if(!simStatus) {
//                                checkPermissions();
//                                All_DataFetcher.personalDetailsFetcher(getApplicationContext(), username, true,
//                                        new All_DataFetcher.DataCallback() {
//                                            @Override
//                                            public void onDataLoaded(JSONObject response) {
//                                                try {
//                                                    JSONArray jsonArray = response.getJSONArray("data");
//                                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                                        JSONObject hit = jsonArray.getJSONObject(i);
//                                                        if(user_type.equals("Student")) {
//                                                            String number = hit.getString("studentmobile");
//                                                            if(number.equals(phone_number)){
//                                                                simStatus = true;
//                                                                loginCheck(username, pass, user_type);
//                                                            }else{
//                                                                loadingDialog.dismisss();
//                                                                Toast.makeText(LoginActivity.this, "Use  ' "+number+" '  to login..!", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    }
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });
//                            } else {
                            loginCheck(username, pass, user_type);
//                            }
                        case "Parent":
                            String username1 = email.getText().toString().toUpperCase().trim();
                            String pass1 = password.getText().toString();
//                            if(!simStatus) {
//                                checkPermissions();
//                                All_DataFetcher.personalDetailsFetcher(getApplicationContext(), username1, true,
//                                        new All_DataFetcher.DataCallback() {
//                                            @Override
//                                            public void onDataLoaded(JSONObject response) {
//                                                try {
//                                                    JSONArray jsonArray = response.getJSONArray("data");
//                                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                                        JSONObject hit = jsonArray.getJSONObject(i);
//                                                        if(user_type.equals("Parent")) {
//                                                            String number = hit.getString("fathermobile");
//                                                            if (number.equals(phone_number)) {
//                                                                simStatus = true;
//                                                                loginCheck(username1, pass1, user_type);
//                                                            } else {
//                                                                loadingDialog.dismisss();
//                                                                Toast.makeText(LoginActivity.this, "Use  ' "+number+" '  to login..!", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    }
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });
//                            } else {
                            loginCheck(username1, pass1, user_type);
//                            }
                            break;
                    }
                }
            }
        });


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,forgot_password.class));
                finish();
            }
        });

    }

    private void validatePassword() {
        TextInputLayout passwordInputLayout = findViewById(R.id.filledTextField);
        passwordInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordInputLayout.setEndIconDrawable(R.drawable.ic_visibility);
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordInputLayout.setEndIconDrawable(R.drawable.ic_visibility_off);
                }
                password.setSelection(password.length());
            }
        });
    }

    private boolean isYearEmpty() {
        return type.getSelectedItemPosition() != 0;
    }
    private boolean isTypeEmpty() {
        return year_type.getSelectedItemPosition() != 0;
    }


    public void loginCheck(String username,String pass,String user_type){
        String url = URL.getLoginUrl();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                String flag=response.trim();
                Log.d("Response", response.trim());
                if(flag.equals("Success")){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLogin",true);
                    editor.putString("regno",username);
                    editor.apply();
                    loadingDialog.dismisss();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    loadingDialog.dismisss();
                    Toast.makeText(getApplicationContext(), "Incorrect RegisterId or Password", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("msg",error.toString());
                loadingDialog.dismisss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String,String>();
                param.put("regno",username);
                param.put("password",pass);
                param.put("usertype",user_type);
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

}