package com.example.bibliodeposito;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bibliodeposito.DB.DBConexion;
import com.example.bibliodeposito.modelo.Imagen;
import com.example.bibliodeposito.modelo.Libro;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {
    private Context mCtx;
    private List<Libro> libroList;
    DBConexion conexion;
    SQLiteDatabase db;
    int singledata;
    itemClickListener itemClickListener;

    //Constructor del Adaptador RecyclerView
    public Adapter(Context mCtx, int singledata, ArrayList<Libro> libroList, SQLiteDatabase db, itemClickListener itemClickListener) {
        this.mCtx = mCtx;
        this.singledata = singledata;
        this.libroList = libroList;
        this.db = db;
        this.itemClickListener = itemClickListener;
    }

    //Metodo para enseñar la lista filtrada
    public void setFilteredList(List<Libro> filteredList) {
        this.libroList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        return new AdapterViewHolder(view, itemClickListener);
    }

    //
    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        conexion = new DBConexion(mCtx);
        Libro libro = libroList.get(position);

        boolean cancelar = false;
        Imagen img = null;

        try {
            img = conexion.elementoImagenIdLibro(libro.getId(), false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(img == null) {
            cancelar = true;
        }

        if(!cancelar) {
            byte[] imagen = img.getImagen();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
            holder.imageView.setImageBitmap(bitmap);
        }

        holder.textViewTitulo.setText(libro.getTitulo());
        holder.textViewAutor.setText(libro.getAutor());
        holder.textViewISBN.setText(libro.getIsbn());
    }

    @Override
    public int getItemCount() {
        return libroList.size();
    }

    //Clase AdapterViewHolder utilizada en para construir los items de la lista
    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewTitulo, textViewAutor, textViewISBN;
        ImageView imageView;
        ImageButton btnList;
        itemClickListener itemClickListener;

        public AdapterViewHolder(View itemView, itemClickListener itemClickListener) {
            super(itemView);
            textViewTitulo = (TextView) itemView.findViewById(R.id.textTitulo);
            textViewAutor = (TextView) itemView.findViewById(R.id.textAutor);
            textViewISBN = (TextView) itemView.findViewById(R.id.textISBN);
            imageView = (ImageView) itemView.findViewById(R.id.fotoLista);
            btnList = (ImageButton) itemView.findViewById(R.id.btn_List);

            this.itemClickListener = itemClickListener;
            btnList.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(libroList.get(getAdapterPosition()));
        }
    }

    public interface itemClickListener {
        void onItemClick(Libro libro);
    }
}
