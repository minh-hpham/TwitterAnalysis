package correction;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.StyleContext.SmallAttributeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import TwitterAnalysis.Mappers;
import Analysis.MessageExpression;

public class Correction {


	
	HashSet<String>labMitlIst=new HashSet<String>();
	long TweetId;
	BufferedWriter writeFileCorrection;
	BufferedWriter writeFileSynonymns;
	BufferedWriter writeModifiedTweets;
	String modifiedTweet;
	Mappers mapHash=new Mappers();
	public String getModifiedTweet()
	{
		return modifiedTweet;
	}
	public void setModiefiedTweet(String str)
	{
		this.modifiedTweet=str;
	}
	public Correction() {
		File file = new File("/Users/surajkath/Documents/HashTag/stats.txt");
		File fil = new File("/Users/surajkath/Documents/HashTag/statsSynonyms.txt");
		File fin = new File("/Users/surajkath/Documents/HashTag/Tweets.txt");
		// if file doesnt exists, then create it
		mapHash.readFile("/Users/surajkath/Documents/HealthHappiness/Data_Set_S1.txt");;
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				writeFileCorrection = new BufferedWriter(fw);
				fil.createNewFile();
				FileWriter fw1=new FileWriter(fil.getAbsoluteFile());
				writeFileSynonymns=new BufferedWriter(fw1);
				fin.createNewFile();
				FileWriter fw2=new FileWriter(fin.getAbsoluteFile());
				writeModifiedTweets=new BufferedWriter(fw2);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		
	}
	
	public void setTweetId(long TweetId)
	{
		this.TweetId=TweetId;
	}
	
	public void  closeAStreams()
	{
		try {
			writeFileCorrection.close();
			writeFileSynonymns.close();
			writeModifiedTweets.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public HashMap<String,String>  sendGet(StringBuffer message){
        
      HashMap<String, String>suggestions=new HashMap<String, String>();
		
      Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]",
              Pattern.UNICODE_CASE | Pattern.CANON_EQ
                      | Pattern.CASE_INSENSITIVE);
      Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(message);
      String simelysRemovedmessage = unicodeOutlierMatcher.replaceAll("");
      simelysRemovedmessage=simelysRemovedmessage.replaceAll("\\s+", " ");
        String words[]=simelysRemovedmessage.split("\\s+");
        StringBuffer parameter=new StringBuffer();
        for(String str:words)
        {
        	
        	{
        		parameter.append(str+"+");
        	}
        }
        String url = "https://languagetool.org:8081?language=en-US&text="+parameter.toString();
        
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(url);
           
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = dbFactory.newDocumentBuilder();
        	
            int statusCode = client.executeMethod(method);

            if (statusCode != -1) {
            	 System.out.println(method.getResponseBodyAsString());
                Document anotherDocument = builder.parse(method.getResponseBodyAsStream());
            
               NodeList nodes =anotherDocument.getElementsByTagName("error");
               
              
              for(int i=0;i<nodes.getLength();i++)
              {
            	  Node node=nodes.item(i);
            	  String errorMessage=node.getAttributes().getNamedItem("msg").getTextContent();
            	  if(errorMessage.indexOf("Possible spelling mistake")>-1)
            	  {	  Integer start=Integer.parseInt(node.getAttributes().getNamedItem("offset").getTextContent());
	            	  Integer end=Integer.parseInt(node.getAttributes().getNamedItem("errorlength").getTextContent());	            
	            	  String word=null;
	            	  if(start+end<simelysRemovedmessage.length())
	            	   word=simelysRemovedmessage.substring(start, start+end);
	            	  else
	            		  word=simelysRemovedmessage.substring(start, simelysRemovedmessage.length());

	            	  String replace=node.getAttributes().getNamedItem("replacements").getTextContent();
	            	 if(replace!=null&&replace.length()>0)
	            	 {
	            		 int index=replace.indexOf("#");
	            		 if(index==-1)
	            		 {
	            			 index=replace.length();
	            		 }
	            		 suggestions.put(word,replace.substring(0,index));
	            	 }
	            	  
	            	  
            	  }
              }
                
             
            }
            
            String str=message.toString();
            for(Entry<String, String> map:suggestions.entrySet())
            {
            	
            	str=str.replaceAll(map.getKey(), map.getValue());
            	writeFileCorrection.write(TweetId+"<>"+map.getValue()+"<>"+map.getKey()+"|"+"spell");
            	writeFileCorrection.newLine();
            }
          
          modifiedTweet=str;
          
           
        } catch (Exception e) {
          e.printStackTrace();
        }
       
       
        return suggestions;
    }
	
	public StringBuffer  getElongatedCorrection(StringBuffer message) throws IOException
	{
		 
		
		
		String words[]=message.toString().split("\\s+");
		
		 for(int i=0;i<words.length;i++)
		 {
			 StringBuffer buffer=new StringBuffer(words[i]);
			 int len=0;
			 while(len+2<buffer.length())
			 {
				 while(len+2<buffer.length()&&buffer.charAt(len)==buffer.charAt(len+1)&&buffer.charAt(len+1)==buffer.charAt(len+2))
				 {
					 buffer.replace(len, len+1,"");
				 }
				 len=len+1; 
			 }
			 
			 if(!words[i].equalsIgnoreCase(buffer.toString()))
			 {
				 writeFileCorrection.write(TweetId+"<>"+words[i]+"<>"+buffer.toString()+"<>"+"E");
	            writeFileCorrection.newLine();
				 words[i]=buffer.toString();
			 }
			//System.out.print(words[i]+"  "); 
		 }
		 message=new StringBuffer();
		 for(int i=0;i<words.length;i++)
		 {
			 message.append(words[i]+" ");
		 }
		 return message;
	}
	
	public int arrangeSynonyms(StringBuffer message)
	{
		 
		
		Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]",
	              Pattern.UNICODE_CASE | Pattern.CANON_EQ
	                      | Pattern.CASE_INSENSITIVE);
	      Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(message);
	      String simelysRemovedmessage = unicodeOutlierMatcher.replaceAll("");
	      simelysRemovedmessage=simelysRemovedmessage.replaceAll("\\s+", " ");
	        String words[]=simelysRemovedmessage.split("\\s+");
			
		 for(int i=0;i<words.length;i++)
		 {
			 String str=words[i];
			 if(words[i].length()>=5&&mapHash.getValues(words[i].toLowerCase())==0)
			 {
				 String url = "http://words.bighugelabs.com/api/2/a657314a65788064938b7692f0bf6baf/"+words[i]+"/xml";
				 try
				 {
					 HttpClient client = new HttpClient();
			            GetMethod method = new GetMethod(url);
			           method.addRequestHeader("X-Mashape-Key", "B6vq4wjtHEmshBLGVCDXmPYentlOp1tZhfCjsntAkkGU1qt72p");
			            
			            int statusCode = client.executeMethod(method);
			          if(statusCode==500)
			        	  return statusCode;
			            
			            // System.out.println(method.getResponseBodyAsString());
			            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			        	DocumentBuilder builder = dbFactory.newDocumentBuilder();
			  
			        	if(method.getResponseBodyAsString()==null||method.getResponseBodyAsString().length()==0)
			        		continue;
			            
			        	Document anotherDocument = builder.parse(method.getResponseBodyAsStream());
			        	NodeList nodes =anotherDocument.getElementsByTagName("words");
			            nodes=nodes.item(0).getChildNodes();
			            int flag=0;
			            
			            for(int j=0;j<nodes.getLength();j++)
			            {
			            			if(nodes.item(j).getAttributes().getNamedItem("r").toString().indexOf("syn")>-1)
			            			if(mapHash.getValues(nodes.item(j).getTextContent())!=0)
			            			{
			            				writeFileSynonymns.write(TweetId+"|"+words[i]+"|"+nodes.item(j).getTextContent()+"|"+"Syn");
			            				writeFileSynonymns.newLine();
			            				
			            				System.out.println(TweetId+"|"+words[i]+"|"+nodes.item(j).getTextContent()+"|"+"Syn");
			            				words[i]=nodes.item(j).getTextContent();
			            				flag=1;
			            				break;
			            			}
			            		
			            }
			            if(flag==0)
			            {
			            	System.out.println(TweetId+"|"+words[i]+"|"+"No Syn Found");
			            	writeFileSynonymns.write(TweetId+"|"+words[i]+"|"+"No Syn Found");
			            	writeFileSynonymns.newLine();
			            }
        				
				 }
				 catch(Exception e)
				 {
					
					 System.out.println("Synonyms error=>"+TweetId+words[i]);
				 }
			 }
		 }
		 
		 return 0;
		
		
	}
	public static void main(String args[]) throws Exception
	{
		Correction obj=new Correction();
		
		File fin=new File("/Users/surajkath/Documents/HashTag/result.txt");
		FileInputStream fis = new FileInputStream(fin);
		 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line = null;
		int count=0;
		while ((line = br.readLine()) != null) {
			
			if(line.length()>0)
			{line=line.replaceAll("\"", "");
			
			byte[] utf8Bytes = line.getBytes("UTF-8");

			line = new String(utf8Bytes, "UTF-8");
			
			try{
			
//				if(count==19)
//				{
//					System.out.println("going to sleep");
//					Thread.sleep(1000*60);
//					count=0;
//				}
		    String array[]=line.split("<>");
		    obj.setTweetId(Long.parseLong(array[0]));
			StringBuffer message=new StringBuffer(array[1].substring(array[1].indexOf("->")+2));
			obj.setModiefiedTweet(message.toString());
//			message=obj.getElongatedCorrection(message);
//			obj.sendGet(message);
			int status=obj.arrangeSynonyms(message);
			System.out.println("status is->"+status);
			if(count==10000)
			{
				System.out.println("usage exceeded");
				break;
			}
			
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				System.out.println(e.getMessage()+" for Id->"+obj.TweetId);
				
			}
			obj.writeModifiedTweets.write(obj.TweetId+"<>"+obj.getModifiedTweet());
			 obj.writeModifiedTweets.newLine();
			count++;
			}
		}
		
		obj.closeAStreams();
	 
		br.close();
		
		
	}

}
