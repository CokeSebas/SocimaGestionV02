package com.odril.socimagestionv02;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SistemaCliente extends ActionBarActivity {
    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    MyAdapterDiez adapter;
    RecyclerView recyclerViewDiez;
    ArrayList<ArrayList<String>> datos;
    DecimalFormat formateador = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sistema_cliente);

        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();

        Cliente = getSharedPreferences("Cliente", MODE_PRIVATE);
        EditarCliente = Cliente.edit();
        EditarUsuario.apply();
        SocimaGestion = new BaseDatos(this, "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();

    }

    void actualizar() {

        datos.clear();
        datos.addAll(apl());
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onBackPressed() {
    }

    ArrayList<ArrayList<String>> apl() {


        ArrayList<ArrayList<String>> outterArray = new ArrayList<>();

        int Con = 0;
        Cursor DetalleOrden;

            DetalleOrden = db.rawQuery("SELECT * from Mv_Orden where idCliente =" + Integer.parseInt(Cliente.getString("IDCliente", "0")) + " order by FFI DESC , Estado ASC", null);


        DetalleOrden.moveToFirst();
        if (!DetalleOrden.isAfterLast()) {
            do {
                ArrayList<String> innerArray = new ArrayList<>();
                innerArray.add(0, DetalleOrden.getString(0)); //OR
                innerArray.add(1, DetalleOrden.getString(1)); //IDCLIENTE
                innerArray.add(2, DetalleOrden.getString(2)); //Direccion


                String idCliente = DetalleOrden.getString(1);
                String TipoPago = DetalleOrden.getString(3);
                String Estado = "" + DetalleOrden.getInt(5);
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
                innerArray.add(4, "" + DetalleOrden.getInt(4)); //Total
                innerArray.add(5, "" + DetalleOrden.getInt(5)); //Estado
                innerArray.add(6, DetalleOrden.getString(6)); //FFI Fecha Ingreso
                innerArray.add(7, DetalleOrden.getString(8)); //Comentario
                innerArray.add(5, EStado); //Estado
                innerArray.add(6, DetalleOrden.getString(6)); //FFI Fecha Ingreso
                innerArray.add(7, DetalleOrden.getString(8)); //Comentario

                Cursor Cliente = db.rawQuery("SELECT Nombre, Credito from Mv_Cliente where CodigoCliente =" + idCliente, null);
                Cliente.moveToFirst();
                if (!Cliente.isAfterLast()) {
                    do {
                        innerArray.add(8, Cliente.getString(0));
                        innerArray.add(9, "" + Cliente.getInt(1));

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

    class MyAdapterDiez extends RecyclerView.Adapter<MyAdapterDiez.ViewHolder> {

        public ArrayList<ArrayList<String>> mDatasetex;


        public MyAdapterDiez(ArrayList<ArrayList<String>> Adpx) {
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
                    viewHolder.EstadoLista.setBackgroundColor(getResources().getColor(R.color.demoextra_card_background_color4));
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

                    Intent N =  new Intent(SistemaCliente.this,VerOrden.class);
                    startActivity(N);
                }
            });

            viewHolder.EditarOrden.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SweetAlertDialog(SistemaCliente.this, SweetAlertDialog.WARNING_TYPE)
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

                                        }
                                        while (Re.moveToNext());
                                    }

                                    db.execSQL("Delete from Mv_Orden where idOrden =" + mDatasetex.get(i).get(0) + "");
                                    db.execSQL("Delete from Mv_Compra where idOrdenCompra =" + mDatasetex.get(i).get(0) + "");
                                    db.execSQL("Delete from Mv_DetalleOrden where idOrden = " + mDatasetex.get(i).get(0) + "");

                                    Intent I = new Intent(SistemaCliente.this, CarroCompleto.class);
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
                    new SweetAlertDialog(SistemaCliente.this, SweetAlertDialog.WARNING_TYPE)
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



                    new SweetAlertDialog(SistemaCliente.this, SweetAlertDialog.WARNING_TYPE)
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
