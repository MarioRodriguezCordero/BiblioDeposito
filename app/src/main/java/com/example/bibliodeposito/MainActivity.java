package com.example.bibliodeposito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.bibliodeposito.DB.DBManager;
import com.example.bibliodeposito.modelo.Libro;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    //private DBManager dbManager;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;
    View header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = findViewById(R.id.menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navView);

        //dbManager = new DBManager(getApplicationContext());
        //dbManager.open();

        //INSERTAR REGISTRO DIRECTO
        //dbManager.insertBook(1, "Caperucita Roja");

        //INSERCCION CON EL MODELO LIBRO
        //dbManager.insertModel(new Libro(2, "pruebas"));

        //dbManager.close();
    }
}