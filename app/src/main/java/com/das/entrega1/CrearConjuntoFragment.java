package com.das.entrega1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class CrearConjuntoFragment extends Fragment {
    private Prenda prendaArribaSeleccionada = null;
    private Prenda prendaAbajoSeleccionada = null;
    private Prenda prendaCalzadoSeleccionada = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.crearconjunto, container, false);

        Button btnElegirArriba = view.findViewById(R.id.btnElegirArriba);
        Button btnElegirAbajo = view.findViewById(R.id.btnElegirAbajo);
        Button btnElegirCalzado = view.findViewById(R.id.btnElegirCalzado);
        Button btnGuardar = view.findViewById(R.id.btnGuardarConjunto);

        BDGestor bdHelper = new BDGestor(getActivity());
        ArrayList<Prenda> listaArriba = bdHelper.obtenerPrendasPorCategoria("arriba");
        ArrayList<Prenda> listaAbajo = bdHelper.obtenerPrendasPorCategoria("abajo");
        ArrayList<Prenda> listaCalzado = bdHelper.obtenerPrendasPorCategoria("calzado");

        if (listaArriba.isEmpty() || listaAbajo.isEmpty() || listaCalzado.isEmpty()) {
            Toast.makeText(getActivity(), "¡Añade al menos una prenda de cada tipo!", Toast.LENGTH_LONG).show();
            btnGuardar.setEnabled(false);
        }

        // Dialogos de selección
        btnElegirArriba.setOnClickListener(v -> mostrarDialogoSeleccion(listaArriba, "Elige parte de arriba", btnElegirArriba, 1));
        btnElegirAbajo.setOnClickListener(v -> mostrarDialogoSeleccion(listaAbajo, "Elige parte de abajo", btnElegirAbajo, 2));
        btnElegirCalzado.setOnClickListener(v -> mostrarDialogoSeleccion(listaCalzado, "Elige calzado", btnElegirCalzado, 3));

        //Boton para guardar el conjunto
        btnGuardar.setOnClickListener(v -> {
            if (prendaArribaSeleccionada != null && prendaAbajoSeleccionada != null && prendaCalzadoSeleccionada != null) {
                if (bdHelper.existeConjunto(prendaArribaSeleccionada.getId(), prendaAbajoSeleccionada.getId(), prendaCalzadoSeleccionada.getId())) {
                    Toast.makeText(getActivity(), "¡Este conjunto ya existe!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean guardado = bdHelper.insertarConjunto(prendaArribaSeleccionada.getId(), prendaAbajoSeleccionada.getId(), prendaCalzadoSeleccionada.getId());
                    if (guardado) {
                        Toast.makeText(getActivity(), getString(R.string.msg_conjunto_guardado), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "Selecciona las 3 prendas", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void mostrarDialogoSeleccion(ArrayList<Prenda> listaRopa, String titulo, Button botonActualizar, int tipoPrenda) {
        // Convertir la lista a un Array para el Diálogo
        String[] nombresRopa = new String[listaRopa.size()];
        for (int i = 0; i < listaRopa.size(); i++) {
            nombresRopa[i] = listaRopa.get(i).getNombre();
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(titulo)
                .setSingleChoiceItems(nombresRopa, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Prenda elegida = listaRopa.get(which);
                        if (tipoPrenda == 1) prendaArribaSeleccionada = elegida;
                        else if (tipoPrenda == 2) prendaAbajoSeleccionada = elegida;
                        else prendaCalzadoSeleccionada = elegida;

                        botonActualizar.setText(elegida.getNombre());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancelar), null)
                .show();
    }
}