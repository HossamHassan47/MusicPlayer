package com.wordpress.hossamhassan47.musicplayer.model;

public class Album {
    private String title;
    private int numOfSongs;
    private int thumbnail;

    public Album() {
    }

    public Album(String title, int numOfSongs, int thumbnail) {
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

    public void incrementNumOfSongs(){
        numOfSongs++;
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.title.equalsIgnoreCase(((Album)obj).getTitle());
    }
}
