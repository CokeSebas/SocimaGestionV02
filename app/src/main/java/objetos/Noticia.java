package objetos;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.odril.socimagestionv02.R;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Ghost on 14-05-15.
 */
public class Noticia extends CardExpand {

    TextView TituloNoticia;
    String TituloN;


    public Noticia(Context context, String titulo) {
        super(context, R.layout.layout_noticias);
        this.TituloN = titulo;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        Log.d("Disando", "Diseñando");
        TituloNoticia = (TextView) view.findViewById(R.id.TituloNoticia);
        TituloNoticia.setText(TituloN);


    }
}


