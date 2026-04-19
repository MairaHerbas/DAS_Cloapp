package com.das.entrega1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PerfilFragment extends Fragment {
    private com.google.firebase.storage.StorageReference storageRef;
    private ImageView ivFotoPerfil;
    private Bitmap fotoBitmap;
    private android.widget.TextView tvProgresoBackup;
    private android.widget.Button btnBackup;
    private BackupReceiver backupReceiver;

    private final ActivityResultLauncher<Void> lanzadorCamara = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    fotoBitmap = bitmap;
                    ivFotoPerfil.setImageBitmap(bitmap); // La mostramos temporalmente
                    subirFotoAlServidor(); // La enviamos a Firebase
                }
            }
    );

    private final ActivityResultLauncher<String> peticionPermisoCamara = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            concedido -> {
                if (concedido) lanzadorCamara.launch(null);
                else Toast.makeText(getContext(), getString(R.string.permiso_camara_denegado), Toast.LENGTH_SHORT).show();
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.perfil, container, false);

        tvProgresoBackup = view.findViewById(R.id.tvProgresoBackup);
        btnBackup = view.findViewById(R.id.btnBackup);
        backupReceiver = new BackupReceiver();

        btnBackup.setOnClickListener(v -> {
            btnBackup.setEnabled(false);
            tvProgresoBackup.setText(getString(R.string.iniciando_conexion));
            android.content.Intent serviceIntent = new android.content.Intent(requireContext(), BackupService.class);
            androidx.core.content.ContextCompat.startForegroundService(requireContext(), serviceIntent);
        });


        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        Button btnTomarFoto = view.findViewById(R.id.btnTomarFoto);

        storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference();
        cargarFotoDePerfil();

        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                lanzadorCamara.launch(null);
            } else {
                peticionPermisoCamara.launch(Manifest.permission.CAMERA);
            }
        });

        return view;
    }

    private void subirFotoAlServidor() {
        Toast.makeText(getContext(), getString(R.string.subiendo_firebase), Toast.LENGTH_SHORT).show();

        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("MisPreferencias", android.content.Context.MODE_PRIVATE);
        String usuarioActual = prefs.getString("usuario_actual", "usuario_desconocido");

        com.google.firebase.storage.StorageReference fotoRef = storageRef.child("fotos/perfil_" + usuarioActual + ".jpg");

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        fotoBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] datos = baos.toByteArray();

        fotoRef.putBytes(datos)
                .addOnSuccessListener(taskSnapshot -> {

                    fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String urlDescarga = uri.toString();
                        Toast.makeText(getContext(), getString(R.string.exito_firebase), Toast.LENGTH_SHORT).show();

                        //Glide para mostrar la foto desde la URL de Firebase
                        com.bumptech.glide.Glide.with(requireContext())
                                .load(urlDescarga)
                                .into(ivFotoPerfil);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), getString(R.string.fallo_firebase) + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void cargarFotoDePerfil() {
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("MisPreferencias", android.content.Context.MODE_PRIVATE);
        String usuarioActual = prefs.getString("usuario_actual", "usuario_desconocido");

        com.google.firebase.storage.StorageReference fotoRef = storageRef.child("fotos/perfil_" + usuarioActual + ".jpg");

        fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (isAdded() && getContext() != null) {
                com.bumptech.glide.Glide.with(requireContext())
                        .load(uri.toString())
                        .into(ivFotoPerfil);
            }
        }).addOnFailureListener(e -> {
        });
    }

    private class BackupReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(android.content.Context context, android.content.Intent intent) {
            if (intent.hasExtra("terminado")) {
                tvProgresoBackup.setText(getString(R.string.backup_completado));
                btnBackup.setEnabled(true); // Volvemos a activar el botón
            } else if (intent.hasExtra("porcentaje")) {
                int progreso = intent.getIntExtra("porcentaje", 0);
                tvProgresoBackup.setText(getString(R.string.sincronizando_nube, progreso));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        android.content.IntentFilter filter = new android.content.IntentFilter(BackupService.ACTION_PROGRESO);

        androidx.core.content.ContextCompat.registerReceiver(
                requireContext(),
                backupReceiver,
                filter,
                androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(backupReceiver);
    }
}