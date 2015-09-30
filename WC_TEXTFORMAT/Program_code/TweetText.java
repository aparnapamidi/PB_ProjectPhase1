package Tweets;

import twitter4j.*;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import java.util.Map;

public class TweetText{
      
		//CONSUMER KEY and SECRET
		private static final String CONSUMER_KEY		= "YHNX4nXymQ8KDFQsdV0BEQVuu";
		private static final String CONSUMER_SECRET 	= "xSY7ViIstQ7AxOQ4OiwgixdGqc51eNUc9VJkNXtvTARtcOOP5p";
		private static final int TWEETS_PER_QUERY		= 1000;
		private static final int MAX_QUERIES			= 1000;

		private static final String SEARCH_TERM			= "ronaldo";


		
		public static String cleanText(String text)
		{
			text = text.replace("\n", "\\n");
			text = text.replace("\t", "\\t");

			return text;
		}

		public static OAuth2Token getOAuth2Token()
		{
			OAuth2Token token = null;
			ConfigurationBuilder cb;

			cb = new ConfigurationBuilder();
			cb.setApplicationOnlyAuthEnabled(true);

			cb.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET);

			try
			{
				token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
			}
			catch (Exception e)
			{
				System.out.println("Could not get OAuth2 token");
				e.printStackTrace();
				System.exit(0);
			}

			return token;
		}

		
		public static Twitter getTwitter()
		{
			OAuth2Token token;

			//	To get a "bearer" token that can be used for our requests
			token = getOAuth2Token();

			ConfigurationBuilder cb = new ConfigurationBuilder();

			cb.setApplicationOnlyAuthEnabled(true);

			cb.setOAuthConsumerKey(CONSUMER_KEY);
			cb.setOAuthConsumerSecret(CONSUMER_SECRET);

			cb.setOAuth2TokenType(token.getTokenType());
			cb.setOAuth2AccessToken(token.getAccessToken());

			// Twitter object creation
			return new TwitterFactory(cb.build()).getInstance();

		}

		public static void main(String[] args)
		{
			//	Tweet count
			int	totalTweets = 0;
			//   To retrieve additional blocks of tweets back in time.
			long maxID = -1;

			Twitter twitter = getTwitter();

			//	Now do a simple search to show that the tokens work
			try
			{

				//	returns rate limits with the Twitter API
				Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");

				//	returns rate limit  for the search API call
				RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");


				//	This is the loop that retrieve multiple blocks of tweets from Twitter
				for (int queryNumber=0;queryNumber < MAX_QUERIES; queryNumber++)
				{
					System.out.printf("\n\n!!! Starting Stream %d\n\n", queryNumber);

					if (searchTweetsRateLimit.getRemaining() == 0)
					{
						System.out.printf("Limit reached !!! Sleeping for %d seconds\n", searchTweetsRateLimit.getSecondsUntilReset());
						Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset()+2) * 1000l);
					}

					Query q = new Query(SEARCH_TERM);			
					q.setCount(TWEETS_PER_QUERY);				
					q.resultType(Query.RECENT);						
					q.setLang("en");							

					//	maxID is -1 means its first call 
					if (maxID != -1)
					{
						q.setMaxId(maxID - 1);
					}

					//	Search the tweets
					QueryResult r = twitter.search(q);

					if (r.getTweets().size() == 0)
					{
						break;			
					}

					for (Status s: r.getTweets())				
					{
						//	Increment tweets counter
						totalTweets++;

						//	Check for the lowest tweet ID to retrieve multiple blocks of tweets
						if (maxID == -1 || s.getId() < maxID)
						{
							maxID = s.getId();
						}	
						System.out.printf(" @%-20s \t  %s\n",
										  s.getUser().getScreenName(),
										  cleanText(s.getText()));

					}

					searchTweetsRateLimit = r.getRateLimitStatus();
				}

			}
			catch (Exception e)
			{
				
				System.out.println("Exception recieved");

				e.printStackTrace();

			}

			System.out.printf("\n\nA total number of tweets = %d \n", totalTweets);
			
		}

	}
