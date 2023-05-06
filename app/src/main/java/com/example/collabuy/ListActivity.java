package com.example.collabuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        final Integer[] idList = {1,2,3,4,5,6,7,8,9,10};
        final String[] nameList={"alumno1","alumno2","alumno3","alumno4","alumno5","alumno6","alumno7","alumno8","alumno9","alumno10"};
        final Integer[] ageList={22,23,24,25,26,27,28,29,30,31};
        ArrayAdapter productAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2,android.R.id.text1,nameList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View panel = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
                        TextView name=(TextView) panel.findViewById(R.id.list_productName);
                        TextView age=(TextView) panel.findViewById(R.id.list_productAmount);
                        name.setText(nameList[position]);
                        age.setText(ageList[position].toString());

                        //When a product is clicked show its content in ProductActivity
                        panel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(ListActivity.this, "Show product " + idList[position], Toast.LENGTH_SHORT).show();
                            }
                        });
                        return panel;
                    }
                };
        ListView lalista = findViewById(R.id.list_productList);
        lalista.setAdapter(productAdapter);
    }
}