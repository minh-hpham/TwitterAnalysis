package TwitterAnalysis;

import java.util.HashMap;
import java.io.*;
public class Mappers {


	HashMap<String ,Float>map=new HashMap<String, Float>();
	
	
	public void readFile(String FilePath)
	{
		
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(FilePath));
			sCurrentLine = br.readLine();
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				String str[]=sCurrentLine.split("\\s+");
				String str1=str[2];
				//System.out.println(str1);
				map.put(str[0],Float.parseFloat(str[2].trim()));
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	public float getValues(String key)
	{
		if(map.containsKey(key))
		{
			return map.get(key);
		}
		return 0;
	}
	
	
	public static void main(String args[])
	{
		Mappers obj=new Mappers();
		obj.readFile("/Users/surajkath/Documents/HealthHappiness/Data_Set_S1.txt");
	}

}
