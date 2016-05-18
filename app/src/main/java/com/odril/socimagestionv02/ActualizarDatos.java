package com.odril.socimagestionv02;

import android.widget.Button;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import Adaptadores.Httppostaux;

/**
 * Created by Jorge Tapia para empresa Odril on 04-06-2015.
 */
public class ActualizarDatos {

    Httppostaux post;
    String URL_connect="http://socimagestion.com/Movil/Datos/ActualizarOrden.php"; //ruta en donde estan nuestros archivos

   /*boolean result_back;
    private ProgressDialog pDialog;*/

    public ActualizarDatos() { }

    public int actualizarOrden(){

        int logstatus=-1;

        //Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
        //y enviarlo mediante POST a nuestro sistema para relizar la validacion
        ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("id", "3"));
        postparameters2send.add(new BasicNameValuePair("pass1","1"));

        //realizamos una peticion y como respuesta obtenes un array JSON
        JSONArray jdata=post.getserverdata(postparameters2send, URL_connect);

        //si lo que obtuvimos no es null
        if (jdata!=null && jdata.length() > 0){

            JSONObject json_data; //creamos un objeto JSON
            try {
                json_data = jdata.getJSONObject(0); //leemos el primer segmento en nuestro caso el unico
                logstatus=json_data.getInt("logstatus");//accedemos al valor
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //validamos el valor obtenido
            if (logstatus==1){// [{"logstatus":"0"}]
                return logstatus;
            }else{
                return 0;
            }
        }else{	//json obtenido invalido verificar parte WEB.
            //mensaje("La password ingresada no es correcta");
            return 0;
        }
    }
}
