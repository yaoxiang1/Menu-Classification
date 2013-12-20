import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class mongodb {
	// one mongodb have one database and can connect to multiple collection sequentially
	DB db;
	DBCollection current_coll;
	
	/**
	 * Config mongodb and set database
	 * @param database
	 * @param id
	 * @param password
	 * @throws UnknownHostException
	 */
	public mongodb(String database, String id, String password) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		db = mongoClient.getDB(database);
	}
	
	/**
	 * The the collection to the current collection field
	 * @param collection_name
	 * @return
	 */
	public DBCollection set_collection(String collection_name){
		current_coll =db.getCollection(collection_name);
		return current_coll;
	}
	
	public DBCollection get_collection(String collection_name){	
		return db.getCollection(collection_name);
	}
	
	/**
	 * Create the basic default BasicDBObject
	 * @return
	 */
	public BasicDBObject db_create_BasicDBObject(){
		BasicDBObject bdbo = new BasicDBObject();
		return bdbo;
	}
	
	/**
	 * query with current collection
	 * @param query, the instruction as BasicDBObejct for the query
	 * @return a cursor which can traverse the query
	 */
	public DBCursor db_query(BasicDBObject query){
		DBCursor cursor = current_coll.find(query);
		return cursor;
	}
	
	/**
	 * query all the info in the current collection
	 * @param query, the instruction as BasicDBObejct for the query
	 * @return a cursor which can traverse the query
	 */
	public DBCursor db_query(){
		DBCursor cursor = current_coll.find();
		return cursor;
	}
	
	/**
	 * query mongodb, return unique field query and put the return
	 */
	public ArrayList<Object> query_distinct_locationid(String distinct_field){	
		ArrayList<Object> distinct_object_list = new ArrayList<Object>();
		for(Object distinct_object:this.current_coll.distinct(distinct_field)) {
			distinct_object_list.add(distinct_object);		
		}	
		return distinct_object_list;
	}
	
	/**
	 * Most basic insertion to insert one key and value pair to each BasicDBObject
	 * @param key, key value to insert NOTE: need to be UNIQUE
	 * @param value, value of the key
	 */
	public void db_one_level_insert(String key, String value){
		BasicDBObject bdbo = this.db_create_BasicDBObject();
		bdbo.append(key, value);
		current_coll.insert(bdbo);
		
	}
	
	
	/**
	 * 
	 * @param key
	 * @param values
	 */
	public void db_one_level_insert(ArrayList<String> name_values, ArrayList<String> values, BasicDBObject bdbo_label){
		BasicDBObject bdbo = this.db_create_BasicDBObject();
		
		for(int i = 0 ; i < name_values.size() ; i++){
			bdbo.append(name_values.get(i),values.get(i) );
		}
		bdbo.append("menu_label",bdbo_label);
		
		current_coll.insert(bdbo);
		
	}
	
	
	
	
	public static void main(String[] args) throws UnknownHostException{
		mongodb query_db = new mongodb("menudata","","");
		query_db.set_collection("menu_label");
		
		
		
//		BasicDBObject bdbo = new BasicDBObject();
//		DBCursor curser = query_db.db_query(bdbo);
//		while(curser.hasNext()){
////			BSONObject dbson = (BSONObject)curser.next();
////			System.out.println(dbson);
////			String unique_key = (String)dbson.keySet().toArray()[1];
////			System.out.print(unique_key + " ");
////			System.out.println(dbson.get(unique_key));
//		}
		
		
	}
	
	

}
