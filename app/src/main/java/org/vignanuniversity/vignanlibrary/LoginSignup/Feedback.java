package org.vignanuniversity.vignanlibrary.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.vignanuniversity.vignanlibrary.LoadingDialog;
import org.vignanuniversity.vignanlibrary.R;

public class Feedback extends AppCompatActivity {

    SharedPreferences preferences;
    Spinner spinner;
    Button submit;
    TextInputEditText feedback;
    RatingBar ratingbar;
    private static String userRate = "";
    String feedback_type = "";
    ImageView back;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String regno = preferences.getString("regno", "");
        loadingDialog = new LoadingDialog(Feedback.this);

        back = findViewById(R.id.on_back_press);
        back.setOnClickListener(v -> onBackPressed());

        spinner = findViewById(R.id.sp1);
        submit = findViewById(R.id.submit);
        feedback = findViewById(R.id.msg);
        ratingbar = findViewById(R.id.ratingbar);
        ImageView ratingimage = findViewById(R.id.ratingimage);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = spinner.getSelectedItem().toString().trim();
                if (!str.equals("Choose a Feedback type"))
                    feedback_type = str;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ratingbar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating < 1) ratingimage.setImageResource(R.drawable.question);
            else if (rating <= 1) ratingimage.setImageResource(R.drawable.emoji1);
            else if (rating <= 2) ratingimage.setImageResource(R.drawable.emoji2);
            else if (rating <= 3) ratingimage.setImageResource(R.drawable.emoji3);
            else if (rating <= 4) ratingimage.setImageResource(R.drawable.emoji4);
            else ratingimage.setImageResource(R.drawable.emoji5);

            animateImage(ratingimage);
            userRate = String.valueOf(rating);
        });

        submit.setOnClickListener(v -> {
            loadingDialog.load();

            String regno_ = regno.toUpperCase();
            String message_ = feedback.getText().toString();

            if (feedback_type.equals("Choose a Feedback type")) {
                loadingDialog.dismisss();
                Toast.makeText(getApplication(), "Please select feedback type", Toast.LENGTH_SHORT).show();
            } else if (ratingbar.getRating() == 0) {
                loadingDialog.dismisss();
                Toast.makeText(getApplication(), "Please select rating", Toast.LENGTH_SHORT).show();
            } else if (message_.isEmpty()) {
                loadingDialog.dismisss();
                Toast.makeText(getApplication(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                // Save locally in SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("last_feedback_type", feedback_type);
                editor.putString("last_feedback_message", message_);
                editor.putString("last_feedback_rating", userRate);
                editor.apply();

                loadingDialog.dismisss();

                feedback.setText("");
                ratingbar.setRating(0);
                spinner.setSelection(0);
            }
        });
    }

    private void animateImage(ImageView ratingimage) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1f, 0, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingimage.startAnimation(scaleAnimation);
    }
}
