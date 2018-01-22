import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**************************to separate the fuseki part and the part of getting data from the api******************************/

public class GetWeather {
    public static void main(String[] args) {
        try {
            GetWeather.call_me("5","45");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String call_me(String lat,String lon) throws Exception {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=7aa4d5777e80b5bdb25527d75c5df6ba";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        //con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print in String
        System.out.println(response.toString());
        //Read JSON response and print
        JSONObject myResponse = new JSONObject(response.toString());
        System.out.println("result after Reading JSON Response");
        System.out.println("weather- "+myResponse.getJSONArray("weather").getJSONObject(0).getString("main"));

        return myResponse.getJSONArray("weather").getJSONObject(0).getString("main");
    }

}
