package com.odril.Socima_Gestion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.odril.socimagestionv02.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class VerOrden extends ActionBarActivity  {

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
    TextView TotalSinIva, Total, Iva, CreditoMaximoCl;
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    RecyclerView recyclerView;
    MyAdapterCinco adapter;
    //  static Activity Fs
    int T;
    int TD;
    double iva;
    String dcto = "";
    RadioGroup RD;
    ///////
    BaseDatos BDSocima;
    Metodos metodos;
    DecimalFormat formateador = new DecimalFormat("###,###");

    ArrayList<ArrayList<String>> datos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_orden);

        //BDSocima = new BaseDatos(this, "SocimaGestion", null, 1);
        //db = BDSocima.getWritableDatabase();

        metodos = new Metodos();
        final Boolean Conexion = metodos.EstadoConexion(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_ver_orden);
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
        NombreCompleto.setText(Usuario.getString("VerNombre", "VACIO"));
        TextView TituloCarroCompleto = (TextView) findViewById(R.id.TituloCarroCompleto);
        TituloCarroCompleto.setTypeface(FuenteUno);
        TextView ClienteCarro = (TextView) findViewById(R.id.ClienteCarro);
        ClienteCarro.setTypeface(FuenteUno);
        TextView OrdenCarro = (TextView) findViewById(R.id.OrdenCarro);
        OrdenCarro.setTypeface(FuenteUno);
        OrdenCompra = (TextView) findViewById(R.id.OrdenCarroCompleto);
        OrdenCompra.setText("" + Usuario.getString("VerOrden", "0"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewTres);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        RD.setEnabled(false);

        RadioButton ch1 = (RadioButton) findViewById(R.id.ch1);
        RadioButton ch2 = (RadioButton) findViewById(R.id.ch2);
        RadioButton ch3 = (RadioButton) findViewById(R.id.ch3);

        String IDCliente = Usuario.getString("VerCodigo", "0");
        final EditText DireccionFacturacion = (EditText) findViewById(R.id.DireccionFact);
        TextView dctoA = (TextView) findViewById(R.id.dctoA);
        EditText DireccionEnvio = (EditText) findViewById(R.id.DireccionEnvio);

        String Direccion = "", DireccionDos = "";

        final EditText Comentario = (EditText) findViewById(R.id.Comentario);
        String TipoPago="", Comen ="";

        //Cursor csr = db.rawQuery("select DireccionFacturacion, TipoPago,Comentario,DireccionEnvio from Mv_Orden where CodigoOrden= " + Integer.parseInt(Usuario.getString("VerOrden","0"))+ " ", null);
        Cursor csr = db.rawQuery("select DireccionFacturacion, TipoPago,Comentario,DireccionEnvio, dcto from Mv_Orden where IdOrden= " + Integer.parseInt(Usuario.getString("VerOrden","0"))+ " ", null);
        {
            if (csr.moveToFirst()) {
                do {
                    Direccion = csr.getString(0);
                    TipoPago = csr.getString(1);
                    Comen = csr.getString(2);
                    DireccionDos = csr.getString(3);
                    dcto = csr.getString(4);
                } while (csr.moveToNext());
            }
        }


        DireccionFacturacion.setText(DireccionDos);
        //System.out.println("dato dcto " + dcto);
        if(dcto != null) {
            dctoA.setText(dcto + "%");
        }else{
            dctoA.setText("0 %");
        }

        switch (TipoPago)
        {
            case "Transferencia":
                ch1.setChecked(false);
                ch2.setChecked(true);
                ch3.setChecked(false);
                ch1.setEnabled(false);
                ch2.setEnabled(true);
                ch3.setEnabled(false);
                break;
            case "Efectivo":
                ch1.setChecked(false);
                ch2.setChecked(false);
                ch3.setChecked(true);
                ch1.setEnabled(false);
                ch2.setEnabled(false);
                ch3.setEnabled(true);
                break;

            default:
                ch1.setChecked(true);
                ch2.setChecked(false);
                ch3.setChecked(false);
                ch1.setEnabled(true);
                ch2.setEnabled(false);
                ch3.setEnabled(false);
                break;
        }

        DireccionEnvio.setText(Direccion);
        Comentario.setText(Comen);

        datos = Adap();
        adapter = new MyAdapterCinco(datos);
        recyclerView.setAdapter(adapter);

        TotalSinIva = (TextView) findViewById(R.id.TotalSinIva);
        Total = (TextView) findViewById(R.id.TotalCompra);
        Iva = (TextView) findViewById(R.id.IVA);
        CreditoMaximoCl = (TextView) findViewById(R.id.CreditoMaximoCl);

        Metodos m = new Metodos();
        boolean v = m.esIgual(Usuario.getString("VerCredito","0"), "0");
        if (v) {
            CreditoMaximoCl.setText("Sin Credito Disponible");
        } else {
            //System.out.println("dato cl " +Usuario.getString("VerCodigo","0"));
            CreditoMaximoCl.setText("$" + formateador.format(Double.parseDouble(Usuario.getString("VerCredito","0"))));
        }
        //CreditoMaximoCl.setText("Sin Credito Disponible");


        Button btnRepetir = (Button) findViewById(R.id.Repetir);

        btnRepetir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new SweetAlertDialog(VerOrden.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Repetir Orden")
                        .setContentText("Se agregaran los productos al carrito")
                        .setConfirmText("Confirmar")
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
                                db.execSQL("Delete from Mv_Compra where idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");
                                db.execSQL("Delete  from Mv_Orden where CodigoOrden =" + Cliente.getInt("OrdenCompra", 0) + "");
                                sDialog
                                        .setTitleText("Orden Duplicada")
                                        .setContentText("Los Productos fueron agregados al carrito sastifactoriamente")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {


                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                //System.out.println("Boton de prueba");

                                                int idOrden = Integer.parseInt(Usuario.getString("VerOrden", "0"));
                                                int idCliente = Integer.parseInt(Usuario.getString("VerCodigo", "0"));
                                                int cliente = 0;
                                                ArrayList<ArrayList<String>> outterArray = new ArrayList<>();

                                                int Con = 0;

                                                Cursor Re = db.rawQuery("SELECT do.idProducto, do.Cantidad, do.Precio, p.Descuento, p.Modelo, o.DireccionFacturacion, o.DireccionEnvio, o.Comentario, o.idCliente FROM Mv_detalleOrden do JOIN Mv_Producto p ON (do.idProducto = p.idProducto) JOIN Mv_Orden o ON (do.idOrden = o.idOrden) WHERE do.idOrden = " + idOrden + "", null);
                                                if (Re.moveToFirst()) {
                                                    do {
                                                        ArrayList<String> innerArray = new ArrayList<>();

                                                        Cursor id = db.rawQuery("SELECT MAX(IdOrden) FROM Mv_Orden", null);
                                                        int idCompraN = 0;
                                                        if (id.moveToFirst()) {
                                                            //idCompraN = id.getInt(0) + 1;
                                                        }
                                                        idCompraN = Cliente.getInt("OrdenCompra", 0);

                                                        String Sql = "INSERT INTO Mv_Compra(idOrdenCompra,idCliente,idProducto,Cantidad,Precio,Descuento,Comentario,NombreProducto)values (" + idCompraN + "," + Re.getInt(8) + "," +
                                                                Re.getInt(0) + "," +
                                                                Re.getInt(1) + ","
                                                                + Re.getInt(2) + "," + Re.getInt(3) +
                                                                ", '" + Re.getString(7) + "','" + Re.getString(4) + "'); ";
                                                        Log.d("STRINGSQLINSERTDESC", Sql);
                                                        db.execSQL(Sql);
                                                        cliente = Re.getInt(8);
                                                        outterArray.add(Con, innerArray);
                                                        Con++;

                                                    }
                                                    while (Re.moveToNext());
                                                }

                                                Intent I = new Intent(VerOrden.this, CarroCompleto.class);
                                                I.putExtra("test", cliente);
                                                startActivity(I);

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


        int idOrden = Integer.parseInt(Usuario.getString("VerOrden", "0"));
        int idCliente = Integer.parseInt(Usuario.getString("VerCodigo", "0"));


        ArrayList<ArrayList<String>> outterArray = new ArrayList<>();


        int Con = 0;

        Cursor Re = db.rawQuery("Select idProducto,Cantidad,Precio,Descuento,NombreProducto from Mv_Compra where idOrdenCompra = " + idOrden + " and idCliente =" + idCliente + "", null);
        if(Re.getCount() == 0){
            //Re = db.rawQuery("SELECT * FROM Mv_DetalleOrden", null);
            Re = db.rawQuery("SELECT do.idProducto, do.Cantidad, do.Precio, p.Descuento, p.Modelo FROM Mv_detalleOrden do JOIN Mv_Producto p ON (do.idProducto = p.idProducto) WHERE do.idOrden = " + idOrden + "", null);
        }

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

            TotalSinIva.setText("" + s);
            Iva.setText("" + (int) iva + "");
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    //______________________________________________________________________________________________________

    class MyAdapterCinco extends RecyclerView.Adapter<MyAdapterCinco.ViewHolder> {

        public ArrayList<ArrayList<String>> mDatasetex;
        String Codigo;


        public MyAdapterCinco(ArrayList<ArrayList<String>> Adpx) {
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

            if(dcto != null){
                double dctoT, totaldcto, ToDos = 0.0;

                /*System.out.println("dato2 dcto " + dcto);
                System.out.println("dato2 dcto " + dcto.length());*/

                if(dcto.length() == 1){
                    dctoT = Double.parseDouble("0.0"+dcto);
                }else{
                    dctoT = Double.parseDouble("0."+dcto);
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

                    TotalSinIva.setText("" + formateador.format(T));
                    Iva.setText("" + formateador.format(iva) + "");
                    //TotalSinIva.setText("" + formateador.format(totaldcto));
                    Total.setText("" + formateador.format(s));

                } else {
                    totaldcto = T;
                    iva = totaldcto / 100;
                    Log.d("TotalIva2", "" + iva);
                    iva = iva * 19;
                    final double s = totaldcto + (int) iva;
                    Log.d("Totaliva3", "" + s);

                    iva = totaldcto / 100;
                    Log.d("TotalIva2", "" + iva);
                    iva = iva * 19;
                    Log.d("Totaliva3", "" + s);

                    TotalSinIva.setText("" + formateador.format(totaldcto));
                    Iva.setText("" + formateador.format(iva) + "");
                    Total.setText("" + formateador.format(s));
                }
            }else{
                //Total.setText("" + formateador.format(T));
                if (T > 0) {
                    iva = (double) T / 100;
                    Log.d("TotalIva2", "" + iva);
                    iva = iva * 19;
                    final int s = T + (int) iva;
                    Log.d("Totaliva3", "" + s);

                    TotalSinIva.setText("" + formateador.format(T));
                    Iva.setText("" + formateador.format(iva) + "");
                    Total.setText("" + formateador.format(s));
                }
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
                Picasso.with(VerOrden.this)
                        .load("file://" + Fondo)
                        .fit()
                        .transform(new RoundedTransformation(10, 0))
                        .into(viewHolder.ImagenProductoCarroDos);
            } else {

                Picasso.with(VerOrden.this)
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
