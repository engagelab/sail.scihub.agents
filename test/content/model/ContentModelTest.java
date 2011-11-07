package content.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import util.JSONProjectReader;

public class ContentModelTest {

	@Ignore
	public void test() throws IOException {
			String read = JSONProjectReader.read("wise4.project.json");
			
			
			HashMap<String, JSONObject> nodeIdToNodeOrSequence = new HashMap<String, JSONObject>();
			HashMap<String, String> fileNameToNodeId = new HashMap<String, String>();
			HashMap<String, String> nodeIdToStepNumber = new HashMap<String, String>();
			
			//get the nodes in the project
			
			JSONObject projectJSONObject = JSONProjectReader.getProjectJSONObject("wise4.project.json");
			
			JSONProjectReader.parseProjectJSONObject(projectJSONObject, nodeIdToNodeOrSequence, fileNameToNodeId, nodeIdToStepNumber);
			
			 Iterator it = fileNameToNodeId.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			        System.out.println(pairs.getKey() + " = " + pairs.getValue());
			        
			        
			        String nodeJson = JSONProjectReader.read((String) pairs.getValue());
			        
			        System.out.println(nodeJson);
			        
			    }
			
			System.out.println("done");
	}
	
	@Test
	public void testGet() throws ClientProtocolException, IOException {
		 // Create an instance of HttpClient.
		
	     HttpClient httpclient = new DefaultHttpClient();
	        try {
	            HttpGet httpget = new HttpGet("http://localhost:9000/media?url=tony&type=IMAGE&origin=obama");

	            System.out.println("executing request " + httpget.getURI());

	            // Create a response handler
	            ResponseHandler<String> responseHandler = new BasicResponseHandler();
	            String responseBody = httpclient.execute(httpget, responseHandler);
	            System.out.println("----------------------------------------");
	            System.out.println(responseBody);
	            System.out.println("----------------------------------------");

	        } finally {
	            // When HttpClient instance is no longer needed,
	            // shut down the connection manager to ensure
	            // immediate deallocation of all system resources
	            httpclient.getConnectionManager().shutdown();
	        }
	    }
		

}
