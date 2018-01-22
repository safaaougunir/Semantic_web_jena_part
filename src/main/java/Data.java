import org.apache.jena.atlas.csv.CSVParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***********************************to fill our map with the initial data of stops*****************************************/

public class Data {

    public static void main(String[] args) {
        Map<Stop, String> map =Data.fillmap();
    }

    public static  Map<Stop, String> fillmap() {
        Map<Stop, String> map = new HashMap<Stop, String>();
        // Initialise a CSV parser
        CSVParser parser = CSVParser.create("Test.txt");

        // Skip the first line that contains headers
        parser.parse1();

        // For each line
        for (List<String> line : parser) {
            Stop s=new Stop();
            s.setId(line.get(0));
            System.out.println(s.getId());

            s.setLat(line.get(3));
            //System.out.println(s.getLat());

            s.setLon(line.get(4));

            s.setLon(line.get(4));

            String stop_Weather = "";
            map.put(s,stop_Weather);
        }
        return map;
        }

}
