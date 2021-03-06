package com.uwimonacs.fstmobile.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uwimonacs.fstmobile.R;
import com.uwimonacs.fstmobile.fragments.VideoFragment;

public class PlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        /*
         *   Moves the YouTube Player into the view as a fragment, to
         *   avoid this class having to extend YouTubeBaseActivity and
         *   extend AppCompatActivity instead
         *
         */
        getSupportFragmentManager().beginTransaction()
                .add(R.id.player_view_holder, VideoFragment.newInstance(getIntent().getStringExtra("VIDEO_ID")))
                .commit();

        final TextView title = (TextView) findViewById(R.id.player_view_title);
        final TextView description = (TextView) findViewById(R.id.player_view_description);

        /*
         *   Video ID, Title and Description are used to load the video
         *   and populate the UI respectively
         */
        title.setText(getIntent().getStringExtra("VIDEO_TITLE"));
        description.setText(getIntent().getStringExtra("VIDEO_DESC"));

        final ViewGroup watchOnYoutube = (ViewGroup) findViewById(R.id.watch_on_youtube);

        /*
        * Launches the video externally in the YouTube app or the
        * web browser as a fallback
        * */
        watchOnYoutube.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + getIntent().getStringExtra("VIDEO_ID")));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + getIntent().getStringExtra("VIDEO_ID")));
                    startActivity(intent);
                }
            }
        });
    }
}
