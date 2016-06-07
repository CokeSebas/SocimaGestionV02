package com.odril.Socima_Gestion;

/**
 * Created by Ghost on 28-09-15.
 */
public class globals {

    private static globals instance;

    // Global variable
    //private int data;
    private boolean running = true;
    private int idVendedor = 0;

    // Restrict the constructor from being instantiated
    private globals(){}


    public void setRunning(boolean running){
        this.running = running;
    }

    public boolean getRunning(){
        return running;
    }

    public void setVendedor(int idVendedor){
        this.idVendedor = idVendedor;
    }

    public int getIdVendedor(){
        return idVendedor;
    }

    public static synchronized globals getInstance(){
        if(instance==null){
            instance=new globals();
        }
        return instance;
    }

}
