package com.example.aerolink;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.author.setText(comment.getAuthor());
        holder.text.setText(comment.getText());
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                comment.getTimestamp(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        );
        holder.timestamp.setText(timeAgo);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView author, text, timestamp;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            author    = itemView.findViewById(R.id.commentAuthor);
            text      = itemView.findViewById(R.id.commentText);
            timestamp = itemView.findViewById(R.id.commentTime);
        }
    }
}
