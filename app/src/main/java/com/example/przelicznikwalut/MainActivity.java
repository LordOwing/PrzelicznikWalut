package com.example.przelicznikwalut;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText amountInput;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private Button convertButton;
    private TextView resultText;
    private ImageView fromCurrencyFlag;
    private ImageView toCurrencyFlag;

    private final String[] currencies = {"PLN", "EUR", "USD", "GBP"};


    private final double EUR_TO_PLN = 4.30;
    private final double USD_TO_PLN = 4.00;
    private final double GBP_TO_PLN = 5.00;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "CurrencyPrefs";
    private static final String FROM_CURRENCY_KEY = "fromCurrency";
    private static final String TO_CURRENCY_KEY = "toCurrency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.amountInput);
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        convertButton = findViewById(R.id.convertButton);
        resultText = findViewById(R.id.resultText);
        fromCurrencyFlag = findViewById(R.id.fromCurrencyFlag);
        toCurrencyFlag = findViewById(R.id.toCurrencyFlag);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }


    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);
    }

    private void setupListeners() {

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
                savePreferences();
            }
        });


        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFlagImage(fromCurrencyFlag, currencies[position]);
                convertCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFlagImage(toCurrencyFlag, currencies[position]);
                convertCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                convertCurrency();
            }
        });
    }


    private void convertCurrency() {
        String amountStr = amountInput.getText().toString();


        if (amountStr.isEmpty()) {
            resultText.setText("Wprowadź kwotę");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                resultText.setText("Kwota nie może być ujemna");
                return;
            }

            String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
            String toCurrency = toCurrencySpinner.getSelectedItem().toString();

            double result = calculateConversion(amount, fromCurrency, toCurrency);
            resultText.setText(String.format("%.2f %s = %.2f %s", amount, fromCurrency, result, toCurrency));

        } catch (NumberFormatException e) {
            resultText.setText("Nieprawidłowy format liczby");
        }
    }


    private double calculateConversion(double amount, String fromCurrency, String toCurrency) {

        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }


        double amountInPln = convertToPln(amount, fromCurrency);
        return convertFromPln(amountInPln, toCurrency);
    }

    private double convertToPln(double amount, String currency) {
        switch (currency) {
            case "EUR": return amount * EUR_TO_PLN;
            case "USD": return amount * USD_TO_PLN;
            case "GBP": return amount * GBP_TO_PLN;
            case "PLN": return amount;
            default: return amount;
        }
    }

    private double convertFromPln(double amountInPln, String currency) {
        switch (currency) {
            case "EUR": return amountInPln / EUR_TO_PLN;
            case "USD": return amountInPln / USD_TO_PLN;
            case "GBP": return amountInPln / GBP_TO_PLN;
            case "PLN": return amountInPln;
            default: return amountInPln;
        }
    }


    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FROM_CURRENCY_KEY, fromCurrencySpinner.getSelectedItemPosition());
        editor.putInt(TO_CURRENCY_KEY, toCurrencySpinner.getSelectedItemPosition());
        editor.apply();
    }

    private void loadSavedPreferences() {
        int savedFromPosition = sharedPreferences.getInt(FROM_CURRENCY_KEY, 0);
        int savedToPosition = sharedPreferences.getInt(TO_CURRENCY_KEY, 1);

        fromCurrencySpinner.setSelection(savedFromPosition);
        toCurrencySpinner.setSelection(savedToPosition);


        updateFlagImage(fromCurrencyFlag, currencies[savedFromPosition]);
        updateFlagImage(toCurrencyFlag, currencies[savedToPosition]);
    }


    private void updateFlagImage(ImageView imageView, String currency) {
        int flagResource;
        switch (currency) {
            case "PLN":
                flagResource = R.drawable.pln_flag;
                break;
            case "EUR":
                flagResource = R.drawable.eur_flag;
                break;
            case "USD":
                flagResource = R.drawable.usd_flag;
                break;
            case "GBP":
                flagResource = R.drawable.gbp_flag;
                break;
            default:
                flagResource = R.drawable.pln_flag;
        }
        imageView.setImageResource(flagResource);
    }
}
