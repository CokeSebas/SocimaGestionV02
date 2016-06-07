package com.odril.Socima_Gestion;

import android.app.DownloadManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;


public class Metodos {


    public Context context;
    public BaseDatos SocimaGestion;
    public SQLiteDatabase db;


    Metodos() {

    }

    public Metodos(BaseDatos SocimaGestion, SQLiteDatabase db, Context context) {
        this.SocimaGestion = SocimaGestion;
        this.db = db;
        this.context = context;


    }

    public boolean EstadoConexion(Context context) {
        ConnectivityManager Conexion = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Conexion != null) {
            NetworkInfo[] Informacion = Conexion.getAllNetworkInfo();
            if (Informacion != null)
                for (int i = 0; i < Informacion.length; i++)
                    if (Informacion[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public void downloadFile(String uRl, String nombre, Context context) {
        Log.d("IMAGENLINK", uRl);
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/SocimaGestion");

        if (!direct.exists()) {
            direct.mkdirs();
        }
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            //  request.setVisibleInDownloadsUi(false);
            //  request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDescription("Actualizando Imagenes.");
            request.setDestinationInExternalPublicDir("/SocimaGestion", nombre);
            mgr.enqueue(request);
        }

    }

    boolean esIgual(Object Uno, Object Dos)
    {

        return Uno == Dos || (Uno != null && Uno.equals(Dos));
    }

}
