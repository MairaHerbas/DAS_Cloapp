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
        EditText etCategoria = view.findViewById(R.id.etEditarCategoria);
        Button btnActualizar = view.findViewById(R.id.btnActualizarPrenda);

        // 1. Recibimos la "mochila" (Bundle) con los datos de la prenda
        Bundle datosRecibidos = getArguments();
        if (datosRecibidos != null) {
            idPrenda = datosRecibidos.getInt("ID");
            etNombre.setText(datosRecibidos.getString("NOMBRE"));
            etCategoria.setText(datosRecibidos.getString("CATEGORIA"));
        }

        // 2. ¿Qué pasa al pulsar Actualizar?
        btnActualizar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString();
            String nuevaCategoria = etCategoria.getText().toString();

            if (!nuevoNombre.isEmpty() && !nuevaCategoria.isEmpty()) {
                // Actualizamos en la Base de Datos
                BDGestor bdHelper = new BDGestor(getActivity());
                boolean actualizado = bdHelper.actualizarPrenda(idPrenda, nuevoNombre, nuevaCategoria);

                if (actualizado) {
                    Toast.makeText(getActivity(), "Prenda modificada", Toast.LENGTH_SHORT).show();

                    // Volvemos automáticamente a la pantalla del Armario
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ArmarioFragment())
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Error al modificar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "No dejes campos vacíos", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}