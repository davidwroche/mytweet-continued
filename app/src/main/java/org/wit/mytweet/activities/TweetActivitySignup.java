package org.wit.mytweet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.wit.mytweet.database.MyTweetDB;
import org.wit.mytweet.R;
import org.wit.mytweet.app.MyTweetApp;
import org.wit.mytweet.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tosh on 9/26/2016.
 */
public class TweetActivitySignup extends AppCompatActivity implements Callback<User> ,View.OnClickListener {

    public Button Signup;
    public EditText firstName;
    public EditText lastName;
    public EditText email;
    public EditText password;

   //We are going to use this database
    MyTweetDB db = new MyTweetDB(this);
    public MyTweetApp app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity_tweet);

        Signup =  (Button)findViewById(R.id.signbut);
        firstName = (EditText)findViewById(R.id.signfn);
        lastName = (EditText)findViewById(R.id.signln);
        email = (EditText)findViewById(R.id.signem);
        password = (EditText)findViewById(R.id.signpass);

        Animation anim1 = AnimationUtils.loadAnimation(this,R.anim.fade_ins);
        Signup.setAnimation(anim1);
        firstName.setAnimation(anim1);
        lastName.setAnimation(anim1);
        email.setAnimation(anim1);
        password.setAnimation(anim1);
        Signup.setOnClickListener(this);

    }

    //We check if all the fields have been filled on the register page
    public void registerUser(View view){

            User user = new User(firstName.getText().toString(), lastName.getText().toString(),
                    email.getText().toString(), password.getText().toString());
            MyTweetApp app = (MyTweetApp) getApplication();
            Call<User> call = (Call<User>) app.tweetService.createUser(user);
            call.enqueue(this);
            Log.v("user", "Added a user");
    }


    //Calls method above and check whether all checks have been validated
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signbut:
                registerUser(v);
        }
    }

    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        //app.users.add(response.body());
        startActivity(new Intent(this, TweetActivityWelcome.class));
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        app.tweetServiceAvailable = false;
        Toast toast = Toast.makeText(this, "Tweet Service Unavailable. Try again later", Toast.LENGTH_LONG);
        toast.show();
        startActivity (new Intent(this, TweetActivityWelcome.class));
    }
}
