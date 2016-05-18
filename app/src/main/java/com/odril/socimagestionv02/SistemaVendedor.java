package com.odril.socimagestionv02;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.text.DecimalFormat;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class SistemaVendedor extends ActionBarActivity implements MaterialTabListener {

    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;
    private Typeface FuenteUno;
    private Typeface FuenteDos;
    private Typeface FuenteTres;
    private Typeface FuenteCuatro;
    Button Catalogo, Actualizar;
    ViewPagerAdapter FPV;
    FragmentManager fs;
    SQLiteDatabase db;
    BaseDatos BDSocima;
    DecimalFormat formateador = new DecimalFormat("###,###");


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fs = getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fs);
        adapter.notifyDataSetChanged();


    }

    @Override


    protected void onCreate(Bundle savedInstanceState) {


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        ImageLoader.getInstance().init(config);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();


        ImageLoader.getInstance().init(config);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sistema_vendedor);

        FuenteUno = Typeface.createFromAsset(getAssets(), "fonts/uno.ttf");
        FuenteDos = Typeface.createFromAsset(getAssets(), "fonts/dos.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/tres.ttf");
        FuenteTres = Typeface.createFromAsset(getAssets(), "fonts/cuatro.ttf");
        Catalogo = (Button) findViewById(R.id.Catalago);
        Catalogo.setTypeface(FuenteCuatro);

        Catalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SistemaVendedor.this, com.odril.socimagestionv02.Catalogo.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

            }
        });

        YoYo.with(Techniques.SlideInUp)
                .duration(2000)
                .playOn(findViewById(R.id.FooterEf));


        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager

        fs = getSupportFragmentManager();

        adapter = new ViewPagerAdapter(fs);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);

            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }

    }


    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            Fragment FG = new FragmentoPerfilVendedor();

            switch (num) {

                case 0:
                    FG = new FragmentoPerfilVendedor();
                    break;
                case 1:
                    FG = new FragmentoInformacionProductos();
            }
            return FG;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String Nombre = "";
            switch (position) {
                case 0:
                    Nombre = "Perfil";
                    break;
                case 1:
                    Nombre = "Productos";
                    break;
            }
            return Nombre;
        }

    }
}
