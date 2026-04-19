package com.das.entrega1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapaFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapa, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        Button btnTiendas = view.findViewById(R.id.btnBuscarTiendas);
        btnTiendas.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng miPosicion = new LatLng(location.getLatitude(), location.getLongitude());
                        buscarTiendasCercanas(miPosicion);
                    } else {
                        Toast.makeText(getContext(), "GPS inactivo o sin señal", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            obtenerYMarcarUbicacion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) onMapReady(mMap);
        }
    }

    private void obtenerYMarcarUbicacion() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    LatLng miPosicion = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(miPosicion).title(getString(R.string.marcador_aqui)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion, 15f));
                }
            });
        }
    }

    private void buscarTiendasCercanas(LatLng posicion) {
        String apiKey = "AIzaSyAELKeUZNVwFXJPfYccHnp_F3VOCWN52C0"; // Tu clave
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + posicion.latitude + "," + posicion.longitude +
                "&radius=10000&type=clothing_store&key=" + apiKey;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");

                        if (!status.equals("OK")) {
                            String errorMsg = jsonObject.optString("error_message", "Sin detalles de Google");
                            android.util.Log.e("ERROR_GOOGLE_PLACES", "Status: " + status + " | Causa: " + errorMsg);
                            Toast.makeText(getContext(), "Bloqueo de Google: " + status, Toast.LENGTH_LONG).show();
                            return;
                        }

                        //  BUSCAMOS LAS TIENDAS
                        JSONArray results = jsonObject.getJSONArray("results");
                        if (results.length() == 0) {
                            Toast.makeText(getContext(), "Definitivamente no hay tiendas en 20km", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            JSONObject loc = place.getJSONObject("geometry").getJSONObject("location");
                            LatLng storePos = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                            mMap.addMarker(new MarkerOptions()
                                    .position(storePos)
                                    .title(place.getString("name"))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Error de red Volley: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(requireContext()).add(request);
    }
}