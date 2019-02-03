package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity {

    private static final int MAX_TEEET_LENGTH = 140;
    private EditText etCompose;
    private Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);


        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);


        //Set Click Listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Error-Handling
                String tweetContent = etCompose.getText().toString();

                if (tweetContent.isEmpty()) return;

                if (tweetContent.length() > MAX_TEEET_LENGTH) {
                    //TODO: Add Snackbar
                    Toast.makeText(ComposeActivity.this, "Length should be less than 140 Characters", Toast.LENGTH_LONG).show();
                }
                //If you tap on button make API call to Twitter

            }
        });

    }
}
