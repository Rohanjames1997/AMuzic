package com.example.amuzic;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.google.gson.JsonObject;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Play_Spotify extends AppCompatActivity implements View.OnClickListener {

    private static final String CLIENT_ID = "7d80050172454d13be81cd222264b969";
    private static final String REDIRECT_URI = "awesomeprotocol123://returnafterlogin";
    private SpotifyAppRemote mSpotifyAppRemote;

    private RequestQueue mQueue;
    TextView textView;
    EditText editText;
    Button submit,bp1,bp2,bp3;
    ListView songsList;
    TextView tv3,tv4,tv5;
    String s1,s2,s3;
    boolean p1=false,p2=false,p3=false;

    HashMap<String,String> searchresults;
    ArrayList<String> arrayList= new ArrayList<>();
    HashMap<Integer,String> hashMap = new HashMap();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

    }
    void initViews()
    {
        setContentView(R.layout.activity_play);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText2);
        submit = findViewById(R.id.button);
        bp1=findViewById(R.id.button_play1);
        bp2=findViewById(R.id.button_play2);
        bp3=findViewById(R.id.button_play3);
        editText.setHint("Enter your playlist index");
        textView.setText("Playing");
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mQueue = Volley.newRequestQueue(this);
        tv3=findViewById(R.id.textView3);
        tv4=findViewById(R.id.textView4);
        tv5=findViewById(R.id.textView5);

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

    private void PlayFromID(String id) {
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:"+id);
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
                if(editText.getText().toString().matches(""))
                    msg("Please enter your choice of playlist");
                else
                    SearchAndPlay(editText.getText().toString());
//                    DetectAndPlay(Integer.parseInt(String.valueOf(editText.getText())))
            case R.id.button_play1:
                if(p1) PlayFromID(s1);
            case R.id.button_play2:
                if(p2) PlayFromID(s2);
            case R.id.button_play3:
                if(p3) PlayFromID(s3);


        }
    }

    private void SearchAndPlay(String search) {

        ArrayList listx = new ArrayList();
        Log.d("SSSS","Going to request!");
        String [] words = search.split("\\s");
        String url = "https://api.spotify.com/v1/search?q=";
        for(int i =0;i<words.length;i++)
        {
            url += words[i];
            if(i != words.length-1)
            {
                url += "%20";
            }
        }
        url += "&type=playlist&limit=3";//+ " -H \"Accept: application/json\" -H \"Content-Type: application/json\" -H \"Authorization: Bearer BQBhJ0O5LnQ7l5Q_-wOGL4NcDBxJ74tiNs9K0Gz5Ur0McqnF4TH9AhZsJjispkzpCP02fVYAq96y8hanFcsWaxB5Za9R27vL_OBVa26QBH_Xq2I9nklK6_nORj0AxQHjGs_7MEvW658B17hwuR7PAyyvu8UdEcZ3Zg\"";
        Log.d("SSSS",url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("SSSS","No error!");
                      //  textView2.setText(response.toString());
                        JSONObject playlist= null;

                        try {
                            playlist = response.getJSONObject("playlists");
                            Log.d("SSSS","playlist chalu chhey  "+ playlist.toString() );

                        } catch (JSONException e) {
                            Log.d("SSSS","playlist cannot fetch json");
                            e.printStackTrace();
                        }
                        JSONArray items= null;
                        try {
                            items =  playlist.getJSONArray("items");
                            for(int i=0;i<items.length();i++){
                                JSONObject item=items.getJSONObject(i);
                                String name=item.getString("name");
                                if(i%3==0) {tv3.setText(name);p1=true;s1=item.getString("id");}
                                else if(i%3==1){tv4.setText(name);p2=true;s2=item.getString("id");}
                                else{tv5.setText(name);p3=true;s3=item.getString("id");}
                            }
                            Log.d("SSSS","name can fetch string");
                        } catch (JSONException e) {
                            Log.d("SSSS","name cannot fetch string " );
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SSSSS","Error response");
                error.printStackTrace();

            }

        }){@Override
        public Map getHeaders() throws AuthFailureError {
            HashMap headers = new HashMap();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            headers.put("Authorization", "Bearer BQDG4FkWhYK8ZecFDmKv6dSdKHrKdhk0Ucm8OeKEX6Dz6FD8mI7QY37pTt3zh42q4K2dn4X0_CmmAcuXn8XvCLaWfcNa_12d-JYIevNXqYqUegbJ-JnCjkdgFgGekDw8D3BCm_J1xPXtH7NTQhyYZID6jTkxWGjzzw");
            return headers;
        }};
       mQueue.add(request);



    }

}

