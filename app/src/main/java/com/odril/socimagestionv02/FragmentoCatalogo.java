package com.odril.socimagestionv02;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapter;
import it.gmariotti.cardslib.library.extra.staggeredgrid.view.CardGridStaggeredView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;


public class FragmentoCatalogo extends Fragment {
    ViewGroup mContainer;


    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;
    ArrayList<Card> cards;
    String DescuentoActivo;
    int Vacio;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    int TotalCompra;
    Metodos M;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmento_catalogo, container, false);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Usuario = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        EditarUsuario = Usuario.edit();
        EditarUsuario.apply();
        initCard();
        M = new Metodos();

        Cliente = getActivity().getSharedPreferences("Cliente", Context.MODE_PRIVATE);
        EditarCliente = Cliente.edit();
        EditarCliente.apply();

        Log.d("ClienteOrden", "" + Cliente.getInt("OrdenCompra", 0));
        Log.d("Cliente", Cliente.getString("IDCliente", "0"));

        FuenteUno = Typeface.createFromAsset(getActivity().getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getActivity().getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getActivity().getAssets(), "fonts/cuatro.ttf");
    }

    private void initCard() {
        String CategoriaID = Usuario.getString("IDSubCategoria", "0");


        DescuentoActivo = "NO";
        SocimaGestion = new BaseDatos(this.getActivity(), "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();
        String[] args = {CategoriaID.trim()};
        //Cursor PC = db.rawQuery("Select idProducto from Mv_categoriaProducto where idCategoria = ?", args);
        Cursor PC = db.rawQuery("Select cp.idProducto from Mv_categoriaProducto cp JOIN Mv_Producto p ON (cp.idProducto = p.idProducto) where cp.idCategoria = ? AND p.Cantidad != 0 ORDER BY SortOrder", args);

        if(PC.getCount() != 0) {
            if (PC.moveToFirst()) {
                cards = new ArrayList<>();
                Vacio = 1;
                do {
                    String FFA = "";
                    String FechaActual = "";
                    String Descuento = "";
                    String FID = "";
                    String FFD = "";

                    int ES = 0, PD = 0, PN = 0, St = 0;
                    int Dz = 0, Des = 0;
                    String Pack = "", Rec = "";

                    String idProducto, Modelo, Descripcion, CodigoProducto, CodigoBodega, StockInicial, Cantidad, Precio, Tam, FFDI, FFDF;
                    idProducto = Modelo = Descripcion = CodigoBodega = CodigoProducto = StockInicial = Cantidad = Precio = Tam = FFA = Descuento = FFDI = FFDF = null;

                    String[] args2 = {"" + PC.getInt(0)};
                    Cursor P = db.rawQuery("Select * from Mv_Producto where idProducto = ? AND Cantidad != 0", args2);
                    //System.out.println("cantidad productos " + P.getCount());
                    if (P.moveToFirst()) {
                        do {
                            Log.d("Producto en categoria", P.getString(1));
                            Modelo = P.getString(1);
                            Descripcion = P.getString(2);
                            CodigoProducto = "" + P.getInt(3);
                            //.-----------------------------------
                            StockInicial = "" + P.getInt(5);
                            Cantidad = "" + P.getInt(6);
                            Precio = "" + P.getInt(7);
                            Double DiezPor = new Double((P.getInt(5) * 10) / 100);
                            if (P.getInt(6) < DiezPor) {
                                ES = 1;
                                St = P.getInt(6);
                            }
                            FFA = P.getString(9);
                            Descuento = "" + P.getInt(10);
                            FFDI = P.getString(11);
                            FFDF = P.getString(12);

                            Calendar cal = new GregorianCalendar();
                            Date date = cal.getTime();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            FechaActual = df.format(date);

                            Date FA = null;
                            Date FAC = null;
                            Date FFID = null;
                            Date FFFD = null;

                            try {
                                FA = df.parse(FFA);
                                FAC = df.parse(FechaActual);
                                FFID = df.parse(FFDI);
                                FFFD = df.parse(FFDF);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar CFA = Calendar.getInstance();
                            Calendar CFAC = Calendar.getInstance();
                            Calendar CFFID = Calendar.getInstance();
                            Calendar CFFFD = Calendar.getInstance();

                            CFA.setTime(FA);
                            CFAC.setTime(FAC);
                            CFFID.setTime(FFID);
                            CFFFD.setTime(FFFD);

                            long Mil1 = CFA.getTimeInMillis();
                            long Mil2 = CFAC.getTimeInMillis();
                            long Mil3 = CFFID.getTimeInMillis();
                            long Mil4 = CFFFD.getTimeInMillis();

                            long Diferencia = Mil1 - Mil2;
                            long Diferencia2 = Mil4 - Mil3;
                            long Diferencia3 = Mil2 - Mil3;


                            long dDias = Math.abs(Diferencia / (24 * 60 * 60 * 1000));
                            long dDias2 = Math.abs(Diferencia2 / (24 * 60 * 60 * 1000));
                            long dDias3 = Math.abs(Diferencia3 / (24 * 60 * 60 * 1000));


                            if (dDias <= 10) {
                                PN = 1;
                                Dz = (int) dDias;
                            }
                            long total = dDias2 - dDias3;

                            if (Integer.parseInt(Descuento) != 0) {
                                //if (total > 0) {
                                PD = 1;
                                Des = (int) total;
                                DescuentoActivo = "SI";
                            } else {
                                DescuentoActivo = "NO";
                            }

                            String[] args3 = {"" + PC.getInt(0)};
                            Cursor D = db.rawQuery("Select idAtributo, Atributo, Descripcion from Mv_DetalleProducto where idProducto = ? ORDER BY idAtributo Asc", args3);
                            if (D.moveToFirst()) {
                                do {
                                    if (D.getInt(0) == 25 || D.getInt(0) == 20) {
                                        Log.d("INFOMACION DEL ATRIBUTO", "Codigo: " + D.getInt(0) + " - Atributo: " + D.getString(1) + " - Descripcion: " + D.getString(2));

                                        if (D.getInt(0) == 20) {
                                            Pack = D.getString(2);
                                        } else if (D.getInt(0) == 25) {
                                            Rec = D.getString(2);
                                        }
                                    }
                                } while (D.moveToNext());
                            }
                            D.close();

                            SquareGridCard card = new SquareGridCard(getActivity());

                            card.Modelo = Modelo;
                            card.EstadoStock = ES;
                            card.ProductoDescuento = PD;
                            card.ProductoNuevo = PN;
                            card.Des = Des;
                            card.Dias = Dz;
                            card.St = St;
                            card.CodigoProducto = CodigoProducto;
                            card.Desc = Descripcion;
                            card.Descuento = Descuento;
                            card.Precio = Precio;
                            card.Pack = Pack;
                            card.CantidadRecomendada = Rec;
                            card.CantidadSk = Cantidad;
                            card.StockInicial = StockInicial;

                            card.color = R.color.demoextra_card_background_color2;
                            card.init(CodigoProducto);
                            cards.add(card);

                        } while (P.moveToNext());
                    }

                } while (PC.moveToNext());
            } else {
                Vacio = 0;
            }
        }
        PC.close();

        CardGridStaggeredArrayAdapter mCardArrayAdapter = new CardGridStaggeredArrayAdapter(getActivity(), cards);

        FuenteUno = Typeface.createFromAsset(getActivity().getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getActivity().getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getActivity().getAssets(), "fonts/cuatro.ttf");

        TextView Categoria = (TextView) getActivity().findViewById(R.id.Categoria);
        String CategoriaPadre = Usuario.getString("NombreCategoriaPadre", "SinCategoria");
        Categoria.setText(CategoriaPadre);
        Categoria.setTypeface(FuenteUno);

        TextView SubCategoria = (TextView) getActivity().findViewById(R.id.SubCategoria);
        String SubCategoriaHijo = Usuario.getString("NombreSubCategoria", "SinSubCategoria");
        SubCategoria.setText(SubCategoriaHijo);
        SubCategoria.setTypeface(FuenteUno);

        CardGridStaggeredView mGridView = (CardGridStaggeredView) getActivity().findViewById(R.id.carddemo_extras_grid_stag);
        mCardArrayAdapter.setInnerViewTypeCount(3);
        mGridView.setColumnCountLandscape(3);

        if (Vacio != 0) {
            mGridView.setAdapter(mCardArrayAdapter);
        }
    }


    public class SquareGridCard extends Card {

        protected String Modelo;
        protected String Desc;
        protected String CantidadRecomendada;
        protected String Pack;
        protected String Precio;
        protected String Descuento;
        protected String CantidadSk;
        protected String StockInicial;
        protected int EstadoStock, ProductoDescuento, ProductoNuevo, color, Dias, Des, St;
        String CodigoProducto;

        LinearLayout Ly;
        TextView tx;

        public SquareGridCard(Context context) {
            super(context, R.layout.test);
        }

        private void init(final String Codigo) {
            SquareGridThumb thumbnail = new SquareGridThumb(getContext(), color, Codigo);
            thumbnail.setExternalUsage(true);
            addCardThumbnail(thumbnail);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            int Paso = 0;
            //if (EstadoStock == 1) {

            int porcentaje = (Integer.parseInt(StockInicial)*10/100);
            if (Integer.parseInt(CantidadSk) < porcentaje) {
                Ly = (LinearLayout) view.findViewById(R.id.LyEstados);
                Ly.setBackgroundColor((Color.parseColor("#ffa3200a")));
                tx = (TextView) view.findViewById(R.id.TextoEstado);
                tx.setText("QUEDAN " + CantidadSk);
            } else if (ProductoDescuento == 1) {
                Ly = (LinearLayout) view.findViewById(R.id.LyEstados);
                Ly.setBackgroundColor((Color.parseColor("#ffd67010")));
                tx = (TextView) view.findViewById(R.id.TextoEstado);
                tx.setText("PRODUCTO CON DESCUENTO");
                //tx.setText("DESCUENTO DISPONIBLE POR " + Des + " DIAS");
                Paso = 1;

            } else if (ProductoNuevo == 1) {
                Ly = (LinearLayout) view.findViewById(R.id.LyEstados);
                Ly.setBackgroundColor((Color.parseColor("#ff0600a3")));
                tx = (TextView) view.findViewById(R.id.TextoEstado);
                tx.setText("PRODUCTO NUEVO ");
                Log.d("PRO", "PRODUCTO NUEVO");

            }
            TextView PCodigo = (TextView) view.findViewById(R.id.PCodigo);
            PCodigo.setText("#" + CodigoProducto);
            PCodigo.setTypeface(FuenteUno);
            TextView PModelo = (TextView) view.findViewById(R.id.PModelo);
            PModelo.setText(Modelo);
            PCodigo.setTypeface(FuenteUno);
            TextView PDesc = (TextView) view.findViewById(R.id.PDesc);
            PDesc.setText(Desc);
            //System.out.println("dato desc " + Desc );
            PCodigo.setTypeface(FuenteUno);
            Log.d("" + CodigoProducto, "PACK :" + CantidadRecomendada);

            if (CantidadRecomendada.equals("")) {
                CantidadRecomendada = "" + 0;
            }

            if (Integer.parseInt(CantidadRecomendada) > 0 && CantidadRecomendada != null) {
                LinearLayout Rc = (LinearLayout) view.findViewById(R.id.Rc);
                Rc.setVisibility(View.VISIBLE);
                TextView CRec = (TextView) view.findViewById(R.id.CantidadRecomendada);
                //CRec.setText("Cantidad Recomendada: " + CantidadRecomendada);
                CRec.setText("Empaque: " + String.valueOf(CantidadRecomendada));
                CRec.setTypeface(FuenteUno);
            } else {
                LinearLayout Rc = (LinearLayout) view.findViewById(R.id.Rc);
                Rc.setVisibility(View.GONE);
            }

            Log.d("" + CodigoProducto, "PACK :" + Pack);
            if (Pack.equals("")) {
                Pack = "" + 0;
            }

            /*System.out.println("dato pack " + Pack + " del producto " + CodigoProducto);
            System.out.println("dato pack " + Pack.trim().length());
            System.out.println("dato pack " + Pack.length());*/

            //int Pack1 = Integer.parseInt(Pack);

            if (Integer.parseInt(Pack.trim()) > 0 && Pack != null) {
            //if (Pack1 > 0 && Pack != null) {
                TextView CRec = (TextView) view.findViewById(R.id.Pack);
                CRec.setText("Pack: " + Pack);
                CRec.setTypeface(FuenteUno);
                LinearLayout Lc = (LinearLayout) view.findViewById(R.id.LcApr);
                Lc.setVisibility(View.VISIBLE);
                LinearLayout Pk = (LinearLayout) view.findViewById(R.id.Pck);
                Pk.setVisibility(View.VISIBLE);
                TextView Aproximado = (TextView) view.findViewById(R.id.Aproximado);
                int p = Integer.parseInt(Precio) / Integer.parseInt(Pack.trim());
                Aproximado.setText("$" + p + " C/U Aprox");
                Aproximado.setTypeface(FuenteUno);
            } else {
                LinearLayout Lc = (LinearLayout) view.findViewById(R.id.LcApr);
                Lc.setVisibility(View.GONE);
                LinearLayout Pk = (LinearLayout) view.findViewById(R.id.Pck);
                Pk.setVisibility(View.GONE);

            }

            TextView PPrecio = (TextView) view.findViewById(R.id.Precio);

            DecimalFormat formateador = new DecimalFormat("###,###.##");
            //PPrecio.setText("$" + Precio);
            PPrecio.setText("$" + formateador.format(Double.parseDouble(Precio)));
            PPrecio.setTypeface(FuenteUno);

            //if (Integer.parseInt(Descuento) > 0 && Paso != 0) {
            if (Integer.parseInt(Descuento) > 0 && Paso == 1) {
                TextView PDescuento = (TextView) view.findViewById(R.id.Descuento);
                PDescuento.setText("Valor $" + formateador.format(Double.parseDouble(Descuento)) + " dscto");
                PDescuento.setTypeface(FuenteCuatro);
            }


            Button btnMas = (Button) view.findViewById(R.id.BtnMas);
            Button btnMenos = (Button) view.findViewById(R.id.BtnMenos);
            final EditText Cantidad = (EditText) view.findViewById(R.id.CantidadCompra);

            if (Cantidad.getText().toString() != "1") {
                Cantidad.setText("1");
            }


            Cantidad.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!(Cantidad.getText().toString().equals("") | Cantidad.getText().toString().isEmpty())) {
                        if (Integer.parseInt(Cantidad.getText().toString()) > Integer.parseInt(CantidadSk)) {
                            Cantidad.setText("" + CantidadSk);
                        } else if (Integer.parseInt(Cantidad.getText().toString()) < 1) {
                            //} else if (Integer.parseInt(Cantidad.getText().toString()) != 1) {
                            Cantidad.setText("1");
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            Cantidad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b && (Cantidad.getText().toString().equals("") | Cantidad.getText().toString().isEmpty())) {
                        Cantidad.setText("1");
                    }
                }
            });

            btnMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Cantidad.getText().toString().isEmpty() | Cantidad.getText().toString().equals("")) {
                        Cantidad.setText("1");
                    }
                    int cantidad = Integer.parseInt(Cantidad.getText().toString());

                    if (cantidad < Integer.parseInt(CantidadSk)) {
                        Cantidad.setText("" + (cantidad + 1));
                    }
                }
            });


            btnMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Cantidad.getText().toString().isEmpty() | Cantidad.getText().toString().equals("")) {
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
                    if (Cantidad.getText().toString().isEmpty() | Cantidad.getText().toString().equals("")) {
                        Cantidad.setText("1");
                    }
                    Cantidad.setText("" + total);
                    return true;
                }
            });
            btnMas.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (Cantidad.getText().toString().isEmpty() | Cantidad.getText().toString().equals("")) {
                        Cantidad.setText("" + CantidadSk);
                    }
                    Cantidad.setText("" + CantidadSk);
                    return true;
                }
            });

            Button btnComprar = (Button) view.findViewById(R.id.BtnComprar);
            btnComprar.setTypeface(FuenteCuatro);
            btnComprar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean v = M.esIgual(Cliente.getString("IDCliente", "0"), "0");

                    if (!(Cantidad.getText().toString().isEmpty() | Cantidad.getText().toString().equals(""))) {

                        if ((Cliente.getInt("OrdenCompra", 0) == 0) | (v)) {
                            Intent i = new Intent(getActivity(), Cliente.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                            getActivity().finish();

                        } else {
                            String idCliente = Cliente.getString("IDCliente", "0");
                            int OrdenC = Cliente.getInt("OrdenCompra", 0);

                            Cursor BuscarProducto = db.rawQuery("Select  * from Mv_Compra where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")", null);

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
                                        LayoutInflater li = getActivity().getLayoutInflater();
                                        View layout = li.inflate(R.layout.custom_toast_error_producto,
                                                (ViewGroup) getActivity().findViewById(R.id.Custom));
                                        layout.setMinimumHeight(80);
                                        TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                        MensajeToast.setText("[ Sin Stock " + Modelo + "   ]");
                                        Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                                        ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                        ts.setView(layout);
                                        ts.show();
                                    } else {
                                        if (DescuentoActivo.equals("SI")) {
                                        //if (Descuento != "0") {
                                            int s = Integer.parseInt(Cantidad.getText().toString()) + BuscarProducto.getInt(3);
                                            if (s >= Integer.parseInt(CantidadSk)) {
                                                String Sql = "Update Mv_Compra SET Cantidad =" + Integer.parseInt(CantidadSk) + ", Descuento =" + Integer.parseInt(Descuento) + ", Comentario ='Producto con descuento de " + Descuento + "' where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")";
                                                Log.d("SQLACTUALIZACIONDESC", Sql);
                                                LayoutInflater li = getActivity().getLayoutInflater();
                                                View layout = li.inflate(R.layout.custom_toast_error_producto,
                                                        (ViewGroup) getActivity().findViewById(R.id.Custom));
                                                layout.setMinimumHeight(80);
                                                TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                                MensajeToast.setText("[ Sin Stock " + Modelo + "   ]");
                                                Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                                                ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                                ts.setView(layout);
                                                ts.show();
                                                db.execSQL(Sql);
                                            } else {
                                                String Sql = "Update Mv_Compra SET Cantidad =" + s + ", Descuento =" + Integer.parseInt(Descuento) + ", Comentario ='Producto con descuento de " + Descuento + "' where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")";
                                                Log.d("SQLACTUALIZACIONDESC", Sql);
                                                db.execSQL(Sql);
                                                LayoutInflater li = getActivity().getLayoutInflater();
                                                View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                        (ViewGroup) getActivity().findViewById(R.id.Custom));
                                                layout.setMinimumHeight(80);
                                                TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                                MensajeToast.setText("[ " + Cantidad.getText().toString() + "  " + Modelo + " ] Agregados");
                                                Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                                                ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                                ts.setView(layout);
                                                ts.show();
                                            }
                                        } else {
                                            int s = Integer.parseInt(Cantidad.getText().toString()) + BuscarProducto.getInt(3);
                                            if (s >= Integer.parseInt(CantidadSk)) {
                                                String Sql = "Update Mv_Compra SET Cantidad =" + Integer.parseInt(CantidadSk) + " where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")";
                                                Toast.makeText(getActivity(), "Se ha vendido el stock completo.", Toast.LENGTH_LONG).show();
                                                Log.d("SQLACTUALIZACIONDESC", Sql);
                                                db.execSQL(Sql);
                                            } else {
                                                String Sql = "Update Mv_Compra SET Cantidad =" + s + " where(( idCliente =" + idCliente + " and idOrdenCompra =" + OrdenC + " ) and idProducto =" + CodigoProducto + ")";
                                                Log.d("SQLACTUALIZACIONDESC", Sql);
                                                db.execSQL(Sql);
                                                LayoutInflater li = getActivity().getLayoutInflater();
                                                View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                                        (ViewGroup) getActivity().findViewById(R.id.Custom));
                                                layout.setMinimumHeight(80);
                                                TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                                MensajeToast.setText("[ " + Cantidad.getText().toString() + "  " + Modelo + " ] Agregados");
                                                Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                                                ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                                ts.setView(layout);
                                                ts.show();
                                            }
                                        }
                                    }
                                } while (BuscarProducto.moveToNext());
                                BuscarProducto.close();
                            } else {
                                int price, desc;
                                price = Integer.parseInt(Precio);
                                desc = Integer.parseInt(Descuento);
                                //if (DescuentoActivo.equals("SI")) {
                                if (desc != 0) {
                                    String Sql = "INSERT INTO Mv_Compra(idOrdenCompra,idCliente,idProducto,Cantidad,Precio,Descuento,Comentario,NombreProducto)values (" + OrdenC + "," + Integer.parseInt(idCliente) + "," +
                                            Integer.parseInt(CodigoProducto) + "," +
                                            Integer.parseInt(Cantidad.getText().toString()) + ","
                                            + Integer.parseInt(Descuento) + "," + 0 +
                                            ",'Producto con Descuento de " + Descuento + "','" + Modelo + "'); ";
                                    Log.d("STRINGSQLINSERTDESC", Sql);
                                    db.execSQL(Sql);
                                    LayoutInflater li = getActivity().getLayoutInflater();
                                    View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                            (ViewGroup) getActivity().findViewById(R.id.Custom));
                                    layout.setMinimumHeight(80);
                                    TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                    MensajeToast.setText("[ " + Cantidad.getText().toString() + "  " + Modelo + " ] Agregados");
                                    Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
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
                                    LayoutInflater li = getActivity().getLayoutInflater();
                                    View layout = li.inflate(R.layout.custom_toast_agregado_producto,
                                            (ViewGroup) getActivity().findViewById(R.id.Custom));
                                    layout.setMinimumHeight(80);
                                    TextView MensajeToast = (TextView) layout.findViewById(R.id.TxToast);
                                    MensajeToast.setText("[ " + Cantidad.getText().toString() + "  " + Modelo + " ] Agregados");
                                    Toast ts = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                                    ts.setGravity(Gravity.TOP | Gravity.RIGHT, 85, 350);
                                    ts.setView(layout);
                                    ts.show();
                                }
                            }
                        }
                    }
                }
            });
        }

        class SquareGridThumb extends CardThumbnail {

            int color;
            String Codigo;

            public SquareGridThumb(Context context, int color, String Codigo) {
                super(context);
                this.color = color;
                this.Codigo = Codigo;
            }


            @Override
            public void setupInnerViewElements(ViewGroup parent, View viewImage) {

                ImageView image = ((ImageView) viewImage);
                if (color > 0) {
                    image.setBackgroundColor(getResources().getColor(color));
                }

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

                try {
                    Picasso.with(getActivity())
                            .load("file://" + Fondo)
                            .fit()
                            .error(R.drawable.sinimagen)
                            .into(image);
                    //Picasso.with(getActivity()).load("file://"+Fondo).resize(100,500).centerCrop().error(R.drawable.sinimagen).into(image);
                    //Picasso.with(getActivity()).load("file://"+Fondo).resizeDimen(50, 100).centerInside().error(R.drawable.sinimagen).into(image);
                } catch (Exception e) {
                    Log.w("ERROR IMG", e);

                }


                image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        EditarUsuario.putString("Producto", CodigoProducto);
                        EditarUsuario.commit();
                        Intent I = new Intent(getActivity(), DetalleProducto.class);
                        getActivity().startActivity(I);
                        return true;
                    }
                });
            }
        }

    }


}

