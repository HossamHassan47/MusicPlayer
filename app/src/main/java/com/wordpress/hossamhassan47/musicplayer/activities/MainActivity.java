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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.adapters.AlbumAdapter;
import com.wordpress.hossamhassan47.musicplayer.model.Album;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int RUNTIME_PERMISSION_CODE = 7;

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;

    private List<Album> albumList;
    public ArrayList<Song> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set activity txtAlbumTitle to Playlists
        setTitle(getResources().getString(R.string.title_albums));

        // Requesting run time permission for Read External Storage.
        androidRuntimePermission();

        // Get Album list
        fillAlbumsList();

        // Get Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_albums);

        // Album albumAdapter
        albumAdapter = new AlbumAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(albumAdapter);
    }

    public void fillAlbumsList() {
        // Album array list
        albumList = new ArrayList<>();
        songsList = new ArrayList<>();

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_error), Toast.LENGTH_LONG);
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_no_music), Toast.LENGTH_LONG);
        } else {
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            do {
                String songTitle = cursor.getString(title);
                String songPath = cursor.getString(path);
                String songAlbum = cursor.getString(album);

                // Adding Media File Names to ListElementsArrayList.
                songsList.add(new Song(songTitle, songPath));
                Album playlist = new Album(songAlbum, 1, R.drawable.ic_playlist);

                int alreadyExistIndex = albumList.indexOf(playlist);
                if (alreadyExistIndex < 0) {
                    albumList.add(playlist);
                } else {
                    albumList.get(alreadyExistIndex).incrementNumOfSongs();
                }

            } while (cursor.moveToNext());
        }

        // Sort Albums by txtAlbumTitle
        Collections.sort(albumList, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
    }


    // Creating Runtime permission function.
    public void androidRuntimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE);
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", null);
                    AlertDialog dialog = alert_builder.create();
                    dialog.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
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
                    fillAlbumsList();
                    albumAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Access denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            fillAlbumsList();
            albumAdapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
