package com.example.aerolink;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final int LOCATION_SETTINGS_REQUEST   = 1002;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore firestore;

    public SearchFragment() { /* Required empty constructor */ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        firestore            = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String postId = (String) marker.getTag();
                // Open PostDetailActivity with postId
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }
        });

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
        } else {
            checkLocationSettingsThenCenter();
        }
    }

    private void checkLocationSettingsThenCenter() {
        LocationRequest req = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest settingsReq = new LocationSettingsRequest.Builder()
                .addLocationRequest(req)
                .setAlwaysShow(true)
                .build();

        SettingsClient client = LocationServices.getSettingsClient(requireActivity());
        client.checkLocationSettings(settingsReq)
                .addOnSuccessListener(r -> centerOnUserAndLoadMarkers())
                .addOnFailureListener(e -> {
                    if (e instanceof ResolvableApiException) {
                        try {
                            ((ResolvableApiException) e)
                                    .startResolutionForResult(getActivity(),
                                            LOCATION_SETTINGS_REQUEST);
                        } catch (IntentSender.SendIntentException ignored) { }
                    }
                });
    }

    private void centerOnUserAndLoadMarkers() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        googleMap.setMyLocationEnabled(true);
        fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                new CancellationTokenSource().getToken()
        ).addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location loc) {
                if (loc != null) {
                    LatLng here = new LatLng(loc.getLatitude(), loc.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 14f));
                }
                addPostMarkers();
            }
        });
    }

    private void addPostMarkers() {
        firestore.collection("posts")
                .get()
                .addOnSuccessListener((QuerySnapshot snaps) -> {
                    for (QueryDocumentSnapshot doc : snaps) {
                        GeoPoint gp = doc.getGeoPoint("location");
                        String title = doc.getString("title");
                        String id    = doc.getId();
                        if (gp != null) {
                            LatLng pos = new LatLng(gp.getLatitude(), gp.getLongitude());
                            Marker marker = googleMap.addMarker(
                                    new MarkerOptions().position(pos).title(title)
                            );
                            marker.setTag(id);
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perm,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(req, perm, grantResults);
        if (req == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationSettingsThenCenter();
        }
    }

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == LOCATION_SETTINGS_REQUEST && res == getActivity().RESULT_OK) {
            centerOnUserAndLoadMarkers();
        }
    }
}