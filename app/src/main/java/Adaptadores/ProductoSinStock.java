package Adaptadores;


import android.content.Context;
import android.graphics.Bitmap;
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


public class ProductoSinStock extends BaseAdapter {
    Context context;
    ArrayList<Producto> Psk;

   public  ProductoSinStock (Context context, ArrayList<Producto> Ps) {
        this.context = context;
        this.Psk = Ps;

    }

    @Override
    public int getCount() {
        return this.Psk.size();
    }

    @Override
    public Object getItem(int position) {
        return this.Psk.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(25))
                .cacheInMemory(true).build();

        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.producto_sin_stock, parent, false);

        }
        TextView Nombre = (TextView) rowView.findViewById(R.id.NombreSk);
        TextView codigo = (TextView) rowView.findViewById(R.id.CodigoSk);
        ImageView Imagen = (ImageView) rowView.findViewById(R.id.ImgSk);

        Log.d("IMAGENSTOCK", "file://" + Psk.get(position).getImg());

        if(!Psk.get(position).getImg().equals("SIN")) {
            Picasso.with(context)
                    .load("file://" + Psk.get(position).getImg())
                    .fit()
                    .error(R.drawable.sinimagen)
                    .into(Imagen);
        }
        else
        {
            Picasso.with(context)
                    .load("file://"+Psk.get(position).getImg())
                    .fit()
                    .error(R.drawable.sinimagen)
                    .into(Imagen);
        }

        Nombre.setText(Psk.get(position).getModelo());
        codigo.setText(Psk.get(position).getIdProducto());

        return rowView;
    }
}

