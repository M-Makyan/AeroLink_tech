package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PostDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);  // ensure this is your detail-layout

        // Wire up views
        ImageView backBtn         = findViewById(R.id.back_button);
        ImageView creatorImage    = findViewById(R.id.creator_image);
        TextView  creatorUsername = findViewById(R.id.creator_username);
        TextView  detailTimestamp = findViewById(R.id.detail_timestamp);
        TextView  detailTitle     = findViewById(R.id.detail_title);
        ImageView detailImage     = findViewById(R.id.detail_image);
        TextView  detailDesc      = findViewById(R.id.detail_description);

        // Back action
        backBtn.setOnClickListener(v -> finish());

        // Grab all the extras
        Intent intent    = getIntent();
        String title     = intent.getStringExtra("title");
        String desc      = intent.getStringExtra("description");
        String imgUrl    = intent.getStringExtra("imageUrl");
        String username  = intent.getStringExtra("username");
        long   timestamp = intent.getLongExtra("timestamp", 0L);

        // Bind the username
        if (username != null && !username.isEmpty()) {
            creatorUsername.setText(username);
        } else {
            creatorUsername.setText("Unknown");
        }

        // (Optional) load creator’s profile pic if you passed one
        String profileUrl = intent.getStringExtra("profileImageUrl");
        if (profileUrl != null && !profileUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileUrl)
                    .circleCrop()
                    .into(creatorImage);
        }

        // Bind time-ago
        CharSequence ago = DateUtils.getRelativeTimeSpanString(
                timestamp,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        );
        detailTimestamp.setText(ago);

        // Bind title & description
        detailTitle.setText(title);
        detailDesc.setText(desc);

        // Bind post image (hide if none)
        if (imgUrl != null && !imgUrl.isEmpty()) {
            detailImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imgUrl)
                    .into(detailImage);
        }
    }
}