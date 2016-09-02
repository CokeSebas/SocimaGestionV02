package com.odril.Socima_Gestion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.odril.socimagestionv02.R;
import com.squareup.picasso.Picasso;


public class FragmentoInformacionBanner extends Fragment {
    ImageView TercerBanner;
    ImageView SegundoBanner;
    ImageView PrimerBanner;
    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;

    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    TextView Titulo;
    FragmentoCatalogo FragmentoCatalogo;

    String UUno, UDos, UTres, IUno, IDos, ITres;
    SQLiteDatabase db;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View ly = inflater.inflate(R.layout.fragmento_informacion, container, false);
        Usuario = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        EditarUsuario = Usuario.edit();
        Titulo = (TextView) ly.findViewById(R.id.TituloInicio);
        PrimerBanner = (ImageView) ly.findViewById(R.id.PrimerBanner);
        SegundoBanner = (ImageView) ly.findViewById(R.id.SegundoBanner);
        TercerBanner = (ImageView) ly.findViewById(R.id.TercerBanner);
        FuenteUno = Typeface.createFromAsset(getActivity().getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getActivity().getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getActivity().getAssets(), "fonts/cuatro.ttf");
        Titulo.setTypeface(FuenteUno);

        final BaseDatos SocimaGestion = new BaseDatos(this.getActivity(), "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();


        Cursor Ban = db.rawQuery("Select * from Mv_Banner", null);
        if (Ban.moveToFirst()) {
            do {
                switch (Ban.getInt(0)) {
                    case 1:
                        UUno = Ban.getString(1);
                        IUno = Ban.getString(2);
                        break;
                    case 2:
                        UDos = Ban.getString(1);
                        IDos = Ban.getString(2);
                        break;
                    case 3:
                        UTres = Ban.getString(1);
                        ITres = Ban.getString(2);
                        break;
                }

            }
            while (Ban.moveToNext());

        }
/*System.out.println("banner 1 " + UUno + ' ' + IUno);
System.out.println("banner 2 " + UDos + ' ' + IDos);
System.out.println("banner 3 " + UTres + ' ' + ITres);*/

        if (!(UUno.equals("Null") | IUno.equals("Null"))) {

            String str = UUno;
            String urld = "";
            String delimiter = "/";
            String[] temp;
            temp = str.split(delimiter);
            //System.out.println("dato00 " + temp[1]);
            if(temp[1].equals("mnt")){
                urld = "file:///mnt/sdcard/SocimaGestion/Banner1.jpg";
            }else if(temp[1].equals("storage")){
                urld = "file:///storage/sdcard0/SocimaGestion/Banner1.jpg";
            }

            Picasso.with(getActivity())
                    //.load("file:///mnt/sdcard/SocimaGestion/Banner1.jpg")
                    .load(urld)
                    //.load("file:///storage/sdcard0/SocimaGestion/Banner1.jpg")
                    .fit()
                    .error(R.drawable.sinimagen)
                    .into(PrimerBanner);
            PrimerBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentoCatalogo = new FragmentoCatalogo();
                    android.support.v4.app.FragmentTransaction FragmentoAccion2 = getActivity().getSupportFragmentManager().beginTransaction();
                    FragmentoAccion2.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
                    FragmentoAccion2.replace(R.id.CatalagoPrincipal, FragmentoCatalogo);
                    FragmentoAccion2.commit();
                    String Nombre = "";
                    //System.out.println("dato error "+ IUno);
                    //if(IUno != ""){
                    if(!IUno.equals("")){
                        Cursor N = SocimaGestion.NombreMenuHijo(db, Integer.parseInt(IUno));
                        if (N.moveToFirst()) {
                            do {
                                Nombre = N.getString(2);
                            } while (N.moveToNext());
                        }
                    }
                    /*Cursor N = SocimaGestion.NombreMenuHijo(db, Integer.parseInt(IUno));
                    if (N.moveToFirst()) {
                        do {
                            Nombre = N.getString(2);
                        } while (N.moveToNext());
                    }*/
                    EditarUsuario.putString("NombreSubCategoria", "" + Nombre);
                    EditarUsuario.putString("IDSubCategoria", "" + IUno);
                    EditarUsuario.apply();
                }
            });
        }
        if (!(UDos.equals("Null") | IDos.equals("Null"))) {
            SegundoBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditarUsuario.putString("Producto",""+IDos);
                    EditarUsuario.apply();
                    Intent I  = new Intent(getActivity(),DetalleProducto.class);
                    getActivity().startActivity(I);
                    getActivity().overridePendingTransition(R.anim.fadeindos, R.anim.fadeoutdos);

                }
            });
            String str = UDos;
            String urld = "";
            String delimiter = "/";
            String[] temp;
            temp = str.split(delimiter);
            //System.out.println("dato00 " + temp[1]);
            if(temp[1].equals("mnt")){
                urld = "file:///mnt/sdcard/SocimaGestion/Banner2.jpg";
            }else if(temp[1].equals("storage")){
                urld = "file:///storage/sdcard0/SocimaGestion/Banner2.jpg";
            }

            Picasso.with(getActivity())
                    //.load("file:///mnt/sdcard/SocimaGestion/Banner2.jpg")
                    .load(urld)
                    //.load("file:///storage/sdcard0/SocimaGestion/Banner2.jpg")
                    .fit()
                    .error(R.drawable.sinimagen)
                    .into(SegundoBanner);
        }
        if (!(UTres.equals("Null") | ITres.equals("Null"))) {
            TercerBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditarUsuario.putString("Producto",""+ITres);
                    EditarUsuario.apply();
                    Intent I  = new Intent(getActivity(),DetalleProducto.class);
                    getActivity().startActivity(I);
                    getActivity().overridePendingTransition(R.anim.fadeindos, R.anim.fadeoutdos);


                }
            });
            String str = UTres;
            String urld = "";
            String delimiter = "/";
            String[] temp;
            temp = str.split(delimiter);
            //System.out.println("dato00 " + temp[1]);
            if(temp[1].equals("mnt")){
                urld = "file:///mnt/sdcard/SocimaGestion/Banner3.jpg";
            }else if(temp[1].equals("storage")){
                urld = "file:///storage/sdcard0/SocimaGestion/Banner3.jpg";
            }

            Picasso.with(getActivity())
                    .load(urld)
                    //.load("file:///mnt/sdcard/SocimaGestion/Banner3.jpg")
                    //.load("file:///storage/sdcard0/SocimaGestion/Banner3.jpg")
                    .fit()
                    .error(R.drawable.sinimagen)
                    .into(TercerBanner);
        }


        return ly;
    }

}
