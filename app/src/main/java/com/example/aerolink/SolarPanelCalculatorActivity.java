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

public class SolarPanelCalculatorActivity extends AppCompatActivity {

    private EditText batteryCapacityInput, panelPowerInput, sunHoursInput, voltageInput;
    private TextView chargeEstimateOutput, panelSizeOutput, dailyEnergyOutput, chargeTimeOutput;
    private Button calculateButton, clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar_panel_calculator);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        batteryCapacityInput = findViewById(R.id.batteryCapacityInput);
        panelPowerInput = findViewById(R.id.panelPowerInput);
        sunHoursInput = findViewById(R.id.sunHoursInput);
        voltageInput = findViewById(R.id.voltageInput);
        chargeEstimateOutput = findViewById(R.id.chargeEstimateOutput);
        panelSizeOutput = findViewById(R.id.panelSizeOutput);
        dailyEnergyOutput = findViewById(R.id.dailyEnergyOutput);
        chargeTimeOutput = findViewById(R.id.chargeTimeOutput);
        calculateButton = findViewById(R.id.calculateButton);
        clearButton = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(view -> calculateSolarEnergy());
        clearButton.setOnClickListener(view -> clearFields());
    }

    private void calculateSolarEnergy() {
        String batteryCapacityStr = batteryCapacityInput.getText().toString();
        String panelPowerStr = panelPowerInput.getText().toString();
        String sunHoursStr = sunHoursInput.getText().toString();
        String voltageStr = voltageInput.getText().toString();

        Double batteryCapacity = batteryCapacityStr.isEmpty() ? null : Double.parseDouble(batteryCapacityStr);
        Double panelPower = panelPowerStr.isEmpty() ? null : Double.parseDouble(panelPowerStr);
        Double sunHours = sunHoursStr.isEmpty() ? null : Double.parseDouble(sunHoursStr);
        Double voltage = voltageStr.isEmpty() ? null : Double.parseDouble(voltageStr);

        if (panelPower != null) {
            double panelSize = panelPower / 1000; // Assuming 1kW per m²
            panelSizeOutput.setText(String.format("Required Panel Size: %.2f m²", panelSize));
        } else {
            panelSizeOutput.setText("Required Panel Size: -");
        }

        if (panelPower != null && sunHours != null) {
            double dailyEnergy = panelPower * sunHours;
            dailyEnergyOutput.setText(String.format("Daily Solar Energy: %.2f Wh/day", dailyEnergy));
        } else {
            dailyEnergyOutput.setText("Daily Solar Energy: -");
        }

        if (batteryCapacity != null && panelPower != null && sunHours != null && voltage != null) {
            double chargeEstimate = (panelPower * sunHours) / voltage;
            chargeEstimateOutput.setText(String.format("Battery Charge Estimate: %.2f Ah", chargeEstimate));

            double chargingTime = batteryCapacity / (panelPower / voltage);
            chargeTimeOutput.setText(String.format("Full Charge Time: %.2f hours", chargingTime));
        } else {
            chargeEstimateOutput.setText("Battery Charge Estimate: -");
            chargeTimeOutput.setText("Full Charge Time: -");
        }

        hideKeyboard();
    }

    private void clearFields() {
        batteryCapacityInput.setText("");
        panelPowerInput.setText("");
        sunHoursInput.setText("");
        voltageInput.setText("");
        chargeEstimateOutput.setText("Battery Charge Estimate: -");
        panelSizeOutput.setText("Required Panel Size: -");
        dailyEnergyOutput.setText("Daily Solar Energy: -");
        chargeTimeOutput.setText("Full Charge Time: -");
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}