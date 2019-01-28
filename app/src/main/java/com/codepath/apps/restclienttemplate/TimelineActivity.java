package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private List<Tweet> tweets;
    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        //Find the recycler view.
        rvTweets = findViewById(R.id.rvTweets);

        //Initialize list of tweets and adapter from data source;
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);

        //Set up recycler view.
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        client = TwitterApp.getRestClient(this);
        populateHomeTimeline();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("sucess", "content refreshed ");
                populateHomeTimeline();
            }
        });

    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("sucess", response.toString());

                List<Tweet> tweetsToAdd = new ArrayList<>();
                //Iterate through the list of tweets that comes from response
                for (int i = 0; i < response.length(); ++i) {

                    try {
                        //Convert each jsonObject into a tweet object
                        JSONObject jsonObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonObject);
                        //Add the tweet into our data source
                        tweetsToAdd.add(tweet);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Clear existing data
                adapter.clear();
                // Show the data we have just received.
                adapter.addTweets(tweetsToAdd);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("failure", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }
}
