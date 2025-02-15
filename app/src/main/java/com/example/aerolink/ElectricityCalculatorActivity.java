package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ElectricityCalculatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_calculator);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnOhmsLaw = findViewById(R.id.btnOhmsLaw);
        btnOhmsLaw.setOnClickListener(v -> {
            Intent intent = new Intent(this, OhmsLawCalculatorActivity.class);
            startActivity(intent);
        });

        ImageButton btnRCCircuit = findViewById(R.id.btnRCCircuit);
        btnRCCircuit.setOnClickListener(v -> {
            Intent intent = new Intent(this, RCCircuitCalculatorActivity.class);
            startActivity(intent);
        });

        ImageButton btnReactance = findViewById(R.id.btnReactance);
        btnReactance.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReactanceCalculatorActivity.class);
            startActivity(intent);
        });

        ImageButton btnPowerSupply = findViewById(R.id.btnPowerSupply);
        btnPowerSupply.setOnClickListener(v -> {
            Intent intent = new Intent(this, PowerSupplyCalculatorActivity.class);
            startActivity(intent);
        });

        ImageButton btnBatteryLife = findViewById(R.id.btnBatteryLife);
        btnBatteryLife.setOnClickListener(v -> {
            Intent intent = new Intent(this, BatteryLifeEstimatorActivity.class);
            startActivity(intent);
        });

        ImageButton btnSolarPower = findViewById(R.id.btnSolarPower);
        btnSolarPower.setOnClickListener(v -> {
            Intent intent = new Intent(this, SolarPanelCalculatorActivity.class);
            startActivity(intent);
        });

        ImageButton btnWireGauge = findViewById(R.id.btnWireGauge);
        btnWireGauge.setOnClickListener(v -> {
            Intent intent = new Intent(this, WireGaugeCalculatorActivity.class);
            startActivity(intent);
        });

    }
}