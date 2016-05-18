package com.odril.socimagestionv02;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class Login extends Activity {

    //DECLARACION DE VARIABLES ----------------------------------------------------------------

    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    EditText EdUsuario, EdClave;
    Button BtnIngresar;

    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;
    Sistema SS;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SS = new Sistema();
        SS.finish();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //INICIALIZACION DE VARIABLES ----------------------------------------------------------------
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();

        FuenteUno = Typeface.createFromAsset(getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/tres.ttf");
        FuenteCuatro = Typeface.createFromAsset(getAssets(), "fonts/cuatro.ttf");


        EdUsuario = (EditText) findViewById(R.id.EdUsuario);
        EdClave = (EditText) findViewById(R.id.EdClave);
        BtnIngresar = (Button) findViewById(R.id.BtnIngresar);
        EdUsuario.setTypeface(FuenteDos);
        EdClave.setTypeface(FuenteDos);
        BtnIngresar.setTypeface(FuenteCuatro);

        YoYo.with(Techniques.SlideInUp)
                .duration(2000)
                .playOn(findViewById(R.id.Login));


        if (Usuario.getString("Login", "NO").equals("NO")) {

            BtnIngresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (EdUsuario.getText().toString().equals("") || EdClave.getText().toString().equals("")) {
                        if (EdUsuario.getText().toString().equals("") & EdClave.getText().toString().equals("")) {
                            Toast.makeText(Login.this, "El Usuario y la Clave no pueden estar en blanco.", Toast.LENGTH_SHORT).show();
                        } else if (EdClave.getText().toString().equals("")) {
                            Toast.makeText(Login.this, "La Clave se encuentra en blanco.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "El Usuario se encuentra en blanco.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        BaseDatos SocimaGestion = new BaseDatos(getApplicationContext(), "SocimaGestion", null, 1);
                        SQLiteDatabase db = SocimaGestion.getWritableDatabase();
                        if (db != null) {
                            String[] args = {EdUsuario.getText().toString(), EdClave.getText().toString()};
                            Cursor C = db.rawQuery("Select CodigoVendedor, Nombre, Email, Meta,Cargo, Estado from Mv_Vendedor where Usuario = ? and clave = ?", args);
                            //Cursor C = db.rawQuery("Select CodigoVendedor, Nombre, Email, Meta,Cargo from Mv_Vendedor where Usuario = ? and clave = ? AND Estado == 1", args);
                            if (C.moveToFirst()) {
                                if(C.getInt(5) == 1){
                                    do {

                                        EditarUsuario.putInt("CodigoVendedor", C.getInt(0));
                                        EditarUsuario.putString("Nombre", C.getString(1));
                                        EditarUsuario.putString("Clave", EdClave.getText().toString());
                                        EditarUsuario.putString("Email", C.getString(2));
                                        EditarUsuario.putInt("Meta", C.getInt(3));
                                        EditarUsuario.putInt("Cargo", C.getInt(4));
                                        EditarUsuario.putString("Login", "SI");
                                        EditarUsuario.apply();
                                        startActivity(new Intent(Login.this, Sistema.class)); //cambiar
                                        finish();
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                    } while (C.moveToNext());
                                }else{
                                    Toast.makeText(Login.this, "Su cuenta ha sido Desactivada como Vendedor", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Login.this, "Error en el Usuario o Clave", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("EstadoBase", "Base de datos no encontrada");
                            Log.d("Acceso", "Denegado");
                        }
                    }
                }
            });

        } else {
            Intent i = new Intent(Login.this, Sistema.class);
            startActivity(i);
            this.finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        }


    }


    //FUNCIONES PRIVADAS  ----------------------------------------------------------------

}
