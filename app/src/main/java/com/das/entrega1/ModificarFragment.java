package com.das.entrega1;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ModificarFragment extends Fragment {
    private int idPrenda;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modificarprenda, container, false);

        EditText etNombre = view.findViewById(R.id.etEditarNombre);
        android.widget.RadioGroup rgCategoria = view.findViewById(R.id.rgCategoria);
        Button btnActualizar = view.findViewById(R.id.btnActualizarPrenda);

        Bundle datosRecibidos = getArguments();
        if (datosRecibidos != null) {
            idPrenda = datosRecibidos.getInt("ID");
            etNombre.setText(datosRecibidos.getString("NOMBRE"));
            // Marcar el RadioButton
            String catInterna = datosRecibidos.getString("CATEGORIA");
            if (catInterna != null) {
                if (catInterna.equals("arriba")) {
                    rgCategoria.check(R.id.rbArriba);
                } else if (catInterna.equals("abajo")) {
                    rgCategoria.check(R.id.rbAbajo);
                } else if (catInterna.equals("calzado")) {
                    rgCategoria.check(R.id.rbCalzado);
                }
            }
        }

        btnActualizar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString();

            // botón está marcado en el RadioGroup?
            int idSeleccionado = rgCategoria.getCheckedRadioButtonId();
            String categoriaInterna = "";

            if (idSeleccionado == R.id.rbArriba) {
                categoriaInterna = "arriba";
            } else if (idSeleccionado == R.id.rbAbajo) {
                categoriaInterna = "abajo";
            } else {
                categoriaInterna = "calzado";
            }

            if (!nuevoNombre.isEmpty()) {
                Uri uriPrenda = Uri.parse(RopaProvider.CONTENT_URI + "/" + idPrenda);

                ContentValues valoresNuevos = new ContentValues();
                valoresNuevos.put("nombre", nuevoNombre);
                valoresNuevos.put("categoria", categoriaInterna);

                int filasModificadas = requireActivity().getContentResolver().update(uriPrenda, valoresNuevos, null, null);

                if (filasModificadas > 0) {
                    Toast.makeText(getActivity(), "Prenda modificada vía Provider", Toast.LENGTH_SHORT).show();
                    View huecoDerecho = getActivity().findViewById(R.id.fragment_container_detalle);
                    if (huecoDerecho != null && huecoDerecho.getVisibility() == View.VISIBLE) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                    }

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ArmarioFragment())
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Error al modificar con Provider", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "No dejes el nombre vacío", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}