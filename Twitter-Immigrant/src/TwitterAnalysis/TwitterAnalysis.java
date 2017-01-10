


package TwitterAnalysis;

import java.sql.Timestamp;
import java.util.Date;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

// SET RUNNABLE
public class TwitterAnalysis implements  Runnable{
	
	  Twitter twitter ;
    private final static String CONSUMER_KEY = "";
    private final static String CONSUMER_KEY_SECRET = "";
    StringBuffer buffer=new StringBuffer();
	private double[][] locations = { {-122.75, 36.8}, {-121.75, 37.8} };
	 Mappers mapper=new Mappers();
	private TextAnalysis textAnalyzer= new TextAnalysis(mapper);;
    public double[][] getLocations() {
		return locations;
	}



	public void setLocations(double[][] locations) {
		this.locations = locations;
	}



	public void run() {

    	ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("ConsumerKey");
        cb.setOAuthConsumerSecret("ConsumerSecret");
        cb.setOAuthAccessToken("Oauth_Access_Token");
        cb.setOAuthAccessTokenSecret("Oauth_Token_Scret");
        
        Configuration conf=cb.build();
        TwitterFactory factory = new TwitterFactory(conf);
         twitter = factory.getInstance();
         
        TwitterStream twitterStream = new TwitterStreamFactory(conf).getInstance();
        
      
        StatusListener listener = new StatusListener() {

			public void onException(Exception arg0) {
                // TODO Auto-generated method stub

            }

            
            public void onDeletionNotice1(StatusDeletionNotice arg0) {
                // TODO Auto-generated method stub

            }

          
            public void onScrubGeo(long arg0, long arg1) {
                // TODO Auto-generated method stub

            }

            public void onStatus(Status status) {
                User user = status.getUser();
                
                // gets Username
                String username = status.getUser().getScreenName();
                buffer.append(username+"\n");
                String profileLocation = user.getLocation();
                buffer.append(profileLocation+"\n");
                long tweetId = status.getId(); 
                String content = status.getText();
                
                
                //for calculating sores
               // mapper.readFile("/Users/surajkath/Documents/HealthHappiness/Data_Set_S1.txt");
               
                
                textAnalyzer.calcScore(status.getId()+"<>userid->"+status.getUser().getName()+"<>message->"+content+"<>geotag->"+status.getGeoLocation().getLatitude()+"  "+status.getGeoLocation().getLongitude()+"<>followers->"+status.getUser().getFollowersCount()+"<>isgeoenabled->"+status.getUser().isGeoEnabled()+"<>timezone->"+status.getUser().getTimeZone()+"<>timestamp->"+new Timestamp(new Date().getTime())+"<>place->"+status.getPlace().getName()+"&"+status.getPlace().getCountry()+"<>location->"+status.getUser().getLocation()+"<>isRetweet->"+status.isRetweet()+"<>isfavourite->"+status.isFavorited()+"<>friendsCount->"+status.getUser().getFriendsCount()+"<>userFacouritesCount->"+status.getUser().getFavouritesCount()+"<>tweetSorce->"+status.getSource());
                buffer.append("location->"+status.getUser().getLocation()+"date "+status.getCreatedAt()+"     geotag"+status.getGeoLocation().getLatitude()+"  "+status.getGeoLocation().getLongitude()+"  place->"+status.getPlace().getName()+"<>"+status.getPlace().getCountry()+"\n");
                buffer.append(content+"\n\n\n");
                
                
             
                
                if(buffer.length()>3000)
                {
                	buffer=new StringBuffer();
                }
                
                try {
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               
               
                
            }

            public void onTrackLimitationNotice(int arg0) {
                // TODO Auto-generated method stub

            }


			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub
				
			}


			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

        };
        FilterQuery fq = new FilterQuery();
      //  String keywords[] = {"football"};
       // fq.track(keywords);
        fq.locations(locations);
        String[] lang = { "en" };
        fq.language(lang);// = "test";

        twitterStream.addListener(listener);
        
       
       
        twitterStream.filter(fq);  
      
    }

    
    
    public StringBuffer getData()
    {
    	return buffer;
    }
    public float getScore()
    {
    	return textAnalyzer.getAverage();
    }
    public static void main(String[] args) throws Exception {
    	new TwitterAnalysis().run();// run the Twitter client
    }
}