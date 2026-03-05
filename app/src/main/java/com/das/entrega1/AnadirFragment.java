package com.das.entrega1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AnadirFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el diseño del fragmento (crear  y cargar el diseño de la pantalla "Añadir Prenda")
        View view = inflater.inflate(R.layout.anadirprenda, container, false);

        // Enlazamos las variables con el XML
        EditText etNombre = view.findViewById(R.id.etNombrePrenda);
        EditText etCategoria = view.findViewById(R.id.etCategoriaPrenda);
        Button btnGuardar = view.findViewById(R.id.btnGuardarPrenda);

        // Sustituye el bloque antiguo por este adaptado a la BD
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String categoria = etCategoria.getText().toString();

            if (!nombre.isEmpty() && !categoria.isEmpty()) {

                // Usamos el Gestor de la BD
                BDGestor bdHelper = new BDGestor(getActivity());
                boolean insertado = bdHelper.insertarPrenda(nombre, categoria);

                if (insertado) {
                    Toast.makeText(getActivity(), "¡Prenda guardada en la base de datos!", Toast.LENGTH_SHORT).show();

                    // Limpiamos los campos
                    etNombre.setText("");
                    etCategoria.setText("");

                } else {
                    Toast.makeText(getActivity(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}