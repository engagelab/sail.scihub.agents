package sail.schihub.agents;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.encorelab.sail.Event;
import org.encorelab.sail.EventResponder;
import org.encorelab.sail.agent.Agent;
import org.jivesoftware.smack.XMPPException;

import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.geo.impl.GeoRssWhere;
import com.google.gdata.data.media.mediarss.MediaCategory;
import com.google.gdata.data.media.mediarss.MediaDescription;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaTitle;
import com.google.gdata.data.youtube.FormUploadToken;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class GoogleClientSecretAgent extends Agent {

	public static void main(String[] args) {
		
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					GoogleClientSecretAgent agent = new GoogleClientSecretAgent();
					agent.setName("GoogleClientSecretAgent"); // can be omitted

					

				
					
					System.out.println("Connecting...");
					agent.connect("imediamac28.uio.no");

					System.out.println("Logging in...");
					agent.login("googleclientsecretagent",
							"googleclientsecretagent");

					agent.setupYoutubeService();
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

	protected YouTubeService service;
	
	public GoogleClientSecretAgent() {
		

	}
	
	
	public void setupYoutubeService() {
		
		
	}
	
	public void setupEventResponders() {
		/**
		 * {
		 *  eventType: 'video_upload_requested',
		 *  payload: {}
		 *  origin: 'mzukowski'
		 * }
		 *
		 * {
		 *   eventType: 'got_google_client_token',
		 *   payload: {
   		 * 	 		token: 'lkajsdlfkjsdlfkjds'
		 * 		}, origin: 'googleclientsecretagent'
		 * }
		 * When the agent sees an event like:
		 * 
		 * {"eventType":"lookup","payload":{"word":"organic"}}
		 * 
		 * it will respond by triggering an event like:
		 * 
		 * {"eventType":"definition","payload":{"definition":"Of, relating to, or denoting compounds containing carbon
		 *  (other than simple binary compounds and salts) and chiefly or ultimately of biological origin","word":"organic"}}
		 */
		listener.addResponder("video_upload_requested", new EventResponder() {
			public void respond(Event ev) {
				
				String fromJid = ev.getFrom();
				String fromUsername = fromJid.split("/")[1];
				System.out.println("video_upload_requested from " + fromUsername );
				
				String origin = ev.getOrigin();
				
				
				
				try {
					YouTubeService service = new YouTubeService("GoogleClient", "AI39si5IM0aBn_mE7Wgj3brs9Zf-ttNoW1l2fse2xx71oYYOtzNxpK0NDrc7bZpjz8jTdh90HMRaGFwHemSeDd1tDZccwDEthA");
					service.setUserCredentials("encoresignup@gmail.com", "enc0relab");
					VideoEntry newEntry = new VideoEntry();

					YouTubeMediaGroup mg = newEntry.getOrCreateMediaGroup();
					mg.setTitle(new MediaTitle());
					mg.getTitle().setPlainTextContent("My Test Movie");
					mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME, "Autos"));
					mg.setKeywords(new MediaKeywords());
					mg.getKeywords().addKeyword("cars");
					mg.getKeywords().addKeyword("funny");
					mg.setDescription(new MediaDescription());
					mg.getDescription().setPlainTextContent("My description");
					mg.setPrivate(false);
					mg.addCategory(new MediaCategory(YouTubeNamespace.DEVELOPER_TAG_SCHEME, "mydevtag"));
					mg.addCategory(new MediaCategory(YouTubeNamespace.DEVELOPER_TAG_SCHEME, "anotherdevtag"));

					newEntry.setGeoCoordinates(new GeoRssWhere(37.0,-122.0));
					// alternatively, one could specify just a descriptive string
					// newEntry.setLocation("Mountain View, CA");

					URL uploadUrl = new URL("http://gdata.youtube.com/action/GetUploadToken");
					FormUploadToken token = service.getFormUploadToken(uploadUrl, newEntry);

					System.out.println(token.getUrl());
					System.out.println(token.getToken());
					
					
					
					 
					 
					 Map<String,Object> map = new HashMap<String,Object>();
					 map.put("token", token.getToken());
					 
					 Event responseEvent = new Event("got_google_client_token", map, "googleclientsecretagent");
					 responseEvent.toJson();
					 xmpp.sendEvent(responseEvent);
					 
				} catch (AuthenticationException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				

			}
		});
	}
}
