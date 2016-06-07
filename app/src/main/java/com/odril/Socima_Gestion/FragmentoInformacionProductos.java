package com.odril.Socima_Gestion;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.odril.socimagestionv02.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import Adaptadores.ProductoSinStock;
import objetos.Producto;

public class FragmentoInformacionProductos extends Fragment {
    TextView TxListaProductosNuevos, TxListaProductosSinStock;
    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        FuenteUno = Typeface.createFromAsset(getActivity().getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getActivity().getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getActivity().getAssets(), "fonts/cuatro.ttf");

        ListView LsProductoSk;
        ListView LsProductoNuevos;


        BaseDatos SocimaGestion = new BaseDatos(this.getActivity(), "SocimaGestion", null, 1);
        SQLiteDatabase db = SocimaGestion.getWritableDatabase();

        View ly = inflater.inflate(R.layout.fragmento_informacion_productos, container, false);
        TxListaProductosNuevos = (TextView) ly.findViewById(R.id.TxTituloProductoNuevo);
        TxListaProductosSinStock = (TextView) ly.findViewById(R.id.TxTituloProductoSinStock);

        TxListaProductosSinStock.setTypeface(FuenteUno);
        TxListaProductosNuevos.setTypeface(FuenteUno);

        Cursor ProductoNuevos = db.rawQuery("SELECT * from Mv_Producto where Cantidad != 0", null);
        //System.out.println("dato productos nuevos " + ProductoNuevos.getCount() );
        LsProductoNuevos = (ListView) ly.findViewById(R.id.LsProductoNuevo);
        ArrayList<Producto> Pnv = new ArrayList<>();
        ProductoNuevos.moveToFirst();
        if (!ProductoNuevos.isAfterLast()) {
            do {
                int CPN = ProductoNuevos.getInt(0);
                String Modelo = ProductoNuevos.getString(1);
                int Precio = ProductoNuevos.getInt(7);
                int cantidad = ProductoNuevos.getInt(6);
                String FAD = ProductoNuevos.getString(9);
                String FechaActual = "";
                Date FAC = null;

                Calendar cal = new GregorianCalendar();
                Date date = cal.getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                FechaActual = df.format(date);
                Date FA = null;

                try {
                    FA = df.parse(FAD);
                    FAC = df.parse(FechaActual);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar CFA = Calendar.getInstance();
                Calendar CFAC = Calendar.getInstance();

                CFAC.setTime(FAC);
                CFA.setTime(FA);
                long Mil1 = CFA.getTimeInMillis();
                long Mil2 = CFAC.getTimeInMillis();

                long Diferencia = Mil1 - Mil2;
                long dDias = Math.abs(Diferencia / (24 * 60 * 60 * 1000));
                /*System.out.println("dato id producto " + CPN);
                System.out.println("dato dias " + dDias);
                System.out.println("dato FAD " + FAD);
                System.out.println("dato FAD " + FA);
                System.out.println("dato FAD " + Mil1);
                System.out.println("dato FAD " + Mil2);*/
                if (dDias <= 10) {
                    Log.d("ProductoNuevo", "" + CPN);
                    Producto P = new Producto("" + CPN, "" + Modelo, "", "", "", "", "" + cantidad, "" + Precio, "", "", "", "", "", null, null, null, null);
                    Pnv.add(P);
                }

            } while (ProductoNuevos.moveToNext());
            ProductoNuevos.close();
        }

        int dia = 0;
        String fechaActual = "";
        String fecha5DiasAntes = "";
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        String mes2 = String.valueOf(fecha.get(Calendar.MONTH) + 1);
        String dia2 = String.valueOf(fecha.get(Calendar.DAY_OF_MONTH));
        String dia3 = String.valueOf(fecha.get(Calendar.DAY_OF_MONTH)-5);
        String mes = "";
        if(mes2.length() == 1){
            mes = "0"  + mes2;
        }else{
            mes = mes2;
        }


        if(dia2.length() == 1){
            dia = 0 + Integer.parseInt(dia2);
            fechaActual = año + "-" + mes + "-0" + dia;
        }else{
            dia = Integer.parseInt(dia2);
            fechaActual = año + "-" + mes + "-" + dia;
        }

        if(dia3.length() == 1){
            dia = 0 + Integer.parseInt(dia2);
            fecha5DiasAntes = año+"-"+mes+"-0"+(dia-5);
        }else{
            dia = Integer.parseInt(dia2);
            fecha5DiasAntes = año+"-"+2+"-"+(dia-5);
        }


        //Cursor ProductosSk = db.rawQuery("SELECT * from Mv_Producto where Cantidad = 0", null);
        Cursor ProductosSk = db.rawQuery("SELECT * from Mv_Producto where Cantidad = 0 AND FFA BETWEEN '" + fecha5DiasAntes + "' AND '" + fechaActual + "'", null);
        //Cursor ProductosSk = db.rawQuery("SELECT * from Mv_Producto where Cantidad = 0 AND FFA BETWEEN '2015-11-01' AND '2015-11-31'", null);
//System.out.println("dato stock productos " + ProductosSk.getCount());
        LsProductoSk = (ListView) ly.findViewById(R.id.LsSinStock);
        ArrayList<Producto> Psk = new ArrayList<>();
        ProductosSk.moveToFirst();

        if (!ProductosSk.isAfterLast()) {
            do {
                int CodigoProducto = ProductosSk.getInt(0);
                String Modelo = ProductosSk.getString(1);
                Producto P = new Producto("" + CodigoProducto, "" + Modelo, "", "", "", "", "", "", "", "", "", "", "", null, null, null, null);
                Psk.add(P);
            } while (ProductosSk.moveToNext());
            ProductosSk.close();
        }
        LsProductoNuevos.setAdapter(new Adaptadores.ProductosNuevos(getActivity(), Pnv));
        LsProductoSk.setAdapter(new ProductoSinStock(getActivity(), Psk));

        return ly;
    }
}


