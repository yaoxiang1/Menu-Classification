import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

// I have black list chicken, so chicken repeat will not have so much affection for the classification

public class Bay_Model_Rest {
	
	public mongodb db ;
	public final double smooth_index = 0.01;
	public ArrayList<String> category_file;
	DBCollection db_collection;
	public HashMap<String,String> mostword;
	
	public HashMap<String,Integer> cat_to_hm_ArrayList_for_length;
	public HashMap<String,HashMap<String,Double>> cat_to_hm;
	
	public Bay_Model_Rest(String database,  String collection, String login, String pw, ArrayList<String> file_list) throws IOException{
		db = new mongodb(database, "", "");	
		db_collection = db.set_collection(collection);		
		category_file = file_list;
		
		mostword = new HashMap<String,String> ();
		EngineProcessText.add_mostwords_to_hm(mostword);
		
		cat_to_hm_ArrayList_for_length = new HashMap<String, Integer>();
		cat_to_hm = new HashMap<String, HashMap<String,Double>>();

		// Get Hash with freq. for a certain category
		for(String string_category_file : category_file){
			ArrayList<String> clean_arraylist_from_text_withcommon = EngineProcessText.text_to_clean_arraylist_withcommon(string_category_file);
			//System.out.println(clean_arraylist_from_text_withcommon);
			for (int i = 0 ; i < clean_arraylist_from_text_withcommon.size(); i ++ ) {
				if(this.mostword.containsKey(clean_arraylist_from_text_withcommon.get(i))){
					clean_arraylist_from_text_withcommon.remove(i);
					i--; // NOTE: ArrayList remove have to i-- to move back i once
				} 
			}
			int total_vob_in_text = clean_arraylist_from_text_withcommon.size();
			cat_to_hm_ArrayList_for_length.put(string_category_file, total_vob_in_text);
			HashMap<String,Double> hashmap_with_freq = EngineProcessText.clean_arraylist_to_hm(clean_arraylist_from_text_withcommon);
			cat_to_hm.put(string_category_file, hashmap_with_freq);
//			System.out.println("---->"+hashmap_with_freq);
		}
	}
	/**
	 * This method is to access the mongodb an put the string groups into ArrayList
	 * restaurant_array is the whole array which stores all the words
	 * ArrayList can store duplicate, therefore we do not need to worry the duplicate one disappear
	 * @return
	 */
	public ArrayList<String> mongodb_location_to_arraylist(BasicDBObject bdbo_query, ArrayList<String> info_field_list) throws UnknownHostException{
		ArrayList<String> restaurant_array = new ArrayList<String>();
//		BasicDBObject query1 = new BasicDBObject("LocationID", LocationID);
			DBCursor cursor = db.db_query(bdbo_query);
			while(cursor.hasNext()){
				BSONObject bson_one_tuple = cursor.next();
				// After get the bson tuple, we will loop through twice to get both the name and description field
				for (String db_info_field : info_field_list ){
					BSONObject bson = ((BSONObject)bson_one_tuple.get(db_info_field));
					Object[] bson_array = bson.keySet().toArray();
					for(int i = 0 ; i < bson_array.length ; i++){
						restaurant_array.add((bson.get(bson_array[i].toString())).toString());
					}
					//System.out.println(restaurant_array);
				}
			}
		return restaurant_array;
		
	}
	
	
	/**
	 * This method is used to categorize a certain restaurant information to different categories
	 * 1. Getting the HM from the mongodb
	 * 2. Calculate relative probabilities for different categories 
	 * 3. Make decision based on cal from different categories
	 * @throws IOException
	 */
	public String bay_model_choose_cat(BasicDBObject bdbo_query, ArrayList<String> info_field) throws IOException{
		
			ArrayList<String> location_info_stringlist = mongodb_location_to_arraylist(bdbo_query, info_field);
			HashMap<String,Double> hm_text_from_restaurat = EngineProcessText.arraylist_to_hashmap(location_info_stringlist);
			
			System.out.println("hm_text: " + hm_text_from_restaurat);
		
			ArrayList<Double> compare_cat_rate = new ArrayList<Double> ();
//			ArrayList<Double> exp_cat_rate = new ArrayList<Double> ();
//			HashMap<String,Double> exp_cat_rate_hm = new HashMap<String,Double> ();

//			double exp_sum = 0;

			for (String cat_file_address : category_file){
				double total = cal_relativefreq_for_cat( cat_file_address, this.cat_to_hm_ArrayList_for_length.get(cat_file_address), this.cat_to_hm.get(cat_file_address), hm_text_from_restaurat, location_info_stringlist );	
//				public double cal_relativefreq_for_cat(String file_category,double hm_words_length, HashMap<String, Double> clean_hm_from_text_withoutcommo, HashMap<String,Double> hm_text, ArrayList<String> location_info_stringlist ) throws IOException{

				//### System.out.println(cat_file_address+":" + total);
				compare_cat_rate.add(total);
//				double exp_numb = Math.exp(total);
//				exp_cat_rate.add(exp_numb);
//				exp_sum += exp_numb;

			}
			
			// Find out which cat have the max value
			double max_cat_rate = Collections.max(compare_cat_rate);
			int index_cat_rate = compare_cat_rate.indexOf(max_cat_rate);
			String chosen_category = category_file.get(index_cat_rate);
			
//			double max_cat_rate_exp = Collections.max(exp_cat_rate);
//			System.out.println("CONFIDENCE: "+ max_cat_rate_exp/exp_sum + " %");

			
			
//###			System.out.println("==================================================================");
//###			 System.out.println(category_file);
//###			 System.out.println(compare_cat_rate);
			System.out.println(chosen_category + " " + max_cat_rate);
			
//			System.out.println("------------------------------------------------------------------------------");
			return chosen_category;
			
	}
	
	/**
	 * NOTE: I assume each category of dishes is equally distributed, 
	 * 		 no presumption for the prior distribution of category 
	 * 
	 * This method calculate the relative frequency for a text file to a certain category
	 * @param text_file_category, the category from a certain type
	 * @param hm_text, 
	 * @param location_info_stringlist
	 * @return The total relative frequency of a certain text relative to a category
	 * @throws IOException
	 */
	public double cal_relativefreq_for_cat(String file_category,double hm_words_length, HashMap<String, Double> clean_hm_from_text_withoutcommo, HashMap<String,Double> hm_text, ArrayList<String> location_info_stringlist ) throws IOException{
		
		//### System.out.println(file_category+": "+clean_hm_from_text_withoutcommo);
		//### System.out.println("totalvac: "+hm_words_length);
		//### System.out.println(location_info_stringlist.size());
		//### System.out.println(clean_hm_from_text_withoutcommo);
		///////////////////////////////////////////////////////////////////////
		double total_added_log_rate = 0 ; 	
		for(String voc_from_text : hm_text.keySet()){
			//### System.out.println("------------------------------------------");
			double voc_from_text_freq = hm_text.get(voc_from_text);	
			//### System.out.println(voc_from_text);
			//### System.out.println("# times occur: " + voc_from_text_freq);	
			//### System.out.println(file_category);
			double cal_rate1 = onevoc_rela_freq_to_cat(clean_hm_from_text_withoutcommo,hm_words_length, voc_from_text, voc_from_text_freq,location_info_stringlist );
			total_added_log_rate += cal_rate1;					
		}			
		//### System.out.println("........................................................................");
//		System.out.println(file_category+" : "+total_added_log_rate);
		return total_added_log_rate;	
	}
	
	
	/**
	 * CALCULATION FUNCTION
	 * For each vocabulary in text, we need to calculate the relative freq relative to category
	 * @param hm_from_category
	 * @param totalVoc_in_HMcategory
	 * @param vac_from_text
	 * @param voc_from_text_freq
	 * @param location_info_stringlist
	 * @return
	 */
	public double onevoc_rela_freq_to_cat(HashMap<String,Double> hm_from_category,double totalVoc_in_HMcategory, String vac_from_text, double voc_from_text_freq, ArrayList<String> location_info_stringlist ){
		double rate1;
		double freqInCat_for_vocInText;
		double cal_rate;
		if(hm_from_category.containsKey(vac_from_text)){// add 1
			freqInCat_for_vocInText = hm_from_category.get(vac_from_text)+ this.smooth_index;
			//### System.out.println("Freq in cat: " + freqInCat_for_vocInText);
			double adjust_size = totalVoc_in_HMcategory + (location_info_stringlist.size()/(1/this.smooth_index));
				rate1 = freqInCat_for_vocInText / adjust_size;
			//### System.out.println("Pi: "+rate1);
			cal_rate = voc_from_text_freq * Math.log(rate1) - log_factorial(voc_from_text_freq);
			//### System.out.println("Final Rate:" + cal_rate);
		}
		else{// 0 + 0.01
			freqInCat_for_vocInText = this.smooth_index;
			//### System.out.println("Freq in cat: " + freqInCat_for_vocInText);
			double adjust_size = totalVoc_in_HMcategory + (location_info_stringlist.size()/(1/this.smooth_index));
			rate1 = freqInCat_for_vocInText / adjust_size;
			//### System.out.println("Pi: "+rate1);
			cal_rate = voc_from_text_freq * Math.log(rate1) - log_factorial(voc_from_text_freq);
			//### System.out.println("Final Rate:" + cal_rate);

		}
		return cal_rate;
	}
	
	/**
	 * Calculate the sum of the log of factorial
	 * @param n, input starting number
	 * @return, result
	 */
	public static double log_factorial(double n) {
        double fact = 1; 
        for (int i = 1; i <= n; i++) {
            fact += Math.log(i);
        }
        return fact;
    }
	
	
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		ArrayList<String> category_file_list = new ArrayList<String>();

		category_file_list.add("CaterData");
		category_file_list.add("ChineseData.txt");
		category_file_list.add("DeliData");
		category_file_list.add("fast_food");	
		category_file_list.add("indianData");
		category_file_list.add("JapanData");
		category_file_list.add("KoreanData");
		category_file_list.add("MexicanData");
		category_file_list.add("PizzaData");
		category_file_list.add("SeafoodData");
		category_file_list.add("ThaiData");
		
		Bay_Model_Rest bm = new Bay_Model_Rest("menudata","menuinfo","","",category_file_list);
		
		ArrayList<String> db_info_field_list = new ArrayList<String>();
		db_info_field_list.add("MenuName");
		
		mongodb db_insert_process = new mongodb("menudata","","");
		db_insert_process.set_collection("cs_restaurant_category");
		ArrayList<Object> distinct_menuid = bm.db.query_distinct_locationid("LocationID");
		System.out.println(distinct_menuid);
		for(Object MenuID : distinct_menuid.toArray()){
			String MenuID_String = ((String)MenuID);
			BasicDBObject bdbo_query = new BasicDBObject("LocationID", MenuID_String);
			System.out.println("LocationID: "+MenuID_String);
			String chosen_category = bm.bay_model_choose_cat( bdbo_query,db_info_field_list);
			System.out.println("FINAL RESULT: "+MenuID_String+" "+chosen_category);
			System.out.println("----------------------------------------------------------------------------------------------");
//			db_insert_process.db_one_level_insert(LocationID_String, chosen_category);
		}
		
	}

}
