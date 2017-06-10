package org.wit.mytweet.app;
import org.wit.mytweet.models.User;
import org.wit.mytweet.models.Tweet;
import org.wit.mytweet.models.Social;


/**
 * Created by Tosh on 12/9/2016.
 */

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface TweetService {
    @GET("/api/users")
    Call<List<User>> getAllUsers();

    @GET("/api/users/{id}")
    Call<User> getUser(@Path("id") String id);

    @POST("/api/users")
    Call<User> createUser(@Body User User);

    @POST("/api/users/update")
    Call<User> updateSettings(@Body User User);


   /* // Tweets Section
    @POST("/api/tweets")
    Call<Tweet> createTweet(@Body Tweet Tweet);*/

    @POST("/api/users/{id}/tweets")
    Call<Tweet> createTweet(@Path("id") String id, @Body Tweet tweet);

    @GET("/api/poster/{id}/tweets")
    Call<List<Tweet>> findTweets(@Path("id") String poster);

    @DELETE("/api/tweets/{id}")
    Call<String> deleteOne(@Path("id") String id);


    @GET("/api/social")
    Call<List<Social>> getSocial();

    @GET("/api/tweets")
    Call<List<Tweet>> findallTweets();


}