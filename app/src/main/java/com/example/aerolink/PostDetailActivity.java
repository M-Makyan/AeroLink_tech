package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class PostDetailActivity extends AppCompatActivity {
    private TextView titleText, descriptionText;
    private ImageView postImage, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        titleText = findViewById(R.id.detail_title);
        descriptionText = findViewById(R.id.detail_description);
        postImage = findViewById(R.id.detail_image);
        backButton = findViewById(R.id.back_button);

        // Get post details from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("imageUrl");

        titleText.setText(title);
        descriptionText.setText(description);

        // Load image if available
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(postImage);
        } else {
            postImage.setVisibility(ImageView.GONE);
        }

        // Set up the back button functionality
        backButton.setOnClickListener(v -> onBackPressed());
    }
}