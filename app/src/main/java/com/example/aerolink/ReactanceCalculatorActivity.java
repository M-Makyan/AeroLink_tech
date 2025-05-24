// src/main/java/com/example/aerolink/CoilResistanceCalculatorActivity.java
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

public class ReactanceCalculatorActivity extends AppCompatActivity {

    private EditText awgInput, turnsInput, coilDiameterInput;
    private TextView wireLengthOutput, resistanceOutput;
    private Button calculateButton, clearButton;

    // copper resistivity in Ω·m
    private static final double COPPER_RESISTIVITY = 1.68e-8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reactance_calculator);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        awgInput            = findViewById(R.id.awgInput);
        turnsInput          = findViewById(R.id.turnsInput);
        coilDiameterInput   = findViewById(R.id.coilDiameterInput);

        wireLengthOutput    = findViewById(R.id.wireLengthOutput);
        resistanceOutput    = findViewById(R.id.resistanceOutput);

        calculateButton     = findViewById(R.id.calculateButton);
        clearButton         = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(v -> calculateCoil());
        clearButton.setOnClickListener(v -> clearFields());
    }

    private void calculateCoil() {
        // parse inputs
        double awg = parseDouble(awgInput);
        int turns = (int) parseDouble(turnsInput);
        double coilDiaMm = parseDouble(coilDiameterInput);

        // 1) wire length (m): length = π × coil_diameter × N
        double wireLength = Math.PI * (coilDiaMm / 1000.0) * turns;
        wireLengthOutput.setText(String.format("Wire Length: %.2f m", wireLength));

        // 2) convert AWG to diameter (mm):
        //    d_mm = 0.127 × 92^((36 − AWG)/39)
        double dWireMm = 0.127 * Math.pow(92.0, (36.0 - awg) / 39.0);

        // cross-sectional area (m²)
        double radius = (dWireMm / 2.0) / 1000.0;
        double area = Math.PI * radius * radius;

        // 3) resistance R = ρ × L / A
        double resistance = COPPER_RESISTIVITY * wireLength / area;
        resistanceOutput.setText(String.format("Coil Resistance: %.4f Ω", resistance));

        hideKeyboard();
    }

    private double parseDouble(EditText e) {
        String s = e.getText().toString().trim();
        return s.isEmpty() ? 0.0 : Double.parseDouble(s);
    }

    private void clearFields() {
        awgInput.setText("");
        turnsInput.setText("");
        coilDiameterInput.setText("");
        wireLengthOutput.setText("Wire Length: -");
        resistanceOutput.setText("Coil Resistance: -");
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}