package com.odril.Socima_Gestion;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.odril.socimagestionv02.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import Adaptadores.Httppostaux;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class Sistema extends ActionBarActivity {

    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    ImageView BtnSalir, BtnVendedor, BtnCliente, BtnActualizar, BtnCerrar;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    SQLiteDatabase DB;
    BaseDatos BDSocima;
    Handler handler = new Handler();
    private ProgressDialog pDialog;
    SharedPreferences ConfiguracionGeneral;
    public static final int progress_bar_type = 0;
    SharedPreferences.Editor EditarConfiguracionGeneral;
    Metodos metodos;
    ImageView Logo;
    String NuevoEquipo;
    globals g = globals.getInstance();
    DecimalFormat formateador = new DecimalFormat("###,###");

    Httppostaux Post;
    String URL_connect = "http://socimagestion.com/Movil/Datos/ActualizarOrden2.php";
    private static final int NOTIF_ALERTA_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sistema);



        metodos = new Metodos();
        ConfiguracionGeneral = getSharedPreferences("General", MODE_PRIVATE);
        EditarConfiguracionGeneral = ConfiguracionGeneral.edit();
        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();
        BDSocima = new BaseDatos(this, "SocimaGestion", null, 1);
        DB = BDSocima.getWritableDatabase();
        Logo = (ImageView) findViewById(R.id.Logo);
        final Boolean Conexion = metodos.EstadoConexion(this);
        NuevoEquipo = ConfiguracionGeneral.getString("NuevoEquipo", "SI");


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Cliente = getSharedPreferences("Cliente", MODE_PRIVATE);
        EditarCliente = Cliente.edit();
        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();

        BtnSalir = (ImageView) findViewById(R.id.BtnSalir);
        BtnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditarUsuario.remove("Login");
                EditarUsuario.remove("CodigoVendedor");
                EditarUsuario.apply();
                EditarCliente.remove("IDCliente");
                EditarCliente.remove("OrdenCompra");
                EditarCliente.apply();

                Intent i = new Intent(Sistema.this, Login.class);
                startActivity(i);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });


        BtnActualizar = (ImageView) findViewById(R.id.BtnAct);
        BtnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                BDSocima = new BaseDatos(Sistema.this,"SocimaGestion",null,1);
                DB = BDSocima.getWritableDatabase();
                if(g.getRunning() == true){
                    new SweetAlertDialog(Sistema.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("!Aviso¡")
                            .setContentText("No se puede ejecutar la actualización, se están descargando imagenes")
                            .show();
                }else{
                    new Actualizacion().execute();
                }
            }
        });


        BtnCerrar = (ImageView) findViewById(R.id.BtnExit);
        BtnCerrar.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view){
                     g.setRunning(false);
                     finish();
                 }
            }
        );

        BtnCliente = (ImageView) findViewById(R.id.BtnCliente);
        BtnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Sistema.this, Cliente.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

            }
        });

        BtnVendedor = (ImageView) findViewById(R.id.BtnVendedor);
        BtnVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Sistema.this, SeguridadVendedor.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


            }
        });

    }

    public void Ms() {
        showDialog(progress_bar_type);
    }

    @Override
    public void onBackPressed() {
    }

    public Cursor getOrdenesConfirmadas() {
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
            fecha5DiasAntes = año+"-"+mes+"-"+(dia-5);
        }

        //System.out.println("Fecha actual " + fechaActual);
        //System.out.println("Fecha 5 días antes " + fecha5DiasAntes);

        //return DB.rawQuery("SELECT * FROM Mv_Orden WHERE Estado = 1", null);
        return DB.rawQuery("SELECT * FROM Mv_Orden WHERE Estado = 1 AND FFI BETWEEN '" + fecha5DiasAntes +  "' AND '" + fechaActual +"'", null);

    }
    public Cursor getDetalleOrden(int idOrden) {
        return DB.rawQuery("SELECT do.idProducto, do.Cantidad, do.Precio, p.Descuento, p.Modelo FROM Mv_detalleOrden do JOIN Mv_Producto p ON (do.idProducto = p.idProducto) WHERE do.idOrden = " + idOrden + "", null);
    }


    class Actualizacion extends AsyncTask<Void, String, Boolean> {
        Handler handler = new Handler();
        String Vendedores = null, Menu = null, Productos = null, Ordenes = null, Clientes = null, Noticias = null, Estados = null, Marcas = null;
        String AVendedores = null, AMenu = null, AProductos = null, AOrdenes = null, AClientes = null, ANoticias = null, AEstados = null, AMarcas = null;

        Httppostaux post;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Sistema.this);
            pDialog.setMessage("Subiendo Ordenes....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost Post = new HttpPost("http://socimagestion.com/Movil/Datos/Actualizacion.php");
                //HttpPost //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Actualizacion.php");
                int ExisteDato = 1;
                JSONObject JObj = null;
                JSONArray Actualizaciones = null;
                JSONArray Vendedor = null;
                JSONArray MenusPadres = null;
                JSONArray MenusHijos = null;
                JSONArray CategoriasProductos = null;
                JSONArray Productox = null;
                JSONArray ProductoDetalle = null;
                JSONArray Ordenex = null;
                JSONArray DetallesOrdenes = null;
                JSONArray Cliente = null;
                JSONArray Notas = null;
                JSONArray Estado = null;
                JSONArray Marca = null;
                JSONArray Banners = null;


                HttpResponse Response = httpClient.execute(Post);
                int Status = Response.getStatusLine().getStatusCode();
                if (Status == 200) {
                    HttpEntity entity = Response.getEntity();
                    String data = EntityUtils.toString(entity);

                    try {
                        JObj = new JSONObject(data);
                    } catch (JSONException e) {
                        ExisteDato = 0;
                        Log.d("ERRORDATOS", "Descarga Actualizacion");
                    }

                    Cursor Cs, Cs2 = null;
                    String CodigoOrden, IdCliente, DireccionF, TipoPago, Total, EstadoO, FFI, FFM, Comentario, VendedorO, DireccionE, ProductId, nombreP, cantidad, precio;
                    Cs = getOrdenesConfirmadas();
                    //System.out.println("total ordenes confirmadas " + Cs.getCount());
                    if (Cs.moveToFirst()) {
                        do {
                            CodigoOrden = Cs.getString(0);
                            IdCliente = Cs.getString(2);
                            DireccionF = Cs.getString(3);
                            TipoPago = Cs.getString(6);
                            Total = Cs.getString(7);
                            EstadoO = Cs.getString(8);
                            FFI = Cs.getString(9);
                            FFM = Cs.getString(10);
                            Comentario = Cs.getString(11);
                            VendedorO = Cs.getString(12);
                            DireccionE = Cs.getString(13);

                            /*System.out.println("dato codigo orden " + CodigoOrden);
                            System.out.println("dato id cliente " + IdCliente);
                            System.out.println("dato direccionF " + DireccionF);
                            System.out.println("dato tipo pago " + TipoPago);
                            System.out.println("dato estado " + EstadoO);
                            System.out.println("dato FFI " + FFI);
                            System.out.println("dato FFM " + FFM);
                            System.out.println("dato comentario " + Comentario);
                            System.out.println("dato Vendedor " + VendedorO);
                            System.out.println("dato DireccionE " + DireccionE);*/

                            httpClient = new DefaultHttpClient();
                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/ActualizarOrden.php");
                            //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/ActualizarOrden.php");
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("idOrden", CodigoOrden));
                            params.add(new BasicNameValuePair("idCliente", IdCliente));
                            params.add(new BasicNameValuePair("direccionF", DireccionF));
                            params.add(new BasicNameValuePair("tipoP", TipoPago));
                            params.add(new BasicNameValuePair("total", Total));
                            params.add(new BasicNameValuePair("estado", EstadoO));
                            params.add(new BasicNameValuePair("FFI", FFI));
                            params.add(new BasicNameValuePair("FFM", FFM));
                            params.add(new BasicNameValuePair("comentario", Comentario));
                            params.add(new BasicNameValuePair("idVendedor", VendedorO));
                            params.add(new BasicNameValuePair("direccionE", DireccionE));

                            Post.setEntity(new UrlEncodedFormEntity(params));
                            HttpResponse resp = httpClient.execute(Post);
                            HttpEntity ent = resp.getEntity();
                            String text = EntityUtils.toString(ent);
                            //System.out.println("guardo : " + text);

                            int idOrden = Cs.getInt(0);
                            Cs2 = getDetalleOrden(idOrden);
                            //System.out.println("total detalle orden " + Cs2.getCount());
                            if(Cs2.moveToFirst()){
                                do {
                                    ProductId = Cs2.getString(0);
                                    nombreP = Cs2.getString(4);
                                    cantidad = Cs2.getString(1);
                                    precio = Cs2.getString(2);
                                    int total = Integer.parseInt(cantidad) * Integer.parseInt(precio);
                                    httpClient = new DefaultHttpClient();
                                    Post = new HttpPost("http://socimagestion.com/Movil/Datos/ActualizarDetalleOrden.php");
                                    //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/ActualizarDetalleOrden.php");

                                    params.add(new BasicNameValuePair("idOrden", CodigoOrden));
                                    params.add(new BasicNameValuePair("productId", ProductId));
                                    params.add(new BasicNameValuePair("nombreProducto", nombreP));
                                    params.add(new BasicNameValuePair("cantidad", cantidad));
                                    params.add(new BasicNameValuePair("precio", precio));
                                    params.add(new BasicNameValuePair("total", String.valueOf(total)));

                                    Post.setEntity(new UrlEncodedFormEntity(params));
                                    HttpResponse resp2 = httpClient.execute(Post);
                                    HttpEntity ent2 = resp2.getEntity();
                                    String text2 = EntityUtils.toString(ent2);
                                    //System.out.println("guardo detalle orden : " + text2);

                                }while(Cs2.moveToNext());

                            }
                        } while (Cs.moveToNext());
                    }
                    //System.out.println("Total Ordenes Confirmadas " + Cs.getCount());

                    if (ExisteDato > 0) {

                        try {
                            Actualizaciones = JObj.getJSONArray("Actualizaciones");
                            ExisteDato = 1;
                        } catch (JSONException e) {
                            ExisteDato = 0;
                            Log.d("ERRORDATOS", "Actualizacion");
                        }

                        if (ExisteDato > 0) {
                            for (int i = 0; i < Actualizaciones.length(); i++)

                            {
                                publishProgress("" + i);

                                JSONObject Actualizacion = Actualizaciones.getJSONObject(i);

                                Vendedores = Actualizacion.getString("Vendedores");
                                Menu = Actualizacion.getString("Menu");
                                Productos = Actualizacion.getString("Productos");
                                Ordenes = Actualizacion.getString("Ordenes");
                                Clientes = Actualizacion.getString("Vendedores");
                                Noticias = Actualizacion.getString("Noticias");
                                Estados = Actualizacion.getString("Vendedores");
                            }


                            AVendedores = ConfiguracionGeneral.getString("AVendedores", "x");
                            AMenu = ConfiguracionGeneral.getString("AMenu", "x");
                            AProductos = ConfiguracionGeneral.getString("AProductos", "x");
                            AOrdenes = ConfiguracionGeneral.getString("AOrdenes", "x");
                            AClientes = ConfiguracionGeneral.getString("AClientes", "x");
                            ANoticias = ConfiguracionGeneral.getString("ANoticias", "x");
                            AEstados = ConfiguracionGeneral.getString("AEstados", "x");
                        }

                        if (!AVendedores.equals(Vendedores) | !AMenu.equals(Menu) | !AProductos.equals(Productos) | !AOrdenes.equals(Ordenes) | !AClientes.equals(Clientes) | !ANoticias.equals(Noticias) | !AEstados.equals(Estados)) {
                            handler.post(new Runnable() {
                                public void run() {
                                    Ms();
                                }
                            });


                        }

                        if (!AVendedores.equals(Vendedores)) {

                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Vendedor.php");
                            //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Vendedor.php");
                            Response = httpClient.execute(Post);
                            Status = Response.getStatusLine().getStatusCode();
                            if (Status == 200) {

                                entity = Response.getEntity();
                                data = EntityUtils.toString(entity);
                                try {
                                    JObj = new JSONObject(data);
                                    ExisteDato = 1;
                                } catch (JSONException e) {
                                    ExisteDato = 0;
                                    Log.d("ERRORDATOS", "Descarga Vendedor");
                                }
                                if (ExisteDato > 0) {
                                    try {
                                        Vendedor = JObj.getJSONArray("Vendedor");
                                        ExisteDato = 1;
                                        BDSocima.vaciarVendedor(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Vendedor");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < Vendedor.length(); i++) {
                                            JSONObject DetalleVendedor = Vendedor.getJSONObject(i);
                                            publishProgress("" + i);


                                            try {
                                                String SqlInsertUsuario = " Insert into Mv_Vendedor(CodigoVendedor,Nombre,Email,Usuario,Clave,Meta,Cargo, Actual, Localidad, Estado) values(" +
                                                        Integer.parseInt(DetalleVendedor.getString("CodigoVendedor")) + ",'" +
                                                        DetalleVendedor.getString("Nombre") + "','" + DetalleVendedor.getString("Email") + "','" +
                                                        DetalleVendedor.getString("Usuario") + "','" +
                                                        DetalleVendedor.getString("Clave") + "'," + DetalleVendedor.getString("Meta") + "," +
                                                        Integer.parseInt(DetalleVendedor.getString("Cargo")) + "," +
                                                        Integer.parseInt(DetalleVendedor.getString("Actual")) + "," +
                                                        DetalleVendedor.getString("Localidad") + "', " +
                                                        Integer.parseInt(DetalleVendedor.getString("Estado")) + ");";


                                                DB.execSQL(SqlInsertUsuario);
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }


                                            EditarConfiguracionGeneral.putString("AVendedores", "" + Vendedores);
                                            EditarConfiguracionGeneral.apply();

                                        }
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Actualizando Vendedores.", Toast.LENGTH_SHORT).show();
                                                EditarConfiguracionGeneral.putString("AVendedores", "" + Vendedores);
                                                EditarConfiguracionGeneral.apply();

                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR VENDEDORES.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        }


                        if (!AMenu.equals(Menu)) {


                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Menu.php");
                            //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Menu.php");
                            Response = httpClient.execute(Post);
                            Status = Response.getStatusLine().getStatusCode();
                            if (Status == 200) {

                                entity = Response.getEntity();
                                data = EntityUtils.toString(entity);

                                try {
                                    JObj = new JSONObject(data);
                                    ExisteDato = 1;
                                } catch (JSONException e) {
                                    ExisteDato = 0;
                                    Log.d("ERRORDATOS", "Descarga Menu");
                                }

                                if (ExisteDato > 0) {
                                    try {
                                        MenusPadres = JObj.getJSONArray("MenuPadre");
                                        ExisteDato = 1;
                                        BDSocima.vaciarMenu(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Menu Padre");
                                    }

                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < MenusPadres.length(); i++) {
                                            JSONObject MenuPadre = MenusPadres.getJSONObject(i);


                                            try {
                                                String SqlInsertMenu = " Insert into Mv_Menu(idMenuPrincipal,Nombre) values(" +
                                                        Integer.parseInt(MenuPadre.getString("Codigo")) + ",'" +
                                                        MenuPadre.getString("NombreCategoria") + "');";

                                                DB.execSQL(SqlInsertMenu);
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                        }
                                    }

                                    try {
                                        MenusHijos = JObj.getJSONArray("MenuHijo");
                                        ExisteDato = 1;
                                        BDSocima.vaciarMenuHijo(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Menu Hijo");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < MenusHijos.length(); i++) {
                                            JSONObject MenuHijo = MenusHijos.getJSONObject(i);

                                            try {

                                                String SqlInsertMenuHijo = "Insert into Mv_MenuHijo(idMenuHijo,idMenuPrincipal,Nombre) values(" +
                                                        Integer.parseInt(MenuHijo.getString("Codigo")) + "," +
                                                        Integer.parseInt(MenuHijo.getString("CodigoPadre")) + ",'" +
                                                        MenuHijo.getString("NombreCategoria") + "');";

                                                DB.execSQL(SqlInsertMenuHijo);
                                                publishProgress("" + i);

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }


                                        }
                                    }

                                    try {
                                        CategoriasProductos = JObj.getJSONArray("CategoriaProducto");
                                        ExisteDato = 1;
                                        BDSocima.vaciarCategoriaProducto(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Categoria Producto");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < CategoriasProductos.length(); i++) {
                                            JSONObject CategoriaProductos = CategoriasProductos.getJSONObject(i);
                                            try {

                                                String SqlInsertCategoriaProducto = " Insert into Mv_categoriaProducto(idProducto,idCategoria) values(" +
                                                        Integer.parseInt(CategoriaProductos.getString("CodigoProducto")) + "," +
                                                        Integer.parseInt(CategoriaProductos.getString("Codigo")) + ");";

                                                DB.execSQL(SqlInsertCategoriaProducto);
                                                publishProgress("" + i);

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                        }
                                    }


                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Actualizando Menu.", Toast.LENGTH_SHORT).show();
                                            EditarConfiguracionGeneral.putString("AMenu", "" + Menu);
                                            EditarConfiguracionGeneral.apply();

                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR MENUS.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }


                        if (!AProductos.equals(Productos)) {

                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Productos.php");
                            //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Productos.php");
                            Response = httpClient.execute(Post);
                            Status = Response.getStatusLine().getStatusCode();
                            if (Status == 200) {

                                entity = Response.getEntity();
                                data = EntityUtils.toString(entity);
                                JSONArray ProductoRelacion = null;

                                try {
                                    JObj = new JSONObject(data);
                                    ExisteDato = 1;
                                } catch (JSONException e) {
                                    ExisteDato = 0;
                                    Log.d("ERRORDATOS", "Descarga Productos");
                                }
                                if (ExisteDato > 0) {

                                    try {
                                        ProductoRelacion = JObj.getJSONArray("ProductoRelacion");
                                        ExisteDato = 1;
                                        BDSocima.vaciarProductoRelacion(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Relacion Producto");
                                    }

                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < ProductoRelacion.length(); i++) {

                                            JSONObject ProductosRelacion = ProductoRelacion.getJSONObject(i);
                                            try {
                                                String SqlProducoRelacion = " Insert into Mv_ProductoRelacion(idProducto,idRelacion) " +
                                                        "values("
                                                        + Integer.parseInt(ProductosRelacion.getString("idProducto")) + ","
                                                        + Integer.parseInt(ProductosRelacion.getString("idRelacion")) + ");";
                                                DB.execSQL(SqlProducoRelacion);
                                                publishProgress("" + i);


                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                        }
                                    }

                                    try {
                                        ProductoDetalle = JObj.getJSONArray("DetalleProducto");
                                        ExisteDato = 1;
                                        BDSocima.vaciarDetalleProducto(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "DetalleProducto");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < ProductoDetalle.length(); i++) {

                                            JSONObject DetalleProducto = ProductoDetalle.getJSONObject(i);
                                            try {
                                                String va = DetalleProducto.getString("Descripcion");

                                                if (DetalleProducto.getString("Descripcion").equals("")) {

                                                    va = "" + 0;
                                                }
                                                String SqlProductoDetalle = " Insert into Mv_DetalleProducto(idProducto,idAtributo,Atributo,Descripcion) " +
                                                        "values("
                                                        + Integer.parseInt(DetalleProducto.getString("idProducto")) + ","
                                                        + Integer.parseInt(DetalleProducto.getString("idAtributo")) + ",'"
                                                        + DetalleProducto.getString("Atributo") + "','"
                                                        + va + "');";


                                                DB.execSQL(SqlProductoDetalle);

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                            publishProgress("" + i);
                                        }
                                    }

                                    try {
                                        Productox = JObj.getJSONArray("Productos");
                                        ExisteDato = 1;
                                        BDSocima.vaciarProducto(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Productos");
                                    }
                                    if (ExisteDato > 0) {

                                        for (int i = 0; i < Productox.length(); i++) {

                                            JSONObject Producto = Productox.getJSONObject(i);
                                            try {
                                                if (Integer.parseInt(Producto.getString("Cantidad")) == 0) {
                                                    DB.execSQL("Delete from Mv_ProductoRelacion where idRelacion =" + Integer.parseInt(Producto.getString("idProducto")) + "");
                                                    DB.execSQL("Delete from Mv_DetalleProducto where idProducto =" + Integer.parseInt(Producto.getString("idProducto")) + "");
                                                } else {
                                                    String SqlProduco = " Insert into Mv_Producto(idProducto,Modelo,Descripcion,CodigoProducto,CodigoBodega,StockInicial,Cantidad,Precio,Tam,FFA,Descuento,FFDI,FFDF,Tag,Imagen,MarcaId,SortOrder,Modificado) " +
                                                            "values("
                                                            + Integer.parseInt(Producto.getString("idProducto")) + ",'"
                                                            + Producto.getString("Modelo").trim() + "','"
                                                            + Producto.getString("Descripcion").trim() + "',"
                                                            + Integer.parseInt(Producto.getString("CodigoProducto")) + ",'"
                                                            + Producto.getString("CodigoBodega").trim() + "',"
                                                            + Integer.parseInt(Producto.getString("StockInicial")) + ","
                                                            + Integer.parseInt(Producto.getString("Cantidad")) + ","
                                                            + Integer.parseInt(Producto.getString("Precio")) + ","
                                                            + Integer.parseInt(Producto.getString("Tam")) + ",'"
                                                            + Producto.getString("FFA").trim() + "',"
                                                            + Integer.parseInt(Producto.getString("Descuento")) + ",'"
                                                            + Producto.getString("FFDI").trim() + "','"
                                                            + Producto.getString("FFDF").trim() + "','"
                                                            + Producto.getString("Tag").trim() + "','"
                                                            + "NO', '" + Producto.getString("Marca").trim() + "', '"
                                                            + Producto.getString("Sort_order").trim() + "', '"
                                                            + Producto.getString("Modificado").trim() + "');";
                                                    DB.execSQL(SqlProduco);
                                                    //System.out.println("guardo producto");
                                                    publishProgress("" + i);
                                                }

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                            /*if(Producto.getString("Cantidad").equals("0")){
                                                NotificationCompat.Builder mBuilder =
                                                        new NotificationCompat.Builder(Sistema.this)
                                                                .setSmallIcon(R.drawable.icono_socima48)
                                                                .setLargeIcon((((BitmapDrawable) getResources()
                                                                        .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                                .setContentTitle("Productos Sin Stock")
                                                                .setContentText("Hay productos sin Stock en el Sistema")
                                                                .setContentInfo("1")
                                                                .setTicker("Alerta!")
                                                                .setAutoCancel(true);

                                                Intent notIntent =
                                                        new Intent(Sistema.this, FragmentoInformacionProductos.class);

                                                PendingIntent contIntent = PendingIntent.getActivity(
                                                        Sistema.this, 0, notIntent, 0);

                                                mBuilder.setContentIntent(contIntent);

                                                NotificationManager mNotificationManager =
                                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                                mNotificationManager.notify(4, mBuilder.build());
                                            }*/

                                        }
                                    }

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Actualizando Productos.", Toast.LENGTH_SHORT).show();
                                            EditarConfiguracionGeneral.putString("AProductos", "" + Productos);
                                            EditarConfiguracionGeneral.apply();

                                        }
                                    });

                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(Sistema.this)
                                                    .setSmallIcon(R.drawable.icono_socima48)
                                                    .setLargeIcon((((BitmapDrawable) getResources()
                                                    .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                    .setContentTitle("Nuevos Productos")
                                                    .setContentText("Se han agregado nuevos productos")
                                                    .setContentInfo("1")
                                                    .setTicker("Alerta!")
                                                    .setAutoCancel(true);

                                    Intent notIntent =
                                            new Intent(Sistema.this, SistemaVendedor.class);

                                    PendingIntent contIntent = PendingIntent.getActivity(
                                            Sistema.this, 0, notIntent, 0);

                                    mBuilder.setContentIntent(contIntent);

                                    NotificationManager mNotificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    mNotificationManager.notify(3, mBuilder.build());
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR PRODUCTOS.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        if (!AOrdenes.equals(Ordenes)) {
                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Ordenes.php");
                            //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Ordenes.php");
                            Response = httpClient.execute(Post);
                            Status = Response.getStatusLine().getStatusCode();
                            if (Status == 200) {

                                entity = Response.getEntity();
                                data = EntityUtils.toString(entity);

                                try {
                                    JObj = new JSONObject(data);
                                    ExisteDato = 1;
                                } catch (JSONException e) {
                                    ExisteDato = 0;
                                    Log.d("ERRORDATOS", "Descarga Orden");
                                }
                                if (ExisteDato > 0) {

                                    try {
                                        Ordenex = JObj.getJSONArray("Orden");
                                        ExisteDato = 1;
                                        BDSocima.vaciarOrden(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Orden");
                                    }

                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < Ordenex.length(); i++) {
                                            JSONObject TOrden = Ordenex.getJSONObject(i);
                                            publishProgress("" + i);

                                            try {

                                                int estado = 0;
                                                if(TOrden.getInt("Estado") == 1){
                                                    estado = 0;
                                                }else if(TOrden.getInt("Estado") == 2){
                                                    estado = 1;
                                                }else if(TOrden.getInt("Estado") == 5){
                                                    estado = 1;
                                                }else if(TOrden.getInt("Estado") == 15){
                                                    estado = 2;
                                                }else if(TOrden.getInt("Estado") == 16){
                                                    estado = 4;
                                                }

                                                String SqlProduco = "Insert into Mv_Orden(idOrden,CodigoOrden,idCliente,DireccionFacturacion,CiudadPago,Region,TipoPago,Total,Estado,FFI,FFM,Vendedor) " +
                                                        "values("
                                                        + Integer.parseInt(TOrden.getString("idOrden")) + ",'"
                                                        + TOrden.getString("Codigo") + "',"
                                                        + Integer.parseInt(TOrden.getString("idCliente")) + ",'"
                                                        + TOrden.getString("DireccionP") + "','"
                                                        + TOrden.getString("CiudadPago") + "','"
                                                        + TOrden.getString("Region") + "','"
                                                        + TOrden.getString("TipoPago") + "',"
                                                        + Integer.parseInt(TOrden.getString("Total")) + ","
                                                        + estado + ",'"
                                                        + TOrden.getString("FFI") + "','"
                                                        + TOrden.getString("FFM") + "',"
                                                        + Integer.parseInt(TOrden.getString("Vendedor")) + ");";

                                                DB.execSQL(SqlProduco);

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                        }
                                    }

                                    try {
                                        DetallesOrdenes = JObj.getJSONArray("DetalleOrden");
                                        ExisteDato = 1;
                                        BDSocima.vaciarDetalleOrden(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "DetalleOrden");
                                    }

                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < DetallesOrdenes.length(); i++) {

                                            JSONObject TDetalleOrden = DetallesOrdenes.getJSONObject(i);

                                            publishProgress("" + i);

                                            try {
                                                String SqlDetalleOrden = " Insert into Mv_DetalleOrden(idOrden,idProducto,Cantidad,Precio,Total) " +
                                                        "values("
                                                        + Integer.parseInt(TDetalleOrden.getString("idOrden")) + ","
                                                        + Integer.parseInt(TDetalleOrden.getString("idProducto")) + ","
                                                        + Integer.parseInt(TDetalleOrden.getString("Cantidad")) + ","
                                                        + Integer.parseInt(TDetalleOrden.getString("Precio")) + ","
                                                        + Integer.parseInt(TDetalleOrden.getString("Total")) + ");";
                                                DB.execSQL(SqlDetalleOrden);

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                        }
                                    }


                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Actualizando Ordenes.", Toast.LENGTH_SHORT).show();
                                            EditarConfiguracionGeneral.putString("AOrdenes", "" + Ordenes);
                                            EditarConfiguracionGeneral.apply();

                                            NotificationCompat.Builder mBuilder =
                                                    new NotificationCompat.Builder(Sistema.this)
                                                            .setSmallIcon(R.drawable.icono_socima48)
                                                            .setLargeIcon((((BitmapDrawable)getResources()
                                                            .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                            .setContentTitle("Nuevos Datos en la aplicación")
                                                            .setContentText("Se han agregado nuevas ordenes")
                                                            .setContentInfo("1")
                                                            .setTicker("Alerta!")
                                                            .setAutoCancel(true);

                                            Intent notIntent =
                                                    new Intent(Sistema.this, SistemaVendedor.class);

                                            PendingIntent contIntent = PendingIntent.getActivity(
                                                    Sistema.this, 0, notIntent, 0);

                                            mBuilder.setContentIntent(contIntent);

                                            NotificationManager mNotificationManager =
                                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                            mNotificationManager.notify(1, mBuilder.build());
                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR ORDENES.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }


                        if (!AClientes.equals(Clientes)) {

                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Cliente.php");
                            //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Cliente.php");
                            Response = httpClient.execute(Post);
                            Status = Response.getStatusLine().getStatusCode();
                            if (Status == 200) {

                                entity = Response.getEntity();
                                data = EntityUtils.toString(entity);
                                try {
                                    JObj = new JSONObject(data);
                                    ExisteDato = 1;
                                } catch (JSONException e) {
                                    ExisteDato = 0;
                                    Log.d("ERRORDATOS", "Descarga Clientes");
                                }
                                if (ExisteDato > 0) {
                                    try {
                                        Cliente = JObj.getJSONArray("Cliente");
                                        ExisteDato = 1;
                                        BDSocima.vaciarCliente(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Clientes");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < Cliente.length(); i++) {

                                            JSONObject TCliente = Cliente.getJSONObject(i);

                                            try {
                                                String SqlInsertCliente = " Insert into Mv_Cliente(CodigoCliente,Nombre,Email,Telefono,Vendedor,Credito,Direccion,Ciudad,Region,Codigo, Rut) " +
                                                        "values(" + Integer.parseInt(TCliente.getString("CodigoCliente"))
                                                        + ",'" + TCliente.getString("Nombre") + "'," +
                                                        "'" + TCliente.getString("Email") + "',"
                                                        + Integer.parseInt(TCliente.getString("Telefono")) + ","
                                                        + Integer.parseInt(TCliente.getString("Vendedor")) + "," +
                                                        "" + Integer.parseInt(TCliente.getString("Credito"))
                                                        + ",'" + TCliente.getString("Direccion") + "','" +
                                                        TCliente.getString("Ciudad") + "'," +
                                                        "'" + TCliente.getString("Region") + "','" +
                                                        TCliente.getString("Codigo") + "','" +
                                                        TCliente.getString("Rut") + "');";

                                                DB.execSQL(SqlInsertCliente);

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                        }
                                    }
                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Actualizando Clientes.", Toast.LENGTH_SHORT).show();
                                            EditarConfiguracionGeneral.putString("AClientes", "" + Clientes);
                                            EditarConfiguracionGeneral.apply();

                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR CLIENTES.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }


                        if (!ANoticias.equals(Noticias)) {


                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Noticias.php");
                            //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Noticias.php");
                            Response = httpClient.execute(Post);
                            Status = Response.getStatusLine().getStatusCode();
                            if (Status == 200) {

                                entity = Response.getEntity();
                                data = EntityUtils.toString(entity);
                                try {
                                    JObj = new JSONObject(data);
                                    ExisteDato = 1;
                                } catch (JSONException e) {
                                    ExisteDato = 0;
                                    Log.d("ERRORDATOS", "Descarga Noticias");
                                }
                                if (ExisteDato > 0) {
                                    try {
                                        Notas = JObj.getJSONArray("Noticia");
                                        ExisteDato = 1;
                                        BDSocima.vaciarNoticias(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Noticias");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < Notas.length(); i++) {
                                            File Archivo = null;
                                            JSONObject TNotas = Notas.getJSONObject(i);
                                            String Images = TNotas.getString("Imagen").trim();
                                            if (Images.isEmpty()) {
                                                Images = "Null";
                                            } else {
                                                Images = TNotas.getString("Imagen").trim();
                                                Log.d("IMAGENNOTICIA", "Noticia" + (i + 1));
//
                                                String Nombre = "Noticia" + (i + 1) + ".jpg";
                                                try {
                                                    File root = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion");
                                                    if (root.exists() && root.isDirectory()) {

                                                    } else {
                                                        root.mkdir();
                                                    }

                                                    Archivo = new File(root + "/" + Nombre);
                                                    String url = Images;

                                                    Archivo.delete();

                                                    HttpClient http = new DefaultHttpClient();
                                                    HttpGet httpGet = new HttpGet(url);
                                                    HttpResponse response = http.execute(httpGet);

                                                    if (response.getStatusLine().getStatusCode() == 200) {
                                                        Log.d("IMAGEN", "Descargando " + Nombre);
                                                        try {
                                                            HttpEntity ent = response.getEntity();
                                                            InputStream inputStream = ent.getContent();
                                                            Boolean status = Archivo.createNewFile();
                                                            FileOutputStream fileOutputStream = new FileOutputStream(Archivo);
                                                            byte[] buffer = new byte[1024];
                                                            long total = 0;
                                                            int count;
                                                            while ((count = inputStream.read(buffer)) != -1) {
                                                                total += count;
                                                                fileOutputStream.write(buffer, 0, count);
                                                            }
                                                            fileOutputStream.close();
                                                            inputStream.close();
                                                            Log.d("Imagen", "Descargada: " + Archivo);

                                                        } catch (Exception e) {
                                                            Log.d("ERROR DESCARGA IMAGEN", e.getCause().toString());

                                                        }
                                                    } else if (response.getStatusLine().getStatusCode() == 404) {
                                                        Log.d("IMAGEN", "No encontrada :" + Nombre);

                                                    }

                                                } catch (Exception e) {
                                                    Log.d("ERROR DESCARGA IMAGEN", e.getCause().toString());
                                                }

                                                //
                                            }
                                            try {
                                                String SqlNoticia = " Insert into Mv_Noticia(Titulo,Noticia,Estado,Imagen) " +
                                                        "values('"
                                                        + TNotas.getString("Titulo") + "','"
                                                        + TNotas.getString("Noticia") + "',0,'" + Archivo + "');";

                                                DB.execSQL(SqlNoticia);
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                        }
                                    }

                                    try {
                                        Banners = JObj.getJSONArray("Banner");
                                        ExisteDato = 1;
                                        BDSocima.vaciarBanner(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "Banner");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < Banners.length(); i++) {

                                            JSONObject Banner = Banners.getJSONObject(i);
                                            try {

                                                String Url = "";
                                                String Informacion = "";
                                                File Archivo = null;

                                                if (Banner.getString("Url").isEmpty()) {
                                                    Url = "Null";
                                                    Informacion = "Null";

                                                } else {
                                                    Url = Banner.getString("Url").trim();
                                                    Informacion = Banner.getString("Informacion").trim();
                                                    //
                                                    String Nombre = "Banner" + (i + 1) + ".jpg";
                                                    try {
                                                        File root = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion");
                                                        if (root.exists() && root.isDirectory()) {

                                                        } else {
                                                            root.mkdir();
                                                        }

                                                        Archivo = new File(root + "/" + Nombre);
                                                        Log.d("Archivo", "" + Archivo);
                                                        String url = Url;

                                                        Archivo.delete();

                                                        HttpClient http = new DefaultHttpClient();
                                                        HttpGet httpGet = new HttpGet(url);
                                                        HttpResponse response = http.execute(httpGet);

                                                        if (response.getStatusLine().getStatusCode() == 200) {
                                                            Log.d("IMAGENBanner", "Descargando " + Nombre);
                                                            try {
                                                                HttpEntity ent = response.getEntity();
                                                                InputStream inputStream = ent.getContent();
                                                                Boolean status = Archivo.createNewFile();
                                                                FileOutputStream fileOutputStream = new FileOutputStream(Archivo);
                                                                byte[] buffer = new byte[1024];
                                                                long total = 0;
                                                                int count;
                                                                while ((count = inputStream.read(buffer)) != -1) {
                                                                    total += count;
                                                                    fileOutputStream.write(buffer, 0, count);
                                                                }
                                                                fileOutputStream.close();
                                                                inputStream.close();
                                                                Log.d("Imagen", "Descargada: " + Archivo);

                                                            } catch (Exception e) {
                                                                Log.d("ERROR DESCARGA IMAGEN", e.getCause().toString());

                                                            }
                                                        } else if (response.getStatusLine().getStatusCode() == 404) {
                                                            Log.d("IMAGEN", "No encontrada :" + Nombre);

                                                        }

                                                    } catch (Exception e) {
                                                        Log.d("ERROR DESCARGA IMAGEN", e.getCause().toString());
                                                    }

                                                    //
                                                }


                                                String SqlNoticia = " Insert into Mv_Banner(BannerId,Url,Informacion) " +
                                                        "values(" + (i + 1) + ",'" + Archivo + "','" + Informacion + "');";


                                                DB.execSQL(SqlNoticia);
                                                Log.d("Banner", "" + SqlNoticia);
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                        }
                                    }


                                    handler.post(new Runnable() {
                                                     public void run() {
                                                         Toast.makeText(getApplicationContext(), "Actualizando Noticias", Toast.LENGTH_SHORT).show();
                                                         EditarConfiguracionGeneral.putString("ANoticias", "" + Noticias);
                                                         EditarConfiguracionGeneral.apply();
                                                     }
                                                 }
                                    );

                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(Sistema.this)
                                                    .setSmallIcon(R.drawable.icono_socima48)
                                                    .setLargeIcon((((BitmapDrawable)getResources()
                                                    .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                    .setContentTitle("Nuevas Noticias")
                                                    .setContentText("Nuevas Noticias en el Sistema")
                                                    .setContentInfo("1")
                                                    .setTicker("Alerta!")
                                                    .setAutoCancel(true);

                                    Intent notIntent =
                                            new Intent(Sistema.this, Noticias.class);

                                    PendingIntent contIntent = PendingIntent.getActivity(
                                            Sistema.this, 0, notIntent, 0);

                                    mBuilder.setContentIntent(contIntent);

                                    NotificationManager mNotificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    mNotificationManager.notify(2, mBuilder.build());

                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR NOTICIAS.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        if (!AEstados.equals(Estados)) {

                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Estados.php");
                            //Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Estados.php");
                            Response = httpClient.execute(Post);
                            Status = Response.getStatusLine().getStatusCode();
                            if (Status == 200) {
                                entity = Response.getEntity();
                                data = EntityUtils.toString(entity);
                                try {
                                    JObj = new JSONObject(data);
                                    ExisteDato = 1;
                                } catch (JSONException e) {
                                    Log.d("ERRORDATOS", "Descarga de Estados");
                                    ExisteDato = 0;
                                }
                                if (ExisteDato > 0) {
                                    try {
                                        Estado = JObj.getJSONArray("Estados");
                                        ExisteDato = 1;
                                        //BDSocima.vaciarEstado(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "ESTADOS");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < Estado.length(); i++) {

                                            JSONObject TEstados = Estado.getJSONObject(i);

                                            try {
                                                String SqlEstado = " Insert into Mv_Estado(idEstado,Estado) " +
                                                        "values("
                                                        + Integer.parseInt(TEstados.getString("idEstado")) + ",'"
                                                        + TEstados.getString("Estado") + "');";
                                                DB.execSQL(SqlEstado);
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }


                                        }
                                    }
                                    handler.post(new Runnable() {
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), "Actualizando Estados..", Toast.LENGTH_SHORT).show();
                                            EditarConfiguracionGeneral.putString("AEstados", "" + Estados);
                                            EditarConfiguracionGeneral.apply();
                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR ESTADOS.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }


                        /*if (!AMarcas.equals(Marcas)) {
                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Marca.php");
                            ////Post = new HttpPost("http://socimagestion/desarrollo/SocimaMovil/Datos/Marca.php");
                            Response = httpClient.execute(Post);
                            Status = Response.getStatusLine().getStatusCode();
                            if (Status == 200) {
                                entity = Response.getEntity();
                                data = EntityUtils.toString(entity);
                                try {
                                    JObj = new JSONObject(data);
                                    ExisteDato = 1;
                                } catch (JSONException e) {
                                    Log.d("ERRORDATOS", "Descarga de Marcas");
                                    ExisteDato = 0;
                                }
                                if (ExisteDato > 0) {
                                    try {
                                        Marca = JObj.getJSONArray("Marca");
                                        ExisteDato = 1;
                                        //BDSocima.vaciarEstado(DB);
                                    } catch (JSONException e) {
                                        ExisteDato = 0;
                                        Log.d("ERRORDATOS", "MARCAS");
                                    }
                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < Marca.length(); i++) {

                                            JSONObject Tmarca = Marca.getJSONObject(i);

                                            try {
                                                String SqlMarca = " Insert into Mv_Marca(idMarca,Nombre) " +
                                                        "values("
                                                        + Integer.parseInt(Tmarca.getString("marca_id")) + ",'"
                                                        + Tmarca.getString("nombre") + "');";
                                                DB.execSQL(SqlMarca);
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                            //////////////////////imagen marca
                                            try {
                                                String Url = "";
                                                String Informacion = "";
                                                File Archivo = null;
                                                Url = "http://socimagestion.com/image/data/"+Tmarca.getString("nombre")+".png";
                                                String Nombre = Tmarca.getString("nombre") + ".png";
                                                try {
                                                    File root = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion");
                                                    if (root.exists() && root.isDirectory()) {

                                                    } else {
                                                        root.mkdir();
                                                    }
                                                    Archivo = new File(root + "/" + Nombre);
                                                    Log.d("Archivo", "" + Archivo);
                                                    String url = Url;
                                                    Archivo.delete();
                                                    HttpClient http = new DefaultHttpClient();
                                                    HttpGet httpGet = new HttpGet(url);
                                                    HttpResponse response = http.execute(httpGet);
                                                    if (response.getStatusLine().getStatusCode() == 200) {
                                                        Log.d("IMAGENMARCA", "Descargando " + Nombre);
                                                        try {
                                                            HttpEntity ent = response.getEntity();
                                                            InputStream inputStream = ent.getContent();
                                                            Boolean status = Archivo.createNewFile();
                                                            FileOutputStream fileOutputStream = new FileOutputStream(Archivo);
                                                            byte[] buffer = new byte[1024];
                                                            long total = 0;
                                                            int count;
                                                            while ((count = inputStream.read(buffer)) != -1) {
                                                                total += count;
                                                                fileOutputStream.write(buffer, 0, count);
                                                            }
                                                            fileOutputStream.close();
                                                            inputStream.close();
                                                            Log.d("ImagenMARCA", "Descargada: " + Archivo);

                                                        } catch (Exception e) {
                                                            Log.d("ERROR DESCARGA IMAGEN MARCA", e.getCause().toString());
                                                        }
                                                    } else if (response.getStatusLine().getStatusCode() == 404) {
                                                        Log.d("IMAGENMARCA", "No encontrada :" + Nombre);
                                                    }
                                                } catch (Exception e) {
                                                    Log.d("ERROR DESCARGA IMAGEN MARCA", e.getCause().toString());
                                                }
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                            ///////////////////////////////////
                                        }
                                    }
                                    handler.post(new Runnable() {
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), "Actualizando Marcas..", Toast.LENGTH_SHORT).show();
                                            EditarConfiguracionGeneral.putString("AMarcas", "" + Marcas);
                                            EditarConfiguracionGeneral.apply();
                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR MARCAS.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }*/
                    }
                    return true;
                } else {
                    return false;
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(Boolean Resultado) {
            pDialog.dismiss();//ocultamos progess dialog.
            super.onPostExecute(Resultado);
            if (Resultado == false) {
                Log.d("ACTUALIZACION", "ERROR : Activando Modo Off-Line");
                /*Intent i = new Intent(Sistema.this, Login.class);
                startActivity(i);
                finish();*/

            } else {
                Log.d("ACTUALIZACION", "Actualizacion Completa");
                //dismissDialog(progress_bar_type);
                /*EditarConfiguracionGeneral.putString("NuevoEquipo", "NO");
                EditarConfiguracionGeneral.apply();
                Intent i = new Intent(Sistema.this, Login.class);
                finish();
                startActivity(i);*/
            }
        }
    }

}