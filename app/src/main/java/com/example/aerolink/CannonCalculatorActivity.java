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

public class CannonCalculatorActivity extends AppCompatActivity {

    private EditText speedInput, angleInput, heightInput;
    private TextView timeOutput, heightOutput, rangeOutput;
    private Button calculateButton, clearButton;
    private static final double GRAVITY = 9.81; // m/s^2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cannon_calculator);

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        speedInput    = (EditText) findViewById(R.id.speedInput);
        angleInput    = (EditText) findViewById(R.id.angleInput);
        heightInput   = (EditText) findViewById(R.id.heightInput);

        timeOutput    = (TextView) findViewById(R.id.timeOutput);
        heightOutput  = (TextView) findViewById(R.id.heightOutput);
        rangeOutput   = (TextView) findViewById(R.id.rangeOutput);

        calculateButton = (Button) findViewById(R.id.calculateButton);
        clearButton     = (Button) findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(v -> calculateProjectile());
        clearButton.setOnClickListener(v -> clearFields());
    }

    private void calculateProjectile() {
        double v0 = parseDouble(speedInput);
        double angleDeg = parseDouble(angleInput);
        double h0 = parseDouble(heightInput);

        double theta = Math.toRadians(angleDeg);
        double v0x = v0 * Math.cos(theta);
        double v0y = v0 * Math.sin(theta);

        // Time of flight: solve y = h0 + v0y*t - 0.5*g*t^2 = 0
        double discriminant = v0y*v0y + 2*GRAVITY*h0;
        double tFlight = (v0y + Math.sqrt(discriminant)) / GRAVITY;

        // Max height: h0 + v0y^2/(2*g)
        double hMax = h0 + (v0y*v0y) / (2*GRAVITY);

        // Range: v0x * time
        double range = v0x * tFlight;

        timeOutput.setText(String.format("Time of Flight: %.2f s", tFlight));
        heightOutput.setText(String.format("Max Height: %.2f m", hMax));
        rangeOutput.setText(String.format("Range: %.2f m", range));

        hideKeyboard();
    }

    private double parseDouble(EditText e) {
        String s = e.getText().toString().trim();
        return s.isEmpty() ? 0 : Double.parseDouble(s);
    }

    private void clearFields() {
        speedInput.setText("");
        angleInput.setText("");
        heightInput.setText("");
        timeOutput.setText("Time of Flight: -");
        heightOutput.setText("Max Height: -");
        rangeOutput.setText("Range: -");
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}