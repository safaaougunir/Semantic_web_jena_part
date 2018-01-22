"# Semantic_web_jena_part" 

this project aims to combines what we learned during semantic web classes.I had combined data from TER lines (https://ressources.data.sncf.com/explore/dataset/sncf-ter-gtfs/) and (http://http://openweathermap.org/) API in order to execute two senarios :  

    + Senario 1 :
        - Get all stations and their position from TER to fill my Sparql database.
        - Get their current weather from openweathermap using their lat and long.
        - Create our Model then generate the ".rdf" using jena and upload it in my local fuseki server.
        - Update the weather of station continiously so that the data would be pseoudo realtime. 
        - Get the data in the front end from the server using Get query with javascript.
  
    + Senario 2 :

        - Get the vehicle position from GTLS Data continiously .
        - Update the postions in the fuseki server (on change).
        - Geting the new potions continiosly in the front end using markers on a map.
    
This part is the Jena part .You will find the Front end source code in this repository https://github.com/safaaougunir/Semantic_web_front.git .
