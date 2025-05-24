package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView backBtn, detailImage;
    private TextView creatorUsername, detailTimestamp, detailTitle, detailDesc;
    private ProgressBar progressBar;
    private Button btnLocatePost;

    private RecyclerView commentsRecycler;
    private FloatingActionButton fabAddComment;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();

    private FirebaseFirestore firestore;
    private String currentPostId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 1) Get postId immediately
        currentPostId = getIntent().getStringExtra("postId");
        if (currentPostId == null) {
            Toast.makeText(this, "Invalid post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) Wire all views
        firestore        = FirebaseFirestore.getInstance();
        backBtn          = findViewById(R.id.back_button);
        creatorUsername  = findViewById(R.id.creator_username);
        detailTimestamp  = findViewById(R.id.detail_timestamp);
        detailTitle      = findViewById(R.id.detail_title);
        detailDesc       = findViewById(R.id.detail_description);
        detailImage      = findViewById(R.id.detail_image);
        progressBar      = findViewById(R.id.detail_progress);
        btnLocatePost    = findViewById(R.id.btnLocatePost);
        commentsRecycler = findViewById(R.id.commentsRecycler);
        fabAddComment    = findViewById(R.id.fabAddComment);

        // 3) Back button
        backBtn.setOnClickListener(v -> finish());

        // 4) Setup comments list (newest on top)
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        commentsRecycler.setLayoutManager(lm);
        commentAdapter = new CommentAdapter(comments);
        commentsRecycler.setAdapter(commentAdapter);

        // 5) FAB to add comment (now safe: postId is valid)
        fabAddComment.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateCommentActivity.class);
            intent.putExtra(CreateCommentActivity.EXTRA_POST_ID, currentPostId);
            startActivity(intent);
        });

        // 6) Disable locate until data is bound
        btnLocatePost.setEnabled(false);

        // 7) Load the post
        loadPost(currentPostId);

        // 8) Listen for comments
        firestore.collection("posts")
                .document(currentPostId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;
                    comments.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Comment c = d.toObject(Comment.class);
                        if (c != null) {
                            c.setId(d.getId());
                            comments.add(c);
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                    if (!comments.isEmpty()) {
                        commentsRecycler.scrollToPosition(0);
                    }
                });

        // 9) Locate on map
        btnLocatePost.setOnClickListener(v -> {
            Intent mapIntent = new Intent(this, SinglePostMapActivity.class);
            mapIntent.putExtra(SinglePostMapActivity.EXTRA_POST_ID, currentPostId);
            startActivity(mapIntent);
        });
    }

    private void loadPost(String postId) {
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(View.GONE);
                    if (!doc.exists()) {
                        Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    bindData(doc);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading post", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void bindData(DocumentSnapshot doc) {
        String title  = doc.getString("title");
        String desc   = doc.getString("description");
        String user   = doc.getString("username");
        Long   ts     = doc.getLong("timestamp");
        String imgUrl = doc.getString("imageUrl");

        creatorUsername.setText(user != null ? user : "Unknown");
        if (ts != null) {
            detailTimestamp.setText(
                    DateUtils.getRelativeTimeSpanString(ts, System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS)
            );
        }
        detailTitle.setText(title != null ? title : "");
        detailDesc.setText(desc != null ? desc : "");

        if (imgUrl != null && !imgUrl.isEmpty()) {
            detailImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imgUrl).into(detailImage);
        } else {
            detailImage.setVisibility(View.GONE);
        }

        // Now allow “Locate on Map”
        btnLocatePost.setEnabled(true);
    }
}