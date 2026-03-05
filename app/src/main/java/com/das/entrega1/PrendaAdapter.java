package com.das.entrega1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PrendaAdapter extends ArrayAdapter<Prenda> {

    public PrendaAdapter(Context context, ArrayList<Prenda> prendas) {
        super(context, 0, prendas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtenemos la prenda actual
        Prenda prendaActual = getItem(position);

        // Si la vista no existe, la "inflamos" usando nuestro diseño de CardView
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.prenda, parent, false);
        }

        // Enlazamos con los TextView de la tarjeta
        TextView tvNombre = convertView.findViewById(R.id.tvNombrePrenda);
        TextView tvCategoria = convertView.findViewById(R.id.tvCategoriaPrenda);

        // Ponemos los textos
        tvNombre.setText(prendaActual.getNombre());
        tvCategoria.setText(prendaActual.getCategoria());

        return convertView;
    }
}
