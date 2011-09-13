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

import com.google.gdata.client.Query;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Category;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.data.geo.impl.GeoRssWhere;
import com.google.gdata.data.media.mediarss.MediaCategory;
import com.google.gdata.data.media.mediarss.MediaDescription;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaPlayer;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.media.mediarss.MediaTitle;
import com.google.gdata.data.youtube.FormUploadToken;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaContent;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeMediaRating;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.data.youtube.YtPublicationState;
import com.google.gdata.data.youtube.YtStatistics;
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

					// FIXME: is this really the best way to keep the agent
					// alive?
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

	public static void printVideoFeed(VideoFeed videoFeed, boolean detailed) {
		for (VideoEntry videoEntry : videoFeed.getEntries()) {
			printVideoEntry(videoEntry, detailed);
		}
	}

	public static boolean printVideoEntry(VideoEntry videoEntry,
			boolean detailed) {
		System.out.println("Title: " + videoEntry.getTitle().getPlainText());

		if (videoEntry.isDraft()) {
			System.out.println("Video is not live");
			YtPublicationState pubState = videoEntry.getPublicationState();
			if (pubState.getState() == YtPublicationState.State.PROCESSING) {
				System.out.println("Video is still being processed.");
				return false;
			} else if (pubState.getState() == YtPublicationState.State.REJECTED) {
				System.out.print("Video has been rejected because: ");
				System.out.println(pubState.getDescription());
				System.out.print("For help visit: ");
				System.out.println(pubState.getHelpUrl());
				return true;
			} else if (pubState.getState() == YtPublicationState.State.FAILED) {
				System.out.print("Video failed uploading because: ");
				System.out.println(pubState.getDescription());
				System.out.print("For help visit: ");
				System.out.println(pubState.getHelpUrl());
				return true;
			}
		}

//		if (videoEntry.getEditLink() != null) {
//			System.out.println("Video is editable by current user.");
//		}
//
//		if (detailed) {
//
//			YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
//
//			System.out.println("Uploaded by: " + mediaGroup.getUploader());
//
//			System.out.println("Video ID: " + mediaGroup.getVideoId());
//			System.out.println("Description: "
//					+ mediaGroup.getDescription().getPlainTextContent());
//
//			MediaPlayer mediaPlayer = mediaGroup.getPlayer();
//			System.out.println("Web Player URL: " + mediaPlayer.getUrl());
//			MediaKeywords keywords = mediaGroup.getKeywords();
//			System.out.print("Keywords: ");
//			for (String keyword : keywords.getKeywords()) {
//				System.out.print(keyword + ",");
//			}
//
//			GeoRssWhere location = videoEntry.getGeoCoordinates();
//			if (location != null) {
//				System.out.println("Latitude: " + location.getLatitude());
//				System.out.println("Longitude: " + location.getLongitude());
//			}
//
//			Rating rating = videoEntry.getRating();
//			if (rating != null) {
//				System.out.println("Average rating: " + rating.getAverage());
//			}
//
//			YtStatistics stats = videoEntry.getStatistics();
//			if (stats != null) {
//				System.out.println("View count: " + stats.getViewCount());
//			}
//			System.out.println();
//
//			System.out.println("\tThumbnails:");
//			for (MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails()) {
//				System.out.println("\t\tThumbnail URL: "
//						+ mediaThumbnail.getUrl());
//				System.out.println("\t\tThumbnail Time Index: "
//						+ mediaThumbnail.getTime());
//				System.out.println();
//			}
//
//			System.out.println("\tMedia:");
//			for (YouTubeMediaContent mediaContent : mediaGroup
//					.getYouTubeContents()) {
//				System.out.println("\t\tMedia Location: "
//						+ mediaContent.getUrl());
//				System.out.println("\t\tMedia Type: " + mediaContent.getType());
//				System.out.println("\t\tDuration: "
//						+ mediaContent.getDuration());
//				System.out.println();
//			}
//
//			for (YouTubeMediaRating mediaRating : mediaGroup
//					.getYouTubeRatings()) {
//				System.out
//						.println("Video restricted in the following countries: "
//								+ mediaRating.getCountries().toString());
//			}
//		}

		return true;
	}

	public void setupEventResponders() {

		/**
		 * sends a message when the video is uploaded
		 * 
		 * { eventType: 'video_uploaded', payload: { id: "id of the video"
		 * token: "upload token" }, origin: 'googleclient }
		 * 
		 * { eventType: 'video_ready', payload: { token: 'upload token' url:
		 * 'url where the video is stored' }, origin: 'googleclientsecretagent'
		 * }
		 */
		listener.addResponder("video_uploaded", new EventResponder() {
			public void respond(Event ev) {

				String fromJid = ev.getFrom();
				String fromUsername = fromJid.split("/")[1];
				System.out.println("video_uploaded from " + fromUsername);

				String origin = ev.getOrigin();

				String id = (String) ev.getPayloadAsMap().get("id");
				String token = (String) ev.getPayloadAsMap().get("token");
				try {
					System.out.println("starting youtube service...");
					YouTubeService service = new YouTubeService(
							"GoogleClient",
							"AI39si5IM0aBn_mE7Wgj3brs9Zf-ttNoW1l2fse2xx71oYYOtzNxpK0NDrc7bZpjz8jTdh90HMRaGFwHemSeDd1tDZccwDEthA");
					service.setUserCredentials("encoresignup@gmail.com",
							"enc0relab");

					String videoEntryUrl = "http://gdata.youtube.com/feeds/api/videos/"
							+ id;
					VideoEntry videoEntry = service.getEntry(new URL(
							videoEntryUrl), VideoEntry.class);
					boolean isFound = printVideoEntry(videoEntry, true);

					System.out.println("links: " + videoEntry.getLinks());
					System.out.println("html link " + videoEntry.getHtmlLink());
					while (isFound == false) {
						try {
							Thread.sleep(3000);
							System.out
									.println("checking you to see if it is finished...");
							
							videoEntry = service.getEntry(new URL(
									videoEntryUrl), VideoEntry.class);
							isFound = printVideoEntry(videoEntry, true);
						} catch (InterruptedException e) {
							System.out.println("checking video killed");
							e.printStackTrace();
						}
					}
					;

					Map<String, Object> map = new HashMap<String, Object>();

					String url = "http://youtube.com/v/" + id;
					System.out.println("url: " + url);
					map.put("url",url );
					map.put("token", token);

					Event responseEvent = new Event("video_ready", map,
							"googleclientsecretagent");
					responseEvent.toJson();
					xmpp.sendEvent(responseEvent);

				} catch (AuthenticationException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ServiceException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		/**
		 * 
		 * { eventType: 'video_upload_requested', payload: {} origin:
		 * 'originator' }
		 * 
		 * { eventType: 'got_google_client_token', payload: { token: 'the upload
		 * token' }, origin: 'googleclientsecretagent' }
		 * 
		 */
		listener.addResponder("video_upload_requested", new EventResponder() {
			public void respond(Event ev) {

				String fromJid = ev.getFrom();
				String fromUsername = fromJid.split("/")[1];
				System.out.println("video_upload_requested from "
						+ fromUsername);

				String origin = ev.getOrigin();

				try {
					System.out.println("starting youtube service...");
					YouTubeService service = new YouTubeService(
							"GoogleClient",
							"AI39si5IM0aBn_mE7Wgj3brs9Zf-ttNoW1l2fse2xx71oYYOtzNxpK0NDrc7bZpjz8jTdh90HMRaGFwHemSeDd1tDZccwDEthA");
					service.setUserCredentials("encoresignup@gmail.com",
							"enc0relab");
					VideoEntry newEntry = new VideoEntry();

					YouTubeMediaGroup mg = newEntry.getOrCreateMediaGroup();
					mg.setTitle(new MediaTitle());
					mg.getTitle().setPlainTextContent("My Test Movie");
					mg.addCategory(new MediaCategory(
							YouTubeNamespace.CATEGORY_SCHEME, "Autos"));
					mg.setKeywords(new MediaKeywords());
					mg.getKeywords().addKeyword("cars");
					mg.getKeywords().addKeyword("funny");
					mg.setDescription(new MediaDescription());
					mg.getDescription().setPlainTextContent("My description");
					mg.setPrivate(false);
					mg.addCategory(new MediaCategory(
							YouTubeNamespace.DEVELOPER_TAG_SCHEME, "scihub"));
					mg.addCategory(new MediaCategory(
							YouTubeNamespace.DEVELOPER_TAG_SCHEME,
							"anotherdevtag"));

					// newEntry.setGeoCoordinates(new GeoRssWhere(37.0,-122.0));
					// alternatively, one could specify just a descriptive
					// string
					// newEntry.setLocation("Mountain View, CA");

					URL uploadUrl = new URL(
							"http://gdata.youtube.com/action/GetUploadToken");
					System.out.println("requesting token...");
					FormUploadToken token = service.getFormUploadToken(
							uploadUrl, newEntry);
					System.out.println("done...");
					System.out.println(token.getUrl());
					System.out.println(token.getToken());

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("token", token.getToken());
					map.put("url", token.getUrl());

					Event responseEvent = new Event("got_google_client_token",
							map, "googleclientsecretagent");
					responseEvent.toJson();
					xmpp.sendEvent(responseEvent);

				} catch (AuthenticationException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ServiceException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
