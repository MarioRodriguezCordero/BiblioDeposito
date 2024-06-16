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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.bibliodeposito.DB.DBConexion;
import com.example.bibliodeposito.modelo.Libro;

import java.util.ArrayList;
import java.util.List;

public class BuscarActivity extends AppCompatActivity implements Adapter.itemClickListener {

    DrawerLayout drawerLayout;
    ImageView menu;

    List<Libro> itemList;

    LinearLayout home, ajustes, cerrarSesion, donar, libros, gestionar, devolver;

    Context context;

    DBConexion conexion;
    SQLiteDatabase sqLiteDatabase;

    SearchView searchView;
    RecyclerView recyclerView;
    Adapter adapter;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        context = getApplicationContext();

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        ajustes = findViewById(R.id.settings);
        cerrarSesion = findViewById(R.id.logout);
        donar = findViewById(R.id.donar);
        gestionar = findViewById(R.id.gestionar);
        libros = findViewById(R.id.libros);
        devolver = findViewById(R.id.devolucion);

        conexion = new DBConexion(context);

        searchView = findViewById(R.id.searchView);
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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        itemList = new ArrayList<>();
        itemList = conexion.lista();

        /*Cargar los libros para el RecylerView y
        luego usar el filterlist vacio para filtrar los libros que no estan en prestamo*/
        cargarLibros();
        filterList("");

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        //Los linear layouts se tienen que poner en todas las pantallas
        //para que el menuDrawer funcione correctamente y al presionar cualquiera de las opciones
        //nos lleve a donde queremos
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(BuscarActivity.this, MainActivity.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(BuscarActivity.this, AjustesActivity.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BuscarActivity.this, "Logout",Toast.LENGTH_SHORT).show();
            }
        });

        donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(BuscarActivity.this, GestionarActivity.class);
            }
        });

        libros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        gestionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(BuscarActivity.this, GestionBuscarActivity.class);
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(BuscarActivity.this, DevolverActivity.class);
            }
        });
    }

    //Metodo que se usa para que el widget SearchView filtre los resultados de RecyclerView
    //este en especifico busca los que no esten prestados
    private void filterList(String text) {
        List<Libro> filterList = new ArrayList<>();
        for(Libro libro : itemList) {
            if(libro.getIsbn().toLowerCase().contains(text.toLowerCase()) && libro.getPrestado() == 0) {
                filterList.add(libro);
            }
        }

        if(filterList.isEmpty()) {
            Toast.makeText(context, "No se encontro el libro", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFilteredList(filterList);
        }
    }

    //Metodo para inicializar el recyclerView
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

    private Libro buscarLibro(String titulo) {
        return conexion.elementoTitulo(titulo);
    }

    //Metodo que utilizan todos los botones de RecyclerView
    //para coger los datos del libro de la lista y llevarlos a la siguiente pantalla
    @Override
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

        bolsa.putInt("devolver", 0);

        Intent i = new Intent(BuscarActivity.this, LibroActivity.class);
        i.putExtras(bolsa);
        startActivity(i);
    }

    //METODOS DEL MENU DRAWER

    //Abrir el menu drawer
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    //Cerrar el menu drawer
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    //Metodo para redirigirte a la pantalla que has pulsado en el Drawer
    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bolsa = new Bundle();
        bolsa.putInt("id", 0);

        intent.putExtras(bolsa);

        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}