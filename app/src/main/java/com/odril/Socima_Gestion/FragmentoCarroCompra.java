package com.odril.Socima_Gestion;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odril.socimagestionv02.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FragmentoCarroCompra extends Fragment {

    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    TextView NombreClienteFrag;
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    MyAdapterDos adapter;
    int TotalCompraDos;
    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    TextView TotalCompra;
    TextView CreditoDisponible;
    TextView CreditoMaximo;
    int T;
    int TD;
    EditText dctoA;
    DecimalFormat formateador = new DecimalFormat("###,###.##");

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        FuenteUno = Typeface.createFromAsset(getActivity().getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getActivity().getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getActivity().getAssets(), "fonts/cuatro.ttf");

        SocimaGestion = new BaseDatos(getActivity(), "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();

        Cliente = getActivity().getSharedPreferences("Cliente", getActivity().MODE_PRIVATE);
        EditarCliente = Cliente.edit();

        Usuario = getActivity().getSharedPreferences("Usuario", getActivity().MODE_PRIVATE);
        EditarUsuario = Usuario.edit();

        return inflater.inflate(R.layout.fragmento_carro, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerviewDos);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapterDos(Adap());
        recyclerView.setAdapter(adapter);

        Button TerminarVenta = (Button) getActivity().findViewById(R.id.BtnTerminarVenta);

        TerminarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CarroCompleto.class);
                //int cliente = Integer.parseInt(Usuario.getString("VerCodigo", "0"));
                int cliente = Integer.parseInt(Cliente.getString("IDCliente", "0"));

                dctoA = (EditText) getActivity().findViewById(R.id.dctoA);
                int idOrden = Cliente.getInt("OrdenCompra", 0);
                int idCliente = Integer.parseInt(Cliente.getString("IDCliente", "0"));

                ArrayList<ArrayList<String>> outterArray = new ArrayList<>();
                int Con = 0;

                Cursor Re = db.rawQuery("Select idProducto,Cantidad,Precio,Descuento,NombreProducto from Mv_Compra where idOrdenCompra = " + idOrden + " and idCliente =" + idCliente + "", null);
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

                        }else {
                            innerArray.add(5, "0"); // Cantidad Max

                        }
                        int TC = Re.getInt(1);
                        int TP = Re.getInt(2);
                        int TD = Re.getInt(3);
                        int ToUno = TC * TP;

                        outterArray.add(Con, innerArray);
                        Con++;


                    }
                    while (Re.moveToNext());
                }

                i.putExtra("test", cliente);
                getActivity().startActivity(i);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                Log.e("app", "Activity name:" + getActivity().getClass().getSimpleName());
                if(getActivity().getClass().getSimpleName().equals("DetalleProducto"))
                {
                    EditarUsuario.putString("DetalleActivo","SI");
                    EditarUsuario.apply();
                }

            }
        });

        TextView Titulo = (TextView) getActivity().findViewById(R.id.TituloCarro);
        NombreClienteFrag = (TextView) getActivity().findViewById(R.id.NombreClienteFrag);
        NombreClienteFrag.setTypeface(FuenteUno);
        if (Cliente.getString("NombreCliente", "Nombre Cliente") != "Nombre Cliente") {
            NombreClienteFrag.setText(Cliente.getString("NombreCliente", "Nombre Cliente"));
        }
        Titulo.setTypeface(FuenteUno);

        TotalCompra = (TextView) getActivity().findViewById(R.id.TotalActual);
        CreditoDisponible = (TextView) getActivity().findViewById(R.id.CreditoDisponible);

    }


    ArrayList<ArrayList<String>> Adap() {


        int idOrden = Cliente.getInt("OrdenCompra", 0);
        int idCliente = Integer.parseInt(Cliente.getString("IDCliente", "0"));


        ArrayList<ArrayList<String>> outterArray = new ArrayList<>();


        int Con = 0;

        Cursor Re = db.rawQuery("Select idProducto,Cantidad,Precio,Descuento,NombreProducto from Mv_Compra where idOrdenCompra = " + idOrden + " and idCliente =" + idCliente + "", null);
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


    class MyAdapterDos extends RecyclerView.Adapter<MyAdapterDos.ViewHolder> {

        public ArrayList<ArrayList<String>> mDatasete;
        String Codigo;


        public MyAdapterDos(ArrayList<ArrayList<String>> Adpx) {
            super();
            this.mDatasete = Adpx;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = View.inflate(viewGroup.getContext(), R.layout.detalle_carro, null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            TotalCompra.setText("Total Actual: $" + formateador.format(Double.parseDouble(String.valueOf(T))));
            //TotalCompra.setText("Total Actual: $" + T
            //CreditoDisponible.setText("Credito Disponible: $" + formateador.format(Double.parseDouble(Cliente.getString("CreditoCliente", "0"))));
            //CreditoDisponible.setText("Credito Disponible: $" + Cliente.getString("CreditoCliente", "0"));
            Integer creditoMaximo = 0;
            Cursor cli = db.rawQuery("SELECT CreditoMaximo FROM Mv_cliente WHERE CodigoCliente = " + Cliente.getString("IDCliente", "0"), null);
            if(cli.moveToFirst()){
                creditoMaximo = cli.getInt(0);
            }
            CreditoDisponible.setText("Credito Disponible: $" + formateador.format(creditoMaximo));
            //CreditoMaximo.setText("Credito Maximo: $" + String.valueOf(creditoMaximo));
            //CreditoMaximo.setText("Credito Maximo: $" + formateador.format(Double.parseDouble(String.valueOf(creditoMaximo))));


            if (!mDatasete.get(i).get(3).equals(0)) {

                int Total = (Integer.parseInt(mDatasete.get(i).get(3)) * Integer.parseInt(mDatasete.get(i).get(1)));
                int TotalDos = Integer.parseInt(mDatasete.get(i).get(2)) * Integer.parseInt(mDatasete.get(i).get(1));
                TD = TotalDos - Total;
                viewHolder.PrecioProductoCarro.setText("$" + formateador.format(TD));

            } else {
                int TotalDos = Integer.parseInt(mDatasete.get(i).get(2)) * Integer.parseInt(mDatasete.get(i).get(1));
                viewHolder.PrecioProductoCarro.setText("$" + formateador.format(TotalDos));

            }
            viewHolder.NombreProductoCarro.setText(mDatasete.get(i).get(4).replace("Ã\u0091","Ñ"));
            viewHolder.CodigoProductoCarro.setText(mDatasete.get(i).get(0));
            viewHolder.CantidadProductoCarro.setText("x " + mDatasete.get(i).get(1));
            final String CantidadMax = mDatasete.get(i).get(5);

            viewHolder.CantidadProductoCarro.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    // get prompts.xml view
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View promptView = layoutInflater.inflate(R.layout.editar_cantidad, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setView(promptView);

                    final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                    // setup a dialog window
                    alertDialogBuilder.setCancelable(true)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                 //   viewHolder.CantidadProductoCarro.setText("x" + editText.getText());

                                    Log.d("Cantidad Maxima", "" + CantidadMax);
                                    if (editText.getText().toString().equals("") | editText.getText().toString().isEmpty()) {
                                        dialog.cancel();

                                    } else if (Integer.parseInt(editText.getText().toString()) <= Integer.parseInt(CantidadMax) && Integer.parseInt(editText.getText().toString()) != 0) {
                                        viewHolder.CantidadProductoCarro.setText("x "+editText.getText());
                                        db.execSQL("update Mv_Compra set Cantidad = " + Integer.parseInt(editText.getText().toString()) + " where idProducto =" + Integer.parseInt(viewHolder.CodigoProductoCarro.getText().toString()) + " and idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");
                                        LayoutInflater li = getActivity().getLayoutInflater();
                                        View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                (ViewGroup) getActivity().findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);

                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ " + Integer.parseInt(editText.getText().toString()) + " de " + viewHolder.NombreProductoCarro.getText().toString() + " ] Cantidad Modificada ");
                                        Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();
                                    } else if (Integer.parseInt(editText.getText().toString()) == 0) {
                                        db.execSQL("Delete  from Mv_Compra where idProducto =" + Integer.parseInt(viewHolder.CodigoProductoCarro.getText().toString()) + " and idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");

                                        FragmentManager Fm = getFragmentManager();
                                        FragmentTransaction ft = Fm.beginTransaction();
                                        FragmentoCarroCompra FGC = new FragmentoCarroCompra();
                                        Log.d("CantidadMenos", "" + (T - TD));
                                        int L = (T - TD);
                                        if (L == 0) {
                                            TotalCompra.setText("Total Actual: $" + 0);
                                        }
                                        ft.replace(R.id.FrgListado, FGC);
                                        ft.commit();
                                        LayoutInflater li = getActivity().getLayoutInflater();
                                        View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                (ViewGroup) getActivity().findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);
                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ " + viewHolder.NombreProductoCarro.getText().toString().replace("Ã\u0091","Ñ") + " Producto Eliminado ] ");
                                        Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();
                                    } else if (Integer.parseInt(editText.getText().toString()) > Integer.parseInt(CantidadMax)) {
                                        db.execSQL("update Mv_Compra set Cantidad = " + CantidadMax + " where idProducto =" + Integer.parseInt(viewHolder.CodigoProductoCarro.getText().toString()) + " and idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");
                                        viewHolder.CantidadProductoCarro.setText("x "+CantidadMax);
                                        LayoutInflater li = getActivity().getLayoutInflater();
                                        View layout = li.inflate(R.layout.custom_toast_error_producto,
                                                (ViewGroup) getActivity().findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);
                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ Se ha comprado el Stock Maximo ]");
                                        Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();
                                    }


                                    FragmentManager Fm = getFragmentManager();
                                    FragmentTransaction ft = Fm.beginTransaction();
                                    FragmentoCarroCompra FGC = new FragmentoCarroCompra();
                                    ft.replace(R.id.FrgListado, FGC);
                                    ft.commit();
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
            viewHolder.ImagenProductoCarro.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
                                    db.execSQL("Delete  from Mv_Compra where idProducto =" + Integer.parseInt(viewHolder.CodigoProductoCarro.getText().toString()) + " and idOrdenCompra =" + Cliente.getInt("OrdenCompra", 0) + "");

                                    FragmentManager Fm = getFragmentManager();
                                    FragmentTransaction ft = Fm.beginTransaction();
                                    FragmentoCarroCompra FGC = new FragmentoCarroCompra();
                                    Log.d("CantidadMenos", "" + (T - TD));
                                    int L = (T - TD);
                                    if (L == 0) {
                                        TotalCompra.setText("Total Actual: $" + 0);
                                    }
                                    ft.replace(R.id.FrgListado, FGC);
                                    ft.commit();

                                    sDialog
                                            .setTitleText("Eliminado!")
                                            .setContentText("El producto se a retirado del carro.!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                }
                            })
                            .show();

                    return true;
                }
            });

            viewHolder.Info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditarUsuario.putString("Producto", viewHolder.CodigoProductoCarro.getText().toString());
                    EditarUsuario.commit();
                    Intent I = new Intent(getActivity(), DetalleProducto.class);
                    getActivity().startActivity(I);
                    DetalleProducto dns = new DetalleProducto();
                    getActivity().overridePendingTransition(R.anim.fadeindos, R.anim.fadeoutdos);

                    // dns.ds.finish();
                }
            });

            Codigo = mDatasete.get(i).get(0);

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
                Picasso.with(getActivity())
                        .load("file://" + Fondo)
                        .fit()
                        .transform(new RoundedTransformation(10, 0))
                        .into(viewHolder.ImagenProductoCarro);
                //Picasso.with(getActivity()).load("file://"+Fondo).resizeDimen(200,500).into(viewHolder.ImagenProductoCarro);
                //Picasso.with(getActivity()).load("file://"+Fondo).resize(100,50).centerCrop().into(viewHolder.ImagenProductoCarro);
            } else {

                Picasso.with(getActivity())
                        .load(R.drawable.sinimagen)
                        .fit()
                        .transform(new RoundedTransformation(10, 0))
                        .into(viewHolder.ImagenProductoCarro);
            }
        }

        @Override
        public int getItemCount() {
            return mDatasete.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView ImagenProductoCarro;
            public TextView NombreProductoCarro;
            public TextView CodigoProductoCarro;
            public TextView PrecioProductoCarro;
            public TextView CantidadProductoCarro;
            public LinearLayout Info;

            public ViewHolder(View itemView) {
                super(itemView);

                ImagenProductoCarro = (ImageView) itemView.findViewById(R.id.ImagenProductoCarro);
                NombreProductoCarro = (TextView) itemView.findViewById(R.id.NombreProductoCarro);
                CodigoProductoCarro = (TextView) itemView.findViewById(R.id.CodigoProductoCarro);
                PrecioProductoCarro = (TextView) itemView.findViewById(R.id.PrecioProductoCarro);
                CantidadProductoCarro = (TextView) itemView.findViewById(R.id.CantidadProductoCarro);
                Info = (LinearLayout) itemView.findViewById(R.id.Info);


            }
        }

    }


}
