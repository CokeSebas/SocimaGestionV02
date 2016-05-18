package Adaptadores;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.odril.socimagestionv02.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import objetos.Producto;

/**
 * Created by Alvaro on 22/02/2015.
 */
public class ProductosNuevos extends BaseAdapter {
    Context contex;
    ArrayList<Producto> Pnv;
    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;


    public ProductosNuevos(Context context, ArrayList<Producto> Pnv) {
        this.contex = context;
        this.Pnv = Pnv;
        FuenteUno = Typeface.createFromAsset(context.getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(context.getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(context.getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(context.getAssets(), "fonts/cuatro.ttf");

    }

    @Override
    public int getCount() {
        return this.Pnv.size();
    }

    @Override
    public Object getItem(int position) {
        return this.Pnv.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.producto_nuevo, parent, false);

        }
        ImageView Imagen = (ImageView) rowView.findViewById(R.id.ImgPN);

        TextView Nombre = (TextView) rowView.findViewById(R.id.NombrePN);
        Nombre.setTypeface(FuenteCuatro);
        TextView Codigo = (TextView) rowView.findViewById(R.id.CodigoPN);
        TextView Cantidad = (TextView) rowView.findViewById(R.id.CantidadPN);
        TextView Precio = (TextView) rowView.findViewById(R.id.PrecioPN);

Log.d("IMAGENSTOCK","file://"+Pnv.get(position).getImg());
       /* Picasso.with(contex)
                .load("file://"+Pnv.get(position).getImg())
                .fit()
                .into(Imagen);
        */


        Nombre.setText(Pnv.get(position).getModelo());
        Codigo.setText(Pnv.get(position).getIdProducto());
        Cantidad.setText(Pnv.get(position).getCantidad());
        Precio.setText(Pnv.get(position).getPrecio());

        return rowView;
    }
}
