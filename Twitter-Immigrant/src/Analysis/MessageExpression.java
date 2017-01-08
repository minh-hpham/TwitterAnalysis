package Analysis;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;





public class MessageExpression {

	
	
	
	public String sendPost(String message){
        String url = "http://text-processing.com/api/sentiment/";
        InputStream in = null;

        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(url);

            //Add any parameter if u want to send it with Post req.
            method.addParameter("text", message);

            int statusCode = client.executeMethod(method);

            if (statusCode != -1) {
                in = method.getResponseBodyAsStream();
            }
            
            String emotion=method.getResponseBodyAsString();
            System.out.println(emotion);
            System.out.println(emotion.substring(emotion.indexOf("label")+"label".length()+1,emotion.lastIndexOf("}")));
            String value=emotion.substring(emotion.indexOf("label")+"label".length()+1,emotion.lastIndexOf("}"));
            
            return value;
        } catch (Exception e) {
            //e.printStackTrace();
        }
		return "can't say";

    }
	
	public static void main(String args[]) throws Exception
	{
		MessageExpression obj=new MessageExpression();
		
		
	}
	
}
