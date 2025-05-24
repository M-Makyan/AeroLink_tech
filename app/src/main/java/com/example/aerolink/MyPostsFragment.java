package com.example.aerolink;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyPostsFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyPostsAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewMyPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        if (me == null) return;
        String username = me.getDisplayName();

        adapter = new MyPostsAdapter(postList);
        recyclerView.setAdapter(adapter);

        // Listen for user's posts
        firestore.collection("posts")
                .whereEqualTo("username", username)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snaps, e) -> {
                    if (e != null) return;
                    postList.clear();
                    for (DocumentSnapshot doc : snaps.getDocuments()) {
                        Post p = doc.toObject(Post.class);
                        p.setId(doc.getId());
                        postList.add(p);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    // Adapter inner class with long-press delete
    private class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder> {
        private final List<Post> posts;

        MyPostsAdapter(List<Post> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post, parent, false);
            return new ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post post = posts.get(position);
            holder.bind(post);

            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete post")
                        .setMessage("Do you want to delete this post?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // delete from Firestore
                            String id = post.getId();
                            firestore.collection("posts").document(id)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Position may have shifted since the click—find by ID instead
                                        int removeIndex = -1;
                                        for (int i = 0; i < postList.size(); i++) {
                                            if (postList.get(i).getId().equals(post.getId())) {
                                                removeIndex = i;
                                                break;
                                            }
                                        }
                                        if (removeIndex != -1) {
                                            postList.remove(removeIndex);
                                            adapter.notifyItemRemoved(removeIndex);
                                        }
                                    })
                                    .addOnFailureListener(err ->
                                            Toast.makeText(getContext(),
                                                    "Delete failed: " + err.getMessage(),
                                                    Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, description, timestamp;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.postTitle);
                description = itemView.findViewById(R.id.postDescription);
                timestamp = itemView.findViewById(R.id.postTimestamp);
            }

            void bind(Post p) {
                title.setText(p.getTitle());
                description.setText(p.getDescription());
                CharSequence ago = DateUtils.getRelativeTimeSpanString(
                        p.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                timestamp.setText(ago);
            }
        }
    }
}