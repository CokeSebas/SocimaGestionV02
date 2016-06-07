package Adaptadores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.odril.Socima_Gestion.BaseDatos;
import com.odril.socimagestionv02.R;

import it.gmariotti.cardslib.library.internal.Card;


public class CustomCard extends Card {
    BaseDatos SocimaGestion;
    SQLiteDatabase db;
    int Estado;


    public CustomCard(Context context) {
        super(context, R.layout.custom_card);
        SocimaGestion = new BaseDatos(context, "SocimaGestion", null, 1);
        db = SocimaGestion.getWritableDatabase();

    }

    public void init(final headerclass Headerss) {

        //No Header

        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                if(Estado == 0) {
                    //System.out.println("noticia leida");
                    //System.out.println("noticia leida " + Estado);
                    Headerss.setTitulo();
                    SocimaGestion.NoticiaLeida(db, Headerss.getTitulo());
                }
            }
        });


    }
}
