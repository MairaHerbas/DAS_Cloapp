package com.das.entrega1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MisConjuntosFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.misconjuntos, container, false);

        RecyclerView rv = view.findViewById(R.id.rvMisConjuntos);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        BDGestor bdHelper = new BDGestor(getActivity());
        java.util.ArrayList<Conjunto> listaConjuntos = bdHelper.obtenerTodosLosConjuntos();

        // Crear el adaptador y le pasamos lo que tiene que hacer al hacer clic largo
        ConjuntoAdapter adapter = new ConjuntoAdapter(listaConjuntos, new ConjuntoAdapter.OnItemClickListener() {
            @Override
            public void onClicLargo(Conjunto conjuntoSeleccionado, int posicion) {
                new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.titulo_borrar_conjunto))
                        .setMessage(getString(R.string.msg_borrar_conjunto))
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(R.string.btn_si_borrar, (dialog, which) -> {
                            bdHelper.borrarConjunto(conjuntoSeleccionado.getId());
                            listaConjuntos.remove(posicion);
                            rv.getAdapter().notifyItemRemoved(posicion); // Animación de borrado
                        })
                        .setNegativeButton(getString(R.string.btn_cancelar), null)
                        .show();
            }
        });

        rv.setAdapter(adapter);

        return view;
    }
}