package com.example.collabuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.collabuy.ConexionPHP;
import com.example.collabuy.ListActivity;
import com.example.collabuy.ProductActivity;
import com.example.collabuy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ProductListAdapter extends BaseAdapter {

    private JSONArray list;
    private Context c;
    private String listId;
    private View.OnClickListener listener;

    public ProductListAdapter(JSONArray list, Context context, String listId){
        this.list = list;
        this.c = context;
        this.listId = listId;
    }

    @Override
    public int getCount() {
        return this.list.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return this.list.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View panel;

        JSONObject element;
        try {
            element = (JSONObject) list.get(i);
            element.getString("comprado")
            boolean bought = element.getString("comprado") == "1";

            //Both layouts have the very same elements and IDs
            if (bought) {
                panel = LayoutInflater.from(c).inflate(R.layout.bought_product_list_item, viewGroup, false);
                Log.d("collalogs", "Product " + element.getString("id") + " is bought");
            }else
                panel = LayoutInflater.from(c).inflate(R.layout.product_list_item,viewGroup,false);

            TextView name = (TextView) panel.findViewById(R.id.list_productName);

            //Cross the product name out if bought
            if (bought)
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            TextView amount = (TextView) panel.findViewById(R.id.list_productAmount);
            name.setText(element.getString("nombre"));
            amount.setText(element.getString("cantidad"));

            //When a product is clicked show its content in ProductActivity
            panel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(c, ProductActivity.class);
                    try {
                        i.putExtra("productId",element.getString("id"));
                        i.putExtra("listId", listId);
                        c.startActivity(i);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            ImageButton buyButton = panel.findViewById(R.id.list_productBuy);

            if (!bought) {
                //Mark the product as bought when clicking on the shopping basket
                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Log.d("ListRegistry", "Bought " + element.getString("nombre") + " at list " + "Lista de la compra");
                            buyProduct(Integer.parseInt(element.getString("id")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }else{
                //Cancel the purchase of the product when clicking on the cross
                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Log.d("ListRegistry", "Cancelled " + element.getString("nombre") + " at list " + "Lista de la compra");
                            cancelProduct(Integer.parseInt(element.getString("id")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            return panel;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void buyProduct(int productId) {
        markBought(productId, 1);
    }

    private void cancelProduct(int productId) {
        markBought(productId, 0);
    }

    private void markBought(int productId, int state) {
        Data data = new Data.Builder()
                .putString("url", "marcarComprado.php")
                .putString("productId", productId+"")
                .putString("listId", listId)
                .putString("comprado", state+"")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(c).getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) c, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String result = workInfo.getOutputData().getString("resultado");

                            if(Objects.isNull(result)) {
                                Log.d("productList", "Connection error, null result");
                            }

                            if(state == 1)
                                Log.d("collalogs", "Product " + productId + " has been bought");
                            else
                                Log.d("collalogs", "Product " + productId + " has been cancelled");

                        }
                    }
                });
        WorkManager.getInstance(c).enqueue(otwr);
    }

}
