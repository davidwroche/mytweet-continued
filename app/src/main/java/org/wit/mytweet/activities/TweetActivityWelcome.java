package org.wit.mytweet.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.wit.mytweet.database.MyTweetDB;
import org.wit.mytweet.R;
import org.wit.mytweet.app.MyTweetApp;
import org.wit.mytweet.models.Social;
import org.wit.mytweet.models.Tweet;
import org.wit.mytweet.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TweetActivityWelcome extends AppCompatActivity implements Callback<List<User>> ,View.OnClickListener
{

    public MyTweetApp app;
    public MyTweetDB db;


    public Button Signup;
    public Button Login;
    public TextView Header;
    public TextView subHeader;
    public TextView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity_tweet);

        //Loads Animation for each button of TextView
        Login =  (Button)findViewById(R.id.welcomeLogin);
        Animation animlog = AnimationUtils.loadAnimation(this,R.anim.zoom_in);

        Signup =  (Button)findViewById(R.id.welcomeSignup);
        Animation animsign = AnimationUtils.loadAnimation(this,R.anim.zoom_in);
        back = (TextView) findViewById(R.id.back);

        Header = (TextView) findViewById(R.id.header);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        subHeader = (TextView) findViewById(R.id.subheader);
        Animation anim1 = AnimationUtils.loadAnimation(this,R.anim.fade_ins);

        back.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bird,0,0,0);
        Animation anim2 = AnimationUtils.loadAnimation(this,R.anim.rotate);


        //Assigns specific animation to each Button or TextView
        Header.setAnimation(anim);
        subHeader.setAnimation(anim1);
        back.setAnimation(anim2);
        Login.setAnimation(animlog);
        Signup.setAnimation(animsign);
        Login.setOnClickListener(this);
        Signup.setOnClickListener(this);

        app = (MyTweetApp) getApplication();


    }


    @Override
    public void onResume()
    {
        super.onResume();
        app.deleteAllUserTweets(); //Deletes all user tweets in database
        app.deleteAllUsers(); //Deletes any previous users of the app
        app.deleteAllSocial(); //Deletes any social aspect still on the device
        app.deleteAllFOLT();
        app.currentUser = null;

        Call<List<User>> call1 = (Call<List<User>>) app.tweetService.getAllUsers();
        call1.enqueue(this);
    }

   /* //Redirects User toward either activity
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcomeLogin:
                Intent login = new Intent(this,TweetActivityLogin.class);
                startActivity(login);
                break;
            case R.id.welcomeSignup:
                Intent signup = new Intent(this,TweetActivitySignup.class);
                startActivity(signup);
                break;
        }
    }*/

    void serviceUnavailableMessage()
    {
        Toast toast = Toast.makeText(this, "Tweet Service Unavailable. Try again later", Toast.LENGTH_LONG);
        toast.show();
    }

    void serviceAvailableMessage()
    {
        Toast toast = Toast.makeText(this, "Tweet Contacted Successfully", Toast.LENGTH_LONG);
        toast.show();
    }



    @Override
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        serviceAvailableMessage();
        app.users = response.body();
        app.tweetServiceAvailable = true;
    }

    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        app.tweetServiceAvailable = false;
        serviceUnavailableMessage();
    }

    @Override
       public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcomeLogin:
                if (app.tweetServiceAvailable)
                {
                    Intent login = new Intent(this,TweetActivityLogin.class);
                    startActivity(login);
                }
                else
                {
                    serviceUnavailableMessage();
                }
                break;
            case R.id.welcomeSignup:
                if (app.tweetServiceAvailable)
                {
                    Intent signup = new Intent(this,TweetActivitySignup.class);
                    startActivity(signup);
                }
                else
                {
                    serviceUnavailableMessage();
                }
                break;
              }
      }
}
