package com.das.entrega1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ModificarFragment extends Fragment {

    private int idPrenda; // Aquí guardaremos el ID de la prenda a editar

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modificarprenda, container, false);

        EditText etNombre = view.findViewById(R.id.etEditarNombre);

        // El RadioGroup que sustituye al Spinner
        android.widget.RadioGroup rgCategoria = view.findViewById(R.id.rgCategoria);
        Button btnActualizar = view.findViewById(R.id.btnActualizarPrenda);

        // 1. Recibimos la "mochila" (Bundle) con los datos de la prenda
        Bundle datosRecibidos = getArguments();
        if (datosRecibidos != null) {
            idPrenda = datosRecibidos.getInt("ID");
            etNombre.setText(datosRecibidos.getString("NOMBRE"));

            // --- CORREGIDO: Marcamos el RadioButton correcto según la BD ---
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

        // 2. ¿Qué pasa al pulsar Actualizar?
        btnActualizar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString();

            // Averiguamos qué botón redondo está marcado
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
                // --- CORREGIDO: Usamos la variable categoriaInterna ---
                BDGestor bdHelper = new BDGestor(getActivity());
                boolean actualizado = bdHelper.actualizarPrenda(idPrenda, nuevoNombre, categoriaInterna);

                if (actualizado) {
                    Toast.makeText(getActivity(), "Prenda modificada", Toast.LENGTH_SHORT).show();

                    // Si estamos en horizontal, borramos el panel derecho después de guardar
                    View huecoDerecho = getActivity().findViewById(R.id.fragment_container_detalle);
                    if (huecoDerecho != null && huecoDerecho.getVisibility() == View.VISIBLE) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                    }

                    // Refrescamos la lista del armario
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ArmarioFragment())
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Error al modificar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "No dejes el nombre vacío", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}