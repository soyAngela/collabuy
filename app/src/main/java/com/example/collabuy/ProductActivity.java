package com.example.collabuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Dictionary;

public class ProductActivity extends AppCompatActivity {

    final String[] nameList={"producto1","producto2","producto3","producto4","producto5","producto6","producto7","producto8","producto9","producto10"};
    final Integer[] amountList={22,23,24,25,26,27,28,29,30,31};
    int amount;
    EditText amountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Bundle extras = getIntent().getExtras();
        int productId = extras.getInt("id");

        getSupportActionBar().setTitle(nameList[productId]);

        amount = amountList[productId];
        amountView = findViewById(R.id.product_amount);
        amountView.setText(amount+"");

        TextView descriptionView = findViewById(R.id.product_description);
        descriptionView.setText(nameList[productId]);
    }

    public void amountUp (View v){
        amount++;
        amountView.setText(amount+"");
    }

    public void amountDown (View v){
        amount--;
        amountView.setText(amount+"");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("amount", amount);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        amount = savedInstanceState.getInt("amount");
        amountView.setText(amount + "");
    }
}