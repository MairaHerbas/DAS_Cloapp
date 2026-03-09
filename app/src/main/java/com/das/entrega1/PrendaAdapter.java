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
    public interface OnItemClickListener {
        void onClicNormal(Prenda prendaSeleccionada);
        void onClicLargo(Prenda prendaSeleccionada, int posicion);
    }
    public PrendaAdapter(ArrayList<Prenda> listaRopa, OnItemClickListener listener) {
        this.listaRopa = listaRopa;
        this.listener = listener;
    }

    // 3. Cargar el diseño de la tarjeta
    @NonNull
    @Override
    public PrendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prenda, parent, false);
        return new PrendaViewHolder(view);
    }

    // 4. Rellenar los datos de la tarjeta actual
    @Override
    public void onBindViewHolder(@NonNull PrendaViewHolder holder, int position) {
        Prenda prendaActual = listaRopa.get(position);

        // Cargar datos
        holder.tvNombre.setText(prendaActual.getNombre());

        String catInterna = prendaActual.getCategoria();
        if (catInterna.equals("arriba")) {
            holder.tvCategoria.setText(holder.itemView.getContext().getString(R.string.cat_arriba));
        } else if (catInterna.equals("abajo")) {
            holder.tvCategoria.setText(holder.itemView.getContext().getString(R.string.cat_abajo));
        } else if (catInterna.equals("calzado")) {
            holder.tvCategoria.setText(holder.itemView.getContext().getString(R.string.cat_calzado));
        } else {
            holder.tvCategoria.setText(catInterna);
        }

        // Cargar foto
        if (prendaActual.getUriFoto() != null && !prendaActual.getUriFoto().isEmpty()) {
            try {
                holder.ivFoto.setImageURI(android.net.Uri.parse(prendaActual.getUriFoto()));
            } catch (Exception e) {
                holder.ivFoto.setImageResource(android.R.drawable.ic_menu_gallery); // Foto por defecto si falla
            }
        } else {
            holder.ivFoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Activar clics
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClicNormal(prendaActual);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onClicLargo(prendaActual, position);
            }
            return true;
        });
    }

    //Obtener num elementos
    @Override
    public int getItemCount() {
        return listaRopa.size();
    }

    //CLASE INTERNA VIEWHOLDER
    public static class PrendaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria;
        android.widget.ImageView ivFoto;
        public PrendaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePrenda);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaPrenda);
            ivFoto = itemView.findViewById(R.id.ivFotoPrenda); // NUEVO
        }
    }
}