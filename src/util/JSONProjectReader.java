package util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class JSONProjectReader {

    public static String read(String filename) throws IOException {
    	URL url = Resources.getResource(filename);
    	String text = Resources.toString(url, Charsets.UTF_8);
    	return text;
    }
    
    /**
	 * Get the project JSONObject from the project url
	 * @param projectUrl the url to the project
	 * e.g.
	 * /Users/geoffreykwan/dev/apache-tomcat-5.5.27/webapps/curriculum/236/wise4.project.json
	 * @return the JSONObject for the project
	 */
	public static JSONObject getProjectJSONObject(String projectUrl) {
		JSONObject projectJSONObject = null;
		
		
		try {
			//get the contents of the file as a string
			String projectJSONString = read(projectUrl);
			
			//create a JSONObject from the string
			projectJSONObject = new JSONObject(projectJSONString);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return projectJSONObject;
	}
	
	/**
	 * Parse the project JSONObject to get all the nodes and sequences and
	 * put them into HashMaps so that we can quickly reference them by id
	 * later.
	 * @param projectJSON the project JSONObject
	 * @param nodeIdToNodeOrSequence a HashMap that stores node id to
	 * node or sequence JSONObject
	 * @param fileNameToNodeId a HashMap that stores filename to node id
	 * @param nodeIdToStepNumber a HashMap that stores node id to step number
	 */
	public static void parseProjectJSONObject(JSONObject projectJSON, 
			HashMap<String, JSONObject> nodeIdToNodeOrSequence,
			HashMap<String, String> fileNameToNodeId,
			HashMap<String, String> nodeIdToStepNumber) {
		
		try {
			//get the nodes in the project
			JSONArray projectNodes = projectJSON.getJSONArray("nodes");
			
			//loop through all the nodes
			for(int x=0; x<projectNodes.length(); x++) {
				//get a node
				JSONObject node = projectNodes.getJSONObject(x);
				
				//get the node id
				String identifier = node.getString("identifier");
				
				//get the filename
				String ref = node.getString("ref");
				
				//add the entries into the HashMaps
				fileNameToNodeId.put(ref, identifier);
				nodeIdToNodeOrSequence.put(identifier, node);
			}
			
			//get the sequences in the project
			JSONArray projectSequences = projectJSON.getJSONArray("sequences");
			
			//loop through all the sequences
			for(int y=0; y<projectSequences.length(); y++) {
				//get a sequence
				JSONObject sequence = projectSequences.getJSONObject(y);
				
				//get the node id
				String identifier = sequence.getString("identifier");
				
				//add an entry into the HashMap
				nodeIdToNodeOrSequence.put(identifier, sequence);
			}
			
			//get the start point for the project
			String startPoint = projectJSON.getString("startPoint");
			JSONObject startPointSequence = nodeIdToNodeOrSequence.get(startPoint);

			//parse the project by traversing through the project from start to finish 
			parseNodeStepNumbers("", startPointSequence, nodeIdToNodeOrSequence, nodeIdToStepNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parse the project to calculate step numbers by traversing the project from start
	 * to finish.
	 * @param stepNumber the current step number, this will hold the activity numbers
	 * so that when we get to a step we can put the activity number together with the
	 * step number such as 1.1
	 * @param node the current node
	 * @param nodeIdToNodeOrSequence the HashMap that store node id to node JSONObject
	 * @param nodeIdToStepNumber the HashMap that we will fill with node id to
	 * step number
	 */
	public static void parseNodeStepNumbers(String stepNumber, JSONObject node, HashMap<String, JSONObject> nodeIdToNodeOrSequence, HashMap<String, String> nodeIdToStepNumber) {
		
		try {
			//get the node type
			String nodeType = node.getString("type");
			
			if(node.getString("type") != null && nodeType.equals("sequence")) {
				//node is a sequence
				
				//get the nodes in the sequence
				JSONArray refs = node.getJSONArray("refs");
				
				/*
				 * check if stepNumber is "", if it is "" it means we are on the
				 * start sequence and we do not need to add an entry for that
				 * but we need to add an entry for all activities and steps
				 */
				if(!stepNumber.equals("")) {
					//this is an activity or step
					
					//get the node id
					String identifier = node.getString("identifier");
					
					//add an entry into the HashMap
					nodeIdToStepNumber.put(identifier, stepNumber);
				}
				
				if(refs != null) {
					//loop through all the nodes in the sequence
					for(int x=0; x<refs.length(); x++) {
						//get a child id
						String childRef = refs.getString(x);
						
						//get the JSONObject for the child
						JSONObject childNode = nodeIdToNodeOrSequence.get(childRef);
						
						/*
						 * make the step number, if we are on activity 1,
						 * stepNumber would be 1 and childStepNumber would
						 * be set to 1 at the moment
						 */
						String childStepNumber = stepNumber;
						
						if(!childStepNumber.equals("")) {
							//add a "." between each level
							childStepNumber += ".";
						}
						
						/*
						 * add the step number, if we are on activity 1,
						 * step 2, childStepNumber would be set to
						 * 1.2
						 */
						childStepNumber += (x + 1);
						
						//recursively parse the children's children
						parseNodeStepNumbers(childStepNumber, childNode, nodeIdToNodeOrSequence, nodeIdToStepNumber);
					}
				}
			} else {
				//node is a leaf node
				
				//get the node id
				String identifier = node.getString("identifier");

				//add an entry into the HashMap
				nodeIdToStepNumber.put(identifier, stepNumber);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

    
}
