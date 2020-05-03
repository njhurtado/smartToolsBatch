package com.smarttools.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoDbConnection {

	public static MongoDatabase getInstance(String dataBaseName, String usr, String pwd, String url, String port) {
		String mongoClientURI = "mongodb://" + usr + ":" + pwd + "@" + url + ":" + port + "/heroku_gfx63z74?authSource=heroku_gfx63z74&authMechanism=SCRAM-SHA-1";
	    MongoClientURI connectionString = new MongoClientURI(mongoClientURI);
	    MongoClient mongoClient = new MongoClient(connectionString);
	    MongoClientOptions.builder().sslEnabled(true).build();
		return mongoClient.getDatabase(dataBaseName);
	}
	
}
