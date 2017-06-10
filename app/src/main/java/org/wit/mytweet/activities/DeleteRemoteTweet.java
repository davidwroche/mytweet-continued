package org.wit.mytweet.activities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David on 12/12/2016.
 */
public class DeleteRemoteTweet implements Callback<String> {
    @Override
    public void onResponse(Call<String> call, Response<String> response) {

    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}
