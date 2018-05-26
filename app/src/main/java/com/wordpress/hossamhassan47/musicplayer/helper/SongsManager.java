package com.wordpress.hossamhassan47.musicplayer.helper;

import android.content.ContentResolver;
import android.os.Environment;
import android.util.Log;

import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {

    // SDCard Path
    final String MEDIA_PATH = new String("/sdcard/");

    private ArrayList<Song> songsList = new ArrayList<Song>();

    // Constructor
    public SongsManager() {

    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     */
    public ArrayList<Song> getAllSongsList() {
        File home = Environment.getExternalStorageDirectory();

        File[] files = home.listFiles(new FileExtensionFilter());
        Log.v("Songs Manager: ", "Length is " + files.length);

        //files != null &&
        if (files.length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {

                Song song = new Song();
                song.setSongTitle(file.getName().substring(0, (file.getName().length() - 4)));
                song.setSongPath(file.getPath());

                Log.v("Songs Manager: ", "Title: " + song.getSongTitle());

                // Adding each song to SongList
                songsList.add(song);
            }
        }

        // return songs list array
        return songsList;
    }

    public void getSongs() {
        File dir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains(".mp3");
                }
            });
            Log.v("Songs Manager: ", "Length is " + files.length);
            for (int i = 0; i < files.length; i++) {
                Log.v("Songs Manager: ", "Title: " + files[i].getName());
            }
        }
    }


    /**
     * Class to filter files which are having .mp3 extension
     */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
