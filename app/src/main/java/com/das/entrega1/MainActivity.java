package com.das.entrega1;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Icono hamburguesa genérico (como pide el PDF) [cite: 228, 230]
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
        NavigationView elnavigation = findViewById(R.id.nav_view);

        // Listener del menú según el PDF [cite: 212, 214, 215]
        elnavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Cambiar el fragmento según lo pulsado
                if (id == R.id.nav_armario) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArmarioFragment()).commit();
                } else if (id == R.id.nav_anadir) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AnadirFragment()).commit();
                }

                // Cerrar el menú [cite: 223]
                elmenudesplegable.closeDrawers();
                return false;
            }
        });

        // Botón atrás para cerrar menú según el PDF [cite: 249, 251, 253, 256, 257]
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
                if (elmenudesplegable.isDrawerOpen(GravityCompat.START)) {
                    elmenudesplegable.closeDrawer(GravityCompat.START);
                } else {
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
        getMenuInflater().inflate(R.menu.menu_superior, menu);
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
    private void cambiarIdioma() {
        // Miramos qué idioma está puesto ahora mismo
        java.util.Locale idiomaActual = getResources().getConfiguration().locale;
        String nuevoIdioma = "es"; // Por defecto lo pasamos a español

        // Si ya está en español, lo pasamos a inglés
        if (idiomaActual.getLanguage().equals("es")) {
            nuevoIdioma = "en";
        }

        // Aplicamos el nuevo idioma
        java.util.Locale locale = new java.util.Locale(nuevoIdioma);
        java.util.Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Reiniciamos la actividad para que se redibujen los textos
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}