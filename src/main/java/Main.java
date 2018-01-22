import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.jena.atlas.csv.CSVParser;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**********this class is just to generate the .ttl in order to initialise our dataset in fusiki and create our model******************/


public class Main {

    public static final String _LANG_FR = "fr"; // The language of the station names
    public static final String _INPUT_FILE_NAME = "Test.txt"; // The input file name
    public static final String _OUTPUT_FILE_NAME = "Test.rdf"; // The output file name
    public static final String _OUTPUT_FORMAT = "Turtle"; // The output format
    public static final String _UTF_8 = "UTF-8"; // Character encoding

    // Namespaces of the vocabularies we use
    public static final String _EX_NS = "http://example.com/stations/";
    public static final String _GEO_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String _RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String _WO_NS ="http://opensensingcity.emse.fr/ontologies/weather#";
    // Prefixes
    public static final String _EX_PREFIX = "ex";
    public static final String _GEO_PREFIX = "geo";
    public static final String _RDFS_PREFIX = "rdfs";
    public static final String _WO_PREFIX = "wo";


    // THE IRIs of the geo: terms we are using
    public static final String _GEO_LAT = "http://www.w3.org/2003/01/geo/wgs84_pos#lat";
    public static final String _GEO_LONG = "http://www.w3.org/2003/01/geo/wgs84_pos#long";
    public static final String _GEO_SPATIAL_THING = "http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing";

    public static final String _WO_WeatherState = "http://opensensingcity.emse.fr/ontologies/weather#WeatherState";


    public static void main(String[] args) {
        // Starts with an empty Jena model
        Model stationGraph = ModelFactory.createDefaultModel();

        // Define prefixes for pretty output
        stationGraph.setNsPrefix(_EX_PREFIX,_EX_NS);
        stationGraph.setNsPrefix(_GEO_PREFIX,_GEO_NS);
        stationGraph.setNsPrefix(_RDFS_PREFIX,_RDFS_NS);
        stationGraph.setNsPrefix(_WO_PREFIX,_WO_NS);


        // We initialise two properties and one resource from the geo: vocabulary
        Resource geo_SpatialThing = stationGraph.createResource(_GEO_SPATIAL_THING);
        Property geo_lat = stationGraph.createProperty(_GEO_LAT);
        Property geo_long = stationGraph.createProperty(_GEO_LONG);

        // We initialise two properties and one resource from the geo: vocabulary
        Property wo_WeatherState = stationGraph.createProperty(_WO_WeatherState);


        // Initialise a CSV parser
        CSVParser parser = CSVParser.create(_INPUT_FILE_NAME);

        // Skip the first line that contains headers
        parser.parse1();

        // For each line, we will generate some triples with the same subject
        for(List<String> line : parser) {

            //Store all what we need in Strings
            String stop_id = line.get(0); // first column
            String stop_name = line.get(1);
            String stop_lat = line.get(3);
            String stop_long = line.get(4);

            String stop_Weather ="";
            //get the weather
            try {
                stop_Weather=GetWeather.call_me(stop_lat,stop_long);
                //stop_Weather="clear";

            } catch (Exception e) {
                e.printStackTrace();
            }


            // We have to catch exceptions
            try {

                // Make a new IRI for the station described in this line
                String stop_iri = _EX_NS + URLEncoder.encode(stop_id,_UTF_8);

                // Create the resource that corresponds to the station
                Resource stop = stationGraph.createResource(stop_iri);

                // Add a type to the station
                stop.addProperty(RDF.type,geo_SpatialThing);

                // Add a label to the station (in French)
                stop.addProperty(RDFS.label,stop_name,_LANG_FR);

                // Add latitude and longitude of the station
                stop.addProperty(geo_lat,stop_lat,XSDDatatype.XSDdecimal);
                stop.addProperty(geo_long,stop_long,XSDDatatype.XSDdecimal);

                //Add Weather to the station
                stop.addProperty(wo_WeatherState, stop_Weather, _LANG_FR);

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // We have to catch I/O exceptions in case the file is not writable
        try {

            // An output stream to save the generated RDF graph
            OutputStream output = new FileOutputStream(new File(_OUTPUT_FILE_NAME));

            // Save the RDF graph to a file
            stationGraph.write(output,_OUTPUT_FORMAT);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



}