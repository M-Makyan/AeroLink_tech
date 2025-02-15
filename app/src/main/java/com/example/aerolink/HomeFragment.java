package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();
        loadPostsFromFirestore();

        FloatingActionButton fabAddPost = view.findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreatePostActivity.class)));

        return view;
    }

    private void loadPostsFromFirestore() {
        db.collection("posts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                postList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("title");
                    String description = document.getString("description");
                    String imageUrl = document.getString("imageUrl");
                    postList.add(new Post(title, description, imageUrl));
                }
                postAdapter.notifyDataSetChanged();
            }
        });
    }
}
