package com.das.entrega1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

public class AnadirFragment extends Fragment {
    private String uriFotoSeleccionada = "";
    private ImageView ivPreviewFoto;
    private ActivityResultLauncher<Intent> lanzadorGaleria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.anadirprenda, container, false);

        EditText etNombre = view.findViewById(R.id.etNombrePrenda);
        android.widget.RadioGroup rgCategoria = view.findViewById(R.id.rgCategoria);
        Button btnGuardar = view.findViewById(R.id.btnGuardarPrenda);
        
        ivPreviewFoto = view.findViewById(R.id.ivPreviewFoto);
        Button btnSeleccionarFoto = view.findViewById(R.id.btnSeleccionarFoto);

        //PREPARAR EL SELECTOR DEL RECEPTOR DE LA FOTO
        lanzadorGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            // ESTA LÍNEA ES MAGIA: Guarda el permiso de la foto para siempre
                            getActivity().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            uriFotoSeleccionada = uri.toString();
                            ivPreviewFoto.setImageURI(uri);
                        }
                    }
                }
        );

        //BOTÓN PARA SELECCIONAR LA FOTO
        btnSeleccionarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            lanzadorGaleria.launch(intent);
        });

        // BOTÓN PARA GUARDAR
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            int idSeleccionado = rgCategoria.getCheckedRadioButtonId();
            String categoriaInterna = "";

            if (idSeleccionado == R.id.rbArriba) {
                categoriaInterna = "arriba";
            } else if (idSeleccionado == R.id.rbAbajo) {
                categoriaInterna = "abajo";
            } else {
                categoriaInterna = "calzado";
            }


            if (!nombre.isEmpty()) {
                //CONTENT PROVIDER
                ContentValues valores = new ContentValues();
                valores.put("nombre", nombre);
                valores.put("categoria", categoriaInterna);
                valores.put("uri_foto", uriFotoSeleccionada);

                Uri uriInsertada = requireActivity().getContentResolver().insert(RopaProvider.CONTENT_URI, valores);

                if (uriInsertada != null) {
                    Toast.makeText(getActivity(), getString(R.string.msg_guardado) + " (vía Provider)", Toast.LENGTH_SHORT).show();
                    avisarServidorFCM(nombre);

                    etNombre.setText("");
                    uriFotoSeleccionada = "";
                    ivPreviewFoto.setImageResource(android.R.drawable.ic_menu_gallery);
                } else {
                    Toast.makeText(getActivity(), "Error al guardar con Provider", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Rellena el nombre", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    private void avisarServidorFCM(String nombreDeLaPrenda) {
        String prendaCodificada = android.net.Uri.encode(nombreDeLaPrenda);

        //IP DE GOOGLE CLOUD
        String url = "http://34.130.150.158:81/notificar_prenda.php?prenda=" + prendaCodificada;

        com.android.volley.toolbox.StringRequest peticion = new com.android.volley.toolbox.StringRequest(
                com.android.volley.Request.Method.GET,
                url,
                response -> {
                    android.util.Log.d("FCM", "Aviso enviado al servidor: " + response);
                },
                error -> {
                    android.util.Log.e("FCM", "Error al avisar al servidor: " + error.getMessage());
                }
        );

        com.android.volley.toolbox.Volley.newRequestQueue(requireContext()).add(peticion);
    }
}