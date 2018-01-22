import com.google.transit.realtime.GtfsRealtime;
import org.apache.jena.rdf.model.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jena.atlas.csv.CSVParser;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import static org.apache.jena.enhanced.BuiltinPersonalities.model;

/******************************this class is the main class for updating the vehicule pos in Fuseki ************************************/
/***************************======> Getting data from GTFS and create our model to do the update query**********************************/

public class VehiculePosRealTime {

    // Namespaces of the vocabularies we use
    public static final String _GEO_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    // Prefixes
    public static final String _GEO_PREFIX = "geo";


    // THE IRIs of the geo: terms we are using
    public static final String _GEO_LAT = "http://www.w3.org/2003/01/geo/wgs84_pos#lat";
    public static final String _GEO_LONG = "http://www.w3.org/2003/01/geo/wgs84_pos#long";

    private static String getVehicleId(GtfsRealtime.VehiclePosition vehicle) {
        if (!vehicle.hasVehicle()) {
            return null;
        }
        GtfsRealtime.VehicleDescriptor desc = vehicle.getVehicle();
        return desc.getLabel();
    }
    public static void main(String[] args) throws Exception {

        //jena
        Model model = ModelFactory.createDefaultModel();

        // Define prefixes for pretty output
        model.setNsPrefix(_GEO_PREFIX,_GEO_NS);

        Property geo_lat = model.createProperty(_GEO_LAT);
        Property geo_long = model.createProperty(_GEO_LONG);


        Map<String, Vehicule> vehiclesById = new ConcurrentHashMap<String, Vehicule>();

        URL url = new URL("http://developer.mbta.com/lib/GTRTFS/Alerts/VehiclePositions.pb");

        //  while (true) {
        GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream());
        for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
            if (entity.hasVehicle()) {
                GtfsRealtime.VehiclePosition vehicle = entity.getVehicle();
                String vehicleId = getVehicleId(vehicle);
                //   vehicleIdsByEntityIds.put(entity.getId(), vehicleId);
                GtfsRealtime.Position position = vehicle.getPosition();
                Vehicule v = new Vehicule();
                v.setId(vehicleId);
                v.setLat(position.getLatitude());
                v.setLon(position.getLongitude());
                v.setLastUpdate(System.currentTimeMillis());
                Vehicule existing = vehiclesById.get(vehicleId);

                if (existing == null || existing.getLat() != v.getLat() || existing.getLon() != v.getLon()) {
                    String vehicleURI = "http://somewhere/vehicle" + vehicleId;
                    StmtIterator iter = model.listStatements();
                    Resource x = model.getResource(vehicleURI);

                    if (existing == null) {

                        Resource vehiclerdf = model.createResource(vehicleURI)
                                .addProperty(geo_lat,Double.toString(v.getLat()))
                                .addProperty(geo_long,Double.toString(v.getLon()));

                        vehiclesById.put(vehicleId, v);
                        System.out.println(v.getId());
                        System.out.println(v.getLat());
                        System.out.println(v.getLon());
                        System.out.println(vehiclerdf.toString());

                        System.out.println("-------------------------------------------------------------");
                    }
                    Resource r = model.getResource(vehicleURI);
                    if (r.getProperty(geo_lat).getDouble() != v.getLat()) {
                        r.removeAll(geo_lat);
                        r.addProperty(geo_lat, Double.toString(v.getLon()));
                    }
                    if (r.getProperty(geo_long).getDouble() != v.getLon()) {
                        r.removeAll(geo_long);
                        r.addProperty(geo_long, Double.toString(v.getLat()));
                    }
                }
            }
        }


        System.out.println(model);
        FusekiVehicule.f(model);
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


