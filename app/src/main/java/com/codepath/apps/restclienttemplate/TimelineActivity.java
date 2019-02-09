package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private List<Tweet> tweets;
    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private static final int REQUEST_CODE = 120;

    static long tweetUID = Long.MAX_VALUE;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
//            Toast.makeText(this, "Succesfully Tapped ", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
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

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i("Endi", "Scolling no data");
                loadNextDataFromApi();

            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

    }

    private void loadNextDataFromApi() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("sucess1", response.toString());

                List<Tweet> tweetsToAppend = new ArrayList<>();
                //Iterate through the list of tweets that comes from response
                for (int i = 0; i < response.length(); ++i) {

                    try {
                        //Convert each jsonObject into a tweet object
                        JSONObject jsonObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonObject);
                        //Add the tweet into our data source
                        tweetsToAppend.add(tweet);

                        /**
                         * I needed to update the tweetUID again as the id will be used several times
                         * if we don't update tweetUID all the time, that means we're just getting the older tweets once,
                         * and after that, we're getting the same old tweets again
                         */
                        if (tweet.uid < tweetUID) {
                            tweetUID = tweet.uid;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                /**
                 * adapter.clear();
                 * we don't want to call adapter.clear(),
                 * because that would clear the original tweets we loaded
                 */
                adapter.addTweets(tweetsToAppend);

                // adapter.notifyItemRangeInserted(adapter.getItemCount(), tweets.size() - 1);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("failure1", throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("failure1", throwable.toString());
            }
        }, tweetUID);
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

                        /**
                         *instead of checking tweet.uid > tweetUID, w
                         * e actually want tweet.uid < tweetUID before we set it.
                         * because the uids are getting smaller as we scroll down,
                         * so we want the uid of the last tweet we loaded
                         */
                        if (tweet.uid < tweetUID) {
                            tweetUID = tweet.uid;
                        }

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
                Log.e("failure", throwable.toString());
            }
        });
    }

    // ActivityOne.java, time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract   from result extras
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            //Add the tweet to the first position
            tweets.add(0, tweet);
            //Notify the adapter for the inserted object
            adapter.notifyItemInserted(0);
            //Smoothly scroll up since the recycler view does not notice it on first.
            rvTweets.smoothScrollToPosition(0);

        }
    }
}
