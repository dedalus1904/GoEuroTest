package forgoeuro;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Java Developer Test for GoEuro. JSON.Simple library was used by implementing.
 * @author eduard rogozhkin
 *
 */
public class GoEuroTest {
	
	public static final String THE_URL = "http://api.goeuro.com/api/v2/position/suggest/en/";
	public static final String HEADER = "id,name,type,latitude,longitude\n";
	public static final String DELIMITER = ",";
	
	/**
	 * Receives data from the server and transforms it into bar String
	 * @param inputArgument Command line argument given by a user
	 * @return  String containing received data
	 * @throws IOException
	 */
	public static String getDataFromServer(String inputArgument) throws IOException {
		URLConnection connection = null;
		InputStream is = null;
		String lineInput;
		
		connection = new URL(THE_URL + inputArgument).openConnection();
		is = connection.getInputStream();
		
		InputStreamReader ireader = new InputStreamReader(is);
		BufferedReader breader = new BufferedReader(ireader);
		StringBuffer wholeInput = new StringBuffer();
		
		while((lineInput = breader.readLine()) != null)
			wholeInput.append(lineInput);
		
		breader.close();
		
		return wholeInput.toString();	
	}
	
	/**
	 * Parses the String with data and puts selected items into array
	 * @param resultString  String to be parsed
	 * @return  Array with selected items
	 */
	public static ArrayList<String> parseJSONArray (String resultString) {
		ArrayList<String> resultArray = new ArrayList<String>();
		JSONParser parser = new JSONParser();
		Object obj = null;
		
		try {
			obj = parser.parse(resultString);
		} catch (ParseException e) {
			System.out.println("Problem by parsing: " + e);
		}
		
		JSONArray array = (JSONArray)obj;
		JSONObject jobj, jobj2;
		for (int i = 0; i < array.size(); i++) {
			jobj = (JSONObject)array.get(i);
			resultArray.add(jobj.get("_id").toString() + DELIMITER);
			resultArray.add(jobj.get("name").toString() + DELIMITER);
			resultArray.add(jobj.get("type").toString() + DELIMITER);
			jobj2 = (JSONObject)jobj.get("geo_position");
			resultArray.add(jobj2.get("latitude").toString() + DELIMITER);
			resultArray.add(jobj2.get("longitude").toString() + "\n");
		}
		
		return resultArray;
	}
	
	/**
	 * Writes selected items into .csv file
	 * @param resultArray  array with parsed data
	 * @throws IOException
	 */
	public static void createFile(ArrayList<String> resultArray) throws IOException {
		FileWriter fWriter = new FileWriter("output.csv");
		fWriter.append(HEADER);
		
		for (int i = 0; i < resultArray.size(); i++) {
			fWriter.append(resultArray.get(i));
		}
		
		fWriter.close();	
	}

	public static void main(String[] args) throws IOException {
		
		String resultString = null;
		
		try {
		resultString = getDataFromServer(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Request without input parameter!");
			System.exit(-1);
		}
		
		if (resultString.equals("[]")) {
			System.out.println("No data available!");
			System.exit(0);
		} else {
		    createFile (parseJSONArray(resultString));
		}
	}
}
