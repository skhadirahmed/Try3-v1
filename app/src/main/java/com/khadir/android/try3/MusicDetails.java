package com.khadir.android.try3;

/**
 * Created by lenovo on 18-Mar-18.
 */

public class MusicDetails {
    private String song_name, artist,data;

    public MusicDetails(String song_name, String artist,String data) {
        this.song_name = song_name;
        this.artist = artist;
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getData() {
        return data;
    }
}
