package com.example.collabuy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CamaraGaleriaDialog extends DialogFragment {
    ListenerdelDialogo listener;
    public interface ListenerdelDialogo {
        void camara();
        void galeria();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        listener =(ListenerdelDialogo) getActivity();
        builder.setTitle("Elige una de las opciones");
        CharSequence[] opciones = {"Camara", "Galeria"};
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    listener.camara();
                }else{
                    listener.galeria();
                }
            }
        });
        return builder.create();
    }
}
