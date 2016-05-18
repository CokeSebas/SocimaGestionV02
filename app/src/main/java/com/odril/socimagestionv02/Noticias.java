package com.odril.socimagestionv02;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.ArrayList;

import Adaptadores.CustomCard;
import Adaptadores.headerclass;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;


public class Noticias extends ActionBarActivity {

    TextView Titulo, Cuerpo, Imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);



        BaseDatos SocimaGestion = new BaseDatos(getApplication(), "SocimaGestion", null, 1);
        SQLiteDatabase db = SocimaGestion.getWritableDatabase();


        Titulo = (TextView) findViewById(R.id.TituloNoticia);

        ArrayList<Card> cards = new ArrayList<Card>();

        Cursor ListaNoticias = db.rawQuery("SELECT * from Mv_Noticia;", null);
        ListaNoticias.moveToFirst();
        int Contador = 0 ;
        if (!ListaNoticias.isAfterLast()) {
            do {

                String TituloNoticia = ListaNoticias.getString(0);
                String Noticia = ListaNoticias.getString(1);
                String Imagen = ListaNoticias.getString(3);
                int Estado =  ListaNoticias.getInt(2);

                CustomCard card = new CustomCard(this);
                headerclass header = new headerclass(this, TituloNoticia, Noticia, Imagen, Estado);

                card.addCardHeader(header);
                card.init(header);
                cards.add(card);

                Contador++;
            }while(ListaNoticias.moveToNext());
        }







        CardArrayRecyclerViewAdapter mCardArrayAdapter = new CardArrayRecyclerViewAdapter(this, cards);
        CardRecyclerView mRecyclerView = (CardRecyclerView) findViewById(R.id.carddemo_recyclerview);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }


    }

    @Override
    public void onBackPressed() {
        Intent i =  new Intent(this,SistemaVendedor.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }
}




