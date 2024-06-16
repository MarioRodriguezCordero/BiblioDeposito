package com.example.bibliodeposito;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;

public class FotografiaActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, ajustes, cerrarSesion, donar, libros, gestionar, devolver;

    Dialog warning;

    Context context;
    DBConexion conexion;
    int id_libro;
    int id;

    ImageView fotoLibro;

    Button btnGuardar, btnCancelar, btnOK;
    TextView warText;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotografia);

        //Inicializar elementos y enlazarlos al layout
        context = getApplicationContext();
        fotoLibro = findViewById(R.id.fot_fotoLibro);
        btnCancelar = findViewById(R.id.fot_btnCancelar);
        btnGuardar = findViewById(R.id.fot_btnGuardar);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        ajustes = findViewById(R.id.settings);
        cerrarSesion = findViewById(R.id.logout);
        donar = findViewById(R.id.donar);
        gestionar = findViewById(R.id.gestionar);
        libros = findViewById(R.id.libros);
        devolver = findViewById(R.id.devolucion);

        warning = new Dialog(FotografiaActivity.this);
        warning.setContentView(R.layout.warning_box);
        warning.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        warning.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dailog_box_bc));
        warning.setCancelable(false);

        btnOK = warning.findViewById(R.id.btnWarningOK);
        warText = warning.findViewById(R.id.war_Text);

        warText.setText("Ingrese una foto para guardar");

        conexion = new DBConexion(context);

        Intent i = getIntent();
        Bundle bolsa = i.getExtras();
        id = bolsa.getInt("id");
        id_libro = bolsa.getInt("id_libro");

        //Si el libro que se ha extraido tiene imagen, se la busca y se pone en el ImageView
        if(id != 0) {
            Imagen img = buscarImagen(id);
            byte[]imagen = img.getImagen();

            //Transforma el array de bytes sacado de la base de datos y lo convierte a bitmap
            // para luego insertarlo en el ImageView
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
            fotoLibro.setImageBitmap(bitmap);
        }

        //Usa la API de ImagePicker para poder utilizar la camara para sacar fotos
        //o usar las fotografias de la galeria
        fotoLibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camaraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
                ImagePicker.with(FotografiaActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.dismiss();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarFoto();
            }
        });

        //Cancela el guardado o actualizacion de la fotografia
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bolsa = new Bundle();

                Libro libro = buscarLibro(id_libro);

                bolsa.putInt("id", libro.getId());
                bolsa.putString("titulo", libro.getTitulo());
                bolsa.putString("isbn", libro.getIsbn());
                bolsa.putString("autor", libro.getAutor());
                bolsa.putString("materia", libro.getMateria());
                bolsa.putString("curso", libro.getCurso());
                bolsa.putInt("anioPublicacion", libro.getAnioPublicacion());
                bolsa.putDouble("danio", libro.getDanio());
                bolsa.putInt("prestado", libro.getPrestado());

                Intent i = new Intent(FotografiaActivity.this, GestionarActivity.class);
                i.putExtras(bolsa);
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
                redirectActivity(FotografiaActivity.this, MainActivity.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(FotografiaActivity.this, AjustesActivity.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FotografiaActivity.this, "Logout",Toast.LENGTH_SHORT).show();
            }
        });

        donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(FotografiaActivity.this, GestionarActivity.class);
            }
        });

        libros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(FotografiaActivity.this, BuscarActivity.class);
            }
        });

        gestionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(FotografiaActivity.this, GestionBuscarActivity.class);
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(FotografiaActivity.this, DevolverActivity.class);
            }
        });
    }

    //Metodo para buscar la imagen en la base de datos
    private Imagen buscarImagen(int id) {
        Imagen img = conexion.elementoImagen(id);

        return img;
    }

    //Metodo para buscar el libro relacionado con la imagen en la base de datos
    private Libro buscarLibro(int id) {
        Libro libro = conexion.elemento(id);

        return libro;
    }

    //Metodo para rellenar los datos de la clase Imagen
    private Imagen llenarImagen() {
        Imagen img = new Imagen();

        img.setId(id);
        img.setImagen(ImageViewToByte(fotoLibro));
        img.setId_libro(id_libro);

        return img;
    }

    //Metodo de guardado y actualizacion de la fotografia
    private void guardarFoto() {
        boolean error = false;
        Imagen img = null;

        try {
            img = llenarImagen();
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        }

        //Si no se ha hecho una fotografia o cogido una imagen de la galeria
        //Salta una Advertencia
        if(!error) {
            if(id == 0) {
                conexion.agregarFoto(img);

                Libro libro = buscarLibro(id_libro);

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

                Intent i = new Intent(FotografiaActivity.this, GestionarActivity.class);
                i.putExtras(bolsa);
                startActivity(i);
            } else {
                conexion.actualizarFoto(id, img);
                Toast.makeText(context, "Foto actualizada con exito", Toast.LENGTH_LONG).show();

                Libro libro = buscarLibro(id_libro);

                Bundle bolsa = new Bundle();
                bolsa.putInt("id", libro.getId());
                bolsa.putString("titulo", libro.getTitulo());
                bolsa.putString("isbn", libro.getIsbn());
                bolsa.putString("autor", libro.getAutor());
                bolsa.putString("materia", libro.getMateria());
                bolsa.putString("curso", libro.getCurso());
                bolsa.putInt("anioPublicacion", libro.getAnioPublicacion());
                bolsa.putDouble("danio", libro.getDanio());

                Intent i = new Intent(FotografiaActivity.this, GestionarActivity.class);
                i.putExtras(bolsa);
                startActivity(i);
            }
        } else {
            warning.show();
        }
    }

    //Recoge los datos de la imagen sacada con la camara o de la galeria y lo inserta en el ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        fotoLibro.setImageURI(uri);
    }

    //Transforma el ImageView a un Array de bytes para poder alamcenarlo en la base de datos
    private byte[] ImageViewToByte(ImageView fotoLibro) {
        Bitmap bitmap = ((BitmapDrawable) fotoLibro.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
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