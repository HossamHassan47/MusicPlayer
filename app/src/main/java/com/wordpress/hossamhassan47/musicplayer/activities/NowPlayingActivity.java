package com.wordpress.hossamhassan47.musicplayer.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.adapters.SongAdapter;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NowPlayingActivity extends AppCompatActivity {

    String albumTitle;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    public ArrayList<Song> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumTitle = getIntent().getExtras().getString("albumTitle","");
        setTitle(albumTitle);

        fillSongsList();

        // Get Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_playlist_songs);

        // Album songAdapter
        songAdapter = new SongAdapter(this, songsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(songAdapter);


        songAdapter.notifyDataSetChanged();
    }

    public void fillSongsList() {
        songsList = new ArrayList<>();

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] column = { MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
        };

        String where = android.provider.MediaStore.Audio.Media.ALBUM + "=?";

        String whereVal[] = { albumTitle };

        String orderBy = android.provider.MediaStore.Audio.Media.TITLE;

        Cursor cursor = contentResolver.query(uri, column, where, whereVal, orderBy);

        if (cursor == null) {
            Toast.makeText(NowPlayingActivity.this, getResources().getString(R.string.toast_error), Toast.LENGTH_LONG);
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(NowPlayingActivity.this, getResources().getString(R.string.toast_no_music), Toast.LENGTH_LONG);
        } else {

            do {
                String songTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String songPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String songArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                songsList.add(new Song(songTitle, songPath, songArtist, duration));
            } while (cursor.moveToNext());
        }
    }

}
