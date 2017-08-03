/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package singletonfactory.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author asortega
 */
public class LeectorWS {

    public LeectorWS() {
    }

    //
    //METODOS QUE SE USAN PARA LEER LOS WS
    //
    public String leerData(Reader r) {
        try {
            StringBuilder cadena = new StringBuilder();//donde vamos a almacenar la respuesta
            int cont;
            while ((cont = r.read()) != -1) {
                cadena.append((char) cont);
            }
            return cadena.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public JSONObject leerURL(String pUrl) throws IOException, JSONException {
        InputStream url = new URL(pUrl).openStream();
        try {
            BufferedReader datos = new BufferedReader(new InputStreamReader(url, Charset.forName("UTF-8")));
            String datosJSON = leerData(datos);
            JSONObject json = new JSONObject(datosJSON);
            return json;
        } finally {
            url.close();
        }
    }
}
