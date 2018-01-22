import org.apache.jena.rdf.model.*;

import org.apache.jena.rdf.model.*;

import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;


/***********************************this class to do the update query of weather*****************************************/


public class FusekiWeather {

    public static void update(Model model) {
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();  // get next statement
            Resource subject = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate
            RDFNode object = stmt.getObject();      // get the object
            String UPDATE_TEMPLATE =

                    "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
                            + "PREFIX ex: <http://example.com/stations/>"
                            + "PREFIX wo: <http://opensensingcity.emse.fr/ontologies/weather#>"
                            + "INSERT DATA"
                            + " {<"+ subject.toString()+">   wo:WeatherState    \"" + object.toString() + "\"@fr  }";

            System.out.println(UPDATE_TEMPLATE);

            UpdateProcessor upp = UpdateExecutionFactory.createRemote(UpdateFactory.create(UPDATE_TEMPLATE),
                    "http://localhost:3030/MyData/update");

            //  String reqStr = UpdateFactory.create(UPDATE_TEMPLATE).toString() ;
            // HttpOp.execHttpPost("http://localhost:3030/vehicles/update", "application/x-www-form-urlencoded", reqStr) ;
            upp.execute();
            //Query the collection, dump output
               /* QueryExecution qe = QueryExecutionFactory.sparqlService(
                        "http://localhost:3030/ds/query", "SELECT * WHERE {?x ?r ?y}");
                ResultSet results = qe.execSelect();
                ResultSetFormatter.out(System.out, results);
                qe.close();*/
        }

    }
}
