package com.wordpress.hossamhassan47.musicplayer.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.adapters.PlaylistAdapter;
import com.wordpress.hossamhassan47.musicplayer.adapters.SongAdapter;
import com.wordpress.hossamhassan47.musicplayer.helper.SongsManager;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.HashMap;

public class NowPlayingActivity extends AppCompatActivity {

    public static final int RUNTIME_PERMISSION_CODE = 7;

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

        SongsManager songsManager = new SongsManager();

        // Requesting run time permission for Read External Storage.
        AndroidRuntimePermission();

        // get all songs from sdcard
        //this.songsList = songsManager.getAllSongsList();
        //songsManager.getSongs();

        // Get Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_playlist_songs);

        // Playlist adapter
        adapter = new SongAdapter(this, songsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        GetAllMediaMp3Files();

        adapter.notifyDataSetChanged();
    }

    public void GetAllMediaMp3Files(){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            Toast.makeText(NowPlayingActivity.this,"Something Went Wrong.", Toast.LENGTH_LONG);
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(NowPlayingActivity.this,"No Music Found on SD Card.", Toast.LENGTH_LONG);
        }
        else {
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

            //Getting Song ID From Cursor.
            //int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {
                // You can also get the Song ID using cursor.getLong(id).
                //long SongID = cursor.getLong(id);

                String songTitle = cursor.getString(title);

                // Adding Media File Names to ListElementsArrayList.
                songsList.add(new Song(songTitle, ""));

            } while (cursor.moveToNext());
        }
    }



    // Creating Runtime permission function.
    public void AndroidRuntimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(NowPlayingActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(NowPlayingActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE);
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", null);
                    AlertDialog dialog = alert_builder.create();
                    dialog.show();
                } else {
                    ActivityCompat.requestPermissions(NowPlayingActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RUNTIME_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }
}
