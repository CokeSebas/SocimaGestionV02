package objetos;


import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alvaro on 15/02/2015.
 */
public class Producto {

    String idProducto, Modelo, Descripcion, CodigoProducto, CodigoBodega, StockInicial, Cantidad, Precio, Tam, FFA, Descuento, FFDI, FFDF, img;
    ArrayList<String> idRelacion = new ArrayList<>();
    ArrayList<String> idAtributo = new ArrayList();
    ArrayList<String> Atributo = new ArrayList<>();
    ArrayList<String> DDescripcion = new ArrayList<>();


    public Producto(String idProducto, String Modelo, String Descripcion, String CodigoProduto, String CodigoBodega, String StockInicial, String Cantidad, String Precio, String Tam, String FFA, String Descuento, String FFDI, String FFDF, ArrayList<String> idRelacion, ArrayList<String> idAtributo, ArrayList<String> Atributo, ArrayList<String> DDescripcion) {

        this.idProducto = idProducto;
        this.Modelo = Modelo;
        this.Descripcion = Descripcion;
        this.CodigoProducto = CodigoProduto;
        this.CodigoBodega = CodigoBodega;
        this.StockInicial = StockInicial;
        this.Cantidad = Cantidad;
        this.Precio = Precio;
        this.Tam = Tam;
        this.FFA = FFA;
        this.Descuento = Descuento;
        this.FFDI = FFDI;
        this.FFDF = FFDF;
        this.idRelacion = idRelacion;
        this.idAtributo = idAtributo;
        this.Atributo = Atributo;
        this.DDescripcion = DDescripcion;

        String Nombre = idProducto;
        File Producto2 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Nombre + "_2.jpg");

        if (Producto2.exists()) {
            this.img = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Nombre + "_2.jpg";

        } else {
            File Producto3 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Nombre + "_3.jpg");
            if (Producto3.exists()) {
                this.img = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Nombre + "_3.jpg";

            } else {
                File Producto4 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Nombre + "_4.jpg");
                if (Producto4.exists()) {
                    this.img = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Nombre + "_4.jpg";

                } else {
                    File Producto5 = new File(Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Nombre + "_5.jpg");
                    if (Producto5.exists()) {
                        this.img = Environment.getExternalStorageDirectory() + "/SocimaGestion/" + Nombre + "_5.jpg";

                    } else {
                        this.img =  "SIN";
                    }
                }
            }

        }

    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getModelo() {
        return Modelo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public String getCodigoProducto() {
        return CodigoProducto;
    }

    public String getCodigoBodega() {
        return CodigoBodega;
    }

    public ArrayList<String> getAtributo() {
        return Atributo;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public ArrayList<String> getDDescripcion() {
        return DDescripcion;
    }

    public String getDescuento() {
        return Descuento;
    }

    public String getFFA() {
        return FFA;
    }

    public String getFFDF() {
        return FFDF;
    }

    public String getFFDI() {
        return FFDI;
    }

    public ArrayList<String> getIdRelacion() {
        return idRelacion;
    }

    public String getPrecio() {
        return Precio;
    }

    public String getImg() {
        return img;
    }

    public String getTam() {
        return Tam;
    }

    public ArrayList<String> getIdAtributo() {
        return idAtributo;
    }

    public String getStockInicial() {
        return StockInicial;
    }

}
