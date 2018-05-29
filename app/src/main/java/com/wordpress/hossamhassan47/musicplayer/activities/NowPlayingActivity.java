package com.wordpress.hossamhassan47.musicplayer.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.adapters.SongAdapter;
import com.wordpress.hossamhassan47.musicplayer.helper.Utilities;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class NowPlayingActivity extends AppCompatActivity
        implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    // Play settings
    private String albumTitle;
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;

    // Duration
    private TextView txtCurrentDuration;
    private TextView txtTotalDuration;

    // Seek Bar
    private SeekBar seekBar;

    // Play buttons
    private ImageView btnRepeat;
    private ImageView btnPrevious;
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnShuffle;

    // Media Player
    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    private Utilities utils;

    // Songs List & Adapter
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    public ArrayList<Song> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumTitle = getIntent().getExtras().getString("albumTitle", "");
        setTitle(albumTitle);

        // List Album Songs
        fillSongsList();

        // Media player
        mediaPlayer = new MediaPlayer();

        utils = new Utilities();

        // Find Controls
        btnRepeat = (ImageView) findViewById(R.id.image_view_repeat);
        btnPrevious = (ImageView) findViewById(R.id.image_view_previous);
        btnPlay = (ImageView) findViewById(R.id.image_view_play);
        btnNext = (ImageView) findViewById(R.id.image_view_next);
        btnShuffle = (ImageView) findViewById(R.id.image_view_shuffle);

        seekBar = (SeekBar) findViewById(R.id.seekBarSong);

        txtCurrentDuration = (TextView) findViewById(R.id.text_view_current_duration);
        txtTotalDuration = (TextView) findViewById(R.id.text_view_total_duration);

        // Seek Bar & Media Player Listeners
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        // By default play first song
        playSong(0);

        // Play button
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // check for already playing
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();

                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.baseline_play_circle_outline_white_48);
                    }
                } else {
                    // Resume song
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.baseline_pause_circle_outline_white_48);
                    }
                }
            }
        });

        // Previous button
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (currentSongIndex > 0) {
                    // Previous song
                    currentSongIndex -= 1;
                } else {
                    // Last Song
                    currentSongIndex = songsList.size() - 1;
                }

                playSong(currentSongIndex);
            }
        });

        // Previous button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (currentSongIndex < (songsList.size() - 1)) {
                    // Next song
                    currentSongIndex += 1;
                } else {
                    // First Song
                    currentSongIndex = 0;
                }

                playSong(currentSongIndex);
            }
        });

        // Repeat button
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;

                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();

                    btnRepeat.setImageResource(R.drawable.ic_action_repeat_off);
                } else {
                    isRepeat = true;

                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();

                    btnRepeat.setImageResource(R.drawable.ic_action_repeat_on);
                }
            }
        });

        // Shuffle button
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;

                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();

                    btnShuffle.setImageResource(R.drawable.ic_action_shuffle_off);
                } else {
                    isShuffle = true;

                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();

                    btnShuffle.setImageResource(R.drawable.ic_action_shuffle_on);
                }
            }
        });

        // Songs Adapter
        songAdapter = new SongAdapter(this, songsList);

        // Songs Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_playlist_songs);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(songAdapter);

        songAdapter.notifyDataSetChanged();
    }

    /**
     * Method to list all Album songs
     */
    public void fillSongsList() {
        songsList = new ArrayList<>();

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] column = {MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
        };

        String where = android.provider.MediaStore.Audio.Media.ALBUM + "=?";

        String whereVal[] = {albumTitle};

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

    /**
     * Method to play a song
     */
    public void playSong(int songIndex) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songsList.get(songIndex).getSongPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.baseline_pause_circle_outline_white_48);

            // Set seek bar values
            seekBar.setProgress(0);
            seekBar.setMax(100);

            // Display current playing song title
            setTitle(songsList.get(currentSongIndex).getSongTitle());

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            txtTotalDuration.setText("" + utils.milliSecondsToTimer(totalDuration));

            // Displaying time completed playing
            txtCurrentDuration.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        // check for repeat is ON or OFF
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt(songsList.size() - 1);

            playSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (songsList.size() - 1)) {
                currentSongIndex += 1;
            } else {
                // play first song
                currentSongIndex = 0;
            }

            playSong(currentSongIndex);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(mUpdateTimeTask);

        mediaPlayer.release();
    }
}
