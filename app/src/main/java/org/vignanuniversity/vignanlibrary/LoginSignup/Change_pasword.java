package org.vignanuniversity.vignanlibrary.LoginSignup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.vignanuniversity.vignanlibrary.Adapter.URL;
import org.vignanuniversity.vignanlibrary.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Change_pasword extends AppCompatActivity {

    com.google.android.material.textfield.TextInputEditText New_pass, old_pass, Re_New_pass;
    Button update;
    SharedPreferences preferences;
    String regno_ = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pasword);
        preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        regno_ = preferences.getString("regno", "");

        // Fixed IDs to match XML layout
        New_pass = findViewById(R.id.new_pass);
        old_pass = findViewById(R.id.oldpass_validation);  // Changed from old_pass to oldpass_validation
        Re_New_pass = findViewById(R.id.new_pass_con);     // Changed from re_new_pass to new_pass_con
        update = findViewById(R.id.submit);

        update.setOnClickListener(v -> {
            String newPassText = Objects.requireNonNull(New_pass.getText()).toString();
            String oldPassText = Objects.requireNonNull(old_pass.getText()).toString();
            String reNewPassText = Objects.requireNonNull(Re_New_pass.getText()).toString();

            // Clear previous errors
            New_pass.setError(null);
            old_pass.setError(null);
            Re_New_pass.setError(null);

            // Check if all fields are filled
            if (newPassText.isEmpty() || oldPassText.isEmpty() || reNewPassText.isEmpty()) {
                if (newPassText.isEmpty()) {
                    New_pass.setError("Please enter new password");
                }
                if (oldPassText.isEmpty()) {
                    old_pass.setError("Please enter old password");
                }
                if (reNewPassText.isEmpty()) {
                    Re_New_pass.setError("Please re-enter new password");
                }
                Toast.makeText(Change_pasword.this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                return;
            }

            // Check if new passwords match
            if (!newPassText.equals(reNewPassText)) {
                New_pass.setError("Passwords do not match");
                Re_New_pass.setError("Passwords do not match");
                Toast.makeText(Change_pasword.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                return;
            }

            // All validations passed, proceed with update
            update();
        });
    }

    public void update() {
        String old_pass_ = Objects.requireNonNull(old_pass.getText()).toString();
        String new_pass_ = Objects.requireNonNull(New_pass.getText()).toString();
        String url = URL.getChangePassUrl();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String flag = response.trim();
                if (flag.equals("Success")) {
                    Toast.makeText(Change_pasword.this, "Password updated successfully", Toast.LENGTH_LONG).show();
                    onBackPressed();
                    finish();
                } else {
                    Toast.makeText(Change_pasword.this, "Incorrect old password", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("msg", error.toString());
                Toast.makeText(Change_pasword.this, "Network error. Please try again.", Toast.LENGTH_LONG).show();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("regno", regno_);
                param.put("old_pass", old_pass_);
                param.put("new_pass", new_pass_);
                return param;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}