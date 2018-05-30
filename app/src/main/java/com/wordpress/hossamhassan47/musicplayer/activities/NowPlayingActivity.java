package com.wordpress.hossamhassan47.musicplayer.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.adapters.RecyclerItemClickListener;
import com.wordpress.hossamhassan47.musicplayer.adapters.SongAdapter;
import com.wordpress.hossamhassan47.musicplayer.helper.Utilities;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Now Playing activity that used to list selected album songs
 *
 * User can play, pause, skip to previous or next song.
 */
public class NowPlayingActivity extends AppCompatActivity
        implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    // Play settings
    private String albumTitle;
    private int currentSongIndex = 0;
    private int previousSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isStarted = false;

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
    private AudioManager mAudioManger;
    private Handler mHandler = new Handler();
    private Utilities utils;

    // Songs List & Adapter
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private SongAdapter songAdapter;
    public ArrayList<Song> songsList;

    AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Permanent loss of audio focus
                        // Pause playback immediately
                        releaseMediaPlayer();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Pause playback
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                        mediaPlayer.start();
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        albumTitle = getIntent().getExtras().getString("albumTitle", "");
        setTitle(albumTitle);

        // List Album Songs
        fillSongsList();

        // Media player
        mediaPlayer = new MediaPlayer();
        mAudioManger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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

        // Play button
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!isStarted) {
                    playSong(0);
                    return;
                }

                // check for already playing
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();

                        songsList.get(currentSongIndex).setPaused(true);

                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.baseline_play_circle_outline_white_48);
                    }
                } else {
                    // Resume song
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        songsList.get(currentSongIndex).setPaused(false);

                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.baseline_pause_circle_outline_white_48);
                    }
                }

                songAdapter.notifyDataSetChanged();
            }
        });

        // Previous button
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int songIndex;
                if (currentSongIndex > 0) {
                    // Previous song
                    songIndex = currentSongIndex - 1;
                } else {
                    // Last Song
                    songIndex = songsList.size() - 1;
                }

                playSong(songIndex);
            }
        });

        // Next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int songIndex;
                if (currentSongIndex < (songsList.size() - 1)) {
                    // Next song
                    songIndex = currentSongIndex + 1;
                } else {
                    // First Song
                    songIndex = 0;
                }

                playSong(songIndex);
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

        mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(songAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),
                        recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        playSong(position);
                    }
                })
        );

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
            // Request audio focus
            int result = mAudioManger.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // Save previous song index to reset its thumbnail
                previousSongIndex = currentSongIndex;

                // Set current song index
                currentSongIndex = songIndex;

                // Set is Started to true
                isStarted = true;

                // Set Previous song playing and paused to false
                songsList.get(previousSongIndex).setPlaying(false);
                songsList.get(previousSongIndex).setPaused(false);

                // Set current song playing to true and paused to false
                songsList.get(songIndex).setPlaying(true);
                songsList.get(songIndex).setPaused(false);

                // Play current song
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

                // Scroll to the current playing song
                mLayoutManager.scrollToPosition(songIndex);

                songAdapter.notifyDataSetChanged();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seek bar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            // Get total and current duration
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
        // Check for repeat is ON or OFF
        if (isRepeat) {
            // Repeat is on --> play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // Shuffle is on --> play a random song
            Random rand = new Random();
            playSong(rand.nextInt(songsList.size() - 1));
        } else {
            // No repeat or shuffle ON --> play next song
            int songIndex;
            if (currentSongIndex < (songsList.size() - 1)) {
                songIndex = currentSongIndex + 1;
            } else {
                // play first song
                songIndex = 0;
            }

            playSong(songIndex);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);

        // Get new current progress position
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Release media player
        mHandler.removeCallbacks(mUpdateTimeTask);
        this.releaseMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Release media player
        mHandler.removeCallbacks(mUpdateTimeTask);
        this.releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;

            mAudioManger.abandonAudioFocus(afChangeListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
