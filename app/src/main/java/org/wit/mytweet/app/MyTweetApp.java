package org.wit.mytweet.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import org.wit.mytweet.database.MyTweetDB;
import org.wit.mytweet.jbcrypt.BCrypt;
import org.wit.mytweet.models.FollowTweets;
import org.wit.mytweet.models.Social;
import org.wit.mytweet.models.Tweet;
import org.wit.mytweet.models.User;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Tosh on 9/29/2016.
 */
public class MyTweetApp extends Application{

    //https://stark-oasis-66285.herokuapp.com
    public String service_url = "http://35.162.89.54:4000";
            //"http://10.0.2.2:4000";
    //https://peaceful-bayou-81612.herokuapp.com

    public boolean         tweetServiceAvailable = false;
    public TweetService tweetService;
    public User             currentUser;

    public List <User>      users        = new ArrayList<User>();
    public List <Tweet>      tweets        = new ArrayList<Tweet>();
    public List <Social>      social        = new ArrayList<Social>();
    public List <Tweet>      followtweets        = new ArrayList<Tweet>();


    // public List<Tweet> mytweets;

    BCrypt bc = new BCrypt();
    SharedPreferences sp;
    MyTweetDB db = new MyTweetDB(this);
    int ID;
    String FNAME;
    String LNAME;
    String _ID;

    public void newUser(User user){
        db.addUser(user);
    }

    public void newTweet(Tweet tweet){
        db.addTweet(tweet);
    }

    public boolean existingEmail (String email){
        for(User user : users){
            if(user.email.equals(email))
            {
                return true;
            }
        }
        return false;
    }

    public boolean validUser (String email, String password)
    {
       // List<User> users = db.getAllUsers();
        for (User user : users)
        {

            if (user.email.equals(email) && BCrypt.checkpw(password,user.password))
            {
                currentUser = user;
                db.addUser(user);
                Log.v("Login Details","hit the Login button" + "  " + email + "  " + password+ "  " );
                SharedPreferences.Editor e = sp.edit();
                Log.v("boom","hit the Login button" + currentUser._id + "  " );
                e.putInt("ID", user.getId());
                e.putString("_ID", currentUser._id);
                e.putString("FNAME",user.getFirstName());
                e.putString("LNAME",user.getLastName());
                e.apply();
                return true;
            }
        }

        return false;
    }


    public ArrayList getTweets(){
        List<Tweet> allTweets = db.getAllTweets();
        ArrayList<Tweet> userTweets = new ArrayList<>();
        for (Tweet tweet : allTweets){
            if(tweet.getUid() == getCurrentUser()){
                userTweets.add(tweet);
            }
        }
        return userTweets;
    }

    public ArrayList getSocial(){
        List<Social> dbSocial = db.getAllSocial();
        ArrayList<Social> userFollowing = new ArrayList<>();
        for (Social social : dbSocial){
//            if(social.getFollower() == currentUser._id){
                userFollowing.add(social);
//            }
        }
        return userFollowing;
    }

    public ArrayList getFolT(){
        List<FollowTweets> dbFolT= db.getAllFollowTweets();
        ArrayList<FollowTweets> userFollowing = new ArrayList<>();
        for (FollowTweets social : dbFolT){
//            if(social.getFollower() == currentUser._id){
            userFollowing.add(social);
//            }
        }
        return userFollowing;
    }

    public void deleteAllTweet(){
        List<Tweet> allTweets = db.getAllTweets();
        for(Tweet tweet: allTweets){
            if(tweet.getUid() == getCurrentUser()){
                db.deleteTweet(tweet);
            }
        }
    }

    public void addtweetstoDB(){
        for(Tweet twe: tweets){
            db.addWebTweet(twe);

        }
    }

    //Populate social table with the people we are following and find their names
    public void addSocialtoDB(){
        for(Social soc: social){
            for(User user: users){
                if(soc.following.equals(user._id)){
                    soc.setFolFname(user.firstName);
                    soc.setFolLname(user.lastName);
                }
            }
            if(soc.follower.equals(currentUser._id)){
                db.addSocial(soc);
            }
        }
    }


    //Populate the people you are following and get their tweets
        public void addFollowtweetstoDB(){
            List<Social> socials = db.getAllSocial();
            for(Social s: socials){
                    if(s.follower.equals(currentUser._id)){
                        for(Tweet t: followtweets){
                            if(t.poster.equals(s.following)){
                                FollowTweets fol = new FollowTweets();
                                fol.setMessage(t.message);
                                fol.setFirstName(s.folFname);
                                fol.setLastName(s.folLname);
                                db.addFollTWEETS(fol);
                            }
                        }

                    }

            }
    }



    public Tweet getSingleTweet(int id){
       return db.getTweet(id);

    }

    public int getCurrentUser(){
        ID = sp.getInt("ID",0); // 0 is default value which means if couldn't find the value in the shared preferences file give it a value of 0.
        return ID;
    }

    public String getCurrentUserFirstName(){
        FNAME = sp.getString("FNAME","");
        return FNAME;
    }

    public User getCurrentUserWeb(){
        //_ID = sp.getString("_ID","");
        return currentUser;
    }

    public String getCurrentUserLastName(){
        LNAME = sp.getString("LNAME", "");
        return LNAME;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(service_url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        tweetService = retrofit.create(TweetService.class);

        Log.v("TweetActivity", "TweetActivity App Started");
        sp = getSharedPreferences("Session", 0); //"Session" is the file name it MUST be the same in all activities that require shared preferences.

    }

    //Called every time app starts to drop previous database session
    public void deleteAllUsers(){
        List<User> allUsers = db.getAllUsers();
        for(User user: allUsers){
                db.deleteUser(user);
        }
    }

    //Called every time app starts to drop previous database session
    public void deleteAllUserTweets(){
        List<Tweet> allTweets = db.getAllTweets();
        for(Tweet twe: allTweets){
            db.deleteTweet(twe);
        }
    }

    //Called every time app starts to drop previous database session
    public void deleteAllSocial(){
        List<Social> allSocial = db.getAllSocial();
        for(Social soc: allSocial){
            db.deleteSocial(soc);
        }
    }

    public void deleteAllFOLT(){
        List<FollowTweets> followTweet = db.getAllFollowTweets();
        for(FollowTweets fol: followTweet){
            db.deleteFOLLW(fol);
        }
    }


}
