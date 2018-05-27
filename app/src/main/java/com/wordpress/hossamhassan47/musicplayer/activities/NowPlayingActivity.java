package com.wordpress.hossamhassan47.musicplayer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.adapters.SongAdapter;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.HashMap;

public class NowPlayingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    public ArrayList<Song> songsList = new ArrayList<Song>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();


        // get all songs from sdcard
        //this.songsList = songsManager.getAllSongsList();
        //songsManager.getSongs();

        // Get Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_playlist_songs);

        // Album adapter
        adapter = new SongAdapter(this, songsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }


}
