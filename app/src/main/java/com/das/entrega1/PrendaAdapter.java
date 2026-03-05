package com.das.entrega1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PrendaAdapter extends RecyclerView.Adapter<PrendaAdapter.PrendaViewHolder> {

    private ArrayList<Prenda> listaRopa;
    private OnItemClickListener listener;

    // 1. Creamos una "antena" (interfaz) para escuchar los clics
    public interface OnItemClickListener {
        void onClicNormal(Prenda prendaSeleccionada);
        void onClicLargo(Prenda prendaSeleccionada, int posicion);
    }

    // 2. Constructor
    public PrendaAdapter(ArrayList<Prenda> listaRopa, OnItemClickListener listener) {
        this.listaRopa = listaRopa;
        this.listener = listener;
    }

    // 3. Inflamos el diseño de la tarjeta (item_prenda.xml)
    @NonNull
    @Override
    public PrendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prenda, parent, false);
        return new PrendaViewHolder(view);
    }

    // 4. Rellenamos los datos de la tarjeta en la posición actual
    @Override
    public void onBindViewHolder(@NonNull PrendaViewHolder holder, int position) {
        Prenda prendaActual = listaRopa.get(position);

        holder.tvNombre.setText(prendaActual.getNombre());
        holder.tvCategoria.setText(prendaActual.getCategoria());

        // Configuramos los clics
        holder.itemView.setOnClickListener(v -> listener.onClicNormal(prendaActual));

        holder.itemView.setOnLongClickListener(v -> {
            listener.onClicLargo(prendaActual, position);
            return true; // true significa que ya hemos gestionado el clic largo
        });
    }

    // 5. Decimos cuántos elementos hay en total
    @Override
    public int getItemCount() {
        return listaRopa.size();
    }

    // --- CLASE INTERNA VIEWHOLDER ---
    // Esta clase "sujeta" los elementos visuales para no buscarlos Toodo el rato
    public static class PrendaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria;

        public PrendaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePrenda);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaPrenda);
        }
    }
}