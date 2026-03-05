package com.das.entrega1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class ArmarioFragment extends Fragment {

    private PrendaAdapter adaptador;
    private static ArrayList<Prenda> listaRopa;
    private BDGestor bdHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.armario, container, false);

        ListView listView = view.findViewById(R.id.listaArmario);
        bdHelper = new BDGestor(getActivity());

        // 1. Cargamos las prendas directamente desde SQLite
        listaRopa = bdHelper.obtenerTodasLasPrendas();

        // 2. Las ponemos en la lista
        adaptador = new PrendaAdapter(getActivity(), listaRopa);
        listView.setAdapter(adaptador);

        // 3. Borrado con Diálogo (Igual que antes, pero borrando de la BD)
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Prenda prendaSeleccionada = listaRopa.get(position);

                new AlertDialog.Builder(getActivity())
                        .setTitle("Eliminar prenda")
                        .setMessage("¿Seguro que quieres borrar '" + prendaSeleccionada.getNombre() + "' de tu armario?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton("Sí, borrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Borramos de la Base de Datos usando su ID
                                bdHelper.borrarPrenda(prendaSeleccionada.getId());

                                // Actualizamos la lista visual
                                listaRopa.remove(position);
                                adaptador.notifyDataSetChanged();

                                Toast.makeText(getActivity(), "Prenda eliminada para siempre", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();

                return true;
            }
        });

        // Modificar Prenda
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Cogemos la prenda tocada
                Prenda prendaATocar = listaRopa.get(position);

                // Preparamos el nuevo Fragmento
                ModificarFragment editarFragment = new ModificarFragment();

                // Le metemos los datos en la mochila (Bundle)
                Bundle mochila = new Bundle();
                mochila.putInt("ID", prendaATocar.getId());
                mochila.putString("NOMBRE", prendaATocar.getNombre());
                mochila.putString("CATEGORIA", prendaATocar.getCategoria());
                editarFragment.setArguments(mochila);

                // Viajamos a la pantalla de Editar
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, editarFragment)
                        .commit();
            }
        });

        return view;
    }
}