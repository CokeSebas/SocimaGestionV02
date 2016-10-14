package com.odril.Socima_Gestion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class BaseDatos extends SQLiteOpenHelper {

    String SqlTablaUsuario = "CREATE TABLE Mv_Vendedor(CodigoVendedor INTEGER PRIMARY KEY, Nombre TEXT, Email TEXT, Usuario TEXT, Clave TEXT, Meta INTEGER, Cargo INTEGER, Actual INTEGER, Localidad TEXT, Estado INTEGER);";
    String SqlMenu = "CREATE TABLE Mv_Menu(idMenuPrincipal INTEGER PRIMARY KEY, Nombre TEXT)";
    String SqlMenuHijo = "CREATE TABLE Mv_MenuHijo(idMenuHijo INTEGER PRIMARY KEY, idMenuPrincipal INTEGER,Nombre TEXT)";
    String SqlCategoriaProducto = "CREATE TABLE Mv_categoriaProducto(idProducto INTEGER, idCategoria INTEGER)";
    String SqlCliente = "CREATE TABLE Mv_Cliente(CodigoCliente INTEGER PRIMARY KEY, Nombre TEXT, Email TEXT, Telefono TEXT,Vendedor INTEGER,Credito INTEGER, Direccion TEXT, Ciudad TEXT,Region TEXT,Codigo TEXT, Rut TEXT, CreditoMaximo INTEGER, Coordenada TEXT)";
    String SqlNoticias = "CREATE TABLE Mv_Noticia(Titulo TEXT, Noticia TEXT, Estado INTEGER, Imagen TEXT)";
    String SqlProducto = "CREATE TABLE Mv_Producto(idProducto INTEGER PRIMARY KEY, Modelo TEXT,Descripcion TEXT, CodigoProducto INTEGER, CodigoBodega TEXT, StockInicial INTEGER, Cantidad INTEGER, Precio INTEGER, Tam INTEGER, FFA TEXT,Descuento INTEGER, FFDI TEXT,FFDF TEXT, Tag TEXT, Imagen TEXT, MarcaId INTEGER, SortOrder INTEGER, Modificado TEXT, Image TEXT, Agotado TEXT)";
    String SqlProductoRelacion = "CREATE TABLE Mv_ProductoRelacion(idProducto INTEGER,idRelacion INTEGER)";
    String SqlDetalleProducto = "CREATE TABLE Mv_DetalleProducto(idProducto INTEGER,idAtributo INTEGER, Atributo TEXT,Descripcion TEXT)";
    String SqlOrden = "CREATE TABLE Mv_Orden(IdOrden INTEGER PRIMARY KEY , CodigoOrden TEXT,idCliente INTEGER,DireccionFacturacion TEXT, CiudadPago TEXT, Region TEXT, TipoPago TEXT, Total INTEGER,Estado INTEGER, FFI TEXT,FFM TEXT, Comentario TEXT,Vendedor INTEGER, DireccionEnvio TEXT, dcto TEXT)";
    String SqlDetalleOrden = "CREATE TABLE Mv_DetalleOrden(idOrden INTEGER,idProducto INTEGER,Cantidad INTEGER,Precio INTEGER,Total INTEGER)";
    String SqlComprar = "CREATE TABLE Mv_Compra(idOrdenCompra INTEGER, idCliente INTEGER ,idProducto INTEGER, Cantidad INTEGER, Precio INTEGER, Descuento  INTEGER,Comentario TEXT,NombreProducto TEXT)";
    String SqlPreparar = "CREATE TABLE Mv_Prepara(idCliente INTEGER, idProducto INTEGER)";
    String SqlBanner = "CREATE TABLE Mv_Banner(BannerId INTEGER, Url TEXT, informacion TEXT)";
    String SqlEstado = "CREATE TABLE Mv_Estado(IdEstado INTEGER, Estado TEXT)";
    String SqlMarca = "CREATE TABLE Mv_Marca(IdMarca INTENGER, Nombre TEXT)";
    String SqlImagenP = "CREATE TABLE IF NOT EXISTS Mv_ImagenP(IdProducto INTEGER, Imagen TEXT)";


    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Limpiar(db);
        Crear(db);
        Log.d("BD", "La BD fue creada");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Limpiar(db);
        Crear(db);
    }

    //METODOS !

    public void Limpiar(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS Mv_Vendedor");
        db.execSQL("DROP TABLE IF EXISTS Mv_Menu");
        db.execSQL("DROP TABLE IF EXISTS Mv_MenuHijo");
        db.execSQL("DROP TABLE IF EXISTS Mv_categoriaProducto");
        db.execSQL("DROP TABLE IF EXISTS Mv_Cliente");
        db.execSQL("DROP TABLE IF EXISTS Mv_DetalleOrden");
        db.execSQL("DROP TABLE IF EXISTS Mv_Noticia");
        db.execSQL("DROP TABLE IF EXISTS Mv_Orden");
        db.execSQL("DROP TABLE IF EXISTS Mv_Producto");
        db.execSQL("DROP TABLE IF EXISTS Mv_Compra");
        db.execSQL("DROP TABLE IF EXISTS Mv_Prepara");
        db.execSQL("DROP TABLE IF EXISTS Mv_DetalleProducto");
        db.execSQL("DROP TABLE IF EXISTS Mv_ProductoRelacion");
        db.execSQL("DROP TABLE IF EXISTS Mv_Banner");
        db.execSQL("DROP TABLE IF EXISTS Mv_Estado");
        db.execSQL("DROP TABLE IF EXISTS Mv_Marca");

    }

    public void Crear(SQLiteDatabase db) {
        db.execSQL(SqlProductoRelacion);
        db.execSQL(SqlTablaUsuario);
        db.execSQL(SqlMenu);
        db.execSQL(SqlMenuHijo);
        db.execSQL(SqlCategoriaProducto);
        db.execSQL(SqlCliente);
        db.execSQL(SqlDetalleOrden);
        db.execSQL(SqlNoticias);
        db.execSQL(SqlOrden);
        db.execSQL(SqlProducto);
        db.execSQL(SqlDetalleProducto);
        db.execSQL(SqlComprar);
        db.execSQL(SqlPreparar);
        db.execSQL(SqlBanner);
        db.execSQL(SqlEstado);
        db.execSQL(SqlMarca);
        db.execSQL(SqlImagenP);
    }

    public void vaciarVendedor(SQLiteDatabase db) {
        db.execSQL("delete from  Mv_Vendedor");
    }

    public void vaciarMenu(SQLiteDatabase db) {
        db.execSQL("delete from  Mv_Menu");
    }

    public void vaciarMenuHijo(SQLiteDatabase db) {
        db.execSQL("delete from  Mv_MenuHijo");
    }

    public void vaciarCategoriaProducto(SQLiteDatabase db) {
        db.execSQL("delete from Mv_categoriaProducto");
    }


    public void vaciarProducto(SQLiteDatabase db) {
        //db.execSQL("delete from  Mv_Producto WHERE modificado != 'SI'");
        db.execSQL("delete from  Mv_Producto");
    }

    public void vaciarBanner(SQLiteDatabase db) {
        db.execSQL("delete from  Mv_Banner");
    }

    public void vaciarDetalleProducto(SQLiteDatabase db) {
        db.execSQL("delete from  Mv_DetalleProducto");
    }

    public void vaciarProductoRelacion(SQLiteDatabase db) {
        db.execSQL("delete from  Mv_ProductoRelacion");
    }

    public void vaciarOrden(SQLiteDatabase db) {
        //Revisar
        //db.execSQL("delete from  Mv_Orden");
        db.execSQL("delete from  Mv_Orden where Estado IN (1, 2, 3, 4) ");
    }

    public void vaciarDetalleOrden(SQLiteDatabase db) {
        //Revisar
        db.execSQL("delete from  Mv_DetalleOrden WHERE idOrden NOT IN (SELECT idOrden FROM Mv_Orden)");
    }

    public void vaciarCliente(SQLiteDatabase db) {
        db.execSQL("delete from  Mv_Cliente");
    }

    public void vaciarNoticias(SQLiteDatabase db) {
        db.execSQL("delete from  Mv_Noticia");
    }


    public Cursor NombreMenuHijo(SQLiteDatabase db, int IdMenu) {
        return db.rawQuery("SELECT * FROM Mv_MenuHijo where idMenuHijo =" + IdMenu, null);
    }

    //public Cursor BuscarCliente(String textSearch) {
    public Cursor BuscarCliente(String textSearch, int idVendedor) {
        //System.out.println("cliente " + textSearch);
        textSearch = textSearch.replace("'", "''");
        SQLiteDatabase db = getReadableDatabase();
        if(idVendedor == 1) {
            return db.rawQuery("SELECT CodigoCliente as _id, Nombre, Credito, Rut" +
                    " FROM Mv_Cliente " +
                    " WHERE Nombre LIKE '%" + textSearch + "%' OR Rut LIKE '%" + textSearch + "%' ", null);
        }else{
            return db.rawQuery("SELECT CodigoCliente as _id, Nombre, Credito, Rut" +
                    " FROM Mv_Cliente " +
                    " WHERE Nombre LIKE '%" + textSearch + "%' OR Rut LIKE '%" + textSearch + "%' ", null);
        }
    }

    public Cursor getCursorProductos(String textSearch) {
        textSearch = textSearch.replace("'", "''");
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT idProducto as _id, Modelo" +
                " FROM Mv_Producto " +
                " WHERE (idProducto LIKE '%" + textSearch + "%' OR Modelo LIKE '%" + textSearch + "%' OR Tag LIKE '%" + textSearch + "%') AND Cantidad != 0;", null);
    }

    public void NoticiaLeida(SQLiteDatabase db, String Titulo) {
        db.execSQL("UPDATE Mv_Noticia set Estado = 1 where Titulo = '" + Titulo + "'");
    }


    public int CantidadNoticias() {

        int Contador = 0;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cc = db.rawQuery("SELECT count(Titulo) from Mv_Noticia where Estado = 0", null);
        if (cc.moveToFirst()) {
            Contador = cc.getInt(0);
            Log.d("ContadorNoticias", "" + Contador);
        }
        return Contador;
    }

}

