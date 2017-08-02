/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package singletonfactory.webservices;

import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author asortega
 */
public class Autocomplete {

    String API = "...";
   
    public Autocomplete() {}
    
    public List buscar(String pParametros) {
        try{
        String vParametros = pParametros.replaceAll(" ", "+");
        String vUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + vParametros + "&types=geocode&language=es&key=" + API;

        JSONObject resultado;
        LeectorWS leerWS = new LeectorWS();
        resultado = leerWS.leerURL(vUrl);
        
        JSONArray array = resultado.getJSONArray("predictions");
        
        List<String> predicciones = new ArrayList<>();
        
        for(int i =0; i<= resultado.length(); i++){
            String description = array.getJSONObject(i).getString("description");
            //System.out.println(description);
            predicciones.add(description);
        }
        
        return predicciones;
        
        
        }catch(IOException  e){
            e.printStackTrace();
        }
        
        return null;
    }

   public List buscarAdapter(KeyAdapter pParametros) {
       List<String> predicciones;
        try{
        String conversion = pParametros.toString();
        String vParametros = conversion.replaceAll(" ", "+");
        String vUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + vParametros + "&types=geocode&language=es&key=" + API;

        JSONObject resultado;
        LeectorWS leerWS = new LeectorWS();
        resultado = leerWS.leerURL(vUrl);
        
        JSONArray array = resultado.getJSONArray("predictions");
        
        predicciones = new ArrayList<>();
        
        for(int i =0; i<= resultado.length(); i++){
            String description = array.getJSONObject(i).getString("description");
            System.out.println(description);
            predicciones.add(description);
        }
        
        return predicciones;
        
        
        }catch(IOException e){
            e.printStackTrace();
        }
        predicciones = new ArrayList<>();
        predicciones.add("mexico");
        return predicciones;
    }
    
}
