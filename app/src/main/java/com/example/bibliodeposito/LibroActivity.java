package com.example.bibliodeposito;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bibliodeposito.DB.DBConexion;
import com.example.bibliodeposito.modelo.Imagen;
import com.example.bibliodeposito.modelo.Libro;

public class LibroActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, ajustes, cerrarSesion, donar, libros, gestionar, devolver;

    Context context;

    TextView txtTitulo, txtIsbn, txtAutor, txtMateria, txtCurso, txtAnioPublicacion, txtDanio, txtEstado;
    ImageView fotoLibro;
    Button btnDevolver, btnTomarPrestado, btnVolver, btnConfirmar, btnCancelar;

    DBConexion conexion;

    Dialog dialog;

    int id;
    int devolver_estado;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libro);

        context = getApplicationContext();
        txtTitulo = findViewById(R.id.lib_txtTitulo);
        txtIsbn = findViewById(R.id.lib_txtIsbn);
        txtAutor = findViewById(R.id.lib_txtAutor);
        txtMateria = findViewById(R.id.lib_txtMateria);
        txtCurso = findViewById(R.id.lib_txtCurso);
        txtAnioPublicacion = findViewById(R.id.lib_txtAnioPublicacion);
        txtDanio = findViewById(R.id.lib_txtDanio);
        txtEstado = findViewById(R.id.lib_txtEstado);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        ajustes = findViewById(R.id.settings);
        cerrarSesion = findViewById(R.id.logout);
        donar = findViewById(R.id.donar);
        gestionar = findViewById(R.id.gestionar);
        libros = findViewById(R.id.libros);
        devolver = findViewById(R.id.devolucion);

        dialog = new Dialog(LibroActivity.this);
        dialog.setContentView(R.layout.dialog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dailog_box_bc));
        dialog.setCancelable(false);

        btnConfirmar = dialog.findViewById(R.id.btnDialogConfirm);
        btnCancelar = dialog.findViewById(R.id.btnDialogCancel);

        fotoLibro = findViewById(R.id.lib_fotoFinal);

        btnVolver = findViewById(R.id.lib_btnVolver);
        btnDevolver =findViewById(R.id.lib_btnDevolver);
        btnTomarPrestado = findViewById(R.id.lib_btnSacar);


        conexion = new DBConexion(context);

        Intent i = getIntent();
        Bundle bolsa = i.getExtras();

        Imagen img = null;
        boolean fotoCancelar = false;

        int prestado = bolsa.getInt("prestado");
        String prestamo = "En deposito";

        if(prestado != 0) {
            prestamo = "En prestamo";

            btnTomarPrestado.setEnabled(false);
            btnDevolver.setEnabled(true);
        } else {
            btnTomarPrestado.setEnabled(true);
            btnDevolver.setEnabled(false);
        }

        id = bolsa.getInt("id");
        txtTitulo.setText(bolsa.getString("titulo"));
        txtIsbn.setText(bolsa.getString("isbn"));
        txtAutor.setText(bolsa.getString("autor"));
        txtMateria.setText(bolsa.getString("materia"));
        txtCurso.setText(bolsa.getString("curso"));
        txtAnioPublicacion.setText(String.valueOf(bolsa.getInt("anioPublicacion")));
        txtDanio.setText(String.valueOf(bolsa.getDouble("danio")));
        txtEstado.setText(prestamo);

        devolver_estado = bolsa.getInt("devolver");

        try {
            img = conexion.elementoImagenIdLibro(id, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(img == null) {
            fotoCancelar = true;
        }

        if(!fotoCancelar) {
            if (img.getId() != 0) {
                byte[] imagen = img.getImagen();

                Bitmap bitmap = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
                fotoLibro.setImageBitmap(bitmap);
            }
        }

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Libro libro = new Libro();

                libro.setTitulo(txtTitulo.getText().toString());
                libro.setAutor(txtAutor.getText().toString());
                libro.setIsbn(txtIsbn.getText().toString());
                libro.setMateria(txtMateria.getText().toString());
                libro.setCurso(txtCurso.getText().toString());
                libro.setAnioPublicacion(Integer.parseInt(txtAnioPublicacion.getText().toString()));
                libro.setDanio(Double.parseDouble(txtDanio.getText().toString()));

                String estado = txtEstado.getText().toString();

                if(estado.equals("En prestamo")) {
                    libro.setPrestado(0);
                } else if (estado.equals("En deposito")) {
                    libro.setPrestado(1);
                }

                conexion.actualizar(id, libro);
                dialog.dismiss();

                Intent i;

                if(devolver_estado != 1){
                    i = new Intent(LibroActivity.this, BuscarActivity.class);
                    Toast.makeText(LibroActivity.this, "Prestamo realizado", Toast.LENGTH_LONG).show();
                } else {
                    i = new Intent(LibroActivity.this, DevolverActivity.class);
                    Toast.makeText(LibroActivity.this, "Devolucion exitosa", Toast.LENGTH_LONG).show();
                }

                startActivity(i);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnTomarPrestado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        btnDevolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;

                if(devolver_estado != 1){
                    i = new Intent(LibroActivity.this, BuscarActivity.class);
                } else {
                    i = new Intent(LibroActivity.this, DevolverActivity.class);
                }

                startActivity(i);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LibroActivity.this, MainActivity.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LibroActivity.this, AjustesActivity.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LibroActivity.this, "Logout",Toast.LENGTH_SHORT).show();
            }
        });

        donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LibroActivity.this, GestionarActivity.class);
            }
        });

        libros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LibroActivity.this, BuscarActivity.class);
            }
        });

        gestionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LibroActivity.this, GestionBuscarActivity.class);
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LibroActivity.this, DevolverActivity.class);
            }
        });
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}