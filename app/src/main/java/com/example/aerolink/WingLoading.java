package com.example.aerolink;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WingLoading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wing_loading);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        EditText inputMass = findViewById(R.id.input_mass);
        EditText inputWingArea = findViewById(R.id.input_wing_area);
        Button btnCalculate = findViewById(R.id.btn_calculate);
        TextView outputResult = findViewById(R.id.output_result);
        ImageView imageView = findViewById(R.id.imageView);

        outputResult.setText("Wing Cubic Loading:");


        btnCalculate.setOnClickListener(v -> {
            String massText = inputMass.getText().toString().trim();
            String wingAreaText = inputWingArea.getText().toString().trim();

            if (massText.isEmpty() || wingAreaText.isEmpty()) {
                outputResult.setText("Wing Cubic Loading: Please enter values for both fields.");
                return;
            }

            try {
                double massKg = Double.parseDouble(massText);
                double wingAreaM2 = Double.parseDouble(wingAreaText);

                if (massKg <= 0 || wingAreaM2 <= 0) {
                    outputResult.setText("Wing Cubic Loading: Mass and Wing Area must be greater than zero.");
                    return;
                }

                double weightOunce = massKg * 35.274;
                double wingAreaSqFt = (wingAreaM2 * 1550) / 144;
                double wingCubicLoading = weightOunce / Math.pow(wingAreaSqFt, 1.5);

                outputResult.setText("Wing Cubic Loading: " + String.format("%.2f", wingCubicLoading));

                hideKeyboard();
            } catch (NumberFormatException e) {
                outputResult.setText("Wing Cubic Loading: Please enter valid numbers.");
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}