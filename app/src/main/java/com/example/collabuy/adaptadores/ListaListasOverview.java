package com.example.collabuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.collabuy.ListActivity;
import com.example.collabuy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class ListaListasOverview extends BaseAdapter{

    //  atributos
    private JSONArray lista;
    private Context c;
    private View.OnClickListener listener;

    public ListaListasOverview(JSONArray listas, Context context){
        this.lista = listas;
        this.c = context;
    }

    @Override
    public int getCount() {
        return this.lista.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return this.lista.get(i);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        JSONObject elemento;
        ViewHolder holder;

        if (view == null){
            LayoutInflater inflater = LayoutInflater.from(this.c);
            view = inflater.inflate(R.layout.item_listalistas, viewGroup, false);

            // se identifican los objetos del layout
            holder = new ViewHolder();
            holder.tituloTextView = view.findViewById(R.id.tituloTextView);
            holder.codigoTextView = view.findViewById(R.id.claveListaTextView);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        try {
            elemento = (JSONObject) lista.get(i);
            holder.tituloTextView.setText(elemento.getString("nombre"));
            holder.codigoTextView.setText(elemento.getString("clave"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, ListActivity.class);
                try {
                    intent.putExtra("idLista", elemento.getString("id"));
                    c.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }


        //se definen los atributos que necesitamos en la vista
        private static class ViewHolder{
            TextView tituloTextView;
            TextView codigoTextView;
        }
}

