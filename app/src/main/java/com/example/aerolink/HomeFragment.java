package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> allPosts;
    private List<Post> displayedPosts;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchBar;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterPosts(s.toString());
            }
            @Override public void afterTextChanged(Editable e) {}
        });

        allPosts = new ArrayList<>();
        displayedPosts = new ArrayList<>();
        postAdapter = new PostAdapter(displayedPosts);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchPostsManually);

        FloatingActionButton fabAddPost = view.findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class))
        );

        listenForPosts();
        return view;
    }

    private void listenForPosts() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error getting documents: ", error);
                        return;
                    }
                    if (value == null) return;

                    allPosts.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            post.setId(doc.getId());   // ← set the Firestore ID
                            allPosts.add(post);
                        }
                    }
                    displayedPosts.clear();
                    displayedPosts.addAll(allPosts);
                    postAdapter.notifyDataSetChanged();
                });
    }

    private void fetchPostsManually() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        allPosts.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Post post = doc.toObject(Post.class);
                            if (post != null) {
                                post.setId(doc.getId()); // ← set the Firestore ID
                                allPosts.add(post);
                            }
                        }
                        displayedPosts.clear();
                        displayedPosts.addAll(allPosts);
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreError", "Failed to fetch posts manually", task.getException());
                    }
                });
    }

    private void filterPosts(String query) {
        String q = query.trim().toLowerCase();
        displayedPosts.clear();
        if (q.isEmpty()) {
            displayedPosts.addAll(allPosts);
        } else {
            for (Post p : allPosts) {
                String title       = p.getTitle() != null ? p.getTitle().toLowerCase() : "";
                String description = p.getDescription() != null ? p.getDescription().toLowerCase() : "";
                if (title.contains(q) || description.contains(q)) {
                    displayedPosts.add(p);
                }
            }
        }
        postAdapter.notifyDataSetChanged();
    }
}