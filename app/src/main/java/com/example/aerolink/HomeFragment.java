package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchPostsManually); // Set up swipe refresh

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();

        listenForPosts(); // Automatic real-time updates

        FloatingActionButton fabAddPost = view.findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreatePostActivity.class)));

        return view;
    }

    private void listenForPosts() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by newest first
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error getting documents: ", error);
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Log.d("Firestore", "No posts found");
                        return;
                    }

                    postList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Post post = doc.toObject(Post.class);
                        postList.add(post);
                    }
                    postAdapter.notifyDataSetChanged();
                });
    }

    private void fetchPostsManually() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false); // Stop the loading animation
                    if (task.isSuccessful() && task.getResult() != null) {
                        postList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Post post = doc.toObject(Post.class);
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreError", "Failed to fetch posts manually", task.getException());
                    }
                });
    }
}