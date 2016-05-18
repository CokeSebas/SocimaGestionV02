package com.odril.socimagestionv02;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import Adaptadores.BuscarProducto;
import Adaptadores.Menu;
import objetos.ItemMenu;
import objetos.ItemSubMenu;


public class Catalogo extends FragmentActivity implements AbsListView.OnScrollListener, AbsListView.OnItemClickListener {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    RelativeLayout drawerView;
    LinearLayout mainView;
    ImageButton Carro;
    Button Vendedores;
    ArrayList<ItemMenu> ListaItems;
    AutoCompleteTextView BuscarProductos;
    Cursor CursorBuscarProducto;
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    BuscarProducto Bsp;
    Menu MenuAdaptador;
    ExpandableListView ListaMenu;
    TextView Inicio;

    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    FragmentoCatalogo FragmentoCatalogo;
    FragmentoInformacionBanner FragmentoInformacionBanner;
    int Contadores;
    static Activity Fs;


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
            finish();

        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();

        EditarUsuario.remove("DetalleActivo");
        EditarUsuario.apply();

        Cliente = getSharedPreferences("Cliente", MODE_PRIVATE);
        EditarCliente = Cliente.edit();

        Inicio = (TextView) findViewById(R.id.Inicio);
        FragmentoInformacionBanner = new FragmentoInformacionBanner();
        android.support.v4.app.FragmentTransaction FragmentoAccion = getSupportFragmentManager().beginTransaction();
        FragmentoAccion.add(R.id.CatalagoPrincipal, FragmentoInformacionBanner).commit();

        Fs = this;

        SocimaGestion = new BaseDatos(this, "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();

        ListaMenu = (ExpandableListView) findViewById(R.id.lvExp);

        MostrarMenu();

        BuscarProductos = (AutoCompleteTextView) findViewById(R.id.BuscarProducto);
        CursorBuscarProducto = SocimaGestion.getCursorProductos("");
        startManagingCursor(CursorBuscarProducto);

        Bsp = new BuscarProducto(this, CursorBuscarProducto);
        BuscarProductos.setThreshold(2);
        BuscarProductos.setAdapter(Bsp);

        BuscarProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                BuscarProductos.setText("");
                int Dis = c.getInt(0);
                Log.d("CodigoBusqueda", "" + Dis);

                EditarUsuario.putString("Producto", "" + Dis);
                EditarUsuario.apply();
                Intent I = new Intent(Catalogo.this, DetalleProducto.class);
                startActivity(I);
                overridePendingTransition(R.anim.fadeindos, R.anim.fadeoutdos);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(BuscarProductos.getWindowToken(), 0);
            }
        });

        Button btnPdfCategoria = (Button) findViewById(R.id.btnPdfCategoria);
        btnPdfCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor CL = db.rawQuery("Select Email FROM Mv_Cliente WHERE CodigoCliente = " + Cliente.getString("IDCliente", "0"), null);
                String correo = null;
                if (CL.moveToFirst()) {
                    correo = CL.getString(0);
                }

                Intent share = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", correo, null));

                share.putExtra(Intent.EXTRA_SUBJECT, "Solicitud de PDF de Productos");
                share.putExtra(Intent.EXTRA_TEXT, "A continuación en el siguiente link podrá ver y descargar el PDF de los productos de Socima : http://socimagestion.com/Mejora/tcpdf/examples/generatorvercategory.php?id=" + Usuario.getString("IDSubCategoria", "0") + "&tipo=ver");

                startActivity(share);
            }
        });


        Vendedores = (Button) findViewById(R.id.Vendedores);
        Vendedores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Catalogo.this, SeguridadVendedor.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        });

        Carro = (ImageButton) findViewById(R.id.Carro);
        drawerView = (RelativeLayout) findViewById(R.id.drawerView);
        mainView = (LinearLayout) findViewById(R.id.mainView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        Inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditarUsuario.putString("NombreCategoriaPadre", "");
                EditarUsuario.putString("NombreSubCategoria", "Inicio");
                EditarUsuario.apply();

                FragmentoInformacionBanner = new FragmentoInformacionBanner();
                android.support.v4.app.FragmentTransaction FragmentoAccion2 = getSupportFragmentManager().beginTransaction();
                FragmentoAccion2.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
                FragmentoAccion2.replace(R.id.CatalagoPrincipal, FragmentoInformacionBanner);
                FragmentoAccion2.addToBackStack(null);

                FragmentoAccion2.commit();
            }
        });
        Contadores = 0;
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.noticias, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
                Contadores = 0;
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
                Contadores = 0;
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(-slideOffset * drawerView.getWidth());
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager Fm = getFragmentManager();
        FragmentTransaction ft = Fm.beginTransaction();
        FragmentoCarroCompra FGC = new FragmentoCarroCompra();
        ft.replace(R.id.FrgListado, FGC);
        ft.commit();
    }

    public void MostrarMenu() {
        CargarMenu();
        MenuAdaptador = new Menu(this, ListaItems);
        ListaMenu.setAdapter(MenuAdaptador);
        ListaMenu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {


            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {


                EditarUsuario.putString("NombreCategoriaPadre", "" + ListaItems.get(groupPosition).getTitulo());
                EditarUsuario.putString("NombreSubCategoria", "" + ListaItems.get(groupPosition).getListaSubMenu().get(childPosition).getNombreLimpio());
                EditarUsuario.putString("IDSubCategoria", "" + ListaItems.get(groupPosition).getListaSubMenu().get(childPosition).getID());
                EditarUsuario.apply();

                /*System.out.println("dato nombre categoria " + ListaItems.get(groupPosition).getTitulo());
                System.out.println("dato nombre sub categoria " + ListaItems.get(groupPosition).getListaSubMenu().get(childPosition).getNombreLimpio());
                System.out.println("dato nombre id categoria " + ListaItems.get(groupPosition).getListaSubMenu().get(childPosition).getID());*/

                FragmentoCatalogo = new FragmentoCatalogo();
                android.support.v4.app.FragmentTransaction FragmentoAccion2 = getSupportFragmentManager().beginTransaction();
                FragmentoAccion2.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
                FragmentoAccion2.replace(R.id.CatalagoPrincipal, FragmentoCatalogo);
                FragmentoAccion2.commit();
                //System.out.println("dato ntest abre catalogo");

                return true;

            }
        });
    }

    public void CargarMenu() {

        ListaItems = new ArrayList<>();

        ArrayList<ItemSubMenu> ListaItemsChilds;
        Cursor ListaItemsMenu = db.rawQuery("SELECT idMenuPrincipal,Nombre FROM Mv_Menu ORDER BY Nombre asc", null);
        //Cursor ListaItemsMenu = db.rawQuery("SELECT DISTINCT mn.idMenuPrincipal, mn.Nombre FROM Mv_Menu mn JOIN Mv_categoriaProducto cp ON (mn.idMenuPrincipal = cp.idCategoria) ORDER BY mn.Nombre asc", null);
        ListaItemsMenu.moveToFirst();
        //System.out.println("dato cantidad categorias " + ListaItemsMenu.getCount());

        if (!ListaItemsMenu.isAfterLast()) {
            do {
                ListaItemsChilds = new ArrayList<>();
                String ID = ListaItemsMenu.getString(0);
                String Nombre = ListaItemsMenu.getString(1);
                if (ID.equals("1406")) {
                    continue;
                }

                //System.out.println("dato categoria padre " + ID);
                Cursor SubItems = db.rawQuery("SELECT DISTINCT mh.*, cp.idCategoria FROM Mv_MenuHijo mh " +
                        "JOIN Mv_categoriaProducto cp ON (mh.idMenuHijo = cp.idCategoria) JOIN Mv_Producto p ON (cp.idProducto = p.idProducto)" +
                        " where mh.idMenuPrincipal = " + ID + " AND p.Cantidad != 0 " +
                        "ORDER BY mh.Nombre asc", null);

                /*Cursor SubItems = db.rawQuery("SELECT DISTINCT mh.*, cp.idCategoria FROM Mv_MenuHijo mh " +
                        "JOIN Mv_categoriaProducto cp ON (mh.idMenuHijo = cp.idCategoria) " +
                        " where mh.idMenuPrincipal = " + ID +
                        " ORDER BY mh.Nombre asc", null);*/

                if(SubItems.getCount() == 0){
                    SubItems = db.rawQuery("SELECT DISTINCT mh.*, cp.idCategoria FROM Mv_MenuHijo mh " +
                            "JOIN Mv_categoriaProducto cp ON (mh.idMenuHijo = cp.idCategoria) " +
                            "JOIN Mv_MenuHijo mh2 ON (mh2.idMenuHijo = mh.idMenuPrincipal) " +
                            " where mh2.idMenuPrincipal = " + ID +
                            " ORDER BY mh.Nombre asc", null);
                }


                SubItems.moveToFirst();
                if (!SubItems.isAfterLast()) {
                    do {
                        //System.out.println("dato categoria hijo 1 " + SubItems.getInt(0));

                        Cursor PC = db.rawQuery("Select cp.idProducto from Mv_categoriaProducto cp JOIN Mv_Producto p ON (cp.idProducto = p.idProducto) where cp.idCategoria = " +  SubItems.getInt(0) + " AND p.Cantidad != 0", null);

                        String IDS = SubItems.getString(0);
                        String IDSP = SubItems.getString(1);
                        String NombreS = ">  " + SubItems.getString(2) + "   ("+PC.getCount()+")";
                        ItemSubMenu itemsChilds = new ItemSubMenu(NombreS, IDS, IDSP);
                        ListaItemsChilds.add(itemsChilds);

                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        Cursor SubItems2 = db.rawQuery("SELECT DISTINCT mh.*, cp.idCategoria FROM Mv_MenuHijo mh " +
                                "JOIN Mv_categoriaProducto cp ON (mh.idMenuHijo = cp.idCategoria) JOIN Mv_Producto p ON (cp.idProducto = p.idProducto)" +
                                " where mh.idMenuPrincipal = " + SubItems.getInt(0) + " AND p.Cantidad != 0 " +
                                "ORDER BY mh.Nombre asc", null);

                        SubItems2.moveToFirst();
                        if (!SubItems2.isAfterLast()) {
                            do {
                                //System.out.println("dato categoria hijo 2 " + SubItems2.getInt(0));
                                Cursor PC2 = db.rawQuery("Select cp.idProducto from Mv_categoriaProducto cp JOIN Mv_Producto p ON (cp.idProducto = p.idProducto) where cp.idCategoria = " +  SubItems2.getInt(0) + " AND p.Cantidad != 0", null);

                                String IDS2 = SubItems2.getString(0);
                                String IDSP2 = SubItems2.getString(1);
                                String NombreS2 = "> >  " + SubItems2.getString(2) + "   ("+PC2.getCount()+")";
                                ItemSubMenu itemsChilds2 = new ItemSubMenu(NombreS2, IDS2, IDSP2);
                                ListaItemsChilds.add(itemsChilds2);
                                //---------------------------------------------------------------------------------------------------------------------------------
                                Cursor SubItems3 = db.rawQuery("SELECT DISTINCT mh.*, cp.idCategoria FROM Mv_MenuHijo mh " +
                                        "JOIN Mv_categoriaProducto cp ON (mh.idMenuHijo = cp.idCategoria) JOIN Mv_Producto p ON (cp.idProducto = p.idProducto)" +
                                        " where mh.idMenuPrincipal = " + SubItems2.getInt(0) + " AND p.Cantidad != 0 " +
                                        "ORDER BY mh.Nombre asc", null);
                                SubItems3.moveToFirst();
                                if(!SubItems3.isAfterLast()){
                                    do{
                                        //System.out.println("dato categoria hijo 3 " + SubItems3.getInt(0));
                                        Cursor PC3 = db.rawQuery("Select cp.idProducto from Mv_categoriaProducto cp JOIN Mv_Producto p ON (cp.idProducto = p.idProducto) where cp.idCategoria = " +  SubItems3.getInt(0) + " AND p.Cantidad != 0", null);

                                        String IDS3 = SubItems3.getString(0);
                                        String IDSP3 = SubItems3.getString(1);
                                        String NombreS3 = "> > >  " + SubItems3.getString(2) + "   ("+PC3.getCount()+")";
                                        ItemSubMenu itemsChilds3 = new ItemSubMenu(NombreS3, IDS3, IDSP3);
                                        ListaItemsChilds.add(itemsChilds3);
                                    }while(SubItems3.moveToNext());
                                }
                                SubItems3.close();
                                //---------------------------------------------------------------------------------------------------------------------------------
                            } while (SubItems2.moveToNext());
                        }
                        SubItems2.close();
                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    } while (SubItems.moveToNext());
                }
                SubItems.close();
                ItemMenu items = new ItemMenu(Nombre, ID, ListaItemsChilds);
                ListaItems.add(items);

            } while (ListaItemsMenu.moveToNext());

        }
        ListaItemsMenu.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopManagingCursor(CursorBuscarProducto);
      //  mDrawerLayout.closeDrawer(Gravity.RIGHT);   // Revisar Utilidad de cerrar el Panel Aqui.

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

    }

}




