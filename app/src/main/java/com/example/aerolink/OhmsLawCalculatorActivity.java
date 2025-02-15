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

public class OhmsLawCalculatorActivity extends AppCompatActivity {

    private EditText voltageInput, currentInput, resistanceInput;
    private TextView powerOutput;
    private Button calculateButton, clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ohms_law_calculator);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        voltageInput = findViewById(R.id.voltageInput);
        currentInput = findViewById(R.id.currentInput);
        resistanceInput = findViewById(R.id.resistanceInput);
        powerOutput = findViewById(R.id.powerOutput);
        calculateButton = findViewById(R.id.calculateButton);
        clearButton = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(view -> calculateOhmsLaw());
        clearButton.setOnClickListener(view -> clearFields());
    }

    private void calculateOhmsLaw() {
        String voltageStr = voltageInput.getText().toString();
        String currentStr = currentInput.getText().toString();
        String resistanceStr = resistanceInput.getText().toString();

        Double voltage = voltageStr.isEmpty() ? null : Double.parseDouble(voltageStr);
        Double current = currentStr.isEmpty() ? null : Double.parseDouble(currentStr);
        Double resistance = resistanceStr.isEmpty() ? null : Double.parseDouble(resistanceStr);

        if (voltage == null && current != null && resistance != null) {
            voltage = current * resistance;
        } else if (current == null && voltage != null && resistance != null) {
            current = voltage / resistance;
        } else if (resistance == null && voltage != null && current != null) {
            resistance = voltage / current;
        }

        Double power = (voltage != null && current != null) ? voltage * current : null;

        if (voltage != null) voltageInput.setText(String.format("%.2f", voltage));
        if (current != null) currentInput.setText(String.format("%.2f", current));
        if (resistance != null) resistanceInput.setText(String.format("%.2f", resistance));

        if (power != null) {
            powerOutput.setText("Power: " + String.format("%.2f W", power));
        } else {
            powerOutput.setText("Power: -");
        }

        hideKeyboard();
    }

    private void clearFields() {
        voltageInput.setText("");
        currentInput.setText("");
        resistanceInput.setText("");
        powerOutput.setText("Power: -");
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}