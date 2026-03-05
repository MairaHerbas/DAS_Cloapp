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
            @Override
            public void onClicNormal(Prenda prendaSeleccionada) {
                ModificarFragment modificarFragment = new ModificarFragment();
                Bundle mochila = new Bundle();
                mochila.putInt("ID", prendaSeleccionada.getId());
                mochila.putString("NOMBRE", prendaSeleccionada.getNombre());
                mochila.putString("CATEGORIA", prendaSeleccionada.getCategoria());
                modificarFragment.setArguments(mochila);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, modificarFragment)
                        .commit();
            }

            // --- CLIC LARGO: Borrar Prenda con Diálogo ---
            @Override
            public void onClicLargo(Prenda prendaSeleccionada, int posicion) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Eliminar prenda")
                        .setMessage("¿Seguro que quieres borrar '" + prendaSeleccionada.getNombre() + "'?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton("Sí, borrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bdHelper.borrarPrenda(prendaSeleccionada.getId());
                                listaRopa.remove(posicion);
                                adaptador.notifyItemRemoved(posicion); // Animación bonita al borrar
                                Toast.makeText(getActivity(), "Prenda eliminada", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        // 4. Enchufamos el adaptador al RecyclerView
        rvArmario.setAdapter(adaptador);

        return view;
    }
}