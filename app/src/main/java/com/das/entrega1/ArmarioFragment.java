package com.das.entrega1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArmarioFragment extends Fragment {

    private PrendaAdapter adaptador;
    private ArrayList<Prenda> listaRopa;
    private BDGestor bdHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.armario, container, false);

        // 1. Buscamos el RecyclerView y le damos un LayoutManager (Obligatorio)
        RecyclerView rvArmario = view.findViewById(R.id.rvArmario);
        rvArmario.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 2. Cargamos los datos de la Base de Datos
        bdHelper = new BDGestor(getActivity());
        listaRopa = bdHelper.obtenerTodasLasPrendas();

        // 3. Creamos el adaptador y gestionamos los clics
        adaptador = new PrendaAdapter(listaRopa, new PrendaAdapter.OnItemClickListener() {

            // --- CLIC NORMAL: Editar Prenda ---
            // --- CLIC NORMAL: Editar Prenda ---
            @Override
            public void onClicNormal(Prenda prendaSeleccionada) {
                // 1. Preparamos el fragmento a abrir (EditarFragment)
                ModificarFragment modificarFragment = new ModificarFragment();
                Bundle mochila = new Bundle();
                mochila.putInt("ID", prendaSeleccionada.getId());
                mochila.putString("NOMBRE", prendaSeleccionada.getNombre());
                mochila.putString("CATEGORIA", prendaSeleccionada.getCategoria());
                modificarFragment.setArguments(mochila);

                // 2. Buscamos si existe el hueco de la derecha (solo existe al girar el móvil)
                View huecoDerecho = getActivity().findViewById(R.id.fragment_container_detalle);

                // 3. Aplicamos la lógica del Laboratorio 6
                if (huecoDerecho != null) {
                    // ESTAMOS EN HORIZONTAL:
                    // Usamos el código del Labo 6 pero reemplazamos el hueco de la derecha
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_detalle, modificarFragment)
                            .commit();
                } else {
                    // ESTAMOS EN VERTICAL:
                    // Usamos el código normal del Labo 6 (reemplaza toda la pantalla)
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, modificarFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }

            // --- CLIC LARGO: Borrar Prenda con Diálogo ---
            @Override
            public void onClicLargo(Prenda prendaSeleccionada, int posicion) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.titulo_borrar_prenda))
                        .setMessage(getString(R.string.msg_borrar_prenda, prendaSeleccionada.getNombre()))
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(getString(R.string.btn_si_borrar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bdHelper.borrarPrenda(prendaSeleccionada.getId());
                                listaRopa.remove(posicion);
                                adaptador.notifyItemRemoved(posicion);
                                Toast.makeText(getActivity(), "Prenda eliminada", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(getString(R.string.btn_cancelar), null)
                        .show();
            }
        });

        // 4. Enchufamos el adaptador al RecyclerView
        rvArmario.setAdapter(adaptador);

        return view;
    }
}