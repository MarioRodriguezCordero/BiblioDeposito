package com.example.bibliodeposito;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bibliodeposito.DB.DBConexion;
import com.example.bibliodeposito.modelo.Imagen;
import com.example.bibliodeposito.modelo.Libro;

import java.util.ArrayList;
import java.util.List;


public class GestionarActivity extends AppCompatActivity {
    Context context;
    EditText txtTitulo, txtIsbn, txtAutor, txtMateria, txtCurso, txtAnioPublicacion, txtDanio;
    Spinner spnEstado;
    Dialog warning, dialog;

    int id;
    String id_buscar;
    DBConexion conexion;
    Button btnGuardar, btnActualizar, btnBorrar, btnVolver, btnOK, btnConfirmar, btnCancelar;

    TextView warTexto;

    DrawerLayout drawerLayout;

    LinearLayout home, ajustes, cerrarSesion, donar, libros, gestionar, devolver;

    ImageView fotoFinal, menu;
    private static Drawable image = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar);

        context = getApplicationContext();
        txtTitulo = findViewById(R.id.ges_txtTitulo);
        txtIsbn = findViewById(R.id.ges_txtIsbn);
        txtAutor = findViewById(R.id.ges_txtAutor);
        txtMateria = findViewById(R.id.ges_txtMateria);
        txtCurso = findViewById(R.id.ges_txtCurso);
        txtAnioPublicacion = findViewById(R.id.ges_txtAnioPublicacion);
        txtDanio = findViewById(R.id.ges_txtDanio);
        spnEstado = findViewById(R.id.ges_Estado);

        btnActualizar = findViewById(R.id.ges_btnActualizar);
        btnBorrar = findViewById(R.id.ges_btnBorrar);
        btnGuardar = findViewById(R.id.ges_btnGuardar);
        btnVolver = findViewById(R.id.ges_btnVolver);
        fotoFinal = findViewById(R.id.ges_fotoFinal);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        ajustes = findViewById(R.id.settings);
        cerrarSesion = findViewById(R.id.logout);
        donar = findViewById(R.id.donar);
        gestionar = findViewById(R.id.gestionar);
        libros = findViewById(R.id.libros);
        devolver = findViewById(R.id.devolucion);

        //Inicializacion y enlazamiento de los PopUps de Advertencia y confirmacion
        warning = new Dialog(GestionarActivity.this);
        warning.setContentView(R.layout.warning_box);
        warning.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        warning.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dailog_box_bc));
        warning.setCancelable(false);

        dialog = new Dialog(GestionarActivity.this);
        dialog.setContentView(R.layout.dialog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dailog_box_bc));
        dialog.setCancelable(false);

        btnCancelar = dialog.findViewById(R.id.btnDialogCancel);
        btnConfirmar = dialog.findViewById(R.id.btnDialogConfirm);
        btnOK = warning.findViewById(R.id.btnWarningOK);
        warTexto = warning.findViewById(R.id.war_Text);

        image = fotoFinal.getDrawable();

        conexion = new DBConexion(context);

        //Inicializar el contenido del spinner
        ArrayList<String> spinnerContent = new ArrayList<>();
        spinnerContent.add("En deposito");
        spinnerContent.add("En prestamo");

        //Insertar el Adaptador y el contenido del spinner dentro del spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerContent);
        spnEstado.setAdapter(arrayAdapter);

        Intent i = getIntent();
        Bundle bolsa = i.getExtras();
        id = bolsa.getInt("id");

        //Si el id no es 0 es un libro de la base de datos
        //por lo que se extrae la informacion y se inserta en los campos correspondientes
        if(id != 0) {
            Imagen img = null;
            boolean fotoCancelar = false;

            int prestado = bolsa.getInt("prestado");

            if(prestado != 0) {
                spnEstado.setSelection(arrayAdapter.getPosition("En prestamo"));
            } else {
                spnEstado.setSelection(arrayAdapter.getPosition("En deposito"));
            }

            txtTitulo.setText(bolsa.getString("titulo"));
            txtIsbn.setText(bolsa.getString("isbn"));
            txtAutor.setText(bolsa.getString("autor"));
            txtMateria.setText(bolsa.getString("materia"));
            txtCurso.setText(bolsa.getString("curso"));
            txtAnioPublicacion.setText(String.valueOf(bolsa.getInt("anioPublicacion")));
            txtDanio.setText(String.valueOf(bolsa.getDouble("danio")));

            //Si el libro no tiene ninguna foto, se cancela la extraccion de la imagen
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
                    fotoFinal.setImageBitmap(bitmap);
                }
            }

            btnGuardar.setEnabled(false);
        } else {
            btnActualizar.setEnabled(false);
            btnBorrar.setEnabled(false);
        }

        //Boton del PopUp de confirmacion de borrar
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrar();
                dialog.dismiss();
            }
        });

        //Boton del PopUp de confirmacion de cancelar
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Boton del PopUp de Advertencia
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.dismiss();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        //Al pulsar el boton borrar se abre el PopUp de confirmacion
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MainActivity.class);
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
                redirectActivity(GestionarActivity.this, MainActivity.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionarActivity.this, AjustesActivity.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GestionarActivity.this, "Logout",Toast.LENGTH_SHORT).show();
            }
        });

        donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        libros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionarActivity.this, BuscarActivity.class);
            }
        });

        gestionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionarActivity.this, GestionBuscarActivity.class);
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GestionarActivity.this, DevolverActivity.class);
            }
        });
    }

    //Metodo par vaciar todos los campos
    private void limpiarCampos() {
        id = 0;

        txtTitulo.setText("");
        txtIsbn.setText("");
        txtAutor.setText("");
        txtMateria.setText("");
        txtCurso.setText("");
        txtAnioPublicacion.setText("");
        txtDanio.setText("");
    }

    //Metodo para extraer todos los datos de un libro
    private Libro llenarDatosLibros() {
        Libro libro = new Libro();

        String t = txtTitulo.getText().toString();
        String i = txtIsbn.getText().toString();
        String a = txtAutor.getText().toString();
        String m = txtMateria.getText().toString();
        String c = txtCurso.getText().toString();
        String an = txtAnioPublicacion.getText().toString();
        String d = txtDanio.getText().toString();

        String estado = String.valueOf(spnEstado.getSelectedItem());

        libro.setId(id);
        libro.setTitulo(t);
        libro.setIsbn(i);
        libro.setAutor(a);
        libro.setMateria(m);
        libro.setCurso(c);
        try {
            libro.setAnioPublicacion(Integer.parseInt(an));
            libro.setDanio(Double.parseDouble(d));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(estado.equals("En prestamo")) {
            libro.setPrestado(1);
        } else if (estado.equals("En deposito")) {
            libro.setPrestado(0);
        }

        return libro;
    }

    private void guardar() {
        Libro libro = llenarDatosLibros();

        //Si al libro le falta el titulo, el autor o el ISBN
        // saltara un PopUp de Advertencia
        if(libro.getTitulo().equals("")){
            warTexto.setText("Necesita insertar el Titulo");
            warning.show();
        } else if (libro.getAutor().equals("")) {
            warTexto.setText("Necesita insertar el Autor");
            warning.show();
        } else if (libro.getIsbn().equals("")) {
            warTexto.setText("Necesita insertar el ISBN");
            warning.show();
        } else {

            //Si la id es 0 es un libro nuevo y se tiene que agregar a la base de datos
            if(id == 0) {
                conexion.agregar(libro);

                Libro libroF = conexion.elementoTitulo(libro.getTitulo());

                Bundle bolsa = new Bundle();
                bolsa.putInt("id", 0);
                bolsa.putInt("id_libro", libroF.getId());

                Intent i = new Intent(context, FotografiaActivity.class);
                i.putExtras(bolsa);
                startActivity(i);
            } else  {
                conexion.actualizar(id, libro);
                Toast.makeText(context, "Actualizado con exito", Toast.LENGTH_LONG).show();

                Imagen img = null;

                try {
                    img = conexion.elementoImagenIdLibro(id, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bundle bolsa = new Bundle();

                if(img != null) {
                    bolsa.putInt("id", img.getId());
                }

                bolsa.putInt("id_libro", libro.getId());

                Intent i = new Intent(context, FotografiaActivity.class);
                i.putExtras(bolsa);
                startActivity(i);
            }
        }
    }

    //Metodo para borrar un libro de la base de datos
    private void borrar() {
        Libro libro = llenarDatosLibros();

        if(id == 0) {
            Toast.makeText(context, "El libro no existe", Toast.LENGTH_LONG).show();
        } else {
            conexion.borrar(id);
            conexion.borrarFoto(id);
            limpiarCampos();
            fotoFinal.setImageDrawable(image);
            btnGuardar.setEnabled(true);
            btnBorrar.setEnabled(false);
            btnActualizar.setEnabled(false);
            Toast.makeText(context, "Eliminado correctamente", Toast.LENGTH_LONG).show();
        }
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