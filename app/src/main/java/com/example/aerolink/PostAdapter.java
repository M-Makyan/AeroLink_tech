package com.example.aerolink;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Bind title & description
        holder.title.setText(post.getTitle());
        holder.description.setText(post.getDescription());

        // Bind timestamp
        CharSequence ago = DateUtils.getRelativeTimeSpanString(
                post.getTimestamp(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        );
        holder.timePosted.setText(ago);

        // Bind image (hide if none)
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUrl())
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        // Click → detail, still pass username & timestamp along
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("title",       post.getTitle());
            intent.putExtra("description", post.getDescription());
            intent.putExtra("imageUrl",    post.getImageUrl());
            intent.putExtra("username",    post.getUsername());
            intent.putExtra("timestamp",   post.getTimestamp());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        final TextView title, description, timePosted;
        final ImageView image;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title       = itemView.findViewById(R.id.postTitle);
            description = itemView.findViewById(R.id.postDescription);
            timePosted  = itemView.findViewById(R.id.postTimestamp);
            image       = itemView.findViewById(R.id.postImage);
        }
    }
}