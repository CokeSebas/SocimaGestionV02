package com.odril.socimagestionv02;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FragmentoPerfilVendedor extends Fragment {

    TextView Meta, Vendedor, TxMeta, TxNoticias, TxListaCliente, Badg, Actual;
    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    ImageView Noticias, Clientes;

    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    MyAdapterCuatro adapter;
    RecyclerView recyclerViewCuatro;
    ArrayList<ArrayList<String>> datos;
    DecimalFormat formateador = new DecimalFormat("###,###");


    @Override
    public void onResume() {
        super.onResume();
        actualizar();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Usuario = this.getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        EditarUsuario = Usuario.edit();

        Cliente = this.getActivity().getSharedPreferences("Cliente", Context.MODE_PRIVATE);
        EditarCliente = Cliente.edit();
        EditarUsuario.apply();

        FuenteUno = Typeface.createFromAsset(getActivity().getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getActivity().getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getActivity().getAssets(), "fonts/cuatro.ttf");

        SocimaGestion = new BaseDatos(this.getActivity(), "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();
        int Contador = SocimaGestion.CantidadNoticias();

        View ly = inflater.inflate(R.layout.fragmento_vendedor, container, false);
        Badg = (TextView) ly.findViewById(R.id.badg);
        Badg.setText("" + Contador);
        if (Contador > 0) {
            Badg.setVisibility(View.VISIBLE);
        }
        Meta = (TextView) ly.findViewById(R.id.Meta);
        Actual = (TextView) ly.findViewById(R.id.Actual);


        TxNoticias = (TextView) ly.findViewById(R.id.TxNoticia);
        TxListaCliente = (TextView) ly.findViewById(R.id.TxListaCliente);
        TxMeta = (TextView) ly.findViewById(R.id.TxMeta);
        Noticias = (ImageView) ly.findViewById(R.id.ImgNoticias);
        Clientes = (ImageView) ly.findViewById(R.id.ImgCliente);
        TxNoticias.setTypeface(FuenteCuatro);
        TxListaCliente.setTypeface(FuenteCuatro);


        Clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Noticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent I = new Intent(getActivity(), Noticias.class);
                getActivity().startActivity(I);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        });

        Clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent In = new Intent(getActivity(), Cliente.class);
                getActivity().startActivity(In);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

            }
        });

        Vendedor = (TextView) ly.findViewById(R.id.TxVendedor);
        Vendedor.setTypeface(FuenteUno);
        TxMeta.setTypeface(FuenteCuatro);

        Meta.setText("" + formateador.format(Usuario.getInt("Meta", 0)));
        Cursor Cs = null;
        Cs = db.rawQuery("SELECT Actual FROM Mv_Vendedor WHERE CodigoVendedor = "+ Usuario.getInt("CodigoVendedor", 0), null);
        if (Cs.moveToFirst()) {
            Actual.setText("" + formateador.format(Cs.getInt(0)));
        }

        Meta.setTypeface(FuenteCuatro);
        Vendedor.setText(Usuario.getString("Nombre", "Vendedor"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewCuatro = (RecyclerView) ly.findViewById(R.id.recyclerviewCuatro);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewCuatro.setLayoutManager(layoutManager);
        datos = apl();
        adapter = new MyAdapterCuatro(datos);
        recyclerViewCuatro.setAdapter(adapter);
        actualizar();

        return ly;

    }

    public void onBackPressed() {
    }

    void actualizar() {

        datos.clear();
        datos.addAll(apl());
        adapter.notifyDataSetChanged();
    }

    ArrayList<ArrayList<String>> apl() {

        ArrayList<ArrayList<String>> outterArray = new ArrayList<>();

        int Con = 0;
        Cursor DetalleOrden;

        int dia = 0,mes3 = 0;
        String fechaActual = "";
        String fecha5DiasAntes = "";
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        String mes2 = String.valueOf(fecha.get(Calendar.MONTH) + 1);
        String dia2 = String.valueOf(fecha.get(Calendar.DAY_OF_MONTH));
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

        if(dia2.length() == 1){
            dia = 0 + Integer.parseInt(dia2);
            fecha5DiasAntes = año+"-0"+(Integer.parseInt(mes)-1)+"-0"+dia;
        }else{
            dia = 0 + Integer.parseInt(dia2);
            fecha5DiasAntes = año+"-0"+(Integer.parseInt(mes)-1)+"-"+dia;
        }

        if (Usuario.getInt("Cargo", 0) == 1) {
            DetalleOrden = db.rawQuery("SELECT * from Mv_Orden where FFI BETWEEN '" + fecha5DiasAntes +  "' AND '" + fechaActual +"' ORDER BY FFI DESC, Estado ASC", null);
        } else {
            DetalleOrden = db.rawQuery("SELECT * from Mv_Orden where Vendedor =" + Usuario.getInt("CodigoVendedor", 0) + " AND FFI BETWEEN '" + fecha5DiasAntes +  "' AND '" + fechaActual +"' ORDER BY FFI DESC, Estado ASC", null);
        }

        DetalleOrden.moveToFirst();
        if (!DetalleOrden.isAfterLast()) {
            do {

                ArrayList<String> innerArray = new ArrayList<>();
                innerArray.add(0, DetalleOrden.getString(0)); //OR
                innerArray.add(1, DetalleOrden.getString(2)); //IDCLIENTE
                Log.d("IDCLIENTEORDEN", DetalleOrden.getString(1));
                innerArray.add(2, DetalleOrden.getString(3)); //Direccion
                //innerArray.add(2, DetalleOrden.getString(2)); //Direccion

                //String idCliente = DetalleOrden.getString(1);
                String idCliente = DetalleOrden.getString(2);
                String TipoPago = DetalleOrden.getString(6);
                //String TipoPago = DetalleOrden.getString(3);
                String Estado = "" + DetalleOrden.getInt(8);
                //String Estado = "" + DetalleOrden.getInt(5);
                String NombreCliente = "";
                String EStado = "";

                switch (Estado) {
                    case "0":
                        EStado = "Pendiente";
                        break;
                    case "1":
                        EStado = "Confirmada";
                        break;
                    case "2":
                        EStado = "Despachada";
                        break;
                    case "3":
                        EStado = "Completada";
                        break;
                    case "4":
                        EStado = "Rechazada";
                        break;
                }

                switch (TipoPago) {
                    case "bank_transfer":
                        TipoPago = "Transferencia";
                        break;
                }
                innerArray.add(3, TipoPago); //TipoPago
                innerArray.add(4, "" + DetalleOrden.getInt(7)); //Total
                innerArray.add(5, "" + DetalleOrden.getInt(8)); //Estado
                innerArray.add(6, DetalleOrden.getString(9)); //FFI Fecha Ingreso
                innerArray.add(7, DetalleOrden.getString(11)); //Comentario
                innerArray.add(5, EStado); //Estado
                innerArray.add(6, DetalleOrden.getString(9)); //FFI Fecha Ingreso
                innerArray.add(7, DetalleOrden.getString(11)); //Comentario

                Cursor Cliente = db.rawQuery("SELECT Nombre, Credito from Mv_Cliente where CodigoCliente =" + idCliente, null);
                Cliente.moveToFirst();
                if (!Cliente.isAfterLast()) {
                    do {
                        innerArray.add(8, Cliente.getString(0));
                        innerArray.add(9, ""+Cliente.getInt(1));

                    } while (Cliente.moveToNext());
                }
                Cliente.close();


                outterArray.add(Con, innerArray);
                Con++;

            } while (DetalleOrden.moveToNext());
            DetalleOrden.close();
        }
        return outterArray;

    }



    class MyAdapterCuatro extends RecyclerView.Adapter<MyAdapterCuatro.ViewHolder> {

        public ArrayList<ArrayList<String>> mDatasetex;


        public MyAdapterCuatro(ArrayList<ArrayList<String>> Adpx) {
            super();
            this.mDatasetex = Adpx;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = View.inflate(viewGroup.getContext(), R.layout.detalle_tres, null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

            String stado = mDatasetex.get(i).get(5);
            Log.d("Estado", mDatasetex.get(i).get(5));

            viewHolder.OrdenCompraLista.setText("#" + mDatasetex.get(i).get(0));
            viewHolder.ClienteLista.setText(mDatasetex.get(i).get(8));
            viewHolder.DireccionFacturacionLista.setText(mDatasetex.get(i).get(2));
            viewHolder.TipoPagoLista.setText(mDatasetex.get(i).get(3));
            viewHolder.TotalLista.setText("$" + formateador.format(Double.parseDouble(mDatasetex.get(i).get(4))));
            viewHolder.EstadoLista.setText(mDatasetex.get(i).get(5));
            viewHolder.FechaRegistroLista.setText(mDatasetex.get(i).get(6));
            viewHolder.ConfirmarOrden.setVisibility(View.VISIBLE);
            viewHolder.EliminarOrden.setVisibility(View.VISIBLE);
            viewHolder.EditarOrden.setVisibility(View.VISIBLE);
            viewHolder.VerOrden.setVisibility(View.VISIBLE);


            switch (stado) {
                case "Pendiente":
                    viewHolder.EstadoLista.setBackgroundColor(getResources().getColor(R.color.demoextra_card_background_color1));
                    break;
                case "Confirmada":
                    viewHolder.EstadoLista.setBackgroundColor(getResources().getColor(R.color.demoextra_card_background_color2));
                    viewHolder.ConfirmarOrden.setVisibility(View.GONE);
                    viewHolder.EliminarOrden.setVisibility(View.GONE);
                    viewHolder.EditarOrden.setVisibility(View.GONE);
                    break;
                case "Enviada":
                    viewHolder.EstadoLista.setBackgroundColor(getResources().getColor(R.color.demoextra_card_background_color4));
                    viewHolder.ConfirmarOrden.setVisibility(View.GONE);
                    viewHolder.EliminarOrden.setVisibility(View.GONE);
                    viewHolder.EditarOrden.setVisibility(View.GONE);
                    break;
                case "Completa":
                    viewHolder.EstadoLista.setBackgroundColor(getResources().getColor(R.color.demoextra_card_background_color5));
                    viewHolder.ConfirmarOrden.setVisibility(View.GONE);
                    viewHolder.EliminarOrden.setVisibility(View.GONE);
                    viewHolder.EditarOrden.setVisibility(View.GONE);
                    break;
                case "Despachada":
                    viewHolder.EstadoLista.setBackgroundColor(getResources().getColor(R.color.demoextra_card_background_color6));
                    viewHolder.ConfirmarOrden.setVisibility(View.GONE);
                    viewHolder.EliminarOrden.setVisibility(View.GONE);
                    viewHolder.EditarOrden.setVisibility(View.GONE);
                    viewHolder.VerOrden.setVisibility(View.GONE);
                    break;

            }
            viewHolder.VerOrden.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditarUsuario.putString("VerOrden",mDatasetex.get(i).get(0));
                    EditarUsuario.putString("VerNombre",mDatasetex.get(i).get(8));
                    EditarUsuario.putString("VerCodigo",mDatasetex.get(i).get(1));
                    EditarUsuario.putString("VerCredito",mDatasetex.get(i).get(9));
                    EditarUsuario.apply();

                    Intent N =  new Intent(getActivity(),VerOrden.class);
                    getActivity().startActivity(N);
                }
            });

            viewHolder.EditarOrden.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("! Editar Orden! ")
                            .setContentText("Desea agregar & eliminar productos.!")
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
                                    /*Random Rss = new Random();
                                    int Rr = Math.abs(Rss.nextInt());
                                    String COD = mDatasetex.get(i).get(0);

                                    EditarCliente.putString("IDCliente", "" + mDatasetex.get(i).get(1));
                                    EditarCliente.putString("NombreCliente", mDatasetex.get(i).get(8));
                                    EditarCliente.putString("CreditoCliente", "" + mDatasetex.get(i).get(9));
                                    EditarCliente.putInt("OrdenCompra", Integer.parseInt(COD));
                                    EditarCliente.apply();
                                    Intent I =  new Intent(getActivity(),Catalogo.class);
                                    getActivity().startActivity(I);
                                    getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);*/

                                    int idOrden = Integer.parseInt(Usuario.getString("VerOrden", "0"));
                                    int idCliente = Integer.parseInt(Usuario.getString("VerCodigo", "0"));
                                    int cliente = 0;
                                    ArrayList<ArrayList<String>> outterArray = new ArrayList<>();

                                    int Con = 0;

                                    //Cursor Re = db.rawQuery("SELECT do.idProducto, do.Cantidad, do.Precio, p.Descuento, p.Modelo, o.DireccionFacturacion, o.DireccionEnvio, o.Comentario, o.idCliente FROM Mv_detalleOrden do JOIN Mv_Producto p ON (do.idProducto = p.idProducto) JOIN Mv_Orden o ON (do.idOrden = o.idOrden) WHERE do.idOrden = " + idOrden + "", null);
                                    Cursor Re = db.rawQuery("SELECT do.idProducto, do.Cantidad, do.Precio, p.Descuento, p.Modelo, o.DireccionFacturacion, o.DireccionEnvio, o.Comentario, o.idCliente FROM Mv_detalleOrden do JOIN Mv_Producto p ON (do.idProducto = p.idProducto) JOIN Mv_Orden o ON (do.idOrden = o.idOrden) WHERE do.idOrden = " + mDatasetex.get(i).get(0) + "", null);

                                    if (Re.moveToFirst()) {
                                        do {
                                            ArrayList<String> innerArray = new ArrayList<>();


                                            int idCompraN = Cliente.getInt("OrdenCompra", 0);

                                            Cursor OR = db.rawQuery("SELECT COUNT(*), Cantidad FROM Mv_Compra WHERE idOrdenCompra = " + idCompraN + " AND idProducto = " + Re.getInt(0), null);

                                            if(OR.moveToFirst()){
                                                if(OR.getInt(0) == 0){
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
                                                }else{
                                                    int cantidad = OR.getInt(1) + 1;
                                                    String Sql = "Update Mv_Compra SET Cantidad =" + cantidad + "  where idOrdenCompra =" + idCompraN + "  and idProducto = " + Re.getInt(0) + "";
                                                    Log.d("STRINGSQLINSERTDESC", Sql);
                                                    db.execSQL(Sql);
                                                    cliente = Re.getInt(8);
                                                    outterArray.add(Con, innerArray);
                                                    Con++;
                                                }
                                            }

                                            db.execSQL("Delete from Mv_Orden where idOrden =" + mDatasetex.get(i).get(0) + "");
                                            db.execSQL("Delete from Mv_Compra where idOrdenCompra =" + mDatasetex.get(i).get(0) + "");
                                            db.execSQL("Delete from Mv_DetalleOrden where idOrden = " + mDatasetex.get(i).get(0) + "");

                                            /*String Sql = "INSERT INTO Mv_Compra(idOrdenCompra,idCliente,idProducto,Cantidad,Precio,Descuento,Comentario,NombreProducto)values (" + idCompraN + "," + Re.getInt(8) + "," +
                                                    Re.getInt(0) + "," +
                                                    Re.getInt(1) + ","
                                                    + Re.getInt(2) + "," + Re.getInt(3) +
                                                    ", '" + Re.getString(7) + "','" + Re.getString(4) + "'); ";
                                            Log.d("STRINGSQLINSERTDESC", Sql);
                                            db.execSQL(Sql);
                                            cliente = Re.getInt(8);
                                            outterArray.add(Con, innerArray);
                                            Con++;*/

                                        }
                                        while (Re.moveToNext());
                                    }
                                    Intent I = new Intent(getActivity(), CarroCompleto.class);
                                    I.putExtra("test", cliente);
                                    startActivity(I);
                                    sDialog.cancel();
                                }
                            })
                            .show();
                }
            });

            viewHolder.ConfirmarOrden.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("! Confirmar Orden! ")
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
                                            .setTitleText("Orden Confirmada!")
                                            .setContentText("La orden se ha confirmado.!")
                                            .setConfirmText("Aceptar")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                    //db.execSQL("update Mv_Orden set Estado = 1 where CodigoOrden = " + mDatasetex.get(i).get(0) + "");
                                                    db.execSQL("update Mv_Orden set Estado = 1 where IdOrden = " + mDatasetex.get(i).get(0) + "");
                                                    actualizar();
                                                    sDialog.cancel();

                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();


                }
            });

            viewHolder.EliminarOrden.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
                                    db.execSQL("Delete from Mv_Orden where idOrden =" + mDatasetex.get(i).get(0) + "");
                                    db.execSQL("Delete from Mv_Compra where idOrdenCompra =" + mDatasetex.get(i).get(0) + "");
                                    db.execSQL("Delete from Mv_DetalleOrden where idOrden = " + mDatasetex.get(i).get(0) + "");

                                    sDialog
                                            .setTitleText("Orden eliminada!")
                                            .setContentText("La orden fue eliminada Satisfactoriamente!")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    actualizar();

                                                    sDialog.cancel();

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
        public int getItemCount() {
            return mDatasetex.size();
        }

        //___________________________________________________
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView OrdenCompraLista, ClienteLista, DireccionFacturacionLista, TipoPagoLista, TotalLista, EstadoLista, FechaRegistroLista;
            ImageButton VerOrden, EditarOrden, ConfirmarOrden, EliminarOrden;

            public ViewHolder(View itemView) {
                super(itemView);

                OrdenCompraLista = (TextView) itemView.findViewById(R.id.OrdenCompraLista);
                ClienteLista = (TextView) itemView.findViewById(R.id.ClienteLista);
                DireccionFacturacionLista = (TextView) itemView.findViewById(R.id.DireccionClienteLista);
                TipoPagoLista = (TextView) itemView.findViewById(R.id.TipoPagoLista);
                TotalLista = (TextView) itemView.findViewById(R.id.TotalLista);
                EstadoLista = (TextView) itemView.findViewById(R.id.EstadoLista);
                FechaRegistroLista = (TextView) itemView.findViewById(R.id.FechaRegistroLista);
                VerOrden = (ImageButton) itemView.findViewById(R.id.VerOrden);
                EditarOrden = (ImageButton) itemView.findViewById(R.id.EditarOrden);
                ConfirmarOrden = (ImageButton) itemView.findViewById(R.id.ConfirmarOrden);
                EliminarOrden = (ImageButton) itemView.findViewById(R.id.CancelarOrden);

                Log.d("Tamaño", "" + mDatasetex.size());

            }
        }

    }
}