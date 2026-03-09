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

        //Crear el RecyclerView
        RecyclerView rvArmario = view.findViewById(R.id.rvArmario);
        rvArmario.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Cargar datos de la BD
        bdHelper = new BDGestor(getActivity());
        listaRopa = bdHelper.obtenerTodasLasPrendas();

        //Gestión de clics y adaptador
        adaptador = new PrendaAdapter(listaRopa, new PrendaAdapter.OnItemClickListener() {

            //CLIC NORMAL: Modificar Prenda
            @Override
            public void onClicNormal(Prenda prendaSeleccionada) {
                //Preparar el fragment
                ModificarFragment modificarFragment = new ModificarFragment();
                Bundle mochila = new Bundle();
                mochila.putInt("ID", prendaSeleccionada.getId());
                mochila.putString("NOMBRE", prendaSeleccionada.getNombre());
                mochila.putString("CATEGORIA", prendaSeleccionada.getCategoria());
                modificarFragment.setArguments(mochila);

                //Rellenar lateral derecho en Armario Horizontal
                View huecoDerecho = getActivity().findViewById(R.id.fragment_container_detalle);

                if (huecoDerecho != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_detalle, modificarFragment)
                            .commit();
                } else {
                    // En Vertical
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, modificarFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }

            //CLIC LARGO: Borrar Prenda con Diálogo
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

        rvArmario.setAdapter(adaptador);

        return view;
    }
}