package com.odril.socimagestionv02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Random;

import Adaptadores.BuscarCliente;


public class Cliente extends ActionBarActivity {


    Typeface FuenteUno;
    Typeface FuenteDos;
    Typeface FuenteTres;
    Typeface FuenteCuatro;
    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    SharedPreferences Cliente;
    SharedPreferences.Editor EditarCliente;
    LinearLayout Contenedor;
    TextView TituloBuscarCliente;
    AutoCompleteTextView BuscarCliente;
    Button SeleccionarCliente, Catalogo, Salir;
    Adaptadores.BuscarCliente AdaptadorBusquedaCliente;
    Cursor CursorCliente;
    BaseDatos BDSocima;
    SQLiteDatabase DB;
    ImageView TercerBanner;
    ImageView SegundoBanner;
    ImageView PrimerBanner;
    static Activity Fs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        Fs = this;
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();
        Cliente = getSharedPreferences("Cliente", MODE_PRIVATE);
        EditarCliente = Cliente.edit();
        BDSocima = new BaseDatos(this, "SocimaGestion", null, 1);
        DB = BDSocima.getWritableDatabase();
        Metodos M = new Metodos();
        EditarUsuario.remove("DetalleActivo");
        EditarUsuario.apply();

        FuenteUno = Typeface.createFromAsset(getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/tres.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/cuatro.ttf");

        TituloBuscarCliente = (TextView) findViewById(R.id.TituloBuscarCliente);
        BuscarCliente = (AutoCompleteTextView) findViewById(R.id.txtSearch);
        SeleccionarCliente = (Button) findViewById(R.id.SelecionarCliente);
        Catalogo = (Button) findViewById(R.id.irCatalogo);
        Salir = (Button) findViewById(R.id.Salir);
        Contenedor = (LinearLayout) findViewById(R.id.ContenedorBotones);
        Contenedor.setVisibility(View.GONE);

        boolean v = M.esIgual(Cliente.getString("IDCliente", "0"), "0");

        if (!v) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            Contenedor.setVisibility(View.VISIBLE);
            BuscarCliente.setText(Cliente.getString("NombreCliente", ""));
            BuscarCliente.setEnabled(false);
        }


        Catalogo.setTypeface(FuenteDos);
        Salir.setTypeface(FuenteDos);
        SeleccionarCliente.setTypeface(FuenteCuatro);
        BuscarCliente.setTypeface(FuenteCuatro);
        TituloBuscarCliente.setTypeface(FuenteDos);

        Usuario.getInt("CodigoVendedor", 0);

        globals g = globals.getInstance();
        g.setVendedor(Usuario.getInt("CodigoVendedor", 0));
        int idVendedor = g.getIdVendedor();

        CursorCliente = BDSocima.BuscarCliente("",0);
        startManagingCursor(CursorCliente);
        AdaptadorBusquedaCliente = new BuscarCliente(this, CursorCliente);
        BuscarCliente.setThreshold(3);
        BuscarCliente.setAdapter(AdaptadorBusquedaCliente);
        Salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.SlideOutDown)
                        .duration(1250)
                        .playOn(findViewById(R.id.ContenedorBotones));
                BuscarCliente.setEnabled(true);
                BuscarCliente.setText("");
                BuscarCliente.setHint("Buscar Cliente");
                EditarCliente.remove("IDCliente");
                EditarCliente.remove("OrdenCompra");
                EditarCliente.apply();
                BuscarCliente.requestFocus();
            }
        });


        YoYo.with(Techniques.SlideInUp)
                .duration(2000)
                .playOn(findViewById(R.id.ContenedorBotones));


        Catalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cliente.this, com.odril.socimagestionv02.Catalogo.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                EditarUsuario.putString("ClienteActivo", "SI");
                EditarUsuario.apply();
            }
        });

        SeleccionarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cliente.this, SeguridadCliente.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

            }
        });

        BuscarCliente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                BuscarCliente.setText(c.getString(1));

                int IDCliente = c.getInt(0);
                String NombreCliente = c.getString(1);
                int Credito = c.getInt(2);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(BuscarCliente.getWindowToken(), 0);

                Random Rss = new Random();
                int Rr = Math.abs(Rss.nextInt());

                EditarCliente.putString("IDCliente", "" + IDCliente);
                EditarCliente.putString("NombreCliente", NombreCliente);
                EditarCliente.putString("CreditoCliente", "" + Credito);
                EditarCliente.putInt("OrdenCompra", Rr);
                EditarCliente.apply();

                Contenedor.setVisibility(View.VISIBLE);

                BuscarCliente.setEnabled(false);
                YoYo.with(Techniques.SlideInUp)
                        .duration(1000)
                        .playOn(findViewById(R.id.ContenedorBotones));
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();

        stopManagingCursor(CursorCliente);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        EditarUsuario.remove("ClienteActivo");
        EditarUsuario.apply();
        EditarUsuario.remove("DetalleActivo");
        EditarUsuario.apply();

    }
}
