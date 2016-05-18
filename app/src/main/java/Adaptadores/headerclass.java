package Adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.odril.socimagestionv02.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import it.gmariotti.cardslib.library.internal.CardHeader;

public class headerclass extends CardHeader {
    String Titulo;
    String Imagen;
    String Noticia;
    int Estado;
    public ImageLoader imageLoader;
    DisplayImageOptions options;
    File cacheDir;
    TextView TxTituloNoticia;
    TextView TxNoticia;
    ImageView ImagenView;


    public headerclass(Context context, String Titulo, String Noticia, String imagen, int Estado) {
        super(context, R.layout.custom_header);
        this.Titulo = Titulo;
        this.Noticia = Noticia;
        this.Imagen = imagen;
        this.Estado = Estado;

        cacheDir = StorageUtils.getOwnCacheDirectory(getContext(), "MyFolderCache");

        // Get singletone instance of ImageLoader
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .discCache(new UnlimitedDiscCache(cacheDir))
                .build();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .build();


    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        TxTituloNoticia = (TextView) view.findViewById(R.id.TituloNoticia);
        TxNoticia = (TextView) view.findViewById(R.id.Noticia);
        ImagenView = (ImageView) view.findViewById(R.id.Imagen);

        final ImageView IvImagen = (ImageView) view.findViewById(R.id.Imagen);

        Log.d("StringImagen", "" + Imagen + "");

        TxTituloNoticia.setText(Titulo);
        if (Estado != 0) {
            TxTituloNoticia.setBackgroundResource(R.color.Leida);
            TxTituloNoticia.setTextColor(Color.GRAY);
            TxNoticia.setTextColor(Color.GRAY);
            String Nota = TxTituloNoticia.getText().toString();
            TxTituloNoticia.setText(Nota + "  #Leída");
            ImagenView.setAlpha(0.5f);
        }
        TxNoticia.setText(Noticia);
        Picasso.with(getContext()).load("file://" + Imagen).into(IvImagen);

    }


    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo() {
        if(Estado == 0) {
            TxTituloNoticia.setBackgroundResource(R.color.Leida);
            TxTituloNoticia.setTextColor(Color.GRAY);
            //System.out.println("dato noticia " + TxTituloNoticia.getText().toString());
            String noticiaArray[] = TxTituloNoticia.getText().toString().split("-");
            //System.out.println("dato noticia " + noticiaArray.length);
            String Nota = TxTituloNoticia.getText().toString();
            if(noticiaArray.length == 1){
                TxTituloNoticia.setText(Nota + " NOTICIA -> Leída");
            }
            TxNoticia.setTextColor(Color.GRAY);
            ImagenView.setAlpha(0.5f);
        }
    }
}