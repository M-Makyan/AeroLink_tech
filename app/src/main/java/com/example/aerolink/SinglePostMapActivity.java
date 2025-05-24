package com.example.aerolink;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.DocumentSnapshot;

public class SinglePostMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_POST_ID = "postId";

    private GoogleMap googleMap;
    private FirebaseFirestore firestore;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post_map);

        // Back button in top right
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get postId extra
        postId = getIntent().getStringExtra(EXTRA_POST_ID);
        if (postId == null) {
            finish();
            return;
        }

        // Load map fragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        // Fetch the post's location and title
        firestore.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(doc -> {
                    GeoPoint gp = doc.getGeoPoint("location");
                    String title = doc.getString("title");
                    if (gp != null) {
                        LatLng pos = new LatLng(gp.getLatitude(), gp.getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(title));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
                    }
                });
    }
}