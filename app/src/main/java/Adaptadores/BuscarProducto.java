package Adaptadores;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.odril.Socima_Gestion.BaseDatos;
import com.odril.socimagestionv02.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Alvaro on 01/03/2015.
 */
public class BuscarProducto extends CursorAdapter {
    private BaseDatos BD;
    public String IS, NS;
    DisplayImageOptions options;

    public BuscarProducto(Context context, Cursor c) {
        super(context, c);
        BD = new BaseDatos(context, "SocimaGestion", null, 1);

    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.busqueda_producto, parent, false);

        TextView Nombre = (TextView) view.findViewById(R.id.lblItemProducto);
        TextView Codigo = (TextView) view.findViewById(R.id.lblCodigoProducto);
        Nombre.setText("" + cursor.getString(1));
        Codigo.setText("" + cursor.getInt(0));

        ImageView Img = (ImageView) view.findViewById(R.id.ImgProducto);


        String Producto = "" + cursor.getInt(0);
        String IMAGEN = "";
        IS = "";

        File Producto2 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_2.jpg");

        if (Producto2.exists()) {
            IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_2.jpg";

        } else {
            File Producto3 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_3.jpg");
            if (Producto3.exists()) {
                IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_3.jpg";

            } else {
                File Producto4 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_4.jpg");
                if (Producto4.exists()) {
                    IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_4.jpg";

                } else {
                    File Producto5 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_5.jpg");
                    if (Producto5.exists()) {
                        IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_5.jpg";

                    } else {
                        NS = IS = IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/sinimagen-1.jpg";

                    }
                }
            }

        }


        Picasso.with(context)
                .load("file://" + IMAGEN)
                .fit()
                .error(R.drawable.sinimagen)
                .into((android.widget.ImageView) view.findViewById(R.id.ImgProducto));

        return view;
    }

    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        Cursor Cs = null;
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);

        }
        String args = "";
        if (constraint != null) {
            args = constraint.toString();
        }
        Cs = BD.getCursorProductos(args);
        return Cs;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView Nombre = (TextView) view.findViewById(R.id.lblItemProducto);
        TextView Codigo = (TextView) view.findViewById(R.id.lblCodigoProducto);
        Nombre.setText("" + cursor.getString(1));
        Codigo.setText("" + cursor.getInt(0));
        ImageView Img = (ImageView) view.findViewById(R.id.ImgProducto);


        String Producto = "" + cursor.getInt(0);
        String IMAGEN = "";
        IS = "";

        File Producto2 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_2.jpg");

        if (Producto2.exists()) {
            IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_2.jpg";

        } else {
            File Producto3 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_3.jpg");
            if (Producto3.exists()) {
                IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_3.jpg";

            } else {
                File Producto4 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_4.jpg");
                if (Producto4.exists()) {
                    IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_4.jpg";

                } else {
                    File Producto5 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_5.jpg");
                    if (Producto5.exists()) {
                        IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Producto + "_5.jpg";

                    } else {
                        NS = IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/sinimagen-1.jpg";

                    }
                }
            }

        }


        Picasso.with(context)
                .load("file://" + IMAGEN)
                .fit()
                .error(R.drawable.sinimagen)
                .into((android.widget.ImageView) view.findViewById(R.id.ImgProducto));


    }


}