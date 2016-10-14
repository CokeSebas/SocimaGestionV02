package com.odril.Socima_Gestion;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import Adaptadores.Httppostaux;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Inicio extends ActionBarActivity {
    // Declaracion de Variables -------------------------------------------------------

    public static final int progress_bar_type = 0;
    Metodos metodos;
    SharedPreferences ConfiguracionGeneral;
    SharedPreferences.Editor EditarConfiguracionGeneral;

    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;

    String NuevoEquipo;
    BaseDatos BDSocima;
    SQLiteDatabase DB;
    ImageView Logo;

    int pdcto = 0;

    globals g = globals.getInstance();
    public boolean running = g.getRunning();

    private static final int NOTIF_ALERTA_ID = 1;


    TimerTask ActividadLogin = new TimerTask() {
        @Override
        public void run() {
            Intent mainIntent = new Intent().setClass(Inicio.this, Login.class);
            startActivity(mainIntent);
            finish();
        }
    };


    // Funciones Privadas -----------------------------------------------------------------
    private ProgressDialog pDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Asignacion de Variables -------------------------------------------------------

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

        // Logica del Sistema -----------------------------------------------------------

        //new DescargaImagen().onCancelled();

        if (Conexion) {
            if (NuevoEquipo.equals("SI")) {
                Log.d("Nuevo equipo", "" + " si");
                new Actualizacion().execute();
                new DescargaImagen().execute();
            } else {
                Log.d("Nuevo equipo", "" + " no");
                new Actualizacion().execute();
                new DescargaImagen().execute();
            }
        } else {
            if (NuevoEquipo.equals("NO")) {
                Timer Tiempo = new Timer();
                Tiempo.schedule(ActividadLogin, 1500);
                System.out.println("no inicia por que descarga imagenes esta activa");
            } else {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("ERROR !")
                        .setContentText("Debes estar conectado a internet la primera vez.!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                finish();
                            }
                        })
                        .show();
            }
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void Ms() {
        showDialog(progress_bar_type);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Actualizando");
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    public Cursor getOrdenesConfirmadas() {
        int dia = 0;
        String fechaActual = "";
        String fecha5DiasAntes = "";
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        String mes2 = String.valueOf(fecha.get(Calendar.MONTH) + 1);
        String dia2 = String.valueOf(fecha.get(Calendar.DAY_OF_MONTH));
        String dia3 = String.valueOf(fecha.get(Calendar.DAY_OF_MONTH) - 5);
        String mes = "";
        if (mes2.length() == 1) {
            mes = "0" + mes2;
        } else {
            mes = mes2;
        }

        if (dia2.length() == 1) {
            dia = 0 + Integer.parseInt(dia2);
            fechaActual = año + "-" + mes + "-0" + dia;
        } else {
            dia = Integer.parseInt(dia2);
            fechaActual = año + "-" + mes + "-" + dia;
        }

        if (dia3.length() == 1) {
            dia = 0 + Integer.parseInt(dia2);
            fecha5DiasAntes = año + "-" + mes + "-0" + (dia - 5);
        } else {
            dia = Integer.parseInt(dia2);
            fecha5DiasAntes = año + "-" + mes + "-" + (dia - 5);
        }

        //return DB.rawQuery("SELECT * FROM Mv_Orden WHERE Estado = 1", null);
        //return DB.rawQuery("SELECT * FROM Mv_Orden WHERE Estado = 1 AND FFI BETWEEN '" + fecha5DiasAntes +  "' AND '" + fechaActual +"'", null);
        return DB.rawQuery("SELECT * FROM Mv_Orden WHERE FFI BETWEEN '" + fecha5DiasAntes + "' AND '" + fechaActual + "'", null);

    }

    public Cursor getDetalleOrden(int idOrden) {
        return DB.rawQuery("SELECT do.idProducto, do.Cantidad, do.Precio, p.Descuento, p.Modelo FROM Mv_detalleOrden do JOIN Mv_Producto p ON (do.idProducto = p.idProducto) WHERE do.idOrden = " + idOrden + "", null);
    }

    public Boolean isOnlineNet() {
        if(isNetworkAvailable()) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost Post = new HttpPost("http://socimagestion.com/admin");
                HttpResponse Response = httpClient.execute(Post);
                int Status = Response.getStatusLine().getStatusCode();
                if (Status == 200) {
                    System.out.println("dato si conecta socimagestion");
                    return true;
                }else{
                    System.out.println("dato no conecta socimagestion");
                    return false;
                }

                /*Process p = Runtime.getRuntime().exec("ping -c 1 www.google.cl");
                int val = p.waitFor();
                boolean reachable = (val == 0);
                System.out.println("dato isOnlinNet " + val);
                System.out.println("dato isOnlinNet2 " + reachable);
                return reachable;*/
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    private Boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public Boolean hasInternet(){
        if(isNetworkAvailable()){
            try{
                HttpURLConnection urlc =(HttpURLConnection)(new URL("http://socimagestion.com/admin").openConnection());
                urlc.setRequestProperty("User-Agent","Test");
                urlc.setRequestProperty("Connection","close");
                urlc.setConnectTimeout(1500);
                //urlc.connect();
                return (urlc.getResponseCode() == 200);
            }catch(IOException e){
                Log.e("LOG_TAG", "No tiene conexion a la pagina",e);
            }
        }else{
            Log.e("LOG_TAG", "Not netwotk available");
        }
        return false;
    }

    public class Planificador {
        Timer timer;
        int counter1 = 0;
        Date fecha;
        boolean finTarea = false;

        public Planificador () {
//se crea un planificador que planificara 2 tareas
            timer = new Timer ( ) ;
//la Tarea1 se ejecuta pasado 5 minutos y luego periódicamente cada 5 minutos
            //timer.schedule ( new Tarea1 () , 10000, 10000) ;
            timer.schedule ( new Tarea1 () , 30000, 100000) ;
            //timer.schedule ( new Tarea1 () , 300000, 300000) ;
        }
        //cuando el contador llega a 10 se desplanifica la tarea
        class Tarea1 extends TimerTask {
            public void run ( ) {
                System.out.println("verifica internet cada 5 minutos") ;
                if (counter1 < 5){
                    //System.out.println ( "El contador tiene valor: "+ counter1) ;
                    //counter1++;
                    //hasInternet();
                    if (isOnlineNet()) {
                        System.out.println("dato si tiene internet");
                        new DescargaImagen().doInBackground();
                        this.cancel();
                        finTarea = true;
                    }else {
                        System.out.println("dato no tiene conexion a internet");
                    }
                }
                else{
//Si la otra tarea ya ha acabado mata al planificador
                    if (finTarea){
                        System.out.println ( "Fin Planificador") ;
                        timer.cancel();
                    }
//Si la otra tarea todavia no ha acabado solo se desplanifica, el
//planificador sigue funcionando
                    else{
                        //System.out.println ( "Fin Tarea1") ;
                        this.cancel () ;
                        //System.out.println ( "Tarea1 desplanificada." ) ;
                        finTarea = true;
                    }
                }
            }
        }
    }

//ACTUALIZACION DE DATOS --------------------------------------------------------------

    class Actualizacion extends AsyncTask<Void, String, Boolean> {
        Handler handler = new Handler();
        String Vendedores = null, Menu = null, Productos = null, Ordenes = null, Clientes = null, Noticias = null, Estados = null, Marcas = null;
        String AVendedores = null, AMenu = null, AProductos = null, AOrdenes = null, AClientes = null, ANoticias = null, AEstados = null, AMarcas = null;

        Httppostaux post;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost Post = new HttpPost("http://socimagestion.com/Movil/Datos/Actualizacion.php");
                //HttpPost //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Actualizacion.php");
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
                    String CodigoOrden, IdCliente, DireccionF, TipoPago, Total, EstadoO, FFI, FFM, Comentario, VendedorO, DireccionE, ProductId, nombreP, cantidad, precio, dcto;
                    Cs = getOrdenesConfirmadas();


                    /*int dia = 0;
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
                    System.out.println("dato fecha actual " + fechaActual);
                    System.out.println("dato fecha 5 días antes " + fecha5DiasAntes);*/

                    System.out.println("dato total ordenes confirmadas " + Cs.getCount());
                    if (Cs.moveToFirst()) {
                        do {
                            //CodigoOrden = Cs.getString(0);
                            CodigoOrden = Cs.getString(1);
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
                            dcto = Cs.getString(14);

                            httpClient = new DefaultHttpClient();
                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/ActualizarOrden.php");
                            ////Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/ActualizarOrden.php");
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
                            params.add(new BasicNameValuePair("dcto", dcto));

                            Post.setEntity(new UrlEncodedFormEntity(params));
                            HttpResponse resp = httpClient.execute(Post);
                            HttpEntity ent = resp.getEntity();
                            String text = EntityUtils.toString(ent);
                            System.out.println("guardo : " + text);
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Actualizando Ordenes.", Toast.LENGTH_LONG).show();
                                }
                            });

                            int idOrden = Cs.getInt(0);
                            Cs2 = getDetalleOrden(idOrden);
                            //System.out.println("total detalle orden " + Cs2.getCount());
                            if (Cs2.moveToFirst()) {
                                do {
                                    ProductId = Cs2.getString(0);
                                    nombreP = Cs2.getString(4);
                                    cantidad = Cs2.getString(1);
                                    precio = Cs2.getString(2);
                                    int total = Integer.parseInt(cantidad) * Integer.parseInt(precio);
                                    httpClient = new DefaultHttpClient();
                                    Post = new HttpPost("http://socimagestion.com/Movil/Datos/ActualizarDetalleOrden.php");
                                    ////Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/ActualizarDetalleOrden.php");

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

                                } while (Cs2.moveToNext());
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Actualizando Ordenes.", Toast.LENGTH_LONG).show();

                                    }
                                });
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
                                Marcas = Actualizacion.getString("Marcas");
                            }


                            AVendedores = ConfiguracionGeneral.getString("AVendedores", "x");
                            AMenu = ConfiguracionGeneral.getString("AMenu", "x");
                            AProductos = ConfiguracionGeneral.getString("AProductos", "x");
                            AOrdenes = ConfiguracionGeneral.getString("AOrdenes", "x");
                            AClientes = ConfiguracionGeneral.getString("AClientes", "x");
                            ANoticias = ConfiguracionGeneral.getString("ANoticias", "x");
                            AEstados = ConfiguracionGeneral.getString("AEstados", "x");
                            AMarcas = ConfiguracionGeneral.getString("AMarcas", "x");
                        }

                        if (!AVendedores.equals(Vendedores) | !AMenu.equals(Menu) | !AProductos.equals(Productos) | !AOrdenes.equals(Ordenes) | !AClientes.equals(Clientes) | !ANoticias.equals(Noticias) | !AEstados.equals(Estados) | !AMarcas.equals(Marcas)) {
                            handler.post(new Runnable() {
                                public void run() {
                                    Ms();
                                }
                            });


                        }

                        if (!AVendedores.equals(Vendedores)) {

                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Vendedor.php");
                            //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Vendedor.php");
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
                                                        Integer.parseInt(DetalleVendedor.getString("Actual")) + ",'" +
                                                        DetalleVendedor.getString("Localidad") + "', " +
                                                        Integer.parseInt(DetalleVendedor.getString("Estado")) + ");";
                                                DB.execSQL(SqlInsertUsuario);
                                                //System.out.println("dato guardo vendedor");
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                            EditarConfiguracionGeneral.putString("AVendedores", "" + Vendedores);
                                            EditarConfiguracionGeneral.apply();

                                        }
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Actualizando Vendedores.", Toast.LENGTH_LONG).show();
                                                EditarConfiguracionGeneral.putString("AVendedores", "" + Vendedores);
                                                EditarConfiguracionGeneral.apply();

                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR VENDEDORES.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }
                        }


                        if (!AMenu.equals(Menu)) {
                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Menu.php");
                            //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Menu.php");
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
                                                //System.out.println("dato guardo categoria padre");
                                                //System.out.println("dato categoria " + MenuPadre.getString("NombreCategoria"));
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
                                                //System.out.println("dato guardo categoria hijo");
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
                                        //System.out.println(e);
                                    }

                                    if (ExisteDato > 0) {
                                        for (int i = 0; i < CategoriasProductos.length(); i++) {
                                            JSONObject CategoriaProductos = CategoriasProductos.getJSONObject(i);
                                            try {

                                                String SqlInsertCategoriaProducto = " Insert into Mv_categoriaProducto(idProducto,idCategoria) values(" +
                                                        Integer.parseInt(CategoriaProductos.getString("CodigoProducto")) + "," +
                                                        Integer.parseInt(CategoriaProductos.getString("Codigo")) + ");";

                                                DB.execSQL(SqlInsertCategoriaProducto);
                                                //System.out.println("dato guardo categoriaproducto");
                                                publishProgress("" + i);

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                        }
                                    }

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Actualizando Menu.", Toast.LENGTH_LONG).show();
                                            EditarConfiguracionGeneral.putString("AMenu", "" + Menu);
                                            EditarConfiguracionGeneral.apply();

                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR MENUS.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }


                        if (!AProductos.equals(Productos)) {

                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Productos.php");
                            //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Productos.php");
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
                                                //System.out.println("dato guardo producto relacion");
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

                                                //System.out.println("dato guardo detalle producto");
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
                                                /*if (Integer.parseInt(Producto.getString("Cantidad")) == 0) {
                                                    DB.execSQL("Delete from Mv_ProductoRelacion where idRelacion =" + Integer.parseInt(Producto.getString("idProducto")) + "");
                                                    DB.execSQL("Delete from Mv_DetalleProducto where idProducto =" + Integer.parseInt(Producto.getString("idProducto")) + "");
                                                } else {*/
                                                String SqlProduco = " Insert into Mv_Producto(idProducto,Modelo,Descripcion,CodigoProducto,CodigoBodega,StockInicial,Cantidad,Precio,Tam,FFA,Descuento,FFDI,FFDF,Tag,Imagen,MarcaId,SortOrder,Modificado,Image,Agotado) " +
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
                                                        + Producto.getString("Modificado").trim() + "', '"
                                                        + Producto.getString("Image").trim() + "', '"
                                                        + Producto.getString("Agotado").trim() + "');";
                                                DB.execSQL(SqlProduco);
                                                //System.out.println("dato guardo producto");
                                                publishProgress("" + i);

                                                if (NuevoEquipo.equals("SI")) {
                                                    String SqlImagenP = "INSERT INTO Mv_ImagenP (idProducto, Imagen) VALUES (" + Integer.parseInt(Producto.getString("idProducto")) + ",'NO');";
                                                    DB.execSQL(SqlImagenP);
                                                    //System.out.println("dato guardo producto 2");
                                                }

                                                //}

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                            //System.out.println("dato descuento " + Producto.getString("Descuento"));
                                            if (Integer.parseInt(Producto.getString("Descuento")) != 0) {
                                                pdcto += 1;
                                                //System.out.println("dato descuento suma 1");
                                            } else {
                                                pdcto += 0;
                                                //System.out.println("dato descuento suma 0");
                                            }

                                            /*if(Producto.getString("Cantidad").equals("0")){
                                                NotificationCompat.Builder mBuilder =
                                                        new NotificationCompat.Builder(Inicio.this)
                                                                .setSmallIcon(R.drawable.icono_socima48)
                                                                .setLargeIcon((((BitmapDrawable) getResources()
                                                                        .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                                .setContentTitle("Productos Sin Stock")
                                                                .setContentText("Hay productos sin Stock en el Sistema")
                                                                .setContentInfo("1")
                                                                .setTicker("Alerta!")
                                                                .setAutoCancel(true);

                                                Intent notIntent =
                                                        new Intent(Inicio.this, SistemaVendedor.class);

                                                PendingIntent contIntent = PendingIntent.getActivity(
                                                        Inicio.this, 0, notIntent, 0);

                                                mBuilder.setContentIntent(contIntent);

                                                NotificationManager mNotificationManager =
                                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                                mNotificationManager.notify(4, mBuilder.build());
                                            }*/

                                        }
                                    }

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Actualizando Productos.", Toast.LENGTH_LONG).show();
                                            EditarConfiguracionGeneral.putString("AProductos", "" + Productos);
                                            EditarConfiguracionGeneral.apply();

                                        }
                                    });

                                    //System.out.println("dato dcto " + pdcto);
                                    if (pdcto != 0) {
                                        NotificationCompat.Builder mBuilder =
                                                new NotificationCompat.Builder(Inicio.this)
                                                        .setSmallIcon(R.drawable.icono_socima48)
                                                        .setLargeIcon((((BitmapDrawable) getResources()
                                                                .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                        .setContentTitle("Se encuentran " + pdcto + " productos con descuentos")
                                                        .setContentText("Productos con descuento")
                                                        .setContentInfo("1")
                                                        .setTicker("Alerta!")
                                                        .setAutoCancel(true);
                                        Intent notIntent =
                                                new Intent(Inicio.this, SistemaVendedor.class);

                                        PendingIntent contIntent = PendingIntent.getActivity(
                                                Inicio.this, 0, notIntent, 0);

                                        mBuilder.setContentIntent(contIntent);

                                        NotificationManager mNotificationManager =
                                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                        mNotificationManager.notify(3, mBuilder.build());
                                    }

                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(Inicio.this)
                                                    .setSmallIcon(R.drawable.icono_socima48)
                                                    .setLargeIcon((((BitmapDrawable) getResources()
                                                            .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                    .setContentTitle("Nuevos Productos")
                                                    .setContentText("Se han agregado nuevos productos")
                                                    .setContentInfo("1")
                                                    .setTicker("Alerta!")
                                                    .setAutoCancel(true);

                                    Intent notIntent =
                                            new Intent(Inicio.this, SistemaVendedor.class);

                                    PendingIntent contIntent = PendingIntent.getActivity(
                                            Inicio.this, 0, notIntent, 0);

                                    mBuilder.setContentIntent(contIntent);

                                    NotificationManager mNotificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    mNotificationManager.notify(4, mBuilder.build());

                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR PRODUCTOS.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }

                        if (!AOrdenes.equals(Ordenes)) {
                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Ordenes.php");
                            //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Ordenes.php");
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
                                                if (TOrden.getInt("Estado") == 1) {
                                                    estado = 0;
                                                } else if (TOrden.getInt("Estado") == 2) {
                                                    estado = 1;
                                                } else if (TOrden.getInt("Estado") == 5) {
                                                    estado = 1;
                                                } else if (TOrden.getInt("Estado") == 15) {
                                                    estado = 2;
                                                } else if (TOrden.getInt("Estado") == 16) {
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
                                                //System.out.println("dato guardo ordenes");

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
                                                //System.out.println("dato guardo detalle orden");
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                        }
                                    }


                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Actualizando Ordenes.", Toast.LENGTH_LONG).show();
                                            EditarConfiguracionGeneral.putString("AOrdenes", "" + Ordenes);
                                            EditarConfiguracionGeneral.apply();
                                        }
                                    });

                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(Inicio.this)
                                                    .setSmallIcon(R.drawable.icono_socima48)
                                                    .setLargeIcon((((BitmapDrawable) getResources()
                                                            .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                    .setContentTitle("Nuevos Datos en la aplicación")
                                                    .setContentText("Se han agregado nuevas ordenes")
                                                    .setContentInfo("1")
                                                    .setTicker("Alerta!")
                                                    .setAutoCancel(true);

                                    Intent notIntent =
                                            new Intent(Inicio.this, SistemaVendedor.class);

                                    PendingIntent contIntent = PendingIntent.getActivity(
                                            Inicio.this, 0, notIntent, 0);

                                    mBuilder.setContentIntent(contIntent);

                                    NotificationManager mNotificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    mNotificationManager.notify(1, mBuilder.build());
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR ORDENES.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }


                        if (!AClientes.equals(Clientes)) {

                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Cliente.php");
                            //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Cliente.php");
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
                                                String SqlInsertCliente = "";
                                                /*SqlInsertCliente = " Insert into Mv_Cliente(CodigoCliente,Nombre,Email,Telefono,Vendedor,Credito,Direccion,Ciudad,Region,Codigo, Rut, CreditoMaximo) " +
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
                                                        TCliente.getString("Rut") + "'," +
                                                        TCliente.getString("CreditoMaximo") + ");";*/

                                                //System.out.println("dato coordenada " + TCliente.getString("fax").length());
                                                if (TCliente.getString("fax").length() != 0) {
                                                    //System.out.println("dato coordenada " + TCliente.get("fax"));
                                                    SqlInsertCliente = " Insert into Mv_Cliente(CodigoCliente,Nombre,Email,Telefono,Vendedor,Credito,Direccion,Ciudad,Region,Codigo, Rut, CreditoMaximo, Coordenada) " +
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
                                                            TCliente.getString("Rut") + "'," +
                                                            TCliente.getString("CreditoMaximo") + ",'" +
                                                            TCliente.getString("fax") + "');";
                                                    DB.execSQL(SqlInsertCliente);
                                                    //System.out.println("dato guardo cliente con coordenada");
                                                } else {
                                                    //System.out.println("dato coordenada nulo");
                                                    SqlInsertCliente = " Insert into Mv_Cliente(CodigoCliente,Nombre,Email,Telefono,Vendedor,Credito,Direccion,Ciudad,Region,Codigo, Rut, CreditoMaximo) " +
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
                                                            TCliente.getString("Rut") + "'," +
                                                            TCliente.getString("CreditoMaximo") + ");";
                                                    DB.execSQL(SqlInsertCliente);
                                                    //System.out.println("dato guardo cliente sin coordenada");
                                                }
                                                //DB.execSQL(SqlInsertCliente);
                                                //System.out.println("dato guardo cliente");

                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }

                                        }
                                    }
                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Actualizando Clientes.", Toast.LENGTH_LONG).show();
                                            EditarConfiguracionGeneral.putString("AClientes", "" + Clientes);
                                            EditarConfiguracionGeneral.apply();

                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR CLIENTES.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }


                        if (!ANoticias.equals(Noticias)) {


                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Noticias.php");
                            //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Noticias.php");
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

                                            }
                                            try {
                                                String SqlNoticia = " Insert into Mv_Noticia(Titulo,Noticia,Estado,Imagen) " +
                                                        "values('"
                                                        + TNotas.getString("Titulo") + "','"
                                                        + TNotas.getString("Noticia") + "',0,'" + Archivo + "');";

                                                DB.execSQL(SqlNoticia);
                                                //System.out.println("dato guardo noticias");
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
//System.out.println("banner " + Banner.getString("Url"));
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
                                                        //System.out.println("nombre banner "+Nombre);
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
                                                }

                                                String SqlNoticia = " Insert into Mv_Banner(BannerId,Url,Informacion) " +
                                                        "values(" + (i + 1) + ",'" + Archivo + "','" + Informacion + "');";

                                                DB.execSQL(SqlNoticia);
                                                //System.out.println("dato guardo banner " + Archivo);
                                                Log.d("Banner", "" + SqlNoticia);
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                        }
                                    }

                                    handler.post(new Runnable() {
                                                     public void run() {
                                                         Toast.makeText(getApplicationContext(), "Actualizando Noticias", Toast.LENGTH_LONG).show();
                                                         EditarConfiguracionGeneral.putString("ANoticias", "" + Noticias);
                                                         EditarConfiguracionGeneral.apply();
                                                     }
                                                 }
                                    );

                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(Inicio.this)
                                                    .setSmallIcon(R.drawable.icono_socima48)
                                                    .setLargeIcon((((BitmapDrawable) getResources()
                                                            .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                                    .setContentTitle("Nuevas Noticias")
                                                    .setContentText("Nuevas Noticias en el Sistema")
                                                    .setContentInfo("1")
                                                    .setTicker("Alerta!")
                                                    .setAutoCancel(true);

                                    Intent notIntent =
                                            new Intent(Inicio.this, Noticias.class);

                                    PendingIntent contIntent = PendingIntent.getActivity(
                                            Inicio.this, 0, notIntent, 0);

                                    mBuilder.setContentIntent(contIntent);

                                    NotificationManager mNotificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    mNotificationManager.notify(2, mBuilder.build());

                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR NOTICIAS.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }


                        if (!AEstados.equals(Estados)) {

                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Estados.php");
                            //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Estados.php");
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
                                                //System.out.println("dato guardo estados");
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }


                                        }
                                    }
                                    handler.post(new Runnable() {
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), "Actualizando Estados..", Toast.LENGTH_LONG).show();
                                            EditarConfiguracionGeneral.putString("AEstados", "" + Estados);
                                            EditarConfiguracionGeneral.apply();
                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR ESTADOS.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }

                        if (!AMarcas.equals(Marcas)) {
                            Post = new HttpPost("http://socimagestion.com/Movil/Datos/Marca.php");
                            //Post = new HttpPost("http://dev.odril.com/dev/socima/Datos/Marca.php");
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
                                                //System.out.println("dato guardo marcas " + Tmarca.getString("nombre"));
                                            } catch (Exception e) {
                                                Log.d("Error:", "" + e.getMessage());
                                            }
                                            //////////////////////imagen marca
                                            try {
                                                String Url, Dir = "";
                                                String Informacion = "";
                                                File Archivo = null;
                                                Dir = Tmarca.getString("image").replace(" ", "%20");
                                                Url = "http://socimagestion.com/image/" + Dir;
                                                //Url = "http://socimagestion.com/image/data/"+Tmarca.getString("nombre")+".png";
                                                String Nombre = Tmarca.getString("nombre") + ".png";
                                                //System.out.println("dato url marca imagen " + Url);
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

                                            Toast.makeText(getApplicationContext(), "Actualizando Marcas..", Toast.LENGTH_LONG).show();
                                            EditarConfiguracionGeneral.putString("AMarcas", "" + Marcas);
                                            EditarConfiguracionGeneral.apply();
                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "ERROR AL ACTUALIZAR MARCAS.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
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
            super.onPostExecute(Resultado);
            if (Resultado == false) {
                Log.d("ACTUALIZACION", "ERROR : Activando Modo Off-Line");
                Intent i = new Intent(Inicio.this, Login.class);
                startActivity(i);
                finish();

            } else {
                Log.d("ACTUALIZACION", "Actualizacion Completa");
                //dismissDialog(progress_bar_type);
                EditarConfiguracionGeneral.putString("NuevoEquipo", "NO");
                EditarConfiguracionGeneral.apply();
                Intent i = new Intent(Inicio.this, Login.class);
                finish();
                startActivity(i);
            }
        }

    }

    public class DescargaImagen extends AsyncTask<Void, String, Boolean> {

        @Override
        protected void onCancelled() {
            g.setRunning(false);
            super.onCancelled();
            super.cancel(true);
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            //super.cancel(true);

            if (g.getRunning() == true) {
                super.onPreExecute();
            } else {
                DescargaImagen.this.cancel(true);
                DescargaImagen.this.onCancelled();
                super.cancel(true);
            }
            new SweetAlertDialog(Inicio.this, SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("¡Aviso!")
                    .setContentText("Se estan actualizando los datos, favor esperar a que se complete")
                    .show();
            //BDSocima = new BaseDatos(getApplicationContext(), "SocimaGestion", null, 1);

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean imagen = false;
            running = g.getRunning();

            DB = BDSocima.getWritableDatabase();
            if (DB != null) {
//////////////actualizar imagenes///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (!NuevoEquipo.equals("SI")) {
                    Cursor C2 = null;
                    C2 = DB.rawQuery("SELECT p.idProducto, p.Imagen, p.Image  FROM Mv_Producto p JOIN Mv_ImagenP ip ON (p.idProducto = ip.idProducto) WHERE p.Modificado = 'TRUE' ORDER BY p.idProducto DESC", null);
                    System.out.println("cantidad imagenes actualizadas " + C2.getCount());
                    if (C2.moveToFirst()) {
                        do {
                            if (isCancelled()) break;
                            for (int i = 2; i < 8; i++) {
                                String data = C2.getString(2);
                                String[] info = data.split("\\.");
                                String Nombre = "";
                                String Nombre4 = "";
                                String url8 = "";
                                if (info.length == 2) {
                                    Nombre = C2.getInt(0) + "_" + i + ".jpg";
                                    Nombre4 = C2.getInt(0) + "_" + i + "-500x500." + info[1];
                                    String[] info2 = info[0].split("/");
                                    if (Nombre4.length() == 19) {
                                        Nombre4 = "0" + Nombre4;
                                    }

                                    if (Nombre.length() == 11) {
                                        Nombre = "0" + Nombre;
                                    }
                                    if (info2.length == 2) {
                                        if (NuevoEquipo.equals("SI")) {
                                            url8 = "http://socimagestion.com/imagenesTablet/SocimaGestion/" + Nombre;
                                        } else {
                                            url8 = "http://socimagestion.com/image/cache/" + info2[0] + "/" + Nombre4;
                                        }
                                    } else {
                                        if (NuevoEquipo.equals("SI")) {
                                            url8 = "http://socimagestion.com/imagenesTablet/SocimaGestion/" + Nombre;
                                        } else {
                                            url8 = "http://socimagestion.com/image/cache/" + info2[0] + "/" + info2[1] + "/" + Nombre4;
                                        }
                                    }
                                } else if (info.length == 3) {
                                    Nombre = C2.getInt(0) + "_" + i + ".jpg";
                                    Nombre4 = C2.getInt(0) + "_" + i + "." + info[1] + "-500x500." + info[2];
                                    String[] info2 = info[0].split("/");
                                    if (Nombre4.length() == 23) {
                                        Nombre4 = "0" + Nombre4;
                                    }
                                    if (Nombre.length() == 11) {
                                        Nombre = "0" + Nombre;
                                    }
                                    if (info2.length == 2) {
                                        if (NuevoEquipo.equals("SI")) {
                                            url8 = "http://socimagestion.com/imagenesTablet/SocimaGestion/" + Nombre;
                                        } else {
                                            url8 = "http://socimagestion.com/image/cache/" + info2[0] + "/" + Nombre4;
                                        }
                                    } else {
                                        if (NuevoEquipo.equals("SI")) {
                                            url8 = "http://socimagestion.com/imagenesTablet/SocimaGestion/" + Nombre;
                                        } else {
                                            url8 = "http://socimagestion.com/image/cache/" + info2[0] + "/" + info2[1] + "/" + Nombre4;
                                        }
                                    }
                                }
                                try {
                                    File root = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion");
                                    if (root.exists() && root.isDirectory()) {

                                    } else {
                                        root.mkdir();
                                    }
                                    if (Nombre.length() == 11) {
                                        Nombre = "0" + Nombre;
                                    }
                                    //System.out.println("url : " + url8);
                                    File Imagen = new File(root + "/" + Nombre);
                                    //System.out.println("imagen : " + Imagen.exists() + " " + Nombre);
                                    Imagen.delete();
                                    HttpClient httpClient8 = new DefaultHttpClient();
                                    HttpGet httpGet8 = new HttpGet(url8);
                                    HttpResponse response8 = httpClient8.execute(httpGet8);
                                    if (response8.getStatusLine().getStatusCode() == 200) {
                                        Log.d("IMAGEN", "actualizando " + Nombre4);
                                        String Sqlim = "UPDATE Mv_ImagenP SET Imagen = 'SI' WHERE idProducto = " + C2.getInt(0);
                                        DB.execSQL(Sqlim);
                                        System.out.println("Descargando imagen nueva " + url8);
                                        try {
                                            HttpEntity entity8 = response8.getEntity();
                                            InputStream inputStream = entity8.getContent();
                                            Boolean status = Imagen.createNewFile();
                                            FileOutputStream fileOutputStream = new FileOutputStream(Imagen);
                                            byte[] buffer = new byte[1024];
                                            long total = 0;
                                            int count;
                                            while ((count = inputStream.read(buffer)) != -1) {
                                                total += count;
                                                fileOutputStream.write(buffer, 0, count);
                                            }
                                            fileOutputStream.close();
                                            inputStream.close();
                                        } catch (Exception e) {
                                            Log.d("ERROR DESCARGA IMAGEN1", Nombre4);
                                        }
                                    } else if (response8.getStatusLine().getStatusCode() == 404) {
                                        Log.d("IMAGEN", "No encontrada8 :" + Nombre4);
                                    }

                                } catch (Exception e) {
                                    Log.d("ERROR DESCARGA IMAGEN", e.toString());
                                }
                            }
                        } while (C2.moveToNext());
                    }
                }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                Cursor C = null;
                if (NuevoEquipo.equals("SI")) {
                    C = DB.rawQuery("SELECT p.idProducto, p.Imagen, p.Image  FROM Mv_Producto p JOIN Mv_ImagenP ip ON (p.idProducto = ip.idProducto)", null);
                    //C = DB.rawQuery("SELECT p.idProducto, p.Imagen, p.Image  FROM Mv_Producto p JOIN Mv_ImagenP ip ON (p.idProducto = ip.idProducto) WHERE ip.Imagen != 'SI'",null);
                    System.out.println("dato equipo nuevo");
                } else {
                    //C = DB.rawQuery("SELECT p.idProducto, p.Imagen, p.Image  FROM Mv_Producto p JOIN Mv_ImagenP ip ON (p.idProducto = ip.idProducto) WHERE ip.Imagen != 'SI' ORDER BY p.idProducto DESC LIMIT 15",null);
                    C = DB.rawQuery("SELECT p.idProducto, p.Imagen, p.Image  FROM Mv_Producto p JOIN Mv_ImagenP ip ON (p.idProducto = ip.idProducto) WHERE ip.Imagen != 'SI' ORDER BY p.idProducto DESC", null);
                    System.out.println("dato equipo viejo");
                }
                System.out.println("cantidad imagenes " + C.getCount());
                if (C.moveToFirst()) {
                    ///////**/////////**///////*///*/*/*///////////////////////////////////////////////////////////////////////////
                    do {
                        //System.out.println("dato conexion internet" + hasInternet());
                        System.out.println("dato conexion internet " + isOnlineNet());
                        //if (!hasInternet()) {
                        if (!isOnlineNet()) {
                            //System.out.println ("dato Iniciamos el Planificador" );
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(Inicio.this)
                                            .setSmallIcon(R.drawable.icono_socima48)
                                            .setLargeIcon((((BitmapDrawable) getResources()
                                                    .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                            .setContentTitle("Descarga de Imagenes")
                                            .setContentText("Se ha interrumpido la Conexión, se retomará automáticamente.")
                                            .setContentInfo("1")
                                            .setTicker("Aviso")
                                            .setAutoCancel(true);

                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            mNotificationManager.notify(8, mBuilder.build());

                            g.setRunning(false);
                            new Planificador() ;
                            break;
                        }

                        if (isCancelled()) break;

                        for (int i = 2; i < 6; i++) {
                            String data = C.getString(2);
                            String[] info = data.split("\\.");
                            String Nombre = "";
                            String Nombre4 = "";
                            String url8 = "";
                            if (info.length == 2) {
                                Nombre = C.getInt(0) + "_" + i + ".jpg";
                                Nombre4 = C.getInt(0) + "_" + i + "-500x500." + info[1];
                                String[] info2 = info[0].split("/");
                                if (Nombre4.length() == 19) {
                                    Nombre4 = "0" + Nombre4;
                                }

                                if (Nombre.length() == 11) {
                                    Nombre = "0" + Nombre;
                                }
                                if (info2.length == 2) {
                                    //System.out.println("http://socimagestion.com/image/cache/"+info2[0]+"/"+Nombre4);
                                    if (NuevoEquipo.equals("SI")) {
                                        url8 = "http://socimagestion.com/imagenesTablet/SocimaGestion/" + Nombre;
                                        //System.out.println("url : " + url8);
                                    } else {
                                        url8 = "http://socimagestion.com/image/cache/" + info2[0] + "/" + Nombre4;
                                        //System.out.println("url : " + url8);
                                    }
                                } else {
                                    if (NuevoEquipo.equals("SI")) {
                                        url8 = "http://socimagestion.com/imagenesTablet/SocimaGestion/" + Nombre;
                                        //System.out.println("url : " + url8);
                                    } else {
                                        url8 = "http://socimagestion.com/image/cache/" + info2[0] + "/" + info2[1] + "/" + Nombre4;
                                        //System.out.println("url : " + url8);
                                    }
                                }
                            } else if (info.length == 3) {
                                Nombre = C.getInt(0) + "_" + i + ".jpg";
                                Nombre4 = C.getInt(0) + "_" + i + "." + info[1] + "-500x500." + info[2];
                                String[] info2 = info[0].split("/");
                                if (Nombre4.length() == 23) {
                                    Nombre4 = "0" + Nombre4;
                                }
                                if (Nombre.length() == 11) {
                                    Nombre = "0" + Nombre;
                                }
                                if (info2.length == 2) {
                                    if (NuevoEquipo.equals("SI")) {
                                        url8 = "http://socimagestion.com/imagenesTablet/SocimaGestion/" + Nombre;
                                        //System.out.println("url : " + url8);
                                    } else {
                                        url8 = "http://socimagestion.com/image/cache/" + info2[0] + "/" + Nombre4;
                                        //System.out.println("url : " + url8);
                                    }
                                    //url8 = "http://socimagestion.com/image/cache/"+info2[0]+"/"+Nombre4;
                                } else {
                                    if (NuevoEquipo.equals("SI")) {
                                        url8 = "http://socimagestion.com/imagenesTablet/SocimaGestion/" + Nombre;
                                        //System.out.println("url : " + url8);
                                    } else {
                                        url8 = "http://socimagestion.com/image/cache/" + info2[0] + "/" + info2[1] + "/" + Nombre4;
                                        //System.out.println("url : " + url8);
                                    }
                                }
                            }

                            try {
                                File root = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion");
                                if (root.exists() && root.isDirectory()) {

                                } else {
                                    root.mkdir();
                                }

                                if (Nombre.length() == 11) {
                                    Nombre = "0" + Nombre;
                                }
                                System.out.println("url : " + url8);
                                File Imagen = new File(root + "/" + Nombre);
                                //System.out.println("imagen : " + Imagen);
                                //System.out.println("imagen : " + Imagen.exists() + " " + Nombre);
                                if (Imagen.exists() == false) {
                                    //Imagen.delete();
                                    HttpClient httpClient8 = new DefaultHttpClient();
                                    HttpGet httpGet8 = new HttpGet(url8);
                                    HttpResponse response8 = httpClient8.execute(httpGet8);
                                    if (response8.getStatusLine().getStatusCode() == 200) {
                                        Log.d("IMAGEN", "Descargando " + Nombre4);
                                        String Sqlim = "UPDATE Mv_ImagenP SET Imagen = 'SI' WHERE idProducto = " + C.getInt(0);
                                        DB.execSQL(Sqlim);
                                        System.out.println("Descargando8 " + url8);
                                        System.out.println("guardada imagen  " + C.getInt(0));
                                        try {
                                            HttpEntity entity8 = response8.getEntity();
                                            InputStream inputStream = entity8.getContent();
                                            Boolean status = Imagen.createNewFile();
                                            FileOutputStream fileOutputStream = new FileOutputStream(Imagen);
                                            byte[] buffer = new byte[1024];
                                            long total = 0;
                                            int count;
                                            while ((count = inputStream.read(buffer)) != -1) {
                                                total += count;
                                                fileOutputStream.write(buffer, 0, count);
                                            }
                                            //String Sqlim = "UPDATE Mv_Producto SET Imagen = 'SI' WHERE idProducto = " + C.getInt(0);
                                            fileOutputStream.close();
                                            inputStream.close();
                                        } catch (Exception e) {
                                            Log.d("ERROR DESCARGA IMAGEN1", Nombre4);
                                        }
                                    } else if (response8.getStatusLine().getStatusCode() == 404) {
                                        Log.d("IMAGEN", "No encontrada8 :" + Nombre4);
                                    }
                                } else if (Imagen.exists()) {
                                    if (NuevoEquipo.equals("SI")) {
                                        String Sqlim = "UPDATE Mv_ImagenP SET Imagen = 'SI' WHERE idProducto = " + C.getInt(0);
                                        DB.execSQL(Sqlim);
                                        System.out.println("imagen ya guardada " + C.getInt(0));
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("ERROR DESCARGA IMAGEN", e.toString());
                            }
                        }
                    } while (C.moveToNext());
                    ///////**/////////**///////*///*/*/*///////////////////////////////////////////////////////////////////////////
                }
                imagen = true;
            }
            return imagen;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(Boolean Resultado) {
            super.onPostExecute(Resultado);
            System.out.println("estadoC " + Resultado);
            if (Resultado == false) {
                System.out.println("termino false");
                g.setRunning(false);
            } else {
                System.out.println("termino true");
                //if (hasInternet()) {
                if (isNetworkAvailable()) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(Inicio.this)
                                    .setSmallIcon(R.drawable.icono_socima48)
                                    .setLargeIcon((((BitmapDrawable) getResources()
                                            .getDrawable(R.drawable.icono_socima48)).getBitmap()))
                                    .setContentTitle("Descarga de Imagenes")
                                    .setContentText("Ha finalizado la descarga de imagenes")
                                    .setContentInfo("1")
                                    .setTicker("Aviso")
                                    .setAutoCancel(true);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify(6, mBuilder.build());

                    g.setRunning(false);
                }
            }
        }

    }

}