package com.example.collabuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        createList();
        getSupportActionBar().setTitle("Lista de la compra");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.list_add:
                Toast.makeText(this, "Currently unimplemented feature", Toast.LENGTH_SHORT).show();
                break;
            case R.id.list_abandon:
                Toast.makeText(this, "Currently unimplemented feature", Toast.LENGTH_SHORT).show();
                break;
            case R.id.list_settings:
                Toast.makeText(this, "Currently unimplemented feature", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createList(){
        final Integer[] idList = {0,1,2,3,4,5,6,7,8,9};
        final String[] nameList={"producto1","producto2","producto3","producto4","producto5","producto6","producto7","producto8","producto9","producto10"};
        final Integer[] amountList={22,23,24,25,26,27,28,29,30,31};
        ArrayAdapter productAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2,android.R.id.text1,nameList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View panel = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
                        TextView name=(TextView) panel.findViewById(R.id.list_productName);
                        TextView age=(TextView) panel.findViewById(R.id.list_productAmount);
                        name.setText(nameList[position]);
                        age.setText(amountList[position].toString());

                        //When a product is clicked show its content in ProductActivity
                        panel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(ListActivity.this, ProductActivity.class);
                                i.putExtra("id",idList[position]);
                                ListActivity.this.startActivity(i);
                            }
                        });

                        ImageButton buyButton = panel.findViewById(R.id.list_productBuy);
                        buyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("ListRegistry", "Bought " + nameList[position] + " at list " + "Lista de la compra");
                                Toast.makeText(ListActivity.this, "Bought product " + idList[position], Toast.LENGTH_SHORT).show();
                            }
                        });

                        return panel;
                    }
                };
        ListView lalista = findViewById(R.id.list_productList);
        lalista.setAdapter(productAdapter);
    }
}