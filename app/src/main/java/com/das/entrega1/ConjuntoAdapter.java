package com.das.entrega1;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ConjuntoAdapter extends RecyclerView.Adapter<ConjuntoAdapter.ConjuntoViewHolder> {
    private ArrayList<Conjunto> listaConjuntos;
    private OnItemClickListener listener; // NUEVO

    // 1. Creamos la antena para el clic largo
    public interface OnItemClickListener {
        void onClicLargo(Conjunto conjuntoSeleccionado, int posicion);
    }

    // 2. Actualizamos el constructor
    public ConjuntoAdapter(ArrayList<Conjunto> listaConjuntos, OnItemClickListener listener) {
        this.listaConjuntos = listaConjuntos;
        this.listener = listener;
    }

    // ... (onCreateViewHolder se queda igual)

    @NonNull
    @Override
    public ConjuntoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conjunto, parent, false);
        return new ConjuntoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuntoViewHolder holder, int position) {
        Conjunto conjunto = listaConjuntos.get(position);

        // Función rápida para cargar la imagen o poner una por defecto
        cargarImagen(holder.ivArriba, conjunto.getArriba().getUriFoto());
        cargarImagen(holder.ivAbajo, conjunto.getAbajo().getUriFoto());
        cargarImagen(holder.ivCalzado, conjunto.getCalzado().getUriFoto());

        // NUEVO: El clic largo para borrar
        holder.itemView.setOnLongClickListener(v -> {
            listener.onClicLargo(conjunto, position);
            return true;
        });
    }

    private void cargarImagen(ImageView iv, String uriString) {
        if (uriString != null && !uriString.isEmpty()) {
            try {
                iv.setImageURI(Uri.parse(uriString));
            } catch (Exception e) {
                iv.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            iv.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() { return listaConjuntos.size(); }

    public static class ConjuntoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivArriba, ivAbajo, ivCalzado;
        public ConjuntoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArriba = itemView.findViewById(R.id.ivConjuntoArriba);
            ivAbajo = itemView.findViewById(R.id.ivConjuntoAbajo);
            ivCalzado = itemView.findViewById(R.id.ivConjuntoCalzado);
        }
    }
}