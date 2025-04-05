package com.example.aerolink;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

public class SearchFragment extends Fragment {

    private MapView mapView;

    public SearchFragment() { /* Required empty constructor */ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout that contains the MapView
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Find the MapView in the layout
        mapView = view.findViewById(R.id.mapView);

        // Load a style, then set the camera position
        mapView.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS,
                style -> {
                    CameraOptions cameraOptions = new CameraOptions.Builder()
                            .center(Point.fromLngLat(-98.0, 39.5))
                            .zoom(2.0)
                            .pitch(0.0)
                            .bearing(0.0)
                            .build();
                    mapView.getMapboxMap().setCamera(cameraOptions);
                }
        );

        return view;
    }
}