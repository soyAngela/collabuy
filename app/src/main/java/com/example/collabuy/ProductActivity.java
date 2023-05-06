package com.example.collabuy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Dictionary;

public class ProductActivity extends AppCompatActivity {

    final String[] nameList={"producto1","producto2","producto3","producto4","producto5","producto6","producto7","producto8","producto9","producto10"};
    final Integer[] amountList={22,23,24,25,26,27,28,29,30,31};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Bundle extras = getIntent().getExtras();
        int productId = extras.getInt("id");
        getSupportActionBar().setTitle(nameList[productId]);
    }
}