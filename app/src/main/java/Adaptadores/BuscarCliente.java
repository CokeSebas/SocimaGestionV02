package Adaptadores;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.odril.socimagestionv02.BaseDatos;
import com.odril.socimagestionv02.globals;
import com.odril.socimagestionv02.R;

/**
 * Created by Alvaro on 27/02/2015.
 */
public class BuscarCliente extends CursorAdapter {
    private BaseDatos BD;

    globals g = globals.getInstance();
    int idVendedor = g.getIdVendedor();


    public BuscarCliente(Context context, Cursor c) {
        super(context, c);
        BD = new BaseDatos(context, "SocimaGestion", null, 1);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final TextView view = (TextView) inflater.inflate(R.layout.busqueda_cliente, parent, false);
        String item = CrearItem(cursor);
        view.setText(item);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String item = CrearItem(cursor);
        ((TextView) view).setText(item);

    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        Cursor Cs = null;
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);

        }
        String args = "";
        if (constraint != null) {
            args = constraint.toString();
        }
        //System.out.println("dato busqueda vendedor " + args);
        //System.out.println("dato codigo vendedor " + idVendedor);

        //Cs = BD.BuscarCliente(args);
        Cs = BD.BuscarCliente(args, idVendedor);
        //System.out.println("dato total clientes " + Cs.getCount());
        return Cs;
    }

    private String CrearItem(Cursor cursor) {
        String item = cursor.getString(1);
        return item;
    }
}

