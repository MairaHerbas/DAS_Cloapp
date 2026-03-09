package com.das.entrega1;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cargarIdioma();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Permisos de notificación
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Icono menu lateral
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
        NavigationView elnavigation = findViewById(R.id.nav_view);

        // Listener del menubar lateral
        elnavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Capturar el panel derecho
                View huecoDerecho = findViewById(R.id.fragment_container_detalle);
                View divisor = findViewById(R.id.divisor_paneles);

                if (id == R.id.nav_armario) {
                    if (huecoDerecho != null) huecoDerecho.setVisibility(View.VISIBLE);
                    if (divisor != null) divisor.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArmarioFragment()).commit();
                } else {
                    // CUALQUIER OTRO MENÚ: Ocultamos panel derecho y separador
                    if (huecoDerecho != null) {
                        huecoDerecho.setVisibility(View.GONE);
                        androidx.fragment.app.Fragment fragmentoDetalle = getSupportFragmentManager().findFragmentById(R.id.fragment_container_detalle);
                        if (fragmentoDetalle != null) {
                            getSupportFragmentManager().beginTransaction().remove(fragmentoDetalle).commit();
                        }
                    }
                    if (divisor != null) divisor.setVisibility(View.GONE);

                    // Cargar el fragmento correspondiente en el hueco principal
                    if (id == R.id.nav_anadir) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AnadirFragment()).commit();
                    } else if (id == R.id.nav_crear_conjunto) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CrearConjuntoFragment()).commit();
                    } else if (id == R.id.nav_mis_conjuntos) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MisConjuntosFragment()).commit();
                    }
                }

                elmenudesplegable.closeDrawers();
                return false;
            }
        });

        // Botón atrás para cerrar menú
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);

                // 1. Si el menú lateral está abierto, lo cerramos
                if (elmenudesplegable.isDrawerOpen(GravityCompat.START)) {
                    elmenudesplegable.closeDrawer(GravityCompat.START);
                }
                // 2. Si hay Fragmentos en la pila, volvemos atrás
                else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                }
                // 3. Si no hay menú ni fragmentos pendientes, cerramos la app con normalidad
                else {
                    finish();
                }
            }
        });

        // Pantalla de inicio
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArmarioFragment()).commit();
        }
    }

    // 1. Cargamos el menú en la barra superior (Del Labo 5)
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    // 2. ¿Qué pasa al pulsar el botón del idioma? (Del Labo 5 y Labo 2)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Si pulsamos el botón de cambiar idioma
        if (id == R.id.menu_idioma) {
            cambiarIdioma();
            return true;
        }

        // Si pulsamos el icono de la hamburguesa (del Labo 6)
        if (id == android.R.id.home) {
            DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
            elmenudesplegable.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // MultiIdioma
    // NUEVO: Métoodo para leer de memoria el idioma al abrir/girar la app
    private void cargarIdioma() {
        android.content.SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        String idiomaGuardado = prefs.getString("idioma", "es"); // Español por defecto

        java.util.Locale locale = new java.util.Locale(idiomaGuardado);
        java.util.Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    // MODIFICADO: Ahora además de cambiarlo, lo guarda en la memoria
    private void cambiarIdioma() {
        android.content.SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        String idiomaActual = prefs.getString("idioma", "es");
        String nuevoIdioma = "es";

        if (idiomaActual.equals("es")) {
            nuevoIdioma = "en";
        }

        // ¡Guardamos el nuevo idioma para que no se olvide al girar!
        prefs.edit().putString("idioma", nuevoIdioma).apply();

        java.util.Locale locale = new java.util.Locale(nuevoIdioma);
        java.util.Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Este comando recarga la pantalla actual en la que estés
        recreate();
    }
}
