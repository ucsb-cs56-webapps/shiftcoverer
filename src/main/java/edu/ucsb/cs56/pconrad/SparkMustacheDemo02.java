package shiftcoverer;


import org.apache.log4j.Logger;

import java.net.UnknownHostException;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;

/**
 * Simple example of using Mustache Templates
 *
 */

public class SparkMustacheDemo02 {

	public static final String CLASSNAME="SparkMustacheDemo02";
	
	public static final Logger log = Logger.getLogger(CLASSNAME);

	public static void main(String[] args) {

		HashMap<String,String> envVars =  
			getNeededEnvVars(new String []{
					"MONGODB_USER",
					"MONGODB_PASS",
					"MONGODB_NAME",
					"MONGODB_HOST",
					"MONGODB_PORT"				
				});
		
		String uriString = mongoDBUri(envVars);

        port(getHerokuAssignedPort());
		
		Map map = new HashMap();
       
        // hello.mustache file is in resources/templates directory
        get("/", (rq, rs) -> new ModelAndView(map, "home.mustache"), new MustacheTemplateEngine());

		get("/post", (rq, rs) -> new ModelAndView(map, "post.mustache"), new MustacheTemplateEngine());
        get("/find", (rq, rs) -> new ModelAndView(map, "find.mustache"), new MustacheTemplateEngine());
		post("/createresult", (rq, rs) -> {
			// log.debug("***************time = " + rq.queryParams("time"));
			// log.debug("***************job = " + rq.queryParams("job"));

			shift newShift = new shift (rq.queryParams("job"), rq.queryParams("time"), rq.queryParams("name"), rq.queryParams("email"));

			writeToDB(newShift, uriString);

			map.put("time", rq.queryParams("time"));
			map.put("job", rq.queryParams("job"));
			map.put("name", rq.queryParams("name"));
			map.put("email", rq.queryParams("email"));


			return new ModelAndView(map, "createresult.mustache");
		}, new MustacheTemplateEngine());
		

		get("/joinresult", (rq, rs) -> new ModelAndView(map, "joinresult.mustache"), new MustacheTemplateEngine());
	}
	
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    /**
       return a HashMap with values of all the environment variables
       listed; print error message for each missing one, and exit if any
       of them is not defined.
    */
    
     public static HashMap<String,String> getNeededEnvVars(String [] neededEnvVars) {

        ProcessBuilder processBuilder = new ProcessBuilder();
    		
		HashMap<String,String> envVars = new HashMap<String,String>();
		
		boolean error=false;		
		for (String k:neededEnvVars) {
			String v = processBuilder.environment().get(k);
			if ( v!= null) {
				envVars.put(k,v);
			} else {
				error = true;
				System.err.println("Error: Must define env variable " + k);
			}
        }
		
		if (error) { System.exit(1); }

		System.out.println("envVars=" + envVars);
		return envVars;	 
    }
	
	public static String mongoDBUri(HashMap<String,String> envVars) {

		System.out.println("envVars=" + envVars);
		
		// mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
		String uriString = "mongodb://" +
			envVars.get("MONGODB_USER") + ":" +
			envVars.get("MONGODB_PASS") + "@" +
			envVars.get("MONGODB_HOST") + ":" +
			envVars.get("MONGODB_PORT") + "/" +
			envVars.get("MONGODB_NAME");
		System.out.println("uriString=" + uriString);
		return uriString;
	}

	// public shift createShift(){
	// 	//turn the user inputs into shift model
	// 	shift newShift = new shift (rq.queryParams("job"), rq.queryParams("time"), rq.queryParams("name"), rq.queryParams("email"));

	// 	return newShift;
	// }


	public static void writeToDB (shift newShift, String uriString){
		//connect to database
        MongoClientURI uri  = new MongoClientURI(uriString); 
        MongoClient client = new MongoClient(uri);
        MongoDatabase db = client.getDatabase(uri.getDatabase());

        //make a new collection
        MongoCollection<Document> shiftsInfo = db.getCollection("shiftsInfo");

        //"shift" to "document"
        Document seedData = new Document("job", newShift.getJob())
        					.append("time", newShift.getTime())
        					.append("name", newShift.getName())
        					.append("email", newShift.getEmail());

        //write to the collection
        shiftsInfo.insertOne(seedData);

	}


















	
}

