package com.das.entrega1;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class SugerenciaFragment extends Fragment {

    private TextView tvCiudad, tvTemperatura, tvDescripcion, tvNombreSugerencia;
    private ImageView ivSugerencia;
    private Button btnActualizar;

    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if ((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted)) {
                    obtenerUbicacionYClima();
                } else {
                    Toast.makeText(getContext(), getString(R.string.permiso_denegado), Toast.LENGTH_SHORT).show();
                    tvCiudad.setText(getString(R.string.ubicacion_denegada));
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sugerencia_clima, container, false);

        tvCiudad = view.findViewById(R.id.tvCiudad);
        tvTemperatura = view.findViewById(R.id.tvTemperatura);
        tvDescripcion = view.findViewById(R.id.tvDescripcionClima);
        tvNombreSugerencia = view.findViewById(R.id.tvNombreSugerencia);
        ivSugerencia = view.findViewById(R.id.ivSugerencia);
        btnActualizar = view.findViewById(R.id.btnActualizarClima);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        btnActualizar.setOnClickListener(v -> pedirPermisosYObtenerClima());

        pedirPermisosYObtenerClima();

        return view;
    }

    private void pedirPermisosYObtenerClima() {
        tvCiudad.setText(getString(R.string.buscando_ubicacion));

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            obtenerUbicacionYClima();
        }
    }

    private void obtenerUbicacionYClima() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    descargarClimaDesdeAPI(location.getLatitude(), location.getLongitude());
                } else {
                    tvCiudad.setText(getString(R.string.aviso_gps_emulador));
                    Toast.makeText(getContext(), getString(R.string.toast_gps_emulador), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void descargarClimaDesdeAPI(double lat, double lon) {
        tvCiudad.setText(getString(R.string.conectando_openweather));

        try {
            ApplicationInfo ai = requireActivity().getPackageManager().getApplicationInfo(requireActivity().getPackageName(), PackageManager.GET_META_DATA);
            String apiKey = ai.metaData.getString("WEATHER_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {
                tvCiudad.setText(getString(R.string.falta_api_key));
                return;
            }

            String idiomaActual = Locale.getDefault().getLanguage();
            String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric&lang=" + idiomaActual;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            String nombreCiudad = response.getString("name");
                            JSONObject main = response.getJSONObject("main");
                            double temperatura = main.getDouble("temp");
                            JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
                            String descripcion = weather.getString("description");

                            tvCiudad.setText(nombreCiudad);
                            tvTemperatura.setText(Math.round(temperatura) + " °C");
                            tvDescripcion.setText(descripcion.substring(0, 1).toUpperCase() + descripcion.substring(1));

                            // --- AQUÍ ESTÁ LA MAGIA: LLAMAMOS A LA BD ---
                            tvNombreSugerencia.setText(getString(R.string.esperando_datos));

                            BDGestor bdGestor = new BDGestor(getContext());
                            Conjunto conjuntoSugerido = bdGestor.sugerirConjuntoPorClima(temperatura);

                            if (conjuntoSugerido != null) {
                                String textoSugerencia = conjuntoSugerido.getArriba().getNombre() +
                                        " + " + conjuntoSugerido.getAbajo().getNombre();
                                tvNombreSugerencia.setText(textoSugerencia);

                                String uriFoto = conjuntoSugerido.getArriba().getUriFoto();
                                if (uriFoto != null && !uriFoto.isEmpty()) {
                                    try {
                                        ivSugerencia.setImageURI(android.net.Uri.parse(uriFoto));
                                    } catch (Exception e) {
                                        ivSugerencia.setImageResource(android.R.drawable.ic_menu_gallery);
                                    }
                                }
                            } else {
                                tvNombreSugerencia.setText("Crea un conjunto primero.");
                            }

                        } catch (JSONException e) {
                            tvCiudad.setText(getString(R.string.error_leer_json));
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        tvCiudad.setText(getString(R.string.error_red));
                        Toast.makeText(getContext(), getString(R.string.error_conectar_openweather), Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}