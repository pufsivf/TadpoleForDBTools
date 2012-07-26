package com.hangum.tadpole.mongodb.core.test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

/**
 * create collection
 * 
 * create newCollection2 
 * 
 * 
 * @author hangum
 * 
 */
public class MongoTestNewCollection {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		ConAndAuthentication testMongoCls = new ConAndAuthentication();
		Mongo mongo = testMongoCls.connection(ConAndAuthentication.serverurl, ConAndAuthentication.port);
		DB db = mongo.getDB("test");
		
		DBObject dbObject = (DBObject) JSON.parse("{capped:true, size:100000}");
		DBCollection dbColl = db.createCollection("newCollection3", dbObject);
		
//		db.cappedcoll.insert({"names": {"First": "Gonza", "Last": "Vieira"}}); 
//		db.cappedcoll.update({"names.First": "Gonza"},{$unset: {"_id":1}});
		
		mongo.close();
	}

}
