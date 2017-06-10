package org.wit.mytweet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.wit.mytweet.models.FollowTweets;
import org.wit.mytweet.models.Social;
import org.wit.mytweet.models.Tweet;
import org.wit.mytweet.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David-Roche on 9/30/2016.
 */
public class MyTweetDB extends SQLiteOpenHelper {

    //Database Version
    private static final int DATABASE_VERSION = 1;
    //Database Name
    private static final String DATABASE_NAME = "MyTweet";
    //User table name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TWEETS = "tweets";
    private static final String TABLE_SOCIAL = "social";
    private static final String TABLE_FOLLTWEETS = "followtweets";



    //User table Column names
    private static final String KEY_ID = "id";
    private static final String _ID = "_id";
    private static final String KEY_FNAME = "fname";
    private static final String KEY_LNAME = "lname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    //Tweets table column names
    private static final String TWEET_ID = "id";
    private static final String _POSTER = "_id";
    private static final String TWEET_MESSAGE = "message";
    private static final String T_ID = "TweetID";
    private static final String TWEET_DATE = "date";
    private static final String TWEET_UID = "Uid";


    private static final String SOCIAL_ID = "id";
    private static final String SID = "_id";
    private static final String FOLLOWING = "following";
    private static final String FOLLOWER = "follower";
    private static final String FOLFIRSTNAME = "folFname";
    private static final String FOLLASTNAME = "folLname";


    private static final String FOLLTWEET_ID = "id";
    private static final String FOLLMESSAGE = "message";
    private static final String FOLLFNAME = "firstName";
    private static final String FOLLLNAME = "lastName";




    public MyTweetDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        //Create User Table
        String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" + KEY_ID + " INTEGER PRIMARY KEY, " + _ID + " TEXT, " +
                KEY_FNAME + " TEXT, " + KEY_LNAME + " TEXT, " + KEY_EMAIL + " TEXT, " + KEY_PASSWORD + " TEXT " + ")";

        //Create Tweets Table
        String CREATE_TWEETS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TWEETS + " (" + TWEET_ID + " INTEGER PRIMARY KEY, " + T_ID + " TEXT, " + _POSTER + " TEXT, " +
                TWEET_MESSAGE + " TEXT, " + TWEET_DATE + " TEXT, " + TWEET_UID + " INTEGER, " + " FOREIGN KEY (" + TWEET_UID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "));";


        //Create Social Table
        String CREATE_SOCIAL_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SOCIAL + " (" + SOCIAL_ID + " INTEGER PRIMARY KEY, " + SID + " TEXT, " +
                FOLLOWING + " TEXT, " + FOLLOWER + " TEXT, " + FOLFIRSTNAME + " TEXT, " + FOLLASTNAME + " TEXT " + ")";

        //Create Social Table
        String CREATE_FOLLTWEETS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FOLLTWEETS + " (" + FOLLTWEET_ID + " INTEGER PRIMARY KEY, " +
                FOLLMESSAGE + " TEXT, " + FOLLFNAME + " TEXT, " + FOLLLNAME + " TEXT "  + ")";



        db.execSQL(CREATE_USERS_TABLE);

        db.execSQL(CREATE_TWEETS_TABLE);

        db.execSQL(CREATE_SOCIAL_TABLE);

        db.execSQL(CREATE_FOLLTWEETS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOCIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLTWEETS);



        onCreate(db);
    }


    //Adding a new User
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(_ID, user.get_Id()); //User web id
        values.put(KEY_FNAME, user.getFirstName()); //First Name
        values.put(KEY_LNAME, user.getLastName()); //Last Name
        values.put(KEY_EMAIL, user.getEmail()); //Email
        values.put(KEY_PASSWORD, user.getPassword()); //Password

        //Inserting Row
        db.insert(TABLE_USERS, null, values);
        db.close(); //Closing database connection

    }

    //Getting one User
    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_FNAME, KEY_LNAME, KEY_EMAIL, KEY_PASSWORD}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        User user = new User(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5));

        //Return User
        return user;
    }

    //Getting all users

    public List<User> getAllUsers(){
        List <User> userList = new ArrayList<User>();

        //Select All Query

        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Looping through all rows and adding to list

        if(cursor.moveToFirst()){
            do{
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setFirstName(cursor.getString(1));
                user.setLastName(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setPassword(cursor.getString(4));


                //Adding user to list
                userList.add(user);
            }while (cursor.moveToNext());
        }

        //return user list
        return userList;

    }

    //Getting User Count
    public int getUserCount(){
        String countQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        cursor.close();

        //return count
        return cursor.getCount();
    }

    //Updating a User
    public int updateUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FNAME, user.getFirstName()); //First Name
        values.put(KEY_LNAME, user.getLastName()); //Last Name
        values.put(KEY_PASSWORD, user.getPassword()); //Password
        values.put(KEY_EMAIL, user.getEmail()); //Email

        // Updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    //Updating a Username
    public int updateEmail(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, user.getEmail()); //First Name

        // Updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    //Updating a Password
    public int updatePassword(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, user.getPassword()); //Password

        // Updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    //Deleting a User

    public void deleteUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[] {String.valueOf(user.getId())});
        db.close();
    }

    //Tweets Section

    public void addTweet(Tweet tweet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TWEET_MESSAGE, tweet.getMessage()); //Tweet Message
        values.put(TWEET_DATE, tweet.getDate()); //Tweet Date
        values.put(TWEET_UID, tweet.getUid()); //Tweet Poster
        values.put(T_ID, tweet.getTID()); //Tweet ID


        //Inserting Row
        db.insert(TABLE_TWEETS, null, values);
        db.close(); //Closing database connection

    }
    //Web tweet
    public void addWebTweet(Tweet tweet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(T_ID, tweet.getTID()); //tweet id
        values.put(_POSTER, tweet.getPoster());
        values.put(TWEET_MESSAGE, tweet.getMessage()); //Tweet Message
        //values.put(TWEET_DATE, tweet.getDate()); //Tweet Date
        values.put(TWEET_UID, tweet.getUid()); //Tweet Poster

        //Inserting Row
        db.insert(TABLE_TWEETS, null, values);
        db.close(); //Closing database connection

    }

    //Getting all tweets

    public List<Tweet> getAllTweets(){
        List <Tweet> tweetList = new ArrayList<Tweet>();

        //Select All Query

        String selectQuery = "SELECT * FROM " + TABLE_TWEETS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Looping through all rows and adding to list

        if(cursor.moveToFirst()){
            do{
                Tweet tweet = new Tweet();
                tweet.setId(Integer.parseInt(cursor.getString(0)));
                tweet.setTid(cursor.getString(1));
                tweet.setPoster(cursor.getString(2));
                tweet.setMessage(cursor.getString(3));
               // tweet.setDate(cursor.getString(2));
                //tweet.setUid(Integer.parseInt(cursor.getString(3)));

                //Adding tweet to list
                tweetList.add(tweet);
            }while (cursor.moveToNext());
        }

        //return tweet list
        return tweetList;

    }



    //Updating a tweet
    public int updateTweet(Tweet tweet){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TWEET_MESSAGE, tweet.getMessage()); //Message

        return db.update(TABLE_TWEETS, values, TWEET_ID + " = ?",
                new String[]{String.valueOf(tweet.getId())});
    }

    //Getting one Tweet
    public Tweet getTweet(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TWEETS, new String[]{TWEET_ID, TWEET_MESSAGE, TWEET_DATE, TWEET_UID }, TWEET_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        Tweet tweet = new Tweet(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                Integer.parseInt(cursor.getString(3)));
        //Return User
        return tweet;
    }

    //Deleting a Tweet

    public void deleteTweet(Tweet tweet){
        SQLiteDatabase db = this.getWritableDatabase()
                ;
        db.delete(TABLE_TWEETS, TWEET_ID + " = ?",
                new String[] {String.valueOf(tweet.getId())});
        db.close();
    }


    public void addSocial(Social social) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SID, social.get_ID());
        values.put(FOLLOWING, social.getFollowing());
        values.put(FOLLOWER, social.getFollower());
        values.put(FOLFIRSTNAME, social.getFolFname());
        values.put(FOLLASTNAME, social.getFolLname());


        //Inserting Row
        db.insert(TABLE_SOCIAL, null, values);
        db.close(); //Closing database connection

    }

    public void deleteSocial(Social social){
        SQLiteDatabase db = this.getWritableDatabase()
                ;
        db.delete(TABLE_SOCIAL, SOCIAL_ID + " = ?",
                new String[] {String.valueOf(social.getID())});
        db.close();
    }


    public List<Social> getAllSocial(){
        List <Social> socialList = new ArrayList<Social>();

        //Select All Query

        String selectQuery = "SELECT * FROM " + TABLE_SOCIAL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Looping through all rows and adding to list

        if(cursor.moveToFirst()){
            do{
                Social social = new Social();
                social.setId(Integer.parseInt(cursor.getString(0)));
                social.set_id(cursor.getString(1));
                social.setFollowing(cursor.getString(2));
                social.setFollower(cursor.getString(3));
                social.setFolFname(cursor.getString(4));
                social.setFolLname(cursor.getString(5));


                //Adding user to list
                socialList.add(social);
            }while (cursor.moveToNext());
        }

        //return user list
        return socialList;

    }


    public void addFollTWEETS(FollowTweets followTweets) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FOLLMESSAGE, followTweets.getMessage());
        values.put(FOLLFNAME, followTweets.getFirstName());
        values.put(FOLLLNAME, followTweets.getLastName());


        //Inserting Row
        db.insert(TABLE_FOLLTWEETS, null, values);
        db.close(); //Closing database connection

    }


    public List<FollowTweets> getAllFollowTweets(){
        List <FollowTweets> followTweetsList = new ArrayList<FollowTweets>();

        //Select All Query

        String selectQuery = "SELECT * FROM " + TABLE_FOLLTWEETS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Looping through all rows and adding to list

        if(cursor.moveToFirst()){
            do{
                FollowTweets followTweets = new FollowTweets();
                followTweets.setId(Integer.parseInt(cursor.getString(0)));
                followTweets.setMessage(cursor.getString(1));
                followTweets.setFirstName(cursor.getString(2));
                followTweets.setLastName(cursor.getString(3));


                //Adding user to list
                followTweetsList.add(followTweets);
            }while (cursor.moveToNext());
        }

        //return user list
        return followTweetsList;

    }

    public void deleteFOLLW(FollowTweets followTweets){
        SQLiteDatabase db = this.getWritableDatabase()
                ;
        db.delete(TABLE_FOLLTWEETS, FOLLTWEET_ID + " = ?",
                new String[] {String.valueOf(followTweets.getId())});
        db.close();
    }




}


