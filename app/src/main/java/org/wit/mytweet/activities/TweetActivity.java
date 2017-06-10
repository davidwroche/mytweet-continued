package org.wit.mytweet.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.wit.mytweet.database.MyTweetDB;
import org.wit.mytweet.R;
import org.wit.mytweet.app.MyTweetApp;
import org.wit.mytweet.models.Tweet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David-Roche on 9/30/2016.
 */
public class TweetActivity extends AppCompatActivity implements Callback<Tweet>,TextWatcher, View.OnClickListener {

    public Button Tweet;
    public Button Contact;
    public Button Email;
    public EditText Message;
    public EditText Date;
    public TextView Counter;
    public TextView accountUser;
    Tweet atweet = new Tweet();

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final String TAG = TweetActivity.class.getSimpleName();
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    //private GestureDetectorCompat gestureDetector;

    public MyTweetApp app;
    MyTweetDB db = new MyTweetDB(this);
    public ArrayList<Tweet> tweets;


    //Tweet Id passed from tweetlist
    int tweetId = 0;

    //Current Position of the tweet that is passed
    int position = 0;

    //Used for Motion Event to get up & down position
    float x1,x2;
    float y1, y2;




    @Override
    public void onCreate(Bundle savedInstanceState) {

        MyTweetApp app = (MyTweetApp) getApplication();

        tweets = app.getTweets(); //Populates the array for swipe action
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_tweet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Tweet = (Button) findViewById(R.id.homeTweet);
        Contact = (Button) findViewById(R.id.homeContact);
        Email = (Button) findViewById(R.id.homeEmail);
        Message = (EditText) findViewById(R.id.homeMessage);
        Date = (EditText) findViewById(R.id.homeDate);
        Counter = (TextView) findViewById(R.id.homeCount);
        accountUser = (TextView) findViewById(R.id.homeUser);


        Tweet.setOnClickListener(this);
        Contact.setOnClickListener(this);
        Email.setOnClickListener(this);

        Message.addTextChangedListener(this);

        //Set the username for the logged in user
        accountUser.append(" " + app.getCurrentUserFirstName() + " " + app.getCurrentUserLastName());

        //Want to check if the tweet is passed from the tweet list and catch the error if not
        try {
            tweetId = (int) getIntent().getExtras().getSerializable("TWEET_ID");
            atweet = app.getSingleTweet(tweetId);
            Message.setText(atweet.getMessage());
            Tweet.setClickable(false);
            Message.setEnabled(false);
            Date.setEnabled(false);
            gettweetPosition();
        } catch (NullPointerException e) {
        }
    }

    //Lets the tweet user pick a contact
    public void callContacts(View v) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),
                REQUEST_CODE_PICK_CONTACTS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();
            retrieveContactEmail();
        }
    }


    //Gets the contact email from the phonebook and sets the contact button to hold the email
    private void retrieveContactEmail() {

        String contactNumber = null;
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();
        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Email.DATA},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        Contact.setText(contactNumber);
        cursorPhone.close();
        Log.d(TAG, "Contact Email Address: " + contactNumber);
    }


    //Starts Email activity on phone which starts default email application on users phone
    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", Contact.getText().toString(), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                "");
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                Message.getText());
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }


    //Redirect menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, TweetActivityList.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    //Creates the date in this format
    public String createDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    //Posts a tweet if it is longer than 0 characters and adds this tweet to the database
    public void postTweet(View view) {
        MyTweetApp app = (MyTweetApp) getApplication();
        String text = Message.getText().toString();
        if (Message.getText().length() > 0) {

            Tweet tweet = new Tweet(text);
            Call<Tweet> calltweet = (Call<Tweet>) app.tweetService.createTweet(app.getCurrentUserWeb().get_Id(), tweet);
            calltweet.enqueue(this);
            Log.v("user", "Added a tweet");


            //db.addTweet(new Tweet(Message.getText().toString(), Date.getText().toString(), app.getCurrentUser()));
            Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.fade_ins);
            Message.setAnimation(anim1);
            Tweet.setAnimation(anim1);
            Toast toast = Toast.makeText(this, "Tweet posted", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //Otherwise we tell the user they cannot post a blank tweet
            Toast toast = Toast.makeText(this, "Tweet cannot be blank", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    /*We listen for text changed in the tweet box and in turn notify the user when they hit the
    * limit. We disable the button when the character length goes over a certain limit*/
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        count = 140;
        Date.setText(createDate());

        if ((s.length() >= count)) {
            Toast.makeText(this, "We have hit 140 characters", Toast.LENGTH_SHORT).show();
            Tweet.setEnabled(false);
            Tweet.setVisibility(View.INVISIBLE);
            Counter.setBackgroundColor(Color.parseColor("#734387"));
            Message.setBackgroundColor(Color.parseColor("#734387"));
        } else {
            Tweet.setEnabled(true);
            Tweet.setVisibility(View.VISIBLE);
            Counter.setBackgroundColor(Color.TRANSPARENT);
            Message.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            Counter.setText(String.valueOf(140 - s.length()));
        }
    }

    //Redirect menu when buttons are clicked, which in turn resets the text of the menu to empty
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeTweet:
                postTweet(v);
                Message.setText("");
                break;
            case R.id.homeContact:
                callContacts(v);
                break;
            case R.id.homeEmail:
                sendEmail();
                break;
        }
    }

    //We get the position of the tweet if it is a tweet that has already been created
    public void gettweetPosition(){

        for(int i=0; i<tweets.size(); i++){
            if(tweetId == tweets.get(i).getId()){
                position = i;
            }
        }

    }


    /*This handles the swipe event of tweets that have already been created. It gets the
    * position of the tweet that is passed from the list view. It detects where a user touches
    * on the screen and lets them swipe forwards or backwards through the array of tweets*/
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN: {
                x1 = event.getX();
                y1 = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = event.getX();
                y2 = event.getY();

                //if left to right sweep event on screen
                if (x1 < x2) {
                    if (position > 0 && position <= tweets.size() - 1 && tweetId != 0) {
                        position--;
                        Message.setText(tweets.get(position).getMessage());
                        Date.setText(tweets.get(position).getDate());
                        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
                        Message.setAnimation(anim1);
                        Date.setAnimation(anim1);
                        accountUser.setAnimation(anim1);
                        Counter.setAnimation(anim1);
                        Contact.setAnimation(anim1);
                        Email.setAnimation(anim1);
                        Tweet.setAnimation(anim1);
                        Tweet.setClickable(false);
                        Message.setEnabled(false);
                        Date.setEnabled(false);
                    }
                }

                //if right to left sweep event on screen
                if (x1 > x2) {
                    if (position >= 0 && position < tweets.size() - 1 && tweetId != 0) {
                        position++;
                        Message.setText(tweets.get(position).getMessage());
                        Date.setText(tweets.get(position).getDate());
                        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
                        Message.setAnimation(anim1);
                        Date.setAnimation(anim1);
                        accountUser.setAnimation(anim1);
                        Counter.setAnimation(anim1);
                        Contact.setAnimation(anim1);
                        Email.setAnimation(anim1);
                        Tweet.setAnimation(anim1);
                        Tweet.setClickable(false);
                        Message.setEnabled(false);
                        Date.setEnabled(false);
                    }
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onResponse(Call<Tweet> call, Response<Tweet> response) {
        startActivity(new Intent(this, TweetActivity.class));
    }

    @Override
    public void onFailure(Call<Tweet> call, Throwable t) {
    }
}
