package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    //Pass in context and list of tweets
    private Context context;
    private List<Tweet> tweets;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    //For each row inflate the layout :
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    //Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //take the data that lives in that positions and attach that data to a particular viewholder that the adapter has given to us.
        Tweet tweet = tweets.get(position);
        //Bind the movie data into the viewholder.
        holder.bind(tweet);
    }

    //Define how many items are in our dataset.
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //Define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImg;
        public TextView tvScreenName;
        public TextView tvBody;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImg = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBody = itemView.findViewById(R.id.tvBody);
        }

        //Our method created to bind the data to the holder.
        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            Glide.with(context).load(tweet.user.profileImgUrl).into(ivProfileImg);
        }
    }
}

