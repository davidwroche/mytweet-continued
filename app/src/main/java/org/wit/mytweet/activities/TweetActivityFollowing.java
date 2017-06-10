package org.wit.mytweet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.wit.mytweet.R;
import org.wit.mytweet.app.MyTweetApp;
import org.wit.mytweet.database.MyTweetDB;
import org.wit.mytweet.models.FollowTweets;
import org.wit.mytweet.models.Social;
import org.wit.mytweet.models.Tweet;
import org.wit.mytweet.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tosh on 12/20/2016.
 */
public class TweetActivityFollowing  extends AppCompatActivity implements Callback<List<Tweet>>,AdapterView.OnItemClickListener,AbsListView.MultiChoiceModeListener {

    public MyTweetApp app;
    private ListView listFol;
    private FollowAdapter adapter;
    private ArrayList<FollowTweets> followTweets;
    MyTweetDB db = new MyTweetDB(this);



    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_folltweet);
        listFol = (ListView)findViewById(R.id.followingTweets);
        app = (MyTweetApp)getApplication();
        followTweets = (ArrayList<FollowTweets>) app.getFolT();

        //Toast.makeText(this, "Retrieving tweets list " + app.users.size(), Toast.LENGTH_SHORT).show();

        adapter = new FollowAdapter(this, followTweets);

        listFol.setAdapter(adapter);
//        listFol.setOnItemClickListener(this);

      /*  listFol.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listFol.setMultiChoiceModeListener(this);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.foll_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyTweetApp app = (MyTweetApp) getApplication();
        switch (item.getItemId()) {

            case R.id.action_refresh:
                retrievethereTweets();
                break;

            case R.id.tweetList:
                startActivity(new Intent(this, TweetActivityList.class));
                break;

            case R.id.socialList:
                startActivity(new Intent(this, SocialActivityList.class));
                break;

            case R.id.followingTweets:
                startActivity(new Intent(this, TweetActivityFollowing.class));
                break;

            case R.id.plus_sign:
                Intent intent = new Intent(this, TweetActivity.class);
                this.startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



    public void retrievethereTweets() {
        Call<List<Tweet>> call = (Call<List<Tweet>>) app.tweetService.findallTweets();
        call.enqueue(this);
    }





    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

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
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
        app.followtweets = response.body();
        app.deleteAllFOLT();
        app.addFollowtweetstoDB();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onFailure(Call<List<Tweet>> call, Throwable t) {

    }
}



class FollowAdapter extends ArrayAdapter<FollowTweets>
{
    private Context context;
    public MyTweetApp app;


    public FollowAdapter(Context context, ArrayList<FollowTweets> followTweets)
    {
        super(context, 0, followTweets);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_folltweet, null);
        }

        FollowTweets followTweets = getItem(position);

        String use1 = "Message : ";
        TextView tweetmessage = (TextView)convertView.findViewById(R.id.followmessage);
        tweetmessage.setText(use1.toString() + followTweets.getMessage());

        String use = "Poster : ";
        TextView tweetuser = (TextView) convertView.findViewById(R.id.followuser);
        tweetuser.setText(use.toString()+ followTweets.getFirstName() + followTweets.getLastName());

        return convertView;
    }
}