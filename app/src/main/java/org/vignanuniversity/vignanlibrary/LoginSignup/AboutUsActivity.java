package org.vignanuniversity.vignanlibrary.LoginSignup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.vignanuniversity.vignanlibrary.R;

public class AboutUsActivity extends AppCompatActivity {

    private TextView tvMember1Name, tvMember1Branch;
    private TextView tvMember2Name, tvMember2Branch;
    private TextView tvMember3Name, tvMember3Branch;
    private TextView tvMember4Name, tvMember4Branch;

    private ImageView ivMember1Profile, ivMember1Github, ivMember1Linkedin;
    private ImageView ivMember2Profile, ivMember2Github, ivMember2Linkedin;
    private ImageView ivMember3Profile, ivMember3Github, ivMember3Linkedin;
    private ImageView ivMember4Profile, ivMember4Github, ivMember4Linkedin;

    // Team member data with API image URLs
    private TeamMember[] teamMembers = {
            new TeamMember("D.OMKAR", "Information Technology",
                    "http://160.187.169.14/jspapi/photos/221FA07124.JPG",
                    "https://github.com/omkarpavan", "https://linkedin.com/in/omkar-darisi-713533343"),
            new TeamMember("N.ANANTH", "AI&ML",
                    "http://160.187.169.14/jspapi/photos/221FA18036.JPG",
                    "https://github.com/priyasharma", "https://linkedin.com/in/priyasharma"),
            new TeamMember("N.B.L.GANESH", "AI&ML",
                    "http://160.187.169.14/jspapi/photos/221FA18060.JPG",
                    "https://github.com/rahulverma", "https://linkedin.com/in/rahulverma"),
            new TeamMember("M.JEEVAN", "AI&ML",
                    "http://160.187.169.14/jspapi/photos/221FA18028.JPG",
                    "https://github.com/anitareddy", "https://linkedin.com/in/anitareddy")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about_us);

        // Setup toolbar
        setupToolbar();

        // Initialize views and setup
        initializeViews();
        setupTeamMembers();
        loadProfileImages();
        setupClickListeners();
        addCardAnimations();
    }

    private void setupToolbar() {
        // Set status bar color with theme color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_teal));
        }

        // Setup toolbar if you have one in your layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("About Us");
            }
        }
    }

    private void initializeViews() {
        // Member 1
        tvMember1Name = findViewById(R.id.tv_member1_name);
        tvMember1Branch = findViewById(R.id.tv_member1_branch);
        ivMember1Profile = findViewById(R.id.iv_member1_profile);
        ivMember1Github = findViewById(R.id.iv_member1_github);
        ivMember1Linkedin = findViewById(R.id.iv_member1_linkedin);

        // Member 2
        tvMember2Name = findViewById(R.id.tv_member2_name);
        tvMember2Branch = findViewById(R.id.tv_member2_branch);
        ivMember2Profile = findViewById(R.id.iv_member2_profile);
        ivMember2Github = findViewById(R.id.iv_member2_github);
        ivMember2Linkedin = findViewById(R.id.iv_member2_linkedin);

        // Member 3
        tvMember3Name = findViewById(R.id.tv_member3_name);
        tvMember3Branch = findViewById(R.id.tv_member3_branch);
        ivMember3Profile = findViewById(R.id.iv_member3_profile);
        ivMember3Github = findViewById(R.id.iv_member3_github);
        ivMember3Linkedin = findViewById(R.id.iv_member3_linkedin);

        // Member 4
        tvMember4Name = findViewById(R.id.tv_member4_name);
        tvMember4Branch = findViewById(R.id.tv_member4_branch);
        ivMember4Profile = findViewById(R.id.iv_member4_profile);
        ivMember4Github = findViewById(R.id.iv_member4_github);
        ivMember4Linkedin = findViewById(R.id.iv_member4_linkedin);
    }

    private void setupTeamMembers() {
        // Set Member 1 data
        tvMember1Name.setText(teamMembers[0].getName());
        tvMember1Branch.setText(teamMembers[0].getBranch());

        // Set Member 2 data
        tvMember2Name.setText(teamMembers[1].getName());
        tvMember2Branch.setText(teamMembers[1].getBranch());

        // Set Member 3 data
        tvMember3Name.setText(teamMembers[2].getName());
        tvMember3Branch.setText(teamMembers[2].getBranch());

        // Set Member 4 data
        tvMember4Name.setText(teamMembers[3].getName());
        tvMember4Branch.setText(teamMembers[3].getBranch());
    }

    private void loadProfileImages() {
        // Glide options for better image loading
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_profile_placeholder) // Show placeholder while loading
                .error(R.drawable.ic_profile_placeholder) // Show placeholder if error
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache images
                .centerCrop(); // Crop to fit

        // Load Member 1 image
        Glide.with(this)
                .load(teamMembers[0].getImageUrl())
                .apply(requestOptions)
                .into(ivMember1Profile);

        // Load Member 2 image
        Glide.with(this)
                .load(teamMembers[1].getImageUrl())
                .apply(requestOptions)
                .into(ivMember2Profile);

        // Load Member 3 image
        Glide.with(this)
                .load(teamMembers[2].getImageUrl())
                .apply(requestOptions)
                .into(ivMember3Profile);

        // Load Member 4 image
        Glide.with(this)
                .load(teamMembers[3].getImageUrl())
                .apply(requestOptions)
                .into(ivMember4Profile);
    }

    private void setupClickListeners() {
        // Member 1 click listeners
        ivMember1Github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Opening " + teamMembers[0].getName() + "'s GitHub");
                openUrl(teamMembers[0].getGithubUrl());
            }
        });

        ivMember1Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Opening " + teamMembers[0].getName() + "'s LinkedIn");
                openUrl(teamMembers[0].getLinkedinUrl());
            }
        });

        // Member 2 click listeners
        ivMember2Github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Opening " + teamMembers[1].getName() + "'s GitHub");
                openUrl(teamMembers[1].getGithubUrl());
            }
        });

        ivMember2Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Opening " + teamMembers[1].getName() + "'s LinkedIn");
                openUrl(teamMembers[1].getLinkedinUrl());
            }
        });

        // Member 3 click listeners
        ivMember3Github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Opening " + teamMembers[2].getName() + "'s GitHub");
                openUrl(teamMembers[2].getGithubUrl());
            }
        });

        ivMember3Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Opening " + teamMembers[2].getName() + "'s LinkedIn");
                openUrl(teamMembers[2].getLinkedinUrl());
            }
        });

        // Member 4 click listeners
        ivMember4Github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Opening " + teamMembers[3].getName() + "'s GitHub");
                openUrl(teamMembers[3].getGithubUrl());
            }
        });

        ivMember4Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Opening " + teamMembers[3].getName() + "'s LinkedIn");
                openUrl(teamMembers[3].getLinkedinUrl());
            }
        });
    }

    private void addCardAnimations() {
        // Add click animations to social media icons for better UX
        addClickAnimation(ivMember1Github);
        addClickAnimation(ivMember1Linkedin);
        addClickAnimation(ivMember2Github);
        addClickAnimation(ivMember2Linkedin);
        addClickAnimation(ivMember3Github);
        addClickAnimation(ivMember3Linkedin);
        addClickAnimation(ivMember4Github);
        addClickAnimation(ivMember4Linkedin);
    }

    private void addClickAnimation(ImageView imageView) {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            showToast("Unable to open link. Please check your internet connection.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Handle back button in toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // TeamMember class to hold member data
    private static class TeamMember {
        private String name;
        private String branch;
        private String imageUrl;
        private String githubUrl;
        private String linkedinUrl;

        public TeamMember(String name, String branch, String imageUrl, String githubUrl, String linkedinUrl) {
            this.name = name;
            this.branch = branch;
            this.imageUrl = imageUrl;
            this.githubUrl = githubUrl;
            this.linkedinUrl = linkedinUrl;
        }

        public String getName() {
            return name;
        }

        public String getBranch() {
            return branch;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getGithubUrl() {
            return githubUrl;
        }

        public String getLinkedinUrl() {
            return linkedinUrl;
        }

        // Optional: Add setter methods if you need to update member data
        public void setName(String name) {
            this.name = name;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setGithubUrl(String githubUrl) {
            this.githubUrl = githubUrl;
        }

        public void setLinkedinUrl(String linkedinUrl) {
            this.linkedinUrl = linkedinUrl;
        }
    }
}