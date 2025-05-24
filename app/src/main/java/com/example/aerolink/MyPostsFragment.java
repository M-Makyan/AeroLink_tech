package com.example.aerolink;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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

        adapter = new MyPostsAdapter(postList, (post, position) -> {
            // Delete fireStore document and update list
            firestore.collection("posts")
                    .document(post.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        postList.remove(position);
                        adapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        recyclerView.setAdapter(adapter);

        // Listen for user's posts
        firestore.collection("posts")
                .whereEqualTo("username", username)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snaps,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;
                        postList.clear();
                        for (DocumentSnapshot doc : snaps.getDocuments()) {
                            Post p = doc.toObject(Post.class);
                            p.setId(doc.getId());
                            postList.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
