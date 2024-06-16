package com.example.bibliodeposito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bibliodeposito.DB.DBConexion;
import com.example.bibliodeposito.modelo.Libro;

import java.util.ArrayList;
import java.util.List;

public class GestionBuscarActivity extends AppCompatActivity implements Adapter.itemClickListener {
    DrawerLayout drawerLayout;
    LinearLayout home, ajustes, cerrarSesion, donar, libros, gestionar, devolver;
    ImageView menu;
    SearchView searchView;
    RecyclerView recyclerView;
    SQLiteDatabase sqLiteDatabase;
    Adapter adapter;
    List<Libro> itemList;

    DBConexion conexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_buscar);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        ajustes = findViewById(R.id.settings);
        cerrarSesion = findViewById(R.id.logout);
        donar = findViewById(R.id.donar);
        gestionar = findViewById(R.id.gestionar);
        libros = findViewById(R.id.libros);
        devolver = findViewById(R.id.devolucion);

        conexion = new DBConexion(this);

        searchView = findViewById(R.id.ges_searchView);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        recyclerView = findViewById(R.id.ges_recyclerView);
        recyclerView.setHasFixedSize(true);

        itemList = new ArrayList<>();
        itemList = conexion.lista();

        cargarLibros();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionBuscarActivity.this, MainActivity.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionBuscarActivity.this, AjustesActivity.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GestionBuscarActivity.this, "Logout",Toast.LENGTH_SHORT).show();
            }
        });

        donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionBuscarActivity.this, GestionarActivity.class);
            }
        });

        libros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionBuscarActivity.this, BuscarActivity.class);
            }
        });

        gestionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionBuscarActivity.this, DevolverActivity.class);
            }
        });
    }

    private void filterList(String text) {
        List<Libro> filterList = new ArrayList<>();
        for(Libro libro : itemList) {
            if(libro.getIsbn().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(libro);
            }
        }

        if(filterList.isEmpty()) {
            Toast.makeText(this, "No se encontro el libro", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFilteredList(filterList);
        }
    }

    private void cargarLibros() {
        sqLiteDatabase = conexion.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM deposito", null);

        ArrayList<Libro> libros = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String titulo = cursor.getString(1);
            String isbn = cursor.getString(2);
            String autor = cursor.getString(3);
            libros.add(new Libro(id, titulo, isbn, autor));
        }
        cursor.close();
        adapter = new Adapter(this, R.layout.list_layout, libros, sqLiteDatabase, this);
        recyclerView.setAdapter(adapter);
    }

    public void onItemClick(Libro libro) {

        Bundle bolsa = new Bundle();

        bolsa.putInt("id", libro.getId());
        bolsa.putString("titulo", libro.getTitulo());
        bolsa.putString("isbn", libro.getIsbn());
        bolsa.putString("autor", libro.getAutor());
        bolsa.putString("materia", libro.getMateria());
        bolsa.putString("curso", libro.getCurso());
        bolsa.putInt("anioPublicacion", libro.getAnioPublicacion());
        bolsa.putDouble("danio", libro.getDanio());
        bolsa.putInt("prestado", libro.getPrestado());

        Intent i = new Intent(GestionBuscarActivity.this, GestionarActivity.class);
        i.putExtras(bolsa);
        startActivity(i);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bolsa = new Bundle();
        bolsa.putInt("id", 0);

        intent.putExtras(bolsa);

        activity.startActivity(intent);
        activity.finish();
    }
}