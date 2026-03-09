package com.das.entrega1;

import android.app.Activity;
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
                BDGestor bdHelper = new BDGestor(getActivity());

                // Guardamos usando la categoriaInterna (arriba, abajo, calzado)
                boolean insertado = bdHelper.insertarPrenda(nombre, categoriaInterna, uriFotoSeleccionada);

                if (insertado) {
                    Toast.makeText(getActivity(), getString(R.string.msg_guardado), Toast.LENGTH_SHORT).show();
                    lanzarNotificacion(nombre);

                    //LIMPIAR FORMULARIO
                    etNombre.setText("");
                    uriFotoSeleccionada = "";
                    ivPreviewFoto.setImageResource(android.R.drawable.ic_menu_gallery);
                } else {
                    Toast.makeText(getActivity(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Rellena el nombre", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // NOTIFICACIÓN AÑADIDO DE PRENDA
    private void lanzarNotificacion(String nombrePrenda) {
        android.app.NotificationManager elManager = (android.app.NotificationManager) getActivity().getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        String channelId = "canal_armario";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel canal = new android.app.NotificationChannel(channelId, "Notificaciones de Armario", android.app.NotificationManager.IMPORTANCE_HIGH);
            elManager.createNotificationChannel(canal);
        }

        android.content.Intent intentApp = new android.content.Intent(getActivity(), MainActivity.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(getActivity(), 0, intentApp, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);

        androidx.core.app.NotificationCompat.Builder elBuilder = new androidx.core.app.NotificationCompat.Builder(getActivity(), channelId)
                .setSmallIcon(android.R.drawable.ic_menu_gallery)
                .setContentTitle("¡Nuevo fichaje en tu armario!")
                .setContentText("Has añadido: " + nombrePrenda)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_view, "VER ARMARIO", pendingIntent);

        // ID ÚNICO para cada notificación
        int idUnico = (int) System.currentTimeMillis();
        elManager.notify(idUnico, elBuilder.build());
    }
}