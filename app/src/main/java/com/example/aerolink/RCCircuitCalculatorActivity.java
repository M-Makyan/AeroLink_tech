// package and imports
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

public class RCCircuitCalculatorActivity extends AppCompatActivity {

    private EditText lengthInput, widthInput, layersInput, copperWeightInput, quantityInput, priceInput;
    private TextView boardAreaOutput, costPerBoardOutput, totalCostOutput, boardsPerPanelOutput;
    private Button calculateButton, clearButton;

    // panel constants (mm)
    private static final double PANEL_W = 450;
    private static final double PANEL_L = 600;
    private static final double BORDER = 10;
    private static final double SPACING = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rc_circuit_calculator);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        lengthInput           = findViewById(R.id.lengthInput);
        widthInput            = findViewById(R.id.widthInput);
        layersInput           = findViewById(R.id.layersInput);
        copperWeightInput     = findViewById(R.id.copperWeightInput);
        quantityInput         = findViewById(R.id.quantityInput);
        priceInput            = findViewById(R.id.priceInput);

        boardAreaOutput       = findViewById(R.id.boardAreaOutput);
        costPerBoardOutput    = findViewById(R.id.costPerBoardOutput);
        totalCostOutput       = findViewById(R.id.totalCostOutput);
        boardsPerPanelOutput  = findViewById(R.id.boardsPerPanelOutput);

        calculateButton = findViewById(R.id.calculateButton);
        clearButton     = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(v -> calculatePCB());
        clearButton.setOnClickListener(v -> clearFields());
    }

    private void calculatePCB() {
        // parse inputs (or treat empty as zero)
        double length       = parseDouble(lengthInput);
        double width        = parseDouble(widthInput);
        double layers       = parseDouble(layersInput);
        double copperWeight = parseDouble(copperWeightInput);
        int    qty          = (int) parseDouble(quantityInput);
        double pricePerCm2  = parseDouble(priceInput);

        // 1) area in mm² then cm²
        double areaMm2 = length * width;
        double areaCm2 = areaMm2 / 100.0;
        boardAreaOutput.setText(String.format("Total Board Area: %.2f cm²", areaCm2));

        // 2) cost per board
        //    we assume cost scales linearly with number of layers and copper weight:
        double costPerBoard = areaCm2 * pricePerCm2 * layers * copperWeight;
        costPerBoardOutput.setText(String.format("Cost per Board: %.2f", costPerBoard));

        // 3) total cost
        double totalCost = costPerBoard * qty;
        totalCostOutput.setText(String.format("Total Cost: %.2f", totalCost));

        // 4) how many boards per standard panel?
        double usableW = PANEL_W - 2 * BORDER + SPACING;
        double usableL = PANEL_L - 2 * BORDER + SPACING;
        int fitX = (int) Math.floor(usableW / (length + SPACING));
        int fitY = (int) Math.floor(usableL / (width  + SPACING));
        int perPanel = Math.max(0, fitX * fitY);
        boardsPerPanelOutput.setText("Boards per Panel: " + perPanel);

        hideKeyboard();
    }

    private double parseDouble(EditText e) {
        String s = e.getText().toString().trim();
        return s.isEmpty() ? 0 : Double.parseDouble(s);
    }

    private void clearFields() {
        lengthInput.setText("");
        widthInput.setText("");
        layersInput.setText("");
        copperWeightInput.setText("");
        quantityInput.setText("");
        priceInput.setText("");

        boardAreaOutput.setText("Total Board Area: -");
        costPerBoardOutput.setText("Cost per Board: -");
        totalCostOutput.setText("Total Cost: -");
        boardsPerPanelOutput.setText("Boards per Panel: -");
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}