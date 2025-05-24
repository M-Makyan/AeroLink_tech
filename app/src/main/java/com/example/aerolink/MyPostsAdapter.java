package com.example.aerolink;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.MyPostViewHolder> {
    public interface OnDeleteClickListener {
        void onDeleteClick(Post post, int position);
    }

    private final List<Post> posts;
    private final OnDeleteClickListener deleteListener;

    public MyPostsAdapter(List<Post> posts, OnDeleteClickListener listener) {
        this.posts = posts;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_post, parent, false);
        return new MyPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.title.setText(post.getTitle());
        holder.description.setText(post.getDescription());
        CharSequence ago = DateUtils.getRelativeTimeSpanString(
                post.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        holder.timePosted.setText(ago);

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUrl())
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(post, position));

        // Open detail by postId instead of passing all fields
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("postId", post.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class MyPostViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, timePosted;
        ImageView image;
        ImageButton btnDelete;

        MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postTitle);
            description = itemView.findViewById(R.id.postDescription);
            timePosted = itemView.findViewById(R.id.postTimestamp);
            image = itemView.findViewById(R.id.postImage);
            btnDelete = itemView.findViewById(R.id.btnDeletePost);
        }
    }
}
