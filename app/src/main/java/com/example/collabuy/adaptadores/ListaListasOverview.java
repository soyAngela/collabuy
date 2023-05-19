package com.example.collabuy.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.collabuy.R;
import java.util.ArrayList;



public class ListaListasOverview extends BaseAdapter implements View.OnClickListener{

    //  atributos
    private ArrayList<String[]> lista;
    private Context c;
    private View.OnClickListener listener;

    public ListaListasOverview(ArrayList<String[]> listas, Context context){
        this.lista = listas;
        this.c = context;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Override
    public Object getItem(int i) {
        return this.lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
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
        holder.tituloTextView.setText(this.lista.get(i)[1]);
        holder.codigoTextView.setText(this.lista.get(i)[0]);
        return view;
    }


        //se definen los atributos que necesitamos en la vista
        private static class ViewHolder{
            TextView tituloTextView;
            TextView codigoTextView;
        }
}

