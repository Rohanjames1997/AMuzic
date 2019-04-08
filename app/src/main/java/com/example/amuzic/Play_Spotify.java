package com.example.amuzic;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.HashMap;

public class Play_Spotify extends AppCompatActivity implements View.OnClickListener {

    private static final String CLIENT_ID = "7d80050172454d13be81cd222264b969";
    private static final String REDIRECT_URI = "awesomeprotocol123://returnafterlogin";
    private SpotifyAppRemote mSpotifyAppRemote;
    TextView textView;
    EditText editText;
    Button submit;
    ArrayList<String> arrayList= new ArrayList<>();
    HashMap<Integer,String> hashMap = new HashMap();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

    }
    void initViews()
    {
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText2);
        submit = findViewById(R.id.button);
        editText.setHint("Enter your playlist index");
        textView.setText("Playing");
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        populateArrayAndMap();
    }

    void populateArrayAndMap()
    {
        arrayList.add(0,"spotify:playlist:37i9dQZF1DWZUTt0fNaCPB");
        arrayList.add(1,"spotify:playlist:37i9dQZF1DX4WYpdgoIcn6");
        arrayList.add(2,"spotify:playlist:37i9dQZF1DX9wC1KY45plY");
        arrayList.add(3,"spotify:playlist:3yiX3ROHK4vo82pR6BO8eW");
        arrayList.add(4,"spotify:playlist:6zcmrXDTtRqTuKjFRzy5vW");

        hashMap.put(0,"Running to Rock 170-190 BPM");
        hashMap.put(1,"Chill Hits");
        hashMap.put(2,"Classic Road Trip Songs");
        hashMap.put(3,"Walking Music");
        hashMap.put(4,"Cycling Playlist");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("SSSSSS","Started!");
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("Play_Spotify", "Connected! Yay!");
                        Log.d("SSSSSS","Connected!");
                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                        Log.d("SSSSSS","Failed!");
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SSSSSS","Destroyed");
        mSpotifyAppRemote.getPlayerApi().pause();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void DetectAndPlay(int index) {
        mSpotifyAppRemote.getPlayerApi().play(arrayList.get(index));
    }

    int getIndexFromActivity()
    {
        //Return an index according to the activity being performed
        return 0;
    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    private void connected() {
        // Play a playlist
        int index = getIndexFromActivity();
        if(editText.getText().toString().matches(""))
            DetectAndPlay(index);
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {

                        textView.setText(new StringBuilder().append("Playing ").append(track.name).append(" by ").append(track.artist.name).append(" from the playlist - \n ").append(hashMap.get(index)).toString());
                        Log.d("Play_Spotify", track.name + " by " + track.artist.name);

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button:
                DetectAndPlay(Integer.parseInt(String.valueOf(editText.getText())));

        }
    }
}

