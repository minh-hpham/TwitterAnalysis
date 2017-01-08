package TwitterAnalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class TextAnalysis {

	
	String text;
	double score;
	File file = new File("E:\\data\\resultFields.txt");
	BufferedWriter bw ;
	float numberOfIterations=0;
	HashMap<String,Integer>countFrequency=new HashMap<String,Integer>();
	Mappers map;
	TextAnalysis(Mappers map)
	{
		this.map=map;
		try {
			if(!file.exists())
				file.createNewFile();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void calcScore(String text)
	{
		
//		String words[]=text.split("\\s+");
//		numberOfIterations++;
//		HashSet<String>distinctStrings=new HashSet<String>();
//		countFrequency=new HashMap<String,Integer>();
//		for(int i=0;i<words.length;i++)
//		{
//			if(countFrequency.containsKey(words[i]))
//			{
//				int count=countFrequency.get(words[i]);
//				count=+1;
//				countFrequency.put(words[i],count);	
//			}
//			countFrequency.put(words[i],1);	
//			distinctStrings.add(words[i]);
//		}
//		float total=0;
//		for(String word:distinctStrings)
//		{
//			Float value=map.getValues(word);
//			value*=countFrequency.get(word);
//			total+=value;
//		}
//		total/=words.length;
//		//System.out.println(text+"    Havg(T)->"+total);
//		//writeIntoFile(text+"    Havg(T)->"+total);
//		
		
		
		
		
		 writeIntoFile(text);
//		score+=total;
	}

	private void writeIntoFile(String content)
	{
			try {
				FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
				bw = new BufferedWriter(fw);
				bw.write(content);
				bw.newLine();
				bw.newLine();
				bw.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	}
	public float getAverage()
	{
		return (float) (0);
	}

}
