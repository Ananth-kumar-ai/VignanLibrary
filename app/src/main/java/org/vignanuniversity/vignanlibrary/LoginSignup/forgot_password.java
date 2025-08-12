package org.vignanuniversity.vignanlibrary.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import org.vignanuniversity.vignanlibrary.Adapter.URL;
import org.vignanuniversity.vignanlibrary.LoadingDialog;
import org.vignanuniversity.vignanlibrary.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class forgot_password extends AppCompatActivity {

    LinearLayout otp_sending,update_password;
    Button send_otp,validate_otp;
    com.google.android.material.textfield.TextInputEditText f_regno , f_New_password ,f_otp;
    Spinner type;
    String otp="";
    LoadingDialog loadingDialog;
    SharedPreferences preferences;

    String user_type = "" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        loadingDialog = new LoadingDialog(this);

        preferences = getSharedPreferences("pref",MODE_PRIVATE);

        //Input Text
        f_regno = findViewById(R.id.forgot_reg_number);
        f_New_password = findViewById(R.id.new_pass);
        f_otp = findViewById(R.id.otp_validation);

        preferences = getSharedPreferences("pref",MODE_PRIVATE);
        type = findViewById(R.id.login_type);

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


        send_otp = findViewById(R.id.submit_button);
        validate_otp = findViewById(R.id.pass_update);

        otp_sending = findViewById(R.id.otp_sending);
        update_password = findViewById(R.id.update_pass);

        send_otp.setOnClickListener(View ->{
//            otp_sending.setVisibility(View.GONE);
//            update_password.setVisibility(View.VISIBLE);

            String usernameToCheck = Objects.requireNonNull(f_regno.getText()).toString().toUpperCase(Locale.ROOT).trim();

            if(usernameToCheck.isEmpty()){
                f_regno.setError("Give Username");
                f_regno.requestFocus();
                return;
            }
            if(user_type.isEmpty() || user_type.equals("Select Type of User")){
                ((TextView)type.getSelectedView()).setError("This field can not be blank");
                type.requestFocus();
                return;
            }else{
                switch (user_type){
                    case "Student":
                    case "Parent":
                        loadingDialog.load();
                        String username = f_regno.getText().toString().toUpperCase().trim();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("f_regno",username);
                        editor.apply();
                        otp(username, user_type);
                        break;
                }
            }
        });

        validate_otp.setOnClickListener(View ->{
            String otpToCheck = Objects.requireNonNull(f_otp.getText()).toString().toUpperCase(Locale.ROOT).trim();
            String newPassword = Objects.requireNonNull(f_New_password.getText()).toString().trim();
            if(otp.isEmpty()){
                f_otp.setError("Give Username");
                f_otp.requestFocus();
                return;
            }if(newPassword.isEmpty()){
                f_New_password.setError("Give Password");
                f_New_password.requestFocus();
                return;
            }else{
                loadingDialog.load();
                String otp = f_otp.getText().toString().toUpperCase(Locale.ROOT).trim();
                String new_pass = f_New_password.getText().toString().trim();
                preferences = getSharedPreferences("pref",MODE_PRIVATE);
                updatePassword(preferences.getString("f_regno",""),otp,new_pass);
            }

        });
    }

    private void updatePassword(String reg_no,String otp_check, String new_pass) {
        String url = URL.getOtp_verifiUrl();
        if(otp.isEmpty())
            Toast.makeText(getApplicationContext(),"Generate a OTP",Toast.LENGTH_SHORT).show();
        else {
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.trim().equals("Success")) {
                        loadingDialog.dismisss();
                        Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), response.trim(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("msg", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<String, String>();
                    param.put("regno",reg_no);
                    param.put("otp", otp);
                    param.put("new_otp",otp_check);
                    param.put("new_pass", new_pass);
                    param.put("usertype", user_type);
                    return param;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);
        }
    }

    private void otp(String username,String user_type) {
        String url = URL.getForgotPassUrl();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String flag=response.trim();
                otp = flag;
                if(flag.equals("Invalid"))
                    Toast.makeText(getApplicationContext(),"Invalid Register ID",Toast.LENGTH_SHORT).show();
                else {
                    loadingDialog.dismisss();
                    otp_sending.setVisibility(View.GONE);
                    update_password.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "OTP SENT", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("otp_error",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String,String>();
                param.put("regno",username);
                param.put("usertype",user_type);
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(forgot_password.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}