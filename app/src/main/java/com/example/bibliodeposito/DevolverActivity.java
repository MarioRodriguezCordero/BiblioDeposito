package com.example.bibliodeposito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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

public class DevolverActivity extends AppCompatActivity implements Adapter.itemClickListener{
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
        setContentView(R.layout.activity_devolver);

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

        searchView = findViewById(R.id.dev_searchView);
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

        recyclerView = findViewById(R.id.dev_recyclerView);
        recyclerView.setHasFixedSize(true);

        itemList = new ArrayList<>();
        itemList = conexion.lista();

        /*Cargar los libros para el RecylerView y
        luego usar el filterlist vacio para filtrar los libros que estan en prestamo*/
        cargarLibros();
        filterList("");

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
                redirectActivity(DevolverActivity.this, MainActivity.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DevolverActivity.this, AjustesActivity.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DevolverActivity.this, "Logout",Toast.LENGTH_SHORT).show();
            }
        });

        donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DevolverActivity.this, GestionarActivity.class);
            }
        });

        libros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DevolverActivity.this, BuscarActivity.class);
            }
        });

        gestionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DevolverActivity.this, GestionBuscarActivity.class);
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    //Metodo que se usa para que el widget SearchView filtre los resultados de RecyclerView
    //este en especifico busca los que estan prestados
    private void filterList(String text) {
        List<Libro> filterList = new ArrayList<>();
        for(Libro libro : itemList) {
            if(libro.getIsbn().toLowerCase().contains(text.toLowerCase()) && libro.getPrestado() == 1) {
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

        bolsa.putInt("devolver", 1);

        Intent i = new Intent(DevolverActivity.this, LibroActivity.class);
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