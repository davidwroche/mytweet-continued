package org.wit.mytweet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.wit.mytweet.database.MyTweetDB;
import org.wit.mytweet.R;
import org.wit.mytweet.app.MyTweetApp;
import org.wit.mytweet.models.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tosh on 10/3/2016.
 */
public class TweetActivityList extends AppCompatActivity implements Callback<List<Tweet>>, AdapterView.OnItemClickListener,AbsListView.MultiChoiceModeListener{
    private ListView listView;
    public MyTweetApp app;
    private TweetAdapter adapter;
    private ArrayList<Tweet> tweets;
    MyTweetDB db = new MyTweetDB(this);
    Timer timer2 = new Timer();
    private List<Tweet> holder = new ArrayList<Tweet>();
    Timer timer = new Timer(true);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_tweet);
        listView = (ListView)findViewById(R.id.tweetList);
        //Animation anim1 = AnimationUtils.loadAnimation(this,R.anim.fade_ins);
        //listView.setAnimation(anim1);
        app = (MyTweetApp)getApplication();
        tweets = (ArrayList<Tweet>) app.getTweets();


        Toast.makeText(this, "Retrieving alltweets list ", Toast.LENGTH_SHORT).show();


        adapter = new TweetAdapter(this, tweets);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);
        automaticTweetUpdater();

    }

/*
Stolen from:
    http://stackoverflow.com/questions/6313986/android-timer-timertask-causing-my-app-to-crash
*/
   public void automaticTweetUpdater(){
       final Handler handler = new Handler();
        TimerTask testing = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        retrieveTweets();
                    }

                });
            }
        };
       timer2.schedule(testing, 10000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Tweet tweet = adapter.getItem(position);
        Intent intent = new Intent(this, TweetActivity.class);
        intent.putExtra("TWEET_ID", tweet.getId());
        startActivity(intent);
        timer2.cancel();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.tweet_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyTweetApp app = (MyTweetApp) getApplication();
        switch (item.getItemId()) {
            case R.id.action_clear:
                clearTweets();
                startActivity(new Intent(this, TweetActivityList.class));
                break;

            case R.id.action_settings:
                timer2.cancel();
                startActivity(new Intent(this, TweetSettings.class));
                break;

            case R.id.socialList:
                timer2.cancel();
                startActivity(new Intent(this, SocialActivityList.class));
                break;

            case R.id.action_refresh:
                timer2.cancel();
                retrieveTweets();
                startActivity(getIntent());
                timer2.cancel();
                break;

            case R.id.action_play:
                retrieveTweets();
                break;

            case R.id.followingTweets:
                timer2.cancel();
                startActivity(new Intent(this, TweetActivityFollowing.class));
                break;

            case R.id.action_pause:
                Toast.makeText(this, "Automatic tweet retrieval paused ", Toast.LENGTH_SHORT).show();
                timer2.cancel();
                break;

            case R.id.plus_sign:
                timer2.cancel();
                Intent intent = new Intent(this, TweetActivity.class);
                this.startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void retrieveTweets() {
        String size = String.valueOf(app.tweets.size());
        Log.i("size is", "" + size);
        Toast.makeText(this, "Retrieving tweets list ", Toast.LENGTH_SHORT).show();
        Log.i("Here","" + app.getCurrentUserWeb()._id);
        Call<List<Tweet>> call = (Call<List<Tweet>>) app.tweetService.findTweets(app.getCurrentUserWeb().get_Id());
        call.enqueue(this);
    }



    public void deleteSelectedTweets() {
        List<Tweet> allTweets = db.getAllTweets();
        for (Tweet tweet : allTweets) {
            for (Tweet tweet1 : holder) {
                if (tweet.getId() == tweet1.getId()) {
                    DeleteRemoteTweet deltweet = new DeleteRemoteTweet();
                    Call<String> call = app.tweetService.deleteOne(tweet.getTID());
                    call.enqueue(deltweet);
                    db.deleteTweet(tweet);
                }
            }


        }
    }


    public void clearTweets() {
        List<Tweet> allTweets = db.getAllTweets();
        for (Tweet tweet : allTweets) {
                    DeleteRemoteTweet deltweet = new DeleteRemoteTweet();
                    Call<String> call = app.tweetService.deleteOne(tweet.getTID());
                    call.enqueue(deltweet);
                    db.deleteTweet(tweet);
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        Tweet tweet = adapter.getItem(position);
        if(checked) {
            holder.add(tweet);
        }

        if(!checked) {
            for (int i = 0; i < holder.size(); i++) {
                if (holder.contains(tweet)) {
                    holder.remove(tweet);
                }
            }
        }

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.tweet_list_context,menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        MyTweetApp app = (MyTweetApp) getApplication();
        switch (menuItem.getItemId()) {
            case R.id.menu_item_delete_tweets:
                deleteSelectedTweets();
                //startActivity(new Intent(this, TweetActivityList.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
        app.tweets = response.body();
        app.tweetServiceAvailable = true;
        app.deleteAllUserTweets();
        app.addtweetstoDB();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onFailure(Call<List<Tweet>> call, Throwable t) {

    }

}


class TweetAdapter extends ArrayAdapter<Tweet>
{
    private Context context;
    public MyTweetApp app;


    public TweetAdapter(Context context, ArrayList<Tweet> tweets)
    {
        super(context, 0, tweets);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_tweet, null);
        }

        Tweet tweets = getItem(position);

        TextView tweetmessage = (TextView)convertView.findViewById(R.id.tweet_message);
        tweetmessage.setText(tweets.getMessage());

     /*  TextView tweetname = (TextView) convertView.findViewById(R.id.tweet_date);
        tweetname.setText(app.getCurrentUserFirstName() );*/

        return convertView;


    }
}