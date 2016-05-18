package com.odril.socimagestionv02;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.takwolf.android.lock9.Lock9View;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SeguridadCliente extends ActionBarActivity {


    SharedPreferences Usuario;
    SharedPreferences.Editor EditarUsuario;
    String ClaveUno = "", ClaveDos = "", ClaveFinal = "";
    int CodigoVendedor, Patron, NumeroError;
    EditText RecuperarClave;
    Lock9View lock9View;
    TextView Nombre, Recuperar;
    Thread thread;
    Button BtnRecuperar;
    TextView Titulo;
    Sistema ClaseSistema;
    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguridad_cliente);
        lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        Titulo = (TextView) findViewById(R.id.Titulo);

        Recuperar = (TextView) findViewById(R.id.Recuperar);
        BtnRecuperar = (Button) findViewById(R.id.BtnRecuperar);
        RecuperarClave = (EditText) findViewById(R.id.RecuperarClave);
        RecuperarClave.setVisibility(View.GONE);
        BtnRecuperar.setVisibility(View.GONE);

        FuenteUno = Typeface.createFromAsset(getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/tres.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/cuatro.ttf");


        Titulo.setTypeface(FuenteUno);
        Recuperar.setTypeface(FuenteCuatro);
        RecuperarClave.setTypeface(FuenteDos);
        BtnRecuperar.setTypeface(FuenteCuatro);


        Recuperar.setVisibility(View.GONE);


        Usuario = getSharedPreferences("Usuario", MODE_PRIVATE);
        EditarUsuario = Usuario.edit();
        CodigoVendedor = Usuario.getInt("CodigoVendedor", 000);

        Patron = Usuario.getInt("Patron" + CodigoVendedor, 000);

        ClaseSistema = new Sistema();



        Comprobar:
        if (Patron == 000) {
            Recuperar.setText("");
            new SweetAlertDialog(this)
                    .setTitleText("Configurando Seguridad!")
                    .setContentText("Para aumentar la seguridad de datos debes crear un patron de seguridad.!")
                    .show();

            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    do {
                        lock9View.setCallBack(new Lock9View.CallBack() {
                            @Override
                            public void onFinish(String password) {
                                ClaveUno = password;
                                if (!ClaveUno.equals("")) {
                                    new SweetAlertDialog(SeguridadCliente.this)
                                            .setTitleText("Una vez màs.!")
                                            .show();
                                }

                            }
                        });
                    } while (ClaveUno == "");
                    do {
                        lock9View.setCallBack(new Lock9View.CallBack() {
                            @Override
                            public void onFinish(String password) {
                                ClaveDos = password;
                                if (!ClaveDos.equals("")) {
                                    if (ClaveUno.equals(ClaveDos)) {
                                        ClaveFinal = ClaveDos;
                                        EditarUsuario.putInt("Patron" + CodigoVendedor, Integer.parseInt(ClaveDos.toString()));
                                        EditarUsuario.apply();

                                        new SweetAlertDialog(SeguridadCliente.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Excelente!")
                                                .setContentText("Tu patron de seguridad se ha creado.!")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.cancel();

                                                        Intent i = new Intent(SeguridadCliente.this, PerfilCliente.class);
                                                        startActivity(i);
                                                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                                                        finish();

                                                    }
                                                })
                                                .show();


                                    } else {
                                        ClaveDos = "";
                                        new SweetAlertDialog(SeguridadCliente.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops...")
                                                .setContentText("Los patrones no son iguales, vuelve a confirmar.!")
                                                .show();
                                    }
                                }

                            }
                        });
                    } while (ClaveDos == "");

                }
            });

            thread.start();

        } else {


            Thread Login = new Thread(new Runnable() {
                @Override
                public void run() {
                    lock9View.setCallBack(new Lock9View.CallBack() {
                        @Override
                        public void onFinish(String password) {

                            if (Patron == Integer.parseInt(password.toString())) {
                                Intent i = new Intent(SeguridadCliente.this, PerfilCliente.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                                finish();
                            } else {
                                new SweetAlertDialog(SeguridadCliente.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Error!")
                                        .setContentText("El Patron no fue el correcto, vuelve a intentarlo!")
                                        .show();
                                Recuperar.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                }
            });

            Login.start();

            Recuperar.setText("RECUPERAR PATRON");
            Recuperar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecuperarClave.setVisibility(View.VISIBLE);
                    BtnRecuperar.setVisibility(View.VISIBLE);
                }
            });

            BtnRecuperar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (RecuperarClave.getText().toString().equals(Usuario.getString("Clave", ""))) {
                        EditarUsuario.putInt("Patron" + CodigoVendedor, 000);
                        EditarUsuario.apply();
                        RecuperarClave.setVisibility(View.GONE);

                        Intent i = new Intent(SeguridadCliente.this, SeguridadCliente.class);
                        startActivity(i);

                        finish();
                    } else {
                        if (NumeroError > 2) {
                            new SweetAlertDialog(SeguridadCliente.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Lo sentimos..")
                                    .setContentText("Debes volver a entrar al sistema.!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            EditarUsuario.remove("Login");
                                            EditarUsuario.apply();
                                            sDialog.cancel();
                                            ClaseSistema.finish();
                                            Intent i = new Intent(SeguridadCliente.this, Login.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                            finish();


                                        }
                                    })
                                    .show();
                        } else if (NumeroError >= 0) {
                            new SweetAlertDialog(SeguridadCliente.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("La clave es erronea, vuelve a intentar.!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            NumeroError = NumeroError + 1;
                                            sDialog.cancel();


                                        }
                                    })
                                    .show();
                        }
                    }


                }
            });


        }


    }

}
