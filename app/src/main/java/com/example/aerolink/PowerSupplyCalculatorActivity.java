package com.example.aerolink;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PowerSupplyCalculatorActivity extends AppCompatActivity {

    private EditText acVoltageInput, dcVoltageInput, loadCurrentInput, rippleVoltageInput, marginInput;
    private TextView transformerRatingOutput, rectifierCurrentOutput, capacitorOutput, regulatorDissipationOutput;
    private Button calculateButton, clearButton;

    // Assumed constants
    private static final double EFFICIENCY = 0.8;         // 80% conversion efficiency
    private static final double INPUT_FREQ = 50.0;       // 50 Hz mains
    private static final double DIODE_DROP = 0.7 * 2;    // two diodes in bridge

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_supply_calculator);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        acVoltageInput               = findViewById(R.id.acVoltageInput);
        dcVoltageInput               = findViewById(R.id.dcVoltageInput);
        loadCurrentInput             = findViewById(R.id.loadCurrentInput);
        rippleVoltageInput           = findViewById(R.id.rippleVoltageInput);
        marginInput                  = findViewById(R.id.marginInput);

        transformerRatingOutput      = findViewById(R.id.transformerRatingOutput);
        rectifierCurrentOutput       = findViewById(R.id.rectifierCurrentOutput);
        capacitorOutput              = findViewById(R.id.capacitorOutput);
        regulatorDissipationOutput   = findViewById(R.id.regulatorDissipationOutput);

        calculateButton = findViewById(R.id.calculateButton);
        clearButton     = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(v -> calculatePowerSupply());
        clearButton.setOnClickListener(v -> clearFields());
    }

    private void calculatePowerSupply() {
        double Vac     = parseDouble(acVoltageInput);
        double Vdc     = parseDouble(dcVoltageInput);
        double Iload   = parseDouble(loadCurrentInput);
        double Vripple = parseDouble(rippleVoltageInput);
        double margin  = parseDouble(marginInput);

        // 1) Transformer VA rating
        double vaRating = (Vdc * Iload / EFFICIENCY) * (1 + margin / 100.0);
        transformerRatingOutput.setText(String.format("Transformer Rating: %.2f VA", vaRating));

        // 2) Rectifier currents
        double iAvg = Iload;
        double iPeak = Iload * Math.PI / 2.0;
        rectifierCurrentOutput.setText(
                String.format("Rectifier Iavg: %.2f A, Ipk: %.2f A", iAvg, iPeak)
        );

        // 3) Smoothing capacitor (full-wave ripple)
        double fr = INPUT_FREQ * 2;
        double capF = Iload / (fr * Vripple);
        double capuF = capF * 1e6;
        capacitorOutput.setText(String.format("Capacitor: %.0f µF", capuF));

        // 4) Regulator dissipation
        double vRectDC = Vac * Math.sqrt(2) - DIODE_DROP;
        double pDiss = (vRectDC - Vdc) * Iload;
        regulatorDissipationOutput.setText(
                String.format("Regulator Dissipation: %.2f W", pDiss)
        );

        hideKeyboard();
    }

    private double parseDouble(EditText e) {
        String s = e.getText().toString().trim();
        return s.isEmpty() ? 0 : Double.parseDouble(s);
    }

    private void clearFields() {
        acVoltageInput.setText("");
        dcVoltageInput.setText("");
        loadCurrentInput.setText("");
        rippleVoltageInput.setText("");
        marginInput.setText("");

        transformerRatingOutput.setText("Transformer Rating: -");
        rectifierCurrentOutput.setText("Rectifier Current: -");
        capacitorOutput.setText("Capacitor: -");
        regulatorDissipationOutput.setText("Regulator Dissipation: -");
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}