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
import android.support.v4.app.DialogFragment;
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
import com.wordpress.hossamhassan47.musicplayer.fragments.AddPlaylistFragment;
import com.wordpress.hossamhassan47.musicplayer.fragments.NoticeDialogListener;
import com.wordpress.hossamhassan47.musicplayer.model.Album;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.wordpress.hossamhassan47.musicplayer.activities.NowPlayingActivity.RUNTIME_PERMISSION_CODE;

public class MainActivity extends AppCompatActivity implements NoticeDialogListener {

    private RecyclerView recyclerView;
    private AlbumAdapter adapter;

    private List<Album> albumList;
    public ArrayList<Song> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set activity title to Playlists
        setTitle(getResources().getString(R.string.title_albums));

        // Get Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Album array list
        albumList = new ArrayList<>();
        songsList = new ArrayList<>();

        // Album adapter
        adapter = new AlbumAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Requesting run time permission for Read External Storage.
        androidRuntimePermission();

        getAllMediaMp3Files();
    }

    public void getAllMediaMp3Files() {
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
            Toast.makeText(MainActivity.this, "Something Went Wrong.", Toast.LENGTH_LONG);
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(MainActivity.this, "No Music Found on SD Card.", Toast.LENGTH_LONG);
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
                }else{
                    albumList.get(alreadyExistIndex).incrementNumOfSongs();
                }

            } while (cursor.moveToNext());
        }
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

                } else {

                }
            }
        }
    }

    /**
     * Adding few albumList for testing
     */
    private void preparePlaylists() {
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            Album a = new Album("Album " + (i + 1), random.nextInt(100), R.drawable.ic_playlist);
            albumList.add(a);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Album a = new Album("Album New", 0, R.drawable.ic_playlist);
        albumList.add(a);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
