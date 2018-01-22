import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.jena.rdf.model.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.atlas.csv.CSVParser;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import static org.apache.jena.enhanced.BuiltinPersonalities.model;


/******************************this class is the main class for updating the weather Fuseki************************************/

public class WeatherRealTime {

    public static void main(String[] args) throws Exception {
        //jena
        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("ex","http://example.com/stations/");
        model.setNsPrefix("wo","http://opensensingcity.emse.fr/ontologies/weather#");


        Map<Stop, String> map = Data.fillmap();


        // Property lat = ResourceFactory.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
        // Property lon = ResourceFactory.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
        //Property weather = ResourceFactory.createProperty("http://opensensingcity.emse.fr/ontologies/weather#WeatherState");
        Property wo_WeatherState = model.createProperty("http://opensensingcity.emse.fr/ontologies/weather#WeatherState");


        //  To not be blocked by the API
        //  while (true) {
        for (Map.Entry<Stop, String> elemt : map.entrySet()) {

            String currentWeather="";
            try {
                currentWeather = GetWeather.call_me(elemt.getKey().getLat(), elemt.getKey().getLat());

            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
            String stop_iri = "http://example.com/stations/" + URLEncoder.encode(elemt.getKey().getId(),"UTF-8");
            // Make a new IRI for the station described in this line



            if (!currentWeather.equals(elemt.getValue())) {
                elemt.setValue(currentWeather);

                // Create the resource that corresponds to the station
                Resource r = model.getResource(stop_iri);
                // .addProperty(lat,elemt.getKey().getLat())
                // .addProperty(lon,elemt.getKey().getLon());
                r.removeAll(wo_WeatherState);
                r.addProperty(wo_WeatherState, currentWeather);
                System.out.print(currentWeather);

            }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        FusekiWeather.update(model);
        //model.write(System.out);

               /* Thread t = new Thread() {
                    public void run() {
                        System.out.println("waiting 30 sec..............");
                    }
                };
                t.start();

                Thread.sleep(30000);*/


        //}


    }
}





