package com.wordpress.hossamhassan47.musicplayer.model;

public class Song {
    private String songTitle;
    private String songPath;

    public Song(){

    }

    public Song(String title, String path) {
        this.songTitle = title;
        this.songPath = path;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }
}
