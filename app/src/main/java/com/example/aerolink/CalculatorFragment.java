package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalculatorFragment extends Fragment {

    public CalculatorFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        ImageButton rocketCalculatorButton = view.findViewById(R.id.btnRocketCalculator);
        ImageButton otherCalculatorButton = view.findViewById(R.id.btnOtherCalculator);

        // Open Rocket Calculator Activity
        rocketCalculatorButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RocketCalculatorActivity.class);
            startActivity(intent);
        });

        // Open Wing Loading Calculator Activity
        otherCalculatorButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WingLoading.class);
            startActivity(intent);
        });

        return view;
    }
}