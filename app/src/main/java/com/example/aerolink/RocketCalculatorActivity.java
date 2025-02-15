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

public class RocketCalculatorActivity extends AppCompatActivity {

    private EditText exhaustVelocityInput, initialMassInput, finalMassInput, thrustInput, massFlowRateInput;
    private TextView deltaVOutput, specificImpulseOutput, thrustOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_calculator);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        exhaustVelocityInput = findViewById(R.id.exhaustVelocityInput);
        initialMassInput = findViewById(R.id.initialMassInput);
        finalMassInput = findViewById(R.id.finalMassInput);
        thrustInput = findViewById(R.id.thrustInput);
        massFlowRateInput = findViewById(R.id.massFlowRateInput);

        deltaVOutput = findViewById(R.id.deltaVOutput);
        specificImpulseOutput = findViewById(R.id.specificImpulseOutput);
        thrustOutput = findViewById(R.id.thrustOutput);

        Button calculateButton = findViewById(R.id.calculateButton);
        Button clearButton = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(v -> calculateValues());
        clearButton.setOnClickListener(v -> clearFields());
    }

    private void calculateValues() {
        try {
            double exhaustVelocity = Double.parseDouble(exhaustVelocityInput.getText().toString());
            double initialMass = Double.parseDouble(initialMassInput.getText().toString());
            double finalMass = Double.parseDouble(finalMassInput.getText().toString());
            double thrust = Double.parseDouble(thrustInput.getText().toString());
            double massFlowRate = Double.parseDouble(massFlowRateInput.getText().toString());

            double deltaV = RocketCalculator.calculateDeltaV(exhaustVelocity, initialMass, finalMass);
            double specificImpulse = RocketCalculator.calculateSpecificImpulse(thrust, massFlowRate);
            double calculatedThrust = RocketCalculator.calculateThrust(exhaustVelocity, massFlowRate);

            deltaVOutput.setText("Delta-V: " + String.format("%.2f m/s", deltaV));
            specificImpulseOutput.setText("Specific Impulse: " + String.format("%.2f s", specificImpulse));
            thrustOutput.setText("Thrust: " + String.format("%.2f N", calculatedThrust));

            hideKeyboard();
        } catch (NumberFormatException e) {
            deltaVOutput.setText("Invalid input!");
            specificImpulseOutput.setText("");
            thrustOutput.setText("");
        }
    }

    private void clearFields() {
        exhaustVelocityInput.setText("");
        initialMassInput.setText("");
        finalMassInput.setText("");
        thrustInput.setText("");
        massFlowRateInput.setText("");

        deltaVOutput.setText("Delta-V: ");
        specificImpulseOutput.setText("Specific Impulse: ");
        thrustOutput.setText("Thrust: ");
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}