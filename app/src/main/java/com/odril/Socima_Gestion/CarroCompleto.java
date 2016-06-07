package com.odril.Socima_Gestion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.odril.socimagestionv02.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CarroCompleto extends ActionBarActivity {

    TextView NombreCompleto;
    TextView OrdenCompra;
    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;
    TextView TotalSinIva, Total, Iva, CreditoMaximoCl, CreditoDisponible;
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    RecyclerView recyclerView;
    MyAdapterTres adapter;
    EditText dctoA;
    //  static Activity Fs
    int T;
    int TD;
    double iva;
    RadioGroup RD;
    DecimalFormat formateador = new DecimalFormat("###,###");

    ArrayList<ArrayList<String>> datos;

    public static boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // No sólo wifi, también GPRS
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        // este bucle debería no ser tan ñapa
        for (int i = 0; i < 2; i++) {
            // ¿Tenemos conexión? ponemos a true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(R.layout.activity_carro_completo);


        FuenteUno = Typeface.createFromAsset(getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getAssets(), "fonts/cuatro.ttf");

        SocimaGestion = new BaseDatos(this, "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();

        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();

        Cliente = getSharedPreferences("Cliente", MODE_PRIVATE);
        EditarCliente = Cliente.edit();
        EditarCliente.apply();
        RD = (RadioGroup) findViewById(R.id.radioGroup);
        NombreCompleto = (TextView) findViewById(R.id.NombreClienteCarroCompleto);
        NombreCompleto.setText(Cliente.getString("NombreCliente", "Nombre Cliente"));
        TextView TituloCarroCompleto = (TextView) findViewById(R.id.TituloCarroCompleto);
        TituloCarroCompleto.setTypeface(FuenteUno);
        TextView ClienteCarro = (TextView) findViewById(R.id.ClienteCarro);
        ClienteCarro.setTypeface(FuenteUno);
        TextView OrdenCarro = (TextView) findViewById(R.id.OrdenCarro);
        OrdenCarro.setTypeface(FuenteUno);
        OrdenCompra = (TextView) findViewById(R.id.OrdenCarroCompleto);
        OrdenCompra.setText("" + Cliente.getInt("OrdenCompra", 0));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewTres);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        String IDCliente = Cliente.getString("IDCliente", "0");
        final EditText DireccionFacturacion = (EditText) findViewById(R.id.DireccionFact);
        final EditText DireccionEnvio = (EditText) findViewById(R.id.DireccionEnvio);

        //Bundle bundle = getIntent().getExtras();
        //String dato = bundle.getString("test");
        int clienteID = getIntent().getExtras().getInt("test");

        String Direccion = "", Ciudad = "", Region = "", Nombre = "";
        Cursor cs = db.rawQuery("select * from Mv_Cliente where CodigoCliente = " + clienteID + " ", null);
        {
            if (cs.moveToFirst()) {
                do {
                    Nombre = cs.getString(1);
                    Direccion = cs.getString(6);
                    Ciudad = cs.getString(7);
                    Region = cs.getString(8);

                } while (cs.moveToNext());
            }
        }
        NombreCompleto.setText(Nombre);
        final EditText Comentario = (EditText) findViewById(R.id.Comentario);
        DireccionFacturacion.setText(Direccion + "- " + Ciudad + "- " + Region);
        DireccionEnvio.setText(Direccion + "- " + Ciudad + "- " + Region);

        datos = Adap();
        adapter = new MyAdapterTres(datos);
        recyclerView.setAdapter(adapter);

        TotalSinIva = (TextView) findViewById(R.id.TotalSinIva);
        Total = (TextView) findViewById(R.id.TotalCompra);
        Iva = (TextView) findViewById(R.id.IVA);
        CreditoDisponible = (TextView) findViewById(R.id.CreditoDisponibleC);
        Metodos m = new Metodos();
        boolean v = m.esIgual(Cliente.getString("CreditoCliente", "0"), "0");
        Integer creditoMaximo = 0;
        Cursor cli = db.rawQuery("SELECT CreditoMaximo FROM Mv_cliente WHERE CodigoCliente = " + Cliente.getString("IDCliente", "0"), null);
        if(cli.moveToFirst()){
            creditoMaximo = cli.getInt(0);
        }

        TotalSinIva.setText("123456789");
        Total.setText("123456789");
        Iva.setText("123456789");
        //CreditoDisponible.setText("$ " + formateador.format(Double.parseDouble(Cliente.getString("CreditoCliente", "0"))));
        CreditoDisponible.setText("$ " + formateador.format(creditoMaximo));

        Button btnConfirmar = (Button) findViewById(R.id.ConfirmarVenta);
        Button btnPendiente = (Button) findViewById(R.id.VentaPendiente);
        Button btnCancelar = (Button) findViewById(R.id.CancelarVenta);


        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new SweetAlertDialog(CarroCompleto.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Cancelar Pedido?")
                        .setContentText("Se eliminara la orden de compra!")
                        .setConfirmText("Eliminar!")
                        .setCancelText("Cancelar")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {
                                db.execSQL("Delete from Mv_Orden where idOrden =" + Cliente.getInt("OrdenCompra", 0) + "");
                                db.execSQL("Delete from Mv_Compra where idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");
                                db.execSQL("Delete from Mv_DetalleOrden where idOrden = " + Cliente.getInt("OrdenCompra", 0) + "");

                                sDialog
                                        .setTitleText("Orden eliminada!")
                                        .setContentText("La orden fue eliminada Satisfactoriamente!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                /*Catalogo cs = new Catalogo();
                                                cs.Fs.finish();*/

                                                Metodos m = new Metodos();

                                                boolean v = m.esIgual(Usuario.getString("DetalleActivo", "NO"), "SI");

                                                Log.d("DEDONDE VIENE", "" + v);

                                                if (v) {
                                                    DetalleProducto DNS = new DetalleProducto();
                                                    DetalleProducto.ds.finish();
                                                    EditarUsuario.remove("DetalleActivo");
                                                    EditarUsuario.apply();
                                                }

                                                Random Rss = new Random();
                                                int Rr = Math.abs(Rss.nextInt());
                                                EditarCliente.putInt("OrdenCompra", Rr);
                                                EditarCliente.apply();
                                                Intent r = new Intent(CarroCompleto.this, Cliente.class);
                                                startActivity(r);
                                                sDialog.cancel();

                                                finish();
                                                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);


                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                            }
                        })
                        .show();

            }

        });
        btnPendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getItemCount() == 0) {


                    new SweetAlertDialog(CarroCompleto.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("! No puedes terminar la venta ! ")
                            .setContentText("El carro esta vacio!")
                            .setConfirmText("Comprar!")
                            .setCancelText("Cancelar Orden")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {
                                    db.execSQL("Delete  from Mv_Compra where idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");

                                    sDialog
                                            .setTitleText("Orden Cancelada!")
                                            .setContentText("La orden fue cancelada Satisfactoriamente!")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    Metodos m = new Metodos();
                                                    Catalogo cs = new Catalogo();
                                                    Catalogo.Fs.finish();
                                                    boolean a = m.esIgual(Usuario.getString("ClienteActivo", "NO"), "SI");

                                                    if (a) {
                                                        Cliente cl = new Cliente();
                                                        com.odril.Socima_Gestion.Cliente.Fs.finish();

                                                    }

                                                    boolean v = m.esIgual(Usuario.getString("DetalleActivo", "NO"), "SI");

                                                    Log.d("DEDONDE VIENE", "" + v);

                                                    if (v) {
                                                        DetalleProducto DNS = new DetalleProducto();
                                                        DetalleProducto.ds.finish();
                                                        EditarUsuario.remove("DetalleActivo");
                                                        EditarUsuario.apply();
                                                    }

                                                   /* Random Rss = new Random();
                                                    int Rr = Math.abs(Rss.nextInt());
                                                    EditarCliente.putInt("OrdenCompra", Rr);
                                                    EditarCliente.apply();*/
                                                    Intent r = new Intent(CarroCompleto.this, Cliente.class);
                                                    startActivity(r);
                                                    sDialog.cancel();

                                                    finish();
                                                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);


                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {

                                    Catalogo cs = new Catalogo();
                                    Catalogo.Fs.finish();

                                    Metodos m = new Metodos();

                                    boolean v = m.esIgual(Usuario.getString("DetalleActivo", "NO"), "SI");

                                    Log.d("DEDONDE VIENE", "" + v);

                                    if (v) {
                                        DetalleProducto DNS = new DetalleProducto();
                                        DetalleProducto.ds.finish();
                                        EditarUsuario.remove("DetalleActivo");
                                        EditarUsuario.apply();
                                    }

                                  /*  Random Rss = new Random();
                                    int Rr = Math.abs(Rss.nextInt());
                                    EditarCliente.putInt("OrdenCompra", Rr);
                                    EditarCliente.apply();*/
                                    Intent r = new Intent(CarroCompleto.this, Catalogo.class);
                                    startActivity(r);
                                    sDialog.cancel();

                                    finish();
                                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                                }
                            })
                            .show();


                } else if (adapter.getItemCount() > 0) {

                    new SweetAlertDialog(CarroCompleto.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("! Orden Pendiente ! ")
                            .setContentText("La orden, quedara sujeta a confirmacion.!")
                            .setConfirmText("Aceptar!")
                            .setCancelText("Cancelar")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {

                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {
                                    sDialog
                                            .setTitleText("Orden Ingresada!")
                                            .setContentText("La orden quedo guardada y pendiente de confirmacion.!")
                                            .setConfirmText("Aceptar")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    String seleccion = "Pago Habitual";
//crear sql ahora
                                                    if (RD.getCheckedRadioButtonId() != -1) {
                                                        int id = RD.getCheckedRadioButtonId();
                                                        View rd = RD.findViewById(id);
                                                        int Rdc = RD.indexOfChild(rd);
                                                        RadioButton rf = (RadioButton) RD.getChildAt(Rdc);
                                                        seleccion = (String) rf.getText();
                                                        Log.d("RADIO SELECT", seleccion);
                                                    }
                                                    String FechaActual = "";
                                                    Calendar cal = new GregorianCalendar();
                                                    Date date = cal.getTime();
                                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                                    FechaActual = df.format(date);


                                                    Cursor csn = db.rawQuery("select * from  Mv_Orden where CodigoOrden = '" + Cliente.getInt("OrdenCompra", 0) + "'", null);

                                                    if (csn.moveToFirst()) {
                                                        String total = Total.getText().toString().replace(".", "");

                                                        db.execSQL("update Mv_Orden set Total =" + Integer.parseInt(total)
                                                                + ", Comentario = '" + Comentario.getText().toString() + "', FFI = '" + FechaActual + "',TipoPago = '" +
                                                                seleccion + "', DireccionFacturacion = '" + DireccionEnvio.getText().toString() + "',DireccionEnvio = '" + DireccionFacturacion.getText().toString() + "' where CodigoOrden = '" + Cliente.getInt("OrdenCompra", 0) + "'");

                                                    } else {
                                                        db.execSQL("Insert into Mv_Orden(CodigoOrden,idCliente,DireccionFacturacion,TipoPago,Total,Estado,FFI,FFM,Comentario,Vendedor,DireccionEnvio, dcto)values('" +
                                                                        Cliente.getInt("OrdenCompra", 0) + "'," +
                                                                        Integer.parseInt(Cliente.getString("IDCliente", "0")) + ",'" + DireccionEnvio.getText().toString() +
                                                                        "','" + seleccion + "'," + Integer.parseInt(Total.getText().toString().replace(".", "")) + ",0,'" + FechaActual + "','"
                                                                        + FechaActual + "','" + Comentario.getText().toString() + "'," + Usuario.getInt("CodigoVendedor", 0) + ",'" + DireccionFacturacion.getText().toString() + "','" + dctoA.getText().toString() + "');");


                                                        Cursor Cs, Cs2 = null;
                                                        Cs = getIdOrden();
                                                        Cs2 = getDatosOrden();
                                                        //System.out.println("datos id orden " + Cs.getCount());
                                                        //System.out.println("datos orden " + Cs2.getCount());
                                                        if (Cs.moveToFirst()) {
                                                            if (Cs2.moveToFirst()) {
                                                                do {
                                                                    int idProducto = Cs2.getInt(0);
                                                                    int cantidad = Cs2.getInt(1);
                                                                    int precio = Cs2.getInt(2);
                                                                    int descuento = Cs2.getInt(3);
                                                                    int total = cantidad * precio;
                                                                    /*System.out.println("dato producto " + idProducto);
                                                                    System.out.println("dato cantidad " + cantidad);
                                                                    System.out.println("dato precio " + precio);
                                                                    System.out.println("dato descuento " + descuento);
                                                                    System.out.println("dato total " + total);
                                                                    System.out.println("dato id orden " + Cs.getInt(0));*/
                                                                    db.execSQL("Insert into Mv_DetalleOrden(idOrden,idProducto,Cantidad,Precio,Total) " +
                                                                            "values(" + Cs.getInt(0) + ", " + idProducto + ", " + cantidad + ", " + precio + ", " + total + ");");
                                                                } while (Cs2.moveToNext());
                                                            }
                                                        }
                                                    }

                                                    // Abrir Informacion del Cliente ---------------
                                                    Metodos m = new Metodos();
                                                    Catalogo cs = new Catalogo();
                                                    //cs.Fs.finish();
                                                    boolean a = m.esIgual(Usuario.getString("ClienteActivo", "NO"), "SI");

                                                    /*if (a) {
                                                        Cliente cl = new Cliente();
                                                        cl.Fs.finish();

                                                    }*/

                                                    boolean v = m.esIgual(Usuario.getString("DetalleActivo", "NO"), "SI");

                                                    Log.d("DEDONDE VIENE", "" + v);

                                                    if (v) {
                                                        DetalleProducto DNS = new DetalleProducto();
                                                        DetalleProducto.ds.finish();
                                                        EditarUsuario.remove("DetalleActivo");
                                                        EditarUsuario.apply();
                                                    }

                                                    Random Rss = new Random();
                                                    int Rr = Math.abs(Rss.nextInt());
                                                    EditarCliente.putInt("OrdenCompra", Rr);
                                                    EditarCliente.apply();
                                                    Intent r = new Intent(CarroCompleto.this, PerfilCliente.class);
                                                    startActivity(r);
                                                    sDialog.cancel();

                                                    finish();
                                                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);


                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();


                }
            }
        });
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TOTAL ITEMS", "" + adapter.getItemCount());

                if (adapter.getItemCount() == 0) {
                    new SweetAlertDialog(CarroCompleto.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("! No puedes terminar la venta ! ")
                            .setContentText("El carro esta vacio!")
                            .setConfirmText("Comprar!")
                            .setCancelText("Cancelar Orden")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {
                                    db.execSQL("Delete  from Mv_Compra where idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");

                                    sDialog
                                            .setTitleText("Orden Cancelada!")
                                            .setContentText("La orden fue cancelada Satisfactoriamente!")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    Metodos m = new Metodos();
                                                    Catalogo cs = new Catalogo();
                                                    Catalogo.Fs.finish();
                                                    boolean a = m.esIgual(Usuario.getString("ClienteActivo", "NO"), "SI");

                                                    if (a) {
                                                        Cliente cl = new Cliente();
                                                        com.odril.Socima_Gestion.Cliente.Fs.finish();

                                                    }

                                                    boolean v = m.esIgual(Usuario.getString("DetalleActivo", "NO"), "SI");

                                                    Log.d("DEDONDE VIENE", "" + v);

                                                    if (v) {
                                                        DetalleProducto DNS = new DetalleProducto();
                                                        DetalleProducto.ds.finish();
                                                        EditarUsuario.remove("DetalleActivo");
                                                        EditarUsuario.apply();
                                                    }

                                                    Random Rss = new Random();
                                                    int Rr = Math.abs(Rss.nextInt());
                                                    EditarCliente.putInt("OrdenCompra", Rr);
                                                    EditarCliente.apply();
                                                    Intent r = new Intent(CarroCompleto.this, CarroCompleto.class);
                                                    startActivity(r);
                                                    sDialog.cancel();

                                                    finish();
                                                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);


                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {

                                    Catalogo cs = new Catalogo();
                                    Catalogo.Fs.finish();

                                    Metodos m = new Metodos();

                                    boolean v = m.esIgual(Usuario.getString("DetalleActivo", "NO"), "SI");

                                    Log.d("DEDONDE VIENE", "" + v);

                                    if (v) {
                                        DetalleProducto DNS = new DetalleProducto();
                                        DetalleProducto.ds.finish();
                                        EditarUsuario.remove("DetalleActivo");
                                        EditarUsuario.apply();
                                    }

                                    Random Rss = new Random();
                                    int Rr = Math.abs(Rss.nextInt());
                                    EditarCliente.putInt("OrdenCompra", Rr);
                                    EditarCliente.apply();
                                    Intent r = new Intent(CarroCompleto.this, CarroCompleto.class);
                                    startActivity(r);
                                    sDialog.cancel();

                                    finish();
                                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                                }
                            })
                            .show();


                } else if (adapter.getItemCount() > 0) {


                    new SweetAlertDialog(CarroCompleto.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("! Orden Completa ! ")
                            .setContentText("La orden, se enviara al sistema.!")
                            .setConfirmText("Aceptar!")
                            .setCancelText("Cancelar")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {

                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {
                                    sDialog
                                            .setTitleText("Orden Completa!")
                                            .setContentText("La orden se a completado.!")
                                            .setConfirmText("Aceptar")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    String seleccion = "Pago Habitual";
//crear sql ahora
                                                    if (RD.getCheckedRadioButtonId() != -1) {
                                                        int id = RD.getCheckedRadioButtonId();
                                                        View rd = RD.findViewById(id);
                                                        int Rdc = RD.indexOfChild(rd);
                                                        RadioButton rf = (RadioButton) RD.getChildAt(Rdc);
                                                        seleccion = (String) rf.getText();
                                                        Log.d("RADIO SELECT", seleccion);
                                                    }
                                                    String FechaActual = "";
                                                    Calendar cal = new GregorianCalendar();
                                                    Date date = cal.getTime();
                                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                                    FechaActual = df.format(date);

                                                    Cursor csn = db.rawQuery("select * from  Mv_Orden where CodigoOrden = '" + Cliente.getInt("OrdenCompra", 0) + "'", null);

                                                    if (csn.moveToFirst()) {
                                                        db.execSQL("update Mv_Orden set Total =" + Integer.parseInt(Total.getText().toString().replace(".",""))
                                                                + ", Comentario = '" + Comentario.getText().toString() + "', FFI = '" + FechaActual + "',TipoPago = '" +
                                                                seleccion + "', DireccionFacturacion = '" + DireccionEnvio.getText().toString() + "', DireccionEnvio = '" + DireccionFacturacion.getText().toString() + "', Estado = 1 where CodigoOrden = '" + Cliente.getInt("OrdenCompra", 0) + "'");

                                                        dctoA.getText().toString().trim();


                                                    } else {
                                                        db.execSQL("Insert into Mv_Orden(CodigoOrden,idCliente,DireccionFacturacion,TipoPago,Total,Estado,FFI,FFM,Comentario,Vendedor,DireccionEnvio, dcto)values('" + Cliente.getInt("OrdenCompra", 0) + "'," + Integer.parseInt(Cliente.getString("IDCliente", "0")) + ",'" + DireccionEnvio.getText().toString() + "','" + seleccion + "'," + Integer.parseInt(Total.getText().toString().replace(".","")) + ",1,'" + FechaActual + "','" + FechaActual + "','" + Comentario.getText().toString() + "'," + Usuario.getInt("CodigoVendedor", 0) + ",'" + DireccionFacturacion.getText().toString() + "','" + dctoA.getText().toString() + "');");

                                                        Cursor Cs, Cs2 = null;
                                                        Cs = getIdOrden();
                                                        Cs2 = getDatosOrden();

                                                        System.out.println("dato id orden " + Cs.getCount());
                                                        if (Cs.moveToFirst()) {
                                                            System.out.println("dato id orden2 " + +Cs.getInt(0));
                                                        }
                                                        if (Cs2.moveToFirst()) {
                                                            do {
                                                                int idProducto = Cs2.getInt(0);
                                                                int cantidad = Cs2.getInt(1);
                                                                int precio = Cs2.getInt(2);
                                                                int descuento = Cs2.getInt(3);
                                                                int total = cantidad * precio;

                                                                System.out.println("dato id orden " + Cs.getInt(0));

                                                                db.execSQL("Insert into Mv_DetalleOrden(idOrden,idProducto,Cantidad,Precio,Total) " +
                                                                        "values(" + Cs.getInt(0) + ", " + idProducto + ", " + cantidad + ", " + precio + ", " + total + ");");
                                                            } while (Cs2.moveToNext());
                                                        }

                                                    }
                                                    // Abrir Informacion del Cliente ---------------
                                                    Metodos m = new Metodos();
                                                    Catalogo cs = new Catalogo();
                                                    Catalogo.Fs.finish();
                                                    boolean a = m.esIgual(Usuario.getString("ClienteActivo", "NO"), "SI");

                                                    /*if (a) {
                                                        Cliente cl = new Cliente();
                                                        cl.Fs.finish();

                                                    }*/

                                                    boolean v = m.esIgual(Usuario.getString("DetalleActivo", "NO"), "SI");

                                                    Log.d("DEDONDE VIENE", "" + v);

                                                    if (v) {
                                                        DetalleProducto DNS = new DetalleProducto();
                                                        DetalleProducto.ds.finish();
                                                        EditarUsuario.remove("DetalleActivo");
                                                        EditarUsuario.apply();
                                                    }

                                                    Random Rss = new Random();
                                                    int Rr = Math.abs(Rss.nextInt());
                                                    EditarCliente.putInt("OrdenCompra", Rr);
                                                    EditarCliente.apply();
                                                    Intent r = new Intent(CarroCompleto.this, PerfilCliente.class);
                                                    startActivity(r);
                                                    sDialog.cancel();

                                                    finish();
                                                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);


                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();


                }

            }
        });


    }

    public Cursor getIdOrden() {
        return db.rawQuery("SELECT MAX(IdOrden) FROM Mv_Orden", null);
    }

    public Cursor getDatosOrden(){
        int idOrden = Cliente.getInt("OrdenCompra", 0);
        int idCliente = Integer.parseInt(Cliente.getString("IDCliente", "0"));
        return db.rawQuery("Select idProducto,Cantidad,Precio,Descuento,NombreProducto from Mv_Compra where idOrdenCompra = " + idOrden + " and idCliente =" + idCliente + "", null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }


    ArrayList<ArrayList<String>> Adap() {


        int idOrden = Cliente.getInt("OrdenCompra", 0);
        int idCliente = Integer.parseInt(Cliente.getString("IDCliente", "0"));


        ArrayList<ArrayList<String>> outterArray = new ArrayList<>();

        int dato = getIntent().getExtras().getInt("test");

        int Con = 0;

        Cursor Re = db.rawQuery("Select idProducto,Cantidad,Precio,Descuento,NombreProducto from Mv_Compra where idOrdenCompra = " + idOrden + " and idCliente =" + dato + "", null);
        //Cursor Re = db.rawQuery("Select idProducto,Cantidad,Precio,Descuento,NombreProducto from Mv_Compra where idOrdenCompra = " + idOrden + "", null);
        if (Re.moveToFirst()) {
            do {
                ArrayList<String> innerArray = new ArrayList<>();

                innerArray.add(0, "" + Re.getInt(0)); // idProducto
                innerArray.add(1, "" + Re.getInt(1)); // Cantidad
                innerArray.add(2, "" + Re.getInt(2)); // Precio
                innerArray.add(3, "" + Re.getInt(3)); // Descuento
                innerArray.add(4, "" + Re.getString(4)); // NombreProducto


                Cursor StockM = db.rawQuery("Select Cantidad from Mv_Producto where idProducto =" + Re.getInt(0) + " ", null);
                if (StockM.moveToFirst()) {
                    do {
                        innerArray.add(5, "" + StockM.getInt(0)); // Cantidad Max
                    } while (StockM.moveToNext());

                } else {
                    innerArray.add(5, "0"); // Cantidad Max

                }

                int TC = Re.getInt(1);
                int TP = Re.getInt(2);
                int TD = Re.getInt(3);
                int ToUno = TC * TP;

                if (TD != 0) {
                    int ToDos = TC * TD;

                    T += ToUno - ToDos;
                } else {
                    T += ToUno;
                }


                outterArray.add(Con, innerArray);
                Con++;


            }
            while (Re.moveToNext());
        }


        return outterArray;
    }

    //___________________________________________________
    void actualizar() {
        Total.setText("" + 0);
        TotalSinIva.setText("" + 0);
        Iva.setText("" + 0);
        TD = T = 0;
        datos.clear();
        datos.addAll(Adap());
        adapter.notifyDataSetChanged();

        if (T > 0) {

            iva = (double) T / 100;
            Log.d("TotalIva2", "" + iva);
            iva = iva * 19;
            final int s = T - (int) iva;
            Log.d("Totaliva3", "" + s);

            TotalSinIva.setText("" + formateador.format(s));
            Iva.setText("" + formateador.format(iva) + "");
        }

    }

    void descuento(){
        dctoA.getText().toString().trim();

        double dctoT, totaldcto, ToDos = 0.0;

        if(dctoA.getText().toString().trim().length() == 1){
            dctoT = Double.parseDouble("0.0"+dctoA.getText().toString().trim());
        }else{
            dctoT = Double.parseDouble("0."+dctoA.getText().toString().trim());
        }

        if (dctoT != 0.0) {
            ToDos = T * dctoT;
            totaldcto = T - ToDos;

            iva = totaldcto / 100;
            Log.d("TotalIva2", "" + iva);
            iva = iva * 19;
            final double s = totaldcto - (int) iva;

            iva = totaldcto / 100;
            Log.d("TotalIva2", "" + iva);
            iva = iva * 19;
            Log.d("Totaliva3", "" + s);

            TotalSinIva.setText("" + formateador.format(s));
            Iva.setText("" + formateador.format(iva) + "");

            //TotalSinIva.setText("" + formateador.format(totaldcto));
            Total.setText("" + formateador.format(totaldcto));
        } else {
            totaldcto = T;
            iva = totaldcto / 100;
            Log.d("TotalIva2", "" + iva);
            iva = iva * 19;
            final double s = totaldcto - (int) iva;
            Log.d("Totaliva3", "" + s);

            iva = totaldcto / 100;
            Log.d("TotalIva2", "" + iva);
            iva = iva * 19;
            Log.d("Totaliva3", "" + s);

            TotalSinIva.setText("" + formateador.format(s));
            Iva.setText("" + formateador.format(iva) + "");
            Total.setText("" + formateador.format(totaldcto));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

    }
    //______________________________________________________________________________________________________

    class MyAdapterTres extends RecyclerView.Adapter<MyAdapterTres.ViewHolder> {

        public ArrayList<ArrayList<String>> mDatasetex;
        String Codigo;


        public MyAdapterTres(ArrayList<ArrayList<String>> Adpx) {
            super();
            this.mDatasetex = Adpx;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = View.inflate(viewGroup.getContext(), R.layout.detalle_dos, null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            Total.setText("" + formateador.format(T));
            if (T > 0) {

                iva = (double) T / 100;
                Log.d("TotalIva2", "" + iva);
                iva = iva * 19;
                final int s = T - (int) iva;
                Log.d("Totaliva3", "" + s);

                TotalSinIva.setText("" + formateador.format(s));
                Iva.setText("" + formateador.format(iva) + "");
            }


            if (!mDatasetex.get(i).get(3).equals("0")) {

                int Total = (Integer.parseInt(mDatasetex.get(i).get(3)) * Integer.parseInt(mDatasetex.get(i).get(1)));
                int TotalDos = Integer.parseInt(mDatasetex.get(i).get(2)) * Integer.parseInt(mDatasetex.get(i).get(1));
                TD = TotalDos - Total;
                viewHolder.PrecioProductoCarroDos.setText("$" + formateador.format(TD));

            } else {
                int TotalDos = Integer.parseInt(mDatasetex.get(i).get(2)) * Integer.parseInt(mDatasetex.get(i).get(1));
                viewHolder.PrecioProductoCarroDos.setText("$" + formateador.format(TotalDos));

            }
            viewHolder.NombreProductoCarroDos.setText(mDatasetex.get(i).get(4));
            viewHolder.CodigoProductoCarroDos.setText(mDatasetex.get(i).get(0));
            viewHolder.CantidadProductoCarroDos.setText("x " + mDatasetex.get(i).get(1));
            final String CantidadMax = mDatasetex.get(i).get(5);

            dctoA = (EditText) findViewById(R.id.dctoA);
            dctoA.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                    //System.out.println("dato de cambio");
                    //System.out.println("dato % dcto " + dctoA.getText().toString().trim());
                    descuento();

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

            viewHolder.CantidadProductoCarroDos.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    // get prompts.xml view
                    LayoutInflater layoutInflater = LayoutInflater.from(CarroCompleto.this);
                    View promptView = layoutInflater.inflate(R.layout.editar_cantidad, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CarroCompleto.this);
                    alertDialogBuilder.setView(promptView);

                    final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                    // setup a dialog window
                    alertDialogBuilder.setCancelable(true)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String Tsl = editText.getText().toString();
                                    Log.d("Cantidad Maxima", "" + CantidadMax);
                                    if (editText.getText().toString().equals("") | editText.getText().toString().isEmpty()) {
                                        dialog.cancel();

                                    } else if (Integer.parseInt(editText.getText().toString()) <= Integer.parseInt(CantidadMax) && Integer.parseInt(editText.getText().toString()) != 0) {
                                        db.execSQL("update Mv_Compra set Cantidad = " + Integer.parseInt(editText.getText().toString()) + " where idProducto =" + Integer.parseInt(viewHolder.CodigoProductoCarroDos.getText().toString()) + " and idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");
                                        LayoutInflater li = getLayoutInflater();
                                        View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                (ViewGroup) findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);

                                        actualizar();

                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ " + Integer.parseInt(editText.getText().toString()) + " de " + viewHolder.NombreProductoCarroDos.getText().toString() + " ] Cantidad Modificada ");
                                        Toast ts = Toast.makeText(CarroCompleto.this, "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();
                                        // viewHolder.CantidadProductoCarroDos.setText("x " + Tsl);


                                    } else if (Integer.parseInt(editText.getText().toString()) == 0) {
                                        db.execSQL("Delete  from Mv_Compra where idProducto =" + Integer.parseInt(viewHolder.CodigoProductoCarroDos.getText().toString()) + " and idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");


                                        Log.d("CantidadMenos", "" + (T - TD));
                                        Total.setText("" + (T - TD));
                                        Log.d("AQUI", "SEgunda OPCION");

                                       /* mDatasetex.remove(i);
                                        adapter.notifyItemRemoved(i);*/
                                        actualizar();

                                        LayoutInflater li = getLayoutInflater();
                                        View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                (ViewGroup) findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);
                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ " + viewHolder.NombreProductoCarroDos.getText().toString() + " Producto Eliminado ] ");
                                        Toast ts = Toast.makeText(CarroCompleto.this, "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();

                                    } else if (Integer.parseInt(editText.getText().toString()) > Integer.parseInt(CantidadMax)) {
                                        db.execSQL("update Mv_Compra set Cantidad = " + CantidadMax + " where idProducto =" + Integer.parseInt(viewHolder.CodigoProductoCarroDos.getText().toString()) + " and idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");
                                        viewHolder.CantidadProductoCarroDos.setText("x " + CantidadMax);
                                        LayoutInflater li = getLayoutInflater();
                                        actualizar();
                                        View layout = li.inflate(R.layout.custom_toast_error_producto,
                                                (ViewGroup) findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);
                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ Se ha comprado el Stock Maximo ]");
                                        Toast ts = Toast.makeText(CarroCompleto.this, "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();
                                        Log.d("AQUI", "Tercera OPCION");

                                    }

                                }

                            })
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create an alert dialog
                    AlertDialog alert = alertDialogBuilder.create();
                    alert.getWindow().setLayout(300, 200);
                    alert.show();


                    return true;
                }
            });



            viewHolder.ImagenProductoCarroDos.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    new SweetAlertDialog(CarroCompleto.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Estas seguro?")
                            .setContentText("Se eliminara el producto!")
                            .setConfirmText("Eliminar!")
                            .setCancelText("Cancelar")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    db.execSQL("Delete  from Mv_Compra where idProducto =" + Integer.parseInt(viewHolder.CodigoProductoCarroDos.getText().toString()) + " and idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");

                                    actualizar();


                                    Log.d("CantidadMenos", "" + (T - TD));
                                    Total.setText("" + (T - TD));

                                    sDialog
                                            .setTitleText("Eliminado!")
                                            .setContentText("El producto se a retirado del carro.!")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                }
                            })
                            .show();

                    return true;
                }
            });

            viewHolder.InfoDos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditarUsuario.putString("Producto", viewHolder.CodigoProductoCarroDos.getText().toString());
                    EditarUsuario.commit();
                    Intent I = new Intent(CarroCompleto.this, DetalleProducto.class);
                    startActivity(I);
                    DetalleProducto dns = new DetalleProducto();
                    // dns.ds.finish();
                }
            });

            Codigo = mDatasetex.get(i).get(0);

            if(Codigo.length() == 5){
                Codigo = 0+Codigo;
            }

            File Fondo = new File(Environment.getExternalStorageDirectory().getPath() + "/SocimaGestion/" + Codigo + "_2.jpg");


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
                Picasso.with(CarroCompleto.this)
                        .load("file://" + Fondo)
                        .fit()
                        .transform(new RoundedTransformation(10, 0))
                        .into(viewHolder.ImagenProductoCarroDos);
            } else {

                Picasso.with(CarroCompleto.this)
                        .load(R.drawable.sinimagen)
                        .fit()
                        .transform(new RoundedTransformation(10, 0))
                        .into(viewHolder.ImagenProductoCarroDos);
            }
        }



        @Override
        public int getItemCount() {
            return mDatasetex.size();
        }

        //___________________________________________________
        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView ImagenProductoCarroDos;
            public TextView NombreProductoCarroDos;
            public TextView CodigoProductoCarroDos;
            public TextView PrecioProductoCarroDos;
            public TextView CantidadProductoCarroDos;
            public LinearLayout InfoDos;

            public ViewHolder(View itemView) {
                super(itemView);

                ImagenProductoCarroDos = (ImageView) itemView.findViewById(R.id.ImagenProductoCarroDos);
                NombreProductoCarroDos = (TextView) itemView.findViewById(R.id.NombreProductoCarroDos);
                CodigoProductoCarroDos = (TextView) itemView.findViewById(R.id.CodigoProductoCarroDos);
                PrecioProductoCarroDos = (TextView) itemView.findViewById(R.id.PrecioProductoCarroDos);
                CantidadProductoCarroDos = (TextView) itemView.findViewById(R.id.CantidadProductoCarroDos);
                InfoDos = (LinearLayout) itemView.findViewById(R.id.InfoDos);


            }
        }

    }
}
