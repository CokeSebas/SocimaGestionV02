package com.odril.Socima_Gestion;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odril.socimagestionv02.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import Adaptadores.RecyclerItemClickListener;


public class DetalleProducto extends ActionBarActivity {
    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    RelativeLayout drawerView;
    LinearLayout mainViewDos;
    ImageButton Carro;
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;
    ImageView Der;
    ImageView Izq;
    ViewPager viewPager;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    Button TerminarVenta;
    int Contadores;
    FragmentoCatalogo FragmentoCatalogo;

    static Activity ds;
    ArrayList<String> Imagenes;
    String idProducto, Modelo, Descripcion, CodigoProducto, CodigoBodega, StockInicial, CantidadSk, Precio, Tam, FFA, Descuento, FFDI, FFDF, Marca, IdCategoria, NombreCategoria, NombreSubCategoria;
    String DescuentoActivo = "NO";

    DecimalFormat formateador = new DecimalFormat("###,###.##");

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
        FragmentManager Fm = getFragmentManager();
        FragmentTransaction ft = Fm.beginTransaction();
        FragmentoCarroCompra FGC = new FragmentoCarroCompra();
        ft.replace(R.id.FrgListado, FGC);
        ft.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_detalle_producto);
        ds = this;
        Carro = (ImageButton) findViewById(R.id.Carro);
        idProducto = Modelo = Descripcion = CodigoBodega = CodigoProducto = StockInicial = CantidadSk = Precio = Tam = FFA = Descuento = FFDI = FFDF = null;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();
        Cliente = getSharedPreferences("Cliente", MODE_PRIVATE);
        EditarCliente = Cliente.edit();

        FuenteUno = Typeface.createFromAsset(getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getAssets(), "fonts/cuatro.ttf");

        drawerView = (RelativeLayout) findViewById(R.id.drawerView);
        mainViewDos = (LinearLayout) findViewById(R.id.mainViewDos);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        SocimaGestion = new BaseDatos(this, "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();
        Log.d("CodigoProducto", "" + Usuario.getString("Producto", "0"));
        CodigoProducto = "" + Usuario.getString("Producto", "0");
//System.out.println("dato categoria " + CodigoProducto);
        if (!CodigoProducto.equals("0")) {

            String[] args2 = {"" + CodigoProducto};
            //Cursor P = db.rawQuery("Select pr.*, cp.idCategoria from Mv_Producto pr JOIN Mv_categoriaProducto cp ON (pr.idProducto = cp.idProducto)  where pr.idProducto = ?", args2);
            //Cursor P = db.rawQuery("Select pr.*, cp.idCategoria, mh.nombre, m.nombre from Mv_Producto pr JOIN Mv_categoriaProducto cp ON (pr.idProducto = cp.idProducto) JOIN Mv_MenuHijo mh ON (mh.idMenuHijo = cp.idCategoria) JOIN Mv_Menu m ON (mh.idMenuPrincipal = m.idMenuPrincipal) where pr.idProducto = ?", args2);
            Cursor P = db.rawQuery("Select pr.* from Mv_Producto pr where pr.idProducto = ?", args2);
            //System.out.println("dato count " + P.getCount());
            if (P.moveToFirst()) {
                do {
                    //System.out.println("dato precio producto " + P.getInt(7));
                    //System.out.println("dato id producto " + P.getInt(0));
                    idProducto = "" + P.getInt(0);
                    Modelo = P.getString(1);
                    Descripcion = P.getString(2);
                    CodigoProducto = "" + P.getInt(3);
                    CodigoBodega = P.getString(4);
                    StockInicial = "" + P.getInt(5);
                    CantidadSk = "" + P.getInt(6);
                    Precio = "" + P.getInt(7);
                    Tam = "" + P.getInt(8);
                    FFA = P.getString(9);
                    Descuento = "" + P.getInt(10);
                    FFDI = P.getString(11);
                    FFDF = P.getString(12);
                    Marca = P.getString(15);
                    /*IdCategoria = P.getString(18);
                    NombreCategoria = P.getString(19);
                    NombreSubCategoria = P.getString(20);*/
                } while (P.moveToNext());
            }

            /*System.out.println("dato categoria producto " + IdCategoria);
            System.out.println("dato categoria producto " + NombreCategoria);
            System.out.println("dato categoria producto " + NombreSubCategoria);*/
///////////////////////////////////////////////////////////////////////////////////////////////////
            ImageView Img = (ImageView) findViewById(R.id.marcaPro);
            String IMAGEN = "";

            String nombreMarca = "";
            Cursor M = db.rawQuery("Select Nombre from Mv_Marca where IdMarca = " + Marca, null);
            if (M.moveToFirst()) {
                nombreMarca = M.getString(0);
            }

            File Marca2 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + nombreMarca + ".png");

            if (Marca2.exists()) {
                IMAGEN = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + nombreMarca + ".png";
            }

            Picasso.with(getApplication())
                    .load("file://" + IMAGEN)
                    .fit()
                    .transform(new RoundedTransformation(0, 0))
                    .into((android.widget.ImageView) findViewById(R.id.marcaPro));

////////////////////////////////////////////////////////////////////////////////////////////////////
            String CR, EQ, MA, LD, CL, FU, AD;
            CR = EQ = MA = LD = CL = FU = AD = "";

            for (int i = 0; i < 7; i++) {
                int Atributo = 0;

                switch (i) {
                    case 0:
                        Atributo = 25;
                        break;
                    case 1:
                        Atributo = 20;
                        break;
                    case 2:
                        Atributo = 19;
                        break;
                    case 3:
                        Atributo = 18;
                        break;
                    case 4:
                        Atributo = 13;
                        break;
                    case 5:
                        Atributo = 16;
                        break;
                    case 6:
                        Atributo = 12;
                        break;
                }
                String[] args3 = {"" + CodigoProducto};
                Cursor D = db.rawQuery("Select Descripcion from Mv_DetalleProducto where idProducto = ? and idAtributo = " + Atributo, args3);
                if (D.moveToFirst()) {
                    do {
                        switch (Atributo) {
                            case 25:
                                CR = D.getString(0);
                                break;
                            case 20:
                                EQ = D.getString(0);
                                break;
                            case 19:
                                MA = D.getString(0);
                                break;
                            case 18:
                                LD = D.getString(0);
                                break;
                            case 13:
                                CL = D.getString(0);
                                break;
                            case 16:
                                FU = D.getString(0);
                                break;
                            case 12:
                                AD = D.getString(0);
                                break;
                        }
                    } while (D.moveToNext());
                }
            }
            TextView NumeroEmpaque = (TextView) findViewById(R.id.NumeroEmpaque);
            TextView NumeroRecomendado = (TextView) findViewById(R.id.NumeroRecomendado);
            TextView Valu = (TextView) findViewById(R.id.Valu);
            Log.d("ValoresPrecio", "" + Precio);
            Log.d("ValoresEmpaque", "" + EQ);

            if(EQ.equals("#REF!")){
                EQ = "0";
            }

            if (EQ.equals("") | EQ.toString().isEmpty()) {
                EQ = "0";
            }
            //System.out.println("dato null " + EQ);
            //System.out.println("dato null " + Precio);
            if (EQ.equals("0") | Precio.equals("0")) {
                Valu.setText("");

            } else {
                if (Precio.equals("0")) {

                } else {
                    int Vals = (Integer.parseInt(Precio) / Integer.parseInt(EQ));
                    Valu.setText("$" + Vals + " c/u Aprox");
                }
            }

            if(CodigoProducto.length() == 5){
                CodigoProducto = "0"+CodigoProducto;
            }


            Imagenes = new ArrayList<>();
            File Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + CodigoProducto + "_2.jpg");
            if (Fondo.exists()) {
                Imagenes.add(Fondo.toString());
            }
            Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + CodigoProducto + "_3.jpg");
            if (Fondo.exists()) {
                Imagenes.add(Fondo.toString());

            }
            Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + CodigoProducto + "_4.jpg");
            if (Fondo.exists()) {
                Imagenes.add(Fondo.toString());

            }
            Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + CodigoProducto + "_5.jpg");
            if (Fondo.exists()) {
                Imagenes.add(Fondo.toString());
            }

            Log.d("Imagenes total", "" + Imagenes.size());

            CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(this);
            viewPager = (ViewPager) findViewById(R.id.pager);

            viewPager.setAdapter(mCustomPagerAdapter);
            Izq = (ImageView) findViewById(R.id.Izq);
            Der = (ImageView) findViewById(R.id.Der);


            TextView TipoMaterial = (TextView) findViewById(R.id.TipoMaterial);
            TextView TipoLineaD = (TextView) findViewById(R.id.TipoLineaD);
            TextView TipoColor = (TextView) findViewById(R.id.TipoColor);
            TextView TipoFuncion = (TextView) findViewById(R.id.TipoFuncion);
            TextView TipoAdicional = (TextView) findViewById(R.id.TipoAdicional);


            NumeroEmpaque.setText(EQ);
            NumeroRecomendado.setText(CR);
            TipoMaterial.setText(MA);
            TipoLineaD.setText(LD);
            TipoColor.setText(CL);
            TipoFuncion.setText(FU);
            TipoAdicional.setText(AD);

            String FechaActual = "";
            Calendar cal = new GregorianCalendar();
            Date date = cal.getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            FechaActual = df.format(date);
            Date FA = null;
            Date FAC = null;
            Date FFIDS = null;
            Date FFFDS = null;

            try {
                FA = df.parse(FFA);
                FAC = df.parse(FechaActual);
                FFIDS = df.parse(FFDI);
                FFFDS = df.parse(FFDF);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar CFA = Calendar.getInstance();
            Calendar CFAC = Calendar.getInstance();
            Calendar CFFID = Calendar.getInstance();
            Calendar CFFFD = Calendar.getInstance();

            CFA.setTime(FA);
            CFAC.setTime(FAC);
            CFFID.setTime(FFIDS);
            CFFFD.setTime(FFFDS);

            long Mil1 = CFA.getTimeInMillis();
            long Mil2 = CFAC.getTimeInMillis();
            long Mil3 = CFFID.getTimeInMillis();
            long Mil4 = CFFFD.getTimeInMillis();

            long Diferencia = Mil1 - Mil2;
            long Diferencia2 = Mil4 - Mil3;
            long Diferencia3 = Mil2 - Mil3;


            long dDias = Diferencia / (24 * 60 * 60 * 1000);
            long dDias2 = Diferencia2 / (24 * 60 * 60 * 1000);
            long dDias3 = Diferencia3 / (24 * 60 * 60 * 1000);
            Log.d("dDias3", "" + dDias3);

            LinearLayout LTC = (LinearLayout) findViewById(R.id.LTC);
            TextView Esd = (TextView) findViewById(R.id.Esd);
            TextView Dsc = (TextView) findViewById(R.id.Descuento);
            int Dz = (Integer.parseInt(StockInicial) / 100) * 10;
            if (dDias <= 10) {

                LTC.setVisibility(LinearLayout.VISIBLE);
                LTC.setBackgroundColor(Color.parseColor("#ff0600a3"));
                Esd.setText("PRODUCTO NUEVO");
            }
            long total = dDias2 - dDias3;

            //if (total > 0) {
            if (Integer.parseInt(Descuento) != 0) {
                LTC.setVisibility(LinearLayout.VISIBLE);
                LTC.setBackgroundColor(Color.parseColor("#ffd67010"));
                Esd.setText("PRODUCTO CON DESCUENTO");
                Dsc.setText("$" + formateador.format(Double.parseDouble(Descuento)) + " (Dscto)");
                Dsc.setVisibility(View.VISIBLE);
                DescuentoActivo = "SI";

            } else {
                LTC.setVisibility(LinearLayout.GONE);
                DescuentoActivo = "NO";

            }

            int porcentaje = (Integer.parseInt(StockInicial)*10/100);
            //if (Integer.parseInt(CantidadSk) < Dz) {
            if (Integer.parseInt(CantidadSk) < porcentaje) {
                LTC.setVisibility(LinearLayout.VISIBLE);
                LTC.setBackgroundColor(Color.parseColor("#ffa3200a"));
                Esd.setText("QUEDAN " + CantidadSk);
            }


            Izq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int Total = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(Total - 1);
                }
            });

            Der.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int Total = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(Total + 1);
                }
            });
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int TM = Imagenes.size();
                    int PaginaActual = viewPager.getCurrentItem();

                    if (TM < 2) {
                        Izq.setVisibility(View.INVISIBLE);
                        Der.setVisibility(View.INVISIBLE);
                    } else if (PaginaActual == 0) {
                        Izq.setVisibility(View.INVISIBLE);
                        Der.setVisibility(View.VISIBLE);
                    } else if (PaginaActual == (TM - 1)) {
                        Izq.setVisibility(View.VISIBLE);
                        Der.setVisibility(View.INVISIBLE);
                    } else if (PaginaActual > 0 && (PaginaActual != TM)) {
                        Izq.setVisibility(View.VISIBLE);
                        Der.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            ArrayList<ArrayList<String>> outterArray = new ArrayList<>();


            String[] args4 = {"" + CodigoProducto};
            int Con = 0;
System.out.println("dato id producto " + CodigoProducto);
            Cursor Re = db.rawQuery("Select idRelacion from Mv_ProductoRelacion where idProducto = ?", args4);
            //Cursor Re = db.rawQuery("Select pr.idRelacion, p.Cantidad, pr.idProducto from Mv_ProductoRelacion pr JOIN Mv_Producto p ON (pr.idProducto = p.idProducto) where pr.idProducto = " + CodigoProducto, null);
            System.out.println("dato producto relacionado " + Re.getCount());
            if(Re.moveToFirst()){
                Cursor can = db.rawQuery("SELECT Cantidad from MV_producto WHERE idProducto = " + Re.getString(0),null);
                if(can.moveToFirst()){
                    System.out.println("dato cantidad pr rela " + can.getString(0));
                    if(can.getInt(0) != 0){
                        if (Re.moveToFirst()) {
                            do {
                                Cursor Rel = db.rawQuery("Select idProducto,Modelo from Mv_Producto where idProducto = " + Re.getString(0), null);
                                //Cursor Rel = db.rawQuery("Select idProducto,Modelo from Mv_Producto where Cantidad != 0 AND idProducto = " + Re.getString(0), null);
                                System.out.println("dato producto relacionado2 " + Rel.getCount());
                                ArrayList<String> innerArray = new ArrayList<>();

                                if (Rel.moveToFirst()) {
                                    do {
                                        innerArray.add(0, "" + Rel.getInt(0));
                                        innerArray.add(1, "" + Rel.getString(1));
                                    } while (Rel.moveToNext());
                                }
                                outterArray.add(Con, innerArray);
                                Con++;
                            }
                            while (Re.moveToNext());
                        }
                    }
                }
            }

            /*if (Re.moveToFirst()) {
                do {
                    System.out.println("dato producto relacionado " + Re.getString(0));
                    //System.out.println("dato producto relacionado cantidad " + Re.getString(1));
                    //System.out.println("dato producto relacionado producto " + Re.getString(2));
                    Cursor Rel = db.rawQuery("Select idProducto,Modelo from Mv_Producto where idProducto = " + Re.getString(0), null);
                    //Cursor Rel = db.rawQuery("Select idProducto,Modelo from Mv_Producto where Cantidad != 0 AND idProducto = " + Re.getString(0), null);
                    System.out.println("dato producto relacionado2 " + Rel.getCount());
                    ArrayList<String> innerArray = new ArrayList<>();

                    if (Rel.moveToFirst()) {
                        do {
                            innerArray.add(0, "" + Rel.getInt(0));
                            innerArray.add(1, "" + Rel.getString(1));
                        } while (Rel.moveToNext());
                    }
                    outterArray.add(Con, innerArray);
                    Con++;
                }
                while (Re.moveToNext());
            }*/


            final MyAdapter adapter = new MyAdapter(outterArray);


            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d("CLICK", "ACPRETADO" + adapter.mDataset.get(position).get(1));
                    EditarUsuario.putString("Producto", "" + adapter.mDataset.get(position).get(0));
                    EditarUsuario.apply();
                    Intent id = new Intent(DetalleProducto.this, DetalleProducto.class);
                    startActivity(id);
                    finish();
                }
            }));
            Contadores = 0;

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.noticias, R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                    Contadores = 0;

                }


                public void onDrawerOpened(View drawerView) {
                    Contadores = 0;


                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    mainViewDos.setTranslationX(-slideOffset * drawerView.getWidth());
                    mDrawerLayout.bringChildToFront(drawerView);
                    mDrawerLayout.requestLayout();
                    mDrawerLayout.setScrimColor(Color.TRANSPARENT);
                    float t = 0.05f;


                    if (t > slideOffset && Contadores == 0) {
                        supportInvalidateOptionsMenu();
                        FragmentManager Fm = getFragmentManager();
                        FragmentTransaction ft = Fm.beginTransaction();
                        FragmentoCarroCompra FGC = new FragmentoCarroCompra();
                        ft.replace(R.id.FrgListado, FGC);
                        ft.commit();
                        Contadores = 1;
                    }


                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            Carro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    supportInvalidateOptionsMenu();
                    FragmentManager Fm = getFragmentManager();
                    FragmentTransaction ft = Fm.beginTransaction();
                    FragmentoCarroCompra FGC = new FragmentoCarroCompra();
                    ft.replace(R.id.FrgListado, FGC);
                    ft.commit();
                    mDrawerLayout.openDrawer(Gravity.RIGHT);

                }
            });

            TextView NombreProducto = (TextView) findViewById(R.id.NombreProducto);
            NombreProducto.setText("" + Modelo);
            NombreProducto.setTypeface(FuenteUno);
            TextView CodProducto = (TextView) findViewById(R.id.CodProducto);
            CodProducto.setText("#" + CodigoProducto);
            CodProducto.setTypeface(FuenteUno);
            TextView PrecioProducto = (TextView) findViewById(R.id.PrecioProducto);
            PrecioProducto.setText("$" + formateador.format(Double.parseDouble(Precio)));
            TextView DetalleDesc = (TextView) findViewById(R.id.DetalleDescripcion);
            DetalleDesc.setText(Descripcion);

            Button btnMas = (Button) findViewById(R.id.BtnMas);
            Button btnMenos = (Button) findViewById(R.id.BtnMenos);
            final EditText Cantidad = (EditText) findViewById(R.id.CantidadCompra);


            Cantidad.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (Cantidad.getText().toString().equals("") | Cantidad.getText().toString().isEmpty()) {

                    } else if (Integer.parseInt(Cantidad.getText().toString()) > Integer.parseInt(CantidadSk)) {
                        Cantidad.setText("" + CantidadSk);

                    } else if (Integer.parseInt(Cantidad.getText().toString()) < 1) {
                        Cantidad.setText("1");
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            Cantidad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b && (Cantidad.getText().equals("") | Cantidad.getText().toString().isEmpty())) {
                        Cantidad.setText("1");

                    }
                }
            });
            btnMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Cantidad.getText().toString().isEmpty() | Cantidad.getText().equals("")) {
                        Cantidad.setText("1");
                    }
                    int cantidad = Integer.parseInt(Cantidad.getText().toString());

                    if (cantidad < Integer.parseInt(CantidadSk)) {
                        Cantidad.setText("" + (cantidad + 1));
                    }
                }
            });

            /*Button btnCatalogo = (Button) findViewById(R.id.catalogo);
            btnCatalogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    System.out.println("test de catalogo " + IdCategoria);

                    //Intent i = new Intent(DetalleProducto.this, FragmentoCatalogo.class);
                    Intent i = new Intent(DetalleProducto.this, Catalogo.class);
                    i.putExtra("categoriax",IdCategoria);
                    i.putExtra("nombreCategoriax",NombreCategoria);
                    i.putExtra("nombreSubcategoriax",NombreSubCategoria);
                    startActivity(i);

                    FragmentoCatalogo = new FragmentoCatalogo();
                    android.support.v4.app.FragmentTransaction FragmentoAccion2 = getSupportFragmentManager().beginTransaction();
                    FragmentoAccion2.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
                    FragmentoAccion2.replace(R.id.CatalagoPrincipal, FragmentoCatalogo);
                    FragmentoAccion2.commit();
                }
            });*/


            btnMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Cantidad.getText().toString().isEmpty() | Cantidad.getText().equals("")) {
                        Cantidad.setText("1");
                    }
                    int cantidad = Integer.parseInt(Cantidad.getText().toString());
                    int Total = cantidad - 1;

                    if (Total <= 0) {
                        Total = 1;
                    }
                    Cantidad.setText("" + Total);


                }
            });

            btnMenos.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int total = 1;
                    if (Cantidad.getText().toString().isEmpty() | Cantidad.getText().equals("")) {
                        Cantidad.setText("1");
                    }
                    Cantidad.setText("" + total);
                    return true;
                }
            });
            btnMas.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {

                    if (Cantidad.getText().toString().isEmpty() | Cantidad.getText().equals("")) {
                        Cantidad.setText("" + CantidadSk);
                    }
                    Cantidad.setText("" + CantidadSk);
                    return true;
                }
            });

            Button btnPdfProducto = (Button) findViewById(R.id.btnPdfProducto);
            btnPdfProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor CL = db.rawQuery("Select Email FROM Mv_Cliente WHERE CodigoCliente = " + Cliente.getString("IDCliente", "0"), null);
                    String correo = null;
                    if (CL.moveToFirst()) {
                        correo = CL.getString(0);
                    }

                    Intent share = new Intent(Intent.ACTION_SENDTO,
                            Uri.fromParts("mailto", correo, null));

                    //share.putExtra(Intent.EXTRA_SUBJECT, Modelo);
                    share.putExtra(Intent.EXTRA_SUBJECT, "Solicitud de PDF de Productos");
                    share.putExtra(Intent.EXTRA_TEXT, "A continuación en el siguiente link podrá ver y descargar el PDF del producto de Socima: http://socimagestion.com/Mejora/tcpdf/examples/generatorProducto.php?id=" + Integer.parseInt(CodigoProducto) + "&tipo=ver");

                    startActivity(share);
                }
            });

            Button btnComprar = (Button) findViewById(R.id.BtnComprar);
            btnComprar.setTypeface(FuenteCuatro);
            btnComprar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (!(Cantidad.getText().toString().isEmpty() | Cantidad.getText().equals(""))) {
                        if (Cliente.getInt("OrdenCompra", 0) == 0 | Cliente.getString("IDCliente", "0").equals("0")) {
                            Intent i = new Intent(DetalleProducto.this, Cliente.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                            finish();

                        } else {
                            String idCliente = Cliente.getString("IDCliente", "0");
                            int OrdenC = Cliente.getInt("OrdenCompra", 0);

                            Cursor BuscarProducto = db.rawQuery("Select  * from Mv_Compra where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")", null);
                            {
                                if (BuscarProducto.moveToFirst()) {
                                    do {
                                        Log.d("PRO-idOrden", "" + BuscarProducto.getInt(0));
                                        Log.d("PRO-idCliente", "" + BuscarProducto.getInt(1));
                                        Log.d("PRO-idProducto", "" + BuscarProducto.getInt(2));
                                        Log.d("PRO-Cantidad", "" + BuscarProducto.getInt(3));
                                        Log.d("PRO-Precio", "" + BuscarProducto.getInt(4));
                                        Log.d("PRO-Descuento", "" + BuscarProducto.getInt(5));
                                        Log.d("PRO-Comentario", "" + BuscarProducto.getString(6));
                                        Log.d("PROD-NombreProducto", "" + BuscarProducto.getString(7));

                                        if (BuscarProducto.getInt(3) == Integer.parseInt(CantidadSk)) {
                                            LayoutInflater li = getLayoutInflater();
                                            View layout = li.inflate(R.layout.custom_toast_error_producto,
                                                    (ViewGroup) findViewById(R.id.Custom));
                                            layout.setMinimumHeight(80);
                                            TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                            MensajeToast.setText("[ Sin Stock " + Modelo + "   ]");
                                            Toast ts = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
                                            ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                            ts.setView(layout);
                                            ts.show();
                                        } else {
                                            if (DescuentoActivo.equals("SI")) {
                                                int s = Integer.parseInt(Cantidad.getText().toString()) + BuscarProducto.getInt(3);
                                                if (s >= Integer.parseInt(CantidadSk)) {
                                                    String Sql = "Update Mv_Compra SET Cantidad =" + Integer.parseInt(CantidadSk) + ", Descuento =" + Integer.parseInt(Descuento) + ", Comentario ='Producto con descuento de " + Descuento + "' where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")";
                                                    Log.d("SQLACTUALIZACIONDESC", Sql);
                                                    LayoutInflater li = getLayoutInflater();
                                                    View layout = li.inflate(R.layout.custom_toast_error_producto,
                                                            (ViewGroup) findViewById(R.id.Custom));
                                                    layout.setMinimumHeight(80);
                                                    TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                                    MensajeToast.setText("[ Sin Stock " + Modelo + "   ]");
                                                    Toast ts = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
                                                    ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                                    ts.setView(layout);
                                                    ts.show();
                                                    db.execSQL(Sql);
                                                } else {
                                                    String Sql = "Update Mv_Compra SET Cantidad =" + s + ", Descuento =" + Integer.parseInt(Descuento) + ", Comentario ='Producto con descuento de " + Descuento + "' where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")";
                                                    Log.d("SQLACTUALIZACIONDESC", Sql);
                                                    db.execSQL(Sql);
                                                    LayoutInflater li = getLayoutInflater();
                                                    View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                            (ViewGroup) findViewById(R.id.Custom));
                                                    layout.setMinimumHeight(80);
                                                    TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                                    MensajeToast.setText("[ " + Cantidad.getText().toString() + "  " + Modelo + " ] Agregados");
                                                    Toast ts = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
                                                    ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                                    ts.setView(layout);
                                                    ts.show();
                                                }
                                            } else {
                                                int s = Integer.parseInt(Cantidad.getText().toString()) + BuscarProducto.getInt(3);
                                                if (s >= Integer.parseInt(CantidadSk)) {
                                                    String Sql = "Update Mv_Compra SET Cantidad =" + Integer.parseInt(CantidadSk) + " where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")";
                                                    Toast.makeText(getApplication(), "Se ha vendido el stock completo.", Toast.LENGTH_LONG).show();
                                                    Log.d("SQLACTUALIZACIONDESC", Sql);
                                                    db.execSQL(Sql);
                                                } else {
                                                    String Sql = "Update Mv_Compra SET Cantidad =" + s + " where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")";
                                                    Log.d("SQLACTUALIZACIONDESC", Sql);
                                                    db.execSQL(Sql);
                                                    LayoutInflater li = getLayoutInflater();
                                                    View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                            (ViewGroup) findViewById(R.id.Custom));
                                                    layout.setMinimumHeight(80);
                                                    TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                                    MensajeToast.setText("[ " + Cantidad.getText().toString() + "  " + Modelo + " ] Agregados");
                                                    Toast ts = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
                                                    ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                                    ts.setView(layout);
                                                    ts.show();
                                                }
                                            }
                                        }
                                    } while (BuscarProducto.moveToNext());

                                } else {
                                    if (DescuentoActivo.equals("SI")) {

                                        String Sql = "INSERT INTO Mv_Compra(idOrdenCompra,idCliente,idProducto,Cantidad,Precio,Descuento,Comentario,NombreProducto)values (" + OrdenC + "," + Integer.parseInt(idCliente) + "," +
                                                Integer.parseInt(CodigoProducto) + "," +
                                                Integer.parseInt(Cantidad.getText().toString()) + ","
                                                + Integer.parseInt(Precio) + "," + Integer.parseInt(Descuento) +
                                                ",'Producto con Descuento de " + Descuento + "','" + Modelo + "'); ";
                                        Log.d("STRINGSQLINSERTDESC", Sql);
                                        db.execSQL(Sql);
                                        LayoutInflater li = getLayoutInflater();
                                        View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                (ViewGroup) findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);
                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ " + Cantidad.getText().toString() + "  " + Modelo + " ] Agregados");
                                        Toast ts = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();
                                    } else {
                                        String Sql = "INSERT INTO Mv_Compra(idOrdenCompra,idCliente,idProducto,Cantidad,Precio,Descuento,Comentario,NombreProducto)values (" + OrdenC + "," + Integer.parseInt(idCliente) + "," +
                                                Integer.parseInt(CodigoProducto) + "," +
                                                Integer.parseInt(Cantidad.getText().toString()) + ","
                                                + Integer.parseInt(Precio) + "," + 0 +
                                                ",' ','" + Modelo + "'); ";
                                        Log.d("STRINGSQLINSERTSinDESC", Sql);
                                        db.execSQL(Sql);
                                        LayoutInflater li = getLayoutInflater();
                                        View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                (ViewGroup) findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);
                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ " + Cantidad.getText().toString() + "  " + Modelo + " ] Agregados");
                                        Toast ts = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fadeintres, R.anim.fadeoutres);
        }
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        public ArrayList<ArrayList<String>> mDataset;
        String Codigo;

        public MyAdapter(ArrayList<ArrayList<String>> dataset) {
            super();
            mDataset = dataset;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = View.inflate(viewGroup.getContext(), R.layout.detalle_producto_relacion, null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            Log.d("IndiceProducto", "" + i + " - " + mDataset.get(i).get(0));
            viewHolder.mTextView.setText(mDataset.get(i).get(1));
            viewHolder.CodigoRelacion.setText(mDataset.get(i).get(0));
            Codigo = mDataset.get(i).get(0);

            File Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + Codigo + "_2.jpg");

            if(Codigo.length() == 5){
                Codigo = "0"+Codigo;
            }

            if (Fondo.exists()) {
                Log.d("Imagen", "2 Existe  :" + Fondo);
            } else {
                Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + Codigo + "_3.jpg");

                if (!Fondo.exists()) {
                    Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + Codigo + "_4.jpg");
                    if (!Fondo.exists()) {
                        Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + Codigo + "_5.jpg");
                        if (Fondo.exists()) {
                            Log.d("Imagen", "5 Existe  :" + Fondo);

                        } else {
                            Log.d("Imagen", "Sin Imagen");
                            Fondo = null;
                        }
                    } else {
                        Log.d("Imagen", "4 Existe  :" + Fondo);
                    }
                } else {
                    Log.d("Imagen", "3 Existe :" + Fondo);
                }
            }

            if (Fondo != null) {
                Picasso.with(getApplication())
                        .load("file://" + Fondo)
                        .fit()
                        .transform(new RoundedTransformation(10, 0))
                        .into(viewHolder.ImagenRelacion);
                //Picasso.with(getApplication()).load("file://" + Fondo).resize(100, 400).centerCrop().into(viewHolder.ImagenRelacion);
            } else {

                Picasso.with(getApplication())
                        .load(R.drawable.sinimagen)
                        .fit()
                        .transform(new RoundedTransformation(10, 0))
                        .into(viewHolder.ImagenRelacion);
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView mTextView;
            public TextView CodigoRelacion;
            public ImageView ImagenRelacion;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.NombreRelacion);
                CodigoRelacion = (TextView) itemView.findViewById(R.id.CodigoRelacion);
                ImagenRelacion = (ImageView) itemView.findViewById(R.id.ImagenRelacion);


            }
        }
    }


    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Imagenes.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.item_imagen, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

            if (Imagenes.size() != 0) {
                Picasso.with(mContext)
                        .load("file://" + Imagenes.get(position))
                        .fit()
                        .into(imageView);
                //Picasso.with(mContext).load("file://" + Imagenes.get(position)).resize(200,400).centerCrop().into(imageView);
                //Picasso.with(mContext).load("file://" + Imagenes.get(position)).resizeDimen(20,100).centerCrop().into(imageView);

                container.addView(itemView);

            }

            return itemView;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

    }


}
