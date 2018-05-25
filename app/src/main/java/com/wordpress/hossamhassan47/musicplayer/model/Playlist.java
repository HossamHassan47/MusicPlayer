package com.wordpress.hossamhassan47.musicplayer.model;

public class Playlist {
    private String title;
    private int numOfSongs;
    private int thumbnail;

    public Playlist() {
    }

    public Playlist(String title, int numOfSongs, int thumbnail) {
        this.title = title;
        this.numOfSongs = numOfSongs;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
