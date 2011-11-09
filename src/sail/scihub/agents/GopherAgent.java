package sail.scihub.agents;

import hirondelle.date4j.DateTime;

import java.net.UnknownHostException;
import java.util.Set;
import java.util.TimeZone;

import org.encorelab.sail.Event;
import org.encorelab.sail.EventResponder;
import org.encorelab.sail.agent.Agent;
import org.jivesoftware.smack.XMPPException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class GopherAgent extends Agent {
	
	private Mongo mongoDBConnection;
	private DB mongoDB;
	
	public static void main(String[] args) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					GopherAgent agent = new GopherAgent();
					agent.setName("TheGopherAgent"); // can be omitted

					System.out.println("Connecting...");
					agent.connect("imediamac28.uio.no");

					System.out.println("Logging in...");
					agent.login("gopheragent",
							"gopheragent");

					System.out.println("Connecting to Mongo...");
					agent.setupDBConnection();
					
					System.out.println("Setting up responders...");
					agent.setupEventResponders();

					System.out.println("Listening for Sail events...");
					agent.listen();

					
					
					System.out.println("Joining groupchat...");
					agent.joinGroupchat("scihub@conference.imediamac28.uio.no");

					// FIXME: is this really the best way to keep the agent alive?
					while (true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							System.out.println("Agent killed.");
						}
					}
				} catch (XMPPException e) {
					System.err.println(e.getMessage());
				}
			}
		};

		thread.start();

		try {
			thread.join();
		} catch (InterruptedException e) {
			System.out.println("Agent killed.");
		}
	}

	
	
	
	/**
	 * Setups of the DB connection
	 */
	public void setupDBConnection() {
		
		try {
			mongoDBConnection = new Mongo( "imediamac28.uio.no" , 27017 );
			mongoDB = mongoDBConnection.getDB( "scihub" );
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}

		
	}
	
	public void printTest() {
		System.out.println("test print....");
		Set<String> colls = mongoDB.getCollectionNames();
		for (String s : colls) {
		    System.out.println(s);
		}
	}
	
	public void setupEventResponders() {
		
		/*
		 * When the agent sees an event like:
		 * 
		 * 
		 * { eventType: 'video_ready', payload: { url: 'http://youtube.com/lakjsdflkjsdfklj'}, origin: 'bob' }
		 * 
		 * it will respond by adding a record to mongo
		 * 
		 * { url: 'http://youtube.com/lakjsdflkjsdfklj', author: 'bob', timestamp: '2011-09...' }
		 */
		listener.addResponder("video_ready", new EventResponder() {
			public void respond(Event ev) {
				String fromJid = ev.getFrom();
				String fromUsername = fromJid.split("/")[1];
				System.out.println("new_submitted_video from " + fromUsername );
				
				String origin = ev.getOrigin();
				
				String url = (String) ev.getPayloadAsMap().get("url");
				
				DBCollection coll = mongoDB.getCollection("videos");
						
		        BasicDBObject doc = new BasicDBObject();
		        doc.put("url", url);
		        doc.put("origin", origin);
		        DateTime now = DateTime.now(TimeZone.getDefault());
		        doc.put("timestamp",now.toString());
		        coll.insert(doc);
			}
		});

}
}
