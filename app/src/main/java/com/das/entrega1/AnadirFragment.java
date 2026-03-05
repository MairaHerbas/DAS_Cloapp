package com.das.entrega1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AnadirFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el diseño del fragmento (crear  y cargar el diseño de la pantalla "Añadir Prenda")
        View view = inflater.inflate(R.layout.anadirprenda, container, false);

        // Enlazamos las variables con el XML
        EditText etNombre = view.findViewById(R.id.etNombrePrenda);
        EditText etCategoria = view.findViewById(R.id.etCategoriaPrenda);
        Button btnGuardar = view.findViewById(R.id.btnGuardarPrenda);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String categoria = etCategoria.getText().toString();

            if (!nombre.isEmpty() && !categoria.isEmpty()) {
                BDGestor bdHelper = new BDGestor(getActivity());
                boolean insertado = bdHelper.insertarPrenda(nombre, categoria);

                if (insertado) {
                    Toast.makeText(getActivity(), "¡Prenda guardada en la base de datos!", Toast.LENGTH_SHORT).show();

                    // --- NUEVO: Lanzamos la notificación ---
                    lanzarNotificacion(nombre);

                    etNombre.setText("");
                    etCategoria.setText("");
                } else {
                    Toast.makeText(getActivity(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void lanzarNotificacion(String nombrePrenda) {
        android.app.NotificationManager elManager = (android.app.NotificationManager) getActivity().getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        String channelId = "canal_armario";

        // 1. Crear el canal de notificaciones (Obligatorio en Android 8+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel canal = new android.app.NotificationChannel(channelId, "Notificaciones de Armario", android.app.NotificationManager.IMPORTANCE_HIGH);
            elManager.createNotificationChannel(canal);
        }

        // 2. Crear un Intent para cuando el usuario pulse la notificación (abre la app)
        android.content.Intent intentApp = new android.content.Intent(getActivity(), MainActivity.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(getActivity(), 0, intentApp, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);

        // 3. Construir la notificación usando código del Laboratorio 4
        androidx.core.app.NotificationCompat.Builder elBuilder = new androidx.core.app.NotificationCompat.Builder(getActivity(), channelId)
                .setSmallIcon(android.R.drawable.ic_menu_gallery) // Icono de la notificación
                .setContentTitle("¡Nuevo fichaje en tu armario!")
                .setContentText("Has añadido: " + nombrePrenda)
                .setAutoCancel(true) // Desaparece al tocarla
                .setContentIntent(pendingIntent) // Acción al tocar el cuerpo
                // Añadimos un botón de acción como pide el Labo 4
                .addAction(android.R.drawable.ic_menu_view, "VER ARMARIO", pendingIntent);

        // 4. Mostrar la notificación
        elManager.notify(1, elBuilder.build());
    }
}