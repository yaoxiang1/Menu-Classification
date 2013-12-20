import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This static class is serve as a engine to process text from an assigned text file
 * For cat_train() method
 * 1. The MAJOR process to for other class to call to clean an assigned text_file
 * 2. process the string to get the ArrayList
 * 3. process the ArrayList to get the HashMap ( with the frequency for each UNIQUE String )
 * @author alan
 *
 */
public class EngineProcessText {
	

/**
 * the list is used to store the current clean ArrayList from the text file
 */
public static ArrayList<String> total_arraylist = new ArrayList<String>(); 

	@SuppressWarnings("resource")
	/**
	 * This is the process of cleaning the data from the text file
	 * 1. Upload the data from the text file
	 * 2. Split and only get all the alphabetical string
	 * 3. take away most popular 1000 English words from the text file list
	 * 
	 * @param textfile, the text file needed to be cleaned 
	 * @return an ArrayList with the clean text String list, NOTE: the ArrayList contain duplicated words
	 * @throws IOException
	 * 
	 * NOTE: store the array to the static ArrayList in the class!
	 */
	public static ArrayList<String> text_to_arraylist_withdup_withcommon(String textfile) throws IOException{
		String line = "";
		BufferedReader br = new BufferedReader(new FileReader(textfile));	
		while ((line = br.readLine()) != null) {
			ArrayList<String> clean_array = split_clean (line);
			if(clean_array.size()>0){
				total_arraylist.addAll(clean_array);
			}
		}
		
		
		return total_arraylist;	
	}
	
	/**
	 * upload the most popular words to collection, therefore, the ArrayList can use remove all clean the ArrayList
	 * @return the collection with most freq words in it
	 * @throws IOException 
	 */
	public static HashMap<String,String> add_mostwords_to_hm(HashMap<String,String> hm_word_list ) throws IOException{
		
		String line_word = "";
		BufferedReader br_word = new BufferedReader(new FileReader("MostWords"));	
		while ((line_word = br_word.readLine()) != null) {
			hm_word_list.put(line_word,"0");
		}	
		return hm_word_list;
	}
	/**
	 * NOTE: salad dressing.  
	 * 		 "dressing." will be split and "." will not be considered a vocabulary
	 * 
	 * The process of cleaning a string input by splitting it with empty space and only get the alphabetical string
	 * @param input, the string input NOT cleaned
	 * @return output, the string output, cleaned
	 */
	public static ArrayList<String> split_clean(String uncleaned_input){
		/////////////////////////////////////////////////////////////
		// NOTE: before split and clean the data, first need to remove all symbol to blank space
		// 		 replace_symbol_to_word(unprocessed text)
		/////////////////////////////////////////////////////////////
		String cleaned_input = EngineProcessText.replace_symbol_to_word(uncleaned_input);
//		System.out.println("Run!!!");
		String[] split_with_empty = cleaned_input.split(" ");
		ArrayList<String> clean_no_space = new ArrayList<String>();
		for(int i = 0 ; i < split_with_empty.length ; i++){
			if(!split_with_empty[i].equals("") && split_with_empty[i].trim().matches("[a-zA-Z]+")){
				clean_no_space.add(split_with_empty[i].toLowerCase().trim());
			}
		}
		return clean_no_space;
	}
	
	/**
	 * 1. The MAJOR process to for other class to call to clean an assigned text_file
	 * 2. process the string to get the ArrayList
	 * 3. process the ArrayList to get the HashMap ( with the frequency for each UNIQUE String )
	 * @param text_file
	 * @return a HashMap, with UNIQUE key and frequency for the value
	 * @throws IOException
	 */
	public static ArrayList<String>  text_to_clean_arraylist_withcommon(String text_file) throws IOException{
	
		ArrayList<String> CleanList_from_Text = text_to_arraylist_withdup_withcommon(text_file);
		return CleanList_from_Text;
		
	}
	
	/**
	 * CAREFUL!!!! I am doing series of process, first have the clean arraylist, and save it to the current memory of the static 
	 * Then, I put ArrayList to the HashMap and then, clean the memory of the total_arraylist
	 * 
	 * 1. get clean ArrayList
	 * 2. put ArrayList to the HashMap
	 * 3. Finally, I will clean the total_arraylist memory
	 * 
	 * Which means, currently cleanarraylist() and clean_arraylist_to_hm() are the process should run together, 
	 * 
	 * @param CleanList_from_Text
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String,Double> clean_arraylist_to_hm(ArrayList<String> CleanList_from_Text) throws IOException{
		
		HashMap<String,Double> hm = arraylist_to_hashmap(CleanList_from_Text);
		// empty the static HashMap ArrayList
		total_arraylist.clear();
		return hm;
	}
	
	/**
	 * Input a clean ArrayList and output HashMap with frequencies
	 * @param clean_ArrayList
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String,Double> arraylist_to_hashmap(ArrayList<String> clean_ArrayList) throws IOException{
		
		HashMap<String,Double> hm = new HashMap<String,Double>();
		for (int i = 0 ; i < clean_ArrayList.size() ; i++){
			String key = clean_ArrayList.get(i).trim();
			if(key.length()>1&&(key.trim().matches("[a-zA-Z]+")) ){

				if(hm.containsKey(key)){
					hm.put(key, hm.get(key)+1);		
				}
				else{
					hm.put(key, 1.00);		
				}
			}
		}
		return hm;
	}
	
	
	/**
	 * Count the number of Vocabulary for a given HashMap
	 * @param a HashMap, with UNIQUE key and frequency for the value
	 * @return the number of Vocabulary for a given HashMap
	 */
	public static int count_total_voc_in_hash(HashMap<String,Double> hm){
		int total_count = 0;
		for (Entry<String, Double> entry : hm.entrySet()) {
		    Double value = entry.getValue();
		    total_count += value;		    
		}				
		return total_count;		
	}
	
	/**
	 * Replace all the symbol to space
	 * @param unprocessed_text
	 * @return
	 */
	public static String replace_symbol_to_word(String unprocessed_text){
//		System.out.println("!!!"+unprocessed_text);
		String processed_text ;
		processed_text = unprocessed_text.replaceAll("[. | , | ! | & | $ | - | _ | / | : | ' | ; | * | ( | ) |-]", " ");
//		System.out.println("!!!"+processed_text);
		return processed_text;
	}
	
	




















public static void main(String[] args) throws IOException {
	
	
	
	System.out.println(EngineProcessText.replace_symbol_to_word("I,am_alan, !*I li(k)e-Dickinson."));
//	
////	HashMap<String,Double> hm1 = cat_train("ChineseData.txt");
////	System.out.println(hm1);
////	
////	HashMap<String,Double> hm2 = cat_train("PizzaData");
////	System.out.println(hm2);
////	
////	HashMap<String,Double> hm3 = cat_train("MexicanData");
////	System.out.println(hm3);
////
////	HashMap<String,Double> hm4 = cat_train("JapanData");
////	System.out.println(hm4);
////
////	HashMap<String,Double> hm5 = cat_train("fast_food");
////	System.out.println(hm5);
////
////	HashMap<String,Double> hm6 = cat_train("ThaiData");
////	System.out.println(hm6);
////	
////	HashMap<String,Double> hm7 = cat_train("SeafoodData");
////	System.out.println(hm7);
////	
////	HashMap<String,Double> hm8 = cat_train("KoreanData");
////	System.out.println(hm8);
////	
////	HashMap<String,Double> hm9 = cat_train("IndianData");
////	System.out.println(hm9);
////	
////	HashMap<String,Double> hm10 = cat_train("DeliData");
////	System.out.println(hm10);
//	
//	HashMap<String,Double> hm11 = cat_train("test");
//	System.out.println(hm11);
//	
//	
	}
}
