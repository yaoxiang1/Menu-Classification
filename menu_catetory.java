import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

/**
 * Script to run to insert the data into the mongodb, with MenuName as word lists and MenuDesc as word lists
 * @author alan
 */
public class menu_category {
	public static Collection<String> word_list;
	
	@SuppressWarnings("resource")
	public static DBCollection read_csv(DB database) throws IOException, ClassNotFoundException, SQLException{
		
		String csvFile = "clean_menu.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "##";
		int count = 0;
		///////////////////////////////////////////////////////////////
		// Mysql query, putting all the menuid and price into a hashmap
		// The checking process can happen easily through constant time
		///////////////////////////////////////////////////////////////
		HashMap<String,Float> hm = new HashMap<String,Float>();
		mysql db = new mysql("root","mysql*alan","localhost","menudata");
		float price = 0;
		String itemid = "";
		ResultSet rs  = db.query("select * from cs_price");
		while (rs.next()){
			itemid = rs.getObject("itemid").toString();
			price = (Float) rs.getObject("price");
			hm.put(itemid, price);
		}
		 
		System.out.println(hm);
		
		
		
		///////////////////////////////////////////////////
		try {
			
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				count++;
				String[] MenuInfo = line.split(cvsSplitBy);

				if(MenuInfo[0].equals("869") && hm.get(MenuInfo[2])!=null && hm.get(MenuInfo[2]) > 4){
//				if( hm.get(MenuInfo[2])!=null && hm.get(MenuInfo[2]) > 4 ){
					if(MenuInfo.length==5){
						System.out.println(MenuInfo[0] +"___"+ MenuInfo[1] +"___"+ MenuInfo[2]+ "___" + split_clean((MenuInfo[3]))+ "___" + split_clean((MenuInfo[4])));
						menu_category.mongo_insert(database, "menuinfo", MenuInfo[0], MenuInfo[1], MenuInfo[2], split_clean((MenuInfo[3])),split_clean((MenuInfo[4])));
					}
					if(MenuInfo.length==4){
						System.out.println(MenuInfo[0] +"___"+ MenuInfo[1] +"___"+ MenuInfo[2]+ "___" + split_clean((MenuInfo[3])));
						menu_category.mongo_insert(database, "menuinfo", MenuInfo[0], MenuInfo[1], MenuInfo[2], split_clean((MenuInfo[3])),new ArrayList<String>());
	
					}
					if(MenuInfo.length==3){
						System.out.println(MenuInfo[0] +"___"+ MenuInfo[1] +"___"+ MenuInfo[2]);
						menu_category.mongo_insert(database, "menuinfo", MenuInfo[0], MenuInfo[1], MenuInfo[2], new ArrayList<String>(),new ArrayList<String>());
	
					}		
				}
	 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	 
		System.out.println("Total "+ count);
		return database.getCollection("menuinfo");
	 }
		

	/**
	 * Before split and clean the data, have to take away all the symbol first 
	 * @param input
	 * @return
	 * @throws IOException 
	 */
	public static ArrayList<String> split_clean(String input) throws IOException{
//		System.out.println("Run!!!");
		//System.out.println(input);
		String cleaned_sympol_input = EngineProcessText.replace_symbol_to_word(input);
		String[] split_with_empty = cleaned_sympol_input.split(" ");
		ArrayList<String> clean_no_space = new ArrayList<String>();
		for(int i = 0 ; i < split_with_empty.length ; i++){
			if(!split_with_empty[i].equals("") && split_with_empty[i].trim().matches("[a-zA-Z]+")){
				clean_no_space.add(split_with_empty[i].trim().toLowerCase());
			}
		}	
		clean_no_space.removeAll(menu_category.word_list);
		return clean_no_space;
	}
	
	@SuppressWarnings("resource")
	public static void upload_wordslist() throws IOException{
		word_list = new ArrayList<String>();
		String line_word = "";
		BufferedReader br_word = new BufferedReader(new FileReader("MostWords"));	
		while ((line_word = br_word.readLine()) != null) {
			word_list.add(line_word);
		}
				
	}
	
	public static DB mongo_config(String database) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(database);
		return db;
	}
	

	public static DBCollection mongo_insert(DB database, String collection, String locationid, String locationname, String orderid, ArrayList<String> array_doc_name, ArrayList<String> array_doc_descript) throws UnknownHostException{
		DBCollection coll =database.getCollection(collection);
		BasicDBObject doc_main = new  BasicDBObject("LocationID",locationid );
						doc_main.append("LocationName", locationname);
						doc_main.append("MenuID", orderid);
						BasicDBObject doc_name_list = new  BasicDBObject();
						for(int i = 0 ; i < array_doc_name.size() ; i++){
							doc_name_list = doc_name_list.append("MenuName_"+i, array_doc_name.get(i));
						}
						doc_main.append("MenuName", doc_name_list);

						BasicDBObject doc_descript_list = new  BasicDBObject();
						for(int i = 0 ; i < array_doc_descript.size() ; i++){
							doc_descript_list = doc_descript_list.append("MenuDesc_"+i, array_doc_descript.get(i));
						}
						doc_main.append("MenuDesc", doc_descript_list);

		coll.insert(doc_main);	
		return coll;
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{
		upload_wordslist();
		DB db = mongo_config("menudata");		
		DBCollection coll = read_csv(db);
		System.out.println("Done!!!");

	}
	
}
