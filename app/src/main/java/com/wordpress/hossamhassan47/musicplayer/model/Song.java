package com.wordpress.hossamhassan47.musicplayer.model;

public class Song {
    private String songTitle;
    private String songPath;
    private String songArtist;
    private String duration;
    private boolean isPlaying;

    public Song(){

    }

    public Song(String title, String path, String artist, String duration) {
        this.songTitle = title;
        this.songPath = path;
        this.songArtist = artist;
        this.duration = duration;
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

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
