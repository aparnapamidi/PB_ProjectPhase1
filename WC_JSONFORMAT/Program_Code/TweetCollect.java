package tweetsjson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.*;
import java.util.Properties;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

public class TweetCollect {

	private TwitterStream twitterStream;
	private String[] keys;
	Properties prop = new Properties();
	FileOutputStream fos;

	public TweetCollect() {

			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setOAuthConsumerKey("YHNX4nXymQ8KDFQsdV0BEQVuu");
			cb.setOAuthConsumerSecret("xSY7ViIstQ7AxOQ4OiwgixdGqc51eNUc9VJkNXtvTARtcOOP5p");
			cb.setOAuthAccessToken("3631101564-xWgjq1qYEFXsPa3H8CzX0thDpQHODwMs3fC58IQ");
			cb.setOAuthAccessTokenSecret("o6Bvlbm4OYjVCKOcAvK4tkGU4TnQoVJ6uwANJ5XkVEXJm");
			cb.setJSONStoreEnabled(true);
			cb.setIncludeEntitiesEnabled(true);

			twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

	}

	public void startTwitter() {

		try {
			fos = new FileOutputStream(new File("C:\\tweets\\tweets.json")); //output json file
		} catch (IOException e) {
			e.printStackTrace();
		}

		String keyString = "Barack,Obama";
		keys = keyString.split(",");
		for (int i = 0; i < keys.length; i++) {
			keys[i] = keys[i].trim();
		}

		twitterStream.addListener(listener);

		System.out.println("Initiating Twitter Streaming.....");

		// Twitter  filter by keys
		FilterQuery query = new FilterQuery().track(keys);
		twitterStream.filter(query);

	}

	public void stopTwitter() {

		System.out.println("Stopping Twitter Streaming.....");
		twitterStream.shutdown();

		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	StatusListener listener = new StatusListener() {

		// The onStatus method is executed every time a new tweet comes in.
		public void onStatus(Status status) {
			// The EventBuilder is used to build an event using the headers and
			// the raw JSON of a tweet
			String lang = status.getUser().getLang();
			System.out.println( " language:" + status.getUser().getLang());
			System.out.println(status.getUser().getScreenName() + "- " + status.getText());

			try {
				if(lang.equals("en"))
				{
				fos.write(DataObjectFactory.getRawJSON(status).getBytes());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
		public void onScrubGeo(long userId, long upToStatusId) {}
		public void onException(Exception ex) {}
		public void onStallWarning(StallWarning warning) {}
	};

	public static void main(String[] args) throws InterruptedException {

		TweetCollect twitter = new TweetCollect();
		twitter.startTwitter();
		//twitter.stopTwitter();

	}

}
