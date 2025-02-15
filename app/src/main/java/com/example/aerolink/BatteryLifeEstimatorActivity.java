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

public class BatteryLifeEstimatorActivity extends AppCompatActivity {

    private EditText capacityInput, loadInput, efficiencyInput;
    private TextView resultOutput;
    private Button calculateButton, clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_life_calculator);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        capacityInput = findViewById(R.id.inputBatteryCapacity);
        loadInput = findViewById(R.id.inputLoad);
        efficiencyInput = findViewById(R.id.inputEfficiency);
        resultOutput = findViewById(R.id.outputBatteryLife);
        calculateButton = findViewById(R.id.btnCalculateBatteryLife);
        clearButton = findViewById(R.id.btnClear);

        calculateButton.setOnClickListener(v -> calculateBatteryLife());
        clearButton.setOnClickListener(v -> clearFields());
    }

    private void calculateBatteryLife() {
        String capacityStr = capacityInput.getText().toString();
        String loadStr = loadInput.getText().toString();
        String efficiencyStr = efficiencyInput.getText().toString();

        if (capacityStr.isEmpty() || loadStr.isEmpty() || efficiencyStr.isEmpty()) {
            resultOutput.setText("Please fill in all fields");
            return;
        }

        double capacity = Double.parseDouble(capacityStr);
        double load = Double.parseDouble(loadStr);
        double efficiency = Double.parseDouble(efficiencyStr) / 100.0;

        if (load == 0 || efficiency == 0) {
            resultOutput.setText("Invalid input values");
            return;
        }

        double batteryLife = (capacity * efficiency) / load;
        resultOutput.setText(String.format("Battery Life: %.2f hours", batteryLife));

        hideKeyboard();
    }

    private void clearFields() {
        capacityInput.setText("");
        loadInput.setText("");
        efficiencyInput.setText("");
        resultOutput.setText("");
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}