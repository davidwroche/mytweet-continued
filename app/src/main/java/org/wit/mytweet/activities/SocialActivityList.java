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
public class SocialActivityList  extends AppCompatActivity implements Callback<List<Social>>,AdapterView.OnItemClickListener,AbsListView.MultiChoiceModeListener {

    public MyTweetApp app;
    private ListView listSoc;
    private SocialAdapter adapter;
    private ArrayList<Social> social;
    MyTweetDB db = new MyTweetDB(this);



    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_following);
        listSoc = (ListView)findViewById(R.id.socialList);
        app = (MyTweetApp)getApplication();
        social = (ArrayList<Social>) app.getSocial();

        //Toast.makeText(this, "Retrieving tweets list " + app.users.size(), Toast.LENGTH_SHORT).show();

        adapter = new SocialAdapter(this, social);

        listSoc.setAdapter(adapter);
       /* listSoc.setOnItemClickListener(this);

        listSoc.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listSoc.setMultiChoiceModeListener(this);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.social_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyTweetApp app = (MyTweetApp) getApplication();
        switch (item.getItemId()) {

            case R.id.action_refresh:
                retrieveSocial();
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, TweetSettings.class));
                break;

            case R.id.tweetList:
                startActivity(new Intent(this, TweetActivityFollowing.class));
                break;

            case R.id.plus_sign:
                Intent intent = new Intent(this, TweetActivity.class);
                this.startActivity(intent);
                break;

            case R.id.socialList:
                startActivity(new Intent(this, SocialActivityList.class));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void retrieveSocial() {
        Call<List<Social>> call = (Call<List<Social>>) app.tweetService.getSocial();
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
    public void onResponse(Call<List<Social>> call, Response<List<Social>> response) {
        app.social = response.body();
        app.deleteAllSocial(); //Deletes any social aspect still on the device
        app.addSocialtoDB(); //populate follower and following to db that target user
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onFailure(Call<List<Social>> call, Throwable t) {

    }
}



class SocialAdapter extends ArrayAdapter<Social>
{
    private Context context;
    public MyTweetApp app;


    public SocialAdapter(Context context, ArrayList<Social> social)
    {
        super(context, 0, social);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_social, null);
        }

        Social social = getItem(position);

        TextView tweetmessage = (TextView)convertView.findViewById(R.id.socialmessage);
        tweetmessage.setText(social.getFolFname() + social.getFolLname());





     /*  TextView tweetname = (TextView) convertView.findViewById(R.id.tweet_date);
        tweetname.setText(app.getCurrentUserFirstName() );*/

        return convertView;


    }
}