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

public class WireGaugeCalculatorActivity extends AppCompatActivity {

    private EditText currentInput, lengthInput, voltageDropInput;
    private TextView areaOutput, gaugeOutput;
    private Button calculateButton, clearButton;
    private static final double COPPER_RESISTIVITY = 1.68e-8; // Ω·m

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wire_gauge_calculator);

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        currentInput     = (EditText) findViewById(R.id.currentInput);
        lengthInput      = (EditText) findViewById(R.id.lengthInput);
        voltageDropInput = (EditText) findViewById(R.id.voltageDropInput);

        areaOutput       = (TextView) findViewById(R.id.areaOutput);
        gaugeOutput      = (TextView) findViewById(R.id.gaugeOutput);

        calculateButton  = (Button) findViewById(R.id.calculateButton);
        clearButton      = (Button) findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(v -> calculateWire());
        clearButton.setOnClickListener(v -> clearFields());
    }

    private void calculateWire() {
        double I = parseDouble(currentInput);
        double L = parseDouble(lengthInput) * 2;  // loop length (m)
        double V = parseDouble(voltageDropInput);

        // Required cross-sectional area (m²)
        double A = COPPER_RESISTIVITY * L * I / V;
        double A_mm2 = A * 1e6;
        areaOutput.setText(String.format("Area: %.2f mm²", A_mm2));

        // Required diameter from area
        double d_m = Math.sqrt(4 * A / Math.PI);
        double d_mm = d_m * 1000;

        // Continuous AWG calculation
        double gaugeVal = 36 - 39 * Math.log10(d_mm / 0.127) / Math.log10(92);
        int recommended = (int) Math.floor(gaugeVal);
        recommended = Math.max(0, Math.min(40, recommended));
        gaugeOutput.setText("AWG: " + recommended);

        hideKeyboard();
    }

    private double parseDouble(EditText e) {
        String s = e.getText().toString().trim();
        return s.isEmpty() ? 0 : Double.parseDouble(s);
    }

    private void clearFields() {
        currentInput.setText("");
        lengthInput.setText("");
        voltageDropInput.setText("");
        areaOutput.setText("Area: -");
        gaugeOutput.setText("AWG: -");
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}