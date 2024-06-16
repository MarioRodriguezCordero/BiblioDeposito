package com.example.bibliodeposito;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, ajustes, cerrarSesion, donar, libros, gestionar, devolver;

    Context context;
    Button btnDonar, btnBuscar, btnGestionar, btnDevolver;

    ActivityResultLauncher<String[]> nPermissionResultLauncher;
    private boolean isReadPermissionGranted = false;
    private boolean isCameraPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private boolean isReadImagesPermissionGranted = false;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Confirma que la aplicacion tenga los permisos requeridos para que funcione correctamente
        nPermissionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if(result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                    isReadPermissionGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isWritePermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if(result.get(Manifest.permission.CAMERA) != null) {
                    isCameraPermissionGranted = result.get(Manifest.permission.CAMERA);
                }

                if(result.get(Manifest.permission.READ_MEDIA_IMAGES) != null) {
                    isReadImagesPermissionGranted = result.get(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
        });

        //Pide los permisos que necesita la aplicacion
        requestPermission();

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        ajustes = findViewById(R.id.settings);
        cerrarSesion = findViewById(R.id.logout);
        donar = findViewById(R.id.donar);
        gestionar = findViewById(R.id.gestionar);
        libros = findViewById(R.id.libros);
        devolver = findViewById(R.id.devolucion);

        context = getApplicationContext();
        btnBuscar = findViewById(R.id.btnBuscar);
        btnDonar = findViewById(R.id.btnDonar);
        btnGestionar = findViewById(R.id.btnGestionar);
        btnDevolver = findViewById(R.id.main_btnDevolver);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, AjustesActivity.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Logout",Toast.LENGTH_SHORT).show();
            }
        });

        donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, GestionarActivity.class);
            }
        });

        libros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, BuscarActivity.class);
            }
        });

        gestionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, GestionBuscarActivity.class);
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, DevolverActivity.class);
            }
        });
    }

    //Metodo para pedir los permisos de camara y acceso a los datos
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestPermission() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isCameraPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isReadImagesPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();

        if(!isReadPermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if(!isWritePermissionGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!isCameraPermissionGranted) {
            permissionRequest.add(Manifest.permission.CAMERA);
        }

        if(!isReadImagesPermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
        }

        if(!permissionRequest.isEmpty()) {
            nPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnDonar) {
            Intent i = new Intent(context, GestionarActivity.class);

            Bundle bolsa = new Bundle();
            bolsa.putInt("id", 0);
            i.putExtras(bolsa);

            startActivity(i);
        } else if (v.getId() == R.id.btnBuscar) {
            Intent i = new Intent(context, BuscarActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.btnGestionar) {
            Intent i = new Intent(context, GestionBuscarActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.main_btnDevolver) {
            Intent i = new Intent(context, DevolverActivity.class);
            startActivity(i);
        }
    }
}