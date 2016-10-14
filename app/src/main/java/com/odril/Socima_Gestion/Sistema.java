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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.net.HttpURLConnection;
import java.net.URL;
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
                    if(isOnlineNet()){
                    //if(hasInternet()){
                        new Actualizacion().execute();
                    }else{
                        new SweetAlertDialog(Sistema.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("!Aviso¡")
                                .setContentText("No se puede ejecutar la actualización, no tiene conexión al Servidor")
                                .show();
                    }
                    //new Actualizacion().execute();
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
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            }catch(IOException e){
                Log.e("LOG_TAG", "No tiene coneccion a la pagina",e);
            }
        }else{
            Log.e("LOG_TAG", "Not netwotk available");
        }
        return false;
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
                    String CodigoOrden, IdCliente, DireccionF, TipoPago, Total, Tipo, FFI, FFM, Comentario, VendedorO, DireccionE, ProductId, nombreP, cantidad, precio;
                    Cs = getOrdenesConfirmadas();
                    System.out.println("total ordenes confirmadas " + Cs.getCount());
                    if (Cs.moveToFirst()) {
                        do {
                            //CodigoOrden = Cs.getString(0);
                            CodigoOrden = Cs.getString(1);
                            IdCliente = Cs.getString(2);
                            DireccionF = Cs.getString(3);
                            TipoPago = Cs.getString(6);
                            Total = Cs.getString(7);
                            Tipo = Cs.getString(8);
                            FFI = Cs.getString(9);
                            FFM = Cs.getString(10);
                            Comentario = Cs.getString(11);
                            VendedorO = Cs.getString(12);
                            DireccionE = Cs.getString(13);

                            //System.out.println("dato codigo orden " + CodigoOrden);
                            /*System.out.println("dato id cliente " + IdCliente);
                            System.out.println("dato direccionF " + DireccionF);
                            System.out.println("dato tipo pago " + TipoPago);*/
                            //System.out.println("dato estado " + Tipo);
                            /*System.out.println("dato FFI " + FFI);
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
                            params.add(new BasicNameValuePair("status", Tipo));
                            params.add(new BasicNameValuePair("FFI", FFI));
                            params.add(new BasicNameValuePair("FFM", FFM));
                            params.add(new BasicNameValuePair("comentario", Comentario));
                            params.add(new BasicNameValuePair("idVendedor", VendedorO));
                            params.add(new BasicNameValuePair("direccionE", DireccionE));

                            Post.setEntity(new UrlEncodedFormEntity(params));
                            HttpResponse resp = httpClient.execute(Post);
                            HttpEntity ent = resp.getEntity();
                            String text = EntityUtils.toString(ent);
                            System.out.println("guardo : " + text);

                            int idOrden = Cs.getInt(0);
                            Cs2 = getDetalleOrden(idOrden);
                            System.out.println("total detalle orden " + Cs2.getCount());
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
                                    System.out.println("guardo detalle orden : " + text2);

                                }while(Cs2.moveToNext());

                            }
                        } while (Cs.moveToNext());
                    }
                    //System.out.println("Total Ordenes Confirmadas " + Cs.getCount());

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