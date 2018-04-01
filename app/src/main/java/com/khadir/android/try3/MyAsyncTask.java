package com.khadir.android.try3;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyAsyncTask extends AsyncTask<ArrayList<MusicDetails>, Void, ArrayList<MusicDetails>> {
    private TextView textView;
    private Context mContext;
    ProgressDialog progressDialog;
    String line = null;
    private ArrayList<MusicDetails> musicDetails;


    public MyAsyncTask(MainActivity mainActivity, ArrayList<MusicDetails> musicDetails) {
        mContext = mainActivity;
        this.musicDetails = musicDetails;
    }

    @Override
    protected void onPostExecute(ArrayList<MusicDetails> musicDetails) {
        super.onPostExecute(musicDetails);
        progressDialog.dismiss();
//        MainActivity.musicDetails = musicDetails;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("please wait while your data is loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIcon(R.drawable.ic_audiotrack);
        progressDialog.show();
    }

    @Override
    protected ArrayList<MusicDetails> doInBackground(ArrayList<MusicDetails>... arrayLists) {
        Cursor cursor;
        String path = "", song_name, artist, data;
        Uri uri_for_album_art = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Uri uri_for_songs = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String projection[] = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM};

        String p[] = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.ALBUM};
        String selection = MediaStore.Audio.Albums.ALBUM + "=?";

        cursor = mContext.getContentResolver().query(uri_for_songs, projection, null, null, MediaStore.Audio.Media.ALBUM_KEY);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        do {
            String song_album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            Log.v("AllMusic", "song album is  " + song_album);

            Cursor album_art_cursor = mContext.getContentResolver().query(uri_for_album_art, p, selection, new String[]{String.valueOf(song_album)}, null);

            if (album_art_cursor != null && album_art_cursor.moveToFirst()) {
                Log.v("AllMusic", "album art cursor is not null");
                path = album_art_cursor.getString(album_art_cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                Log.v("AllMusic", "path to album art is " + path);
                album_art_cursor.close();
            }

            song_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            Log.v("song_name", "" + song_name);
            artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            Log.v("artist", "" + artist);
            data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Log.v("String from data", data);
            musicDetails.add(new MusicDetails(song_name, artist, data, path));
        } while (cursor.moveToNext());

        cursor.close();
        return musicDetails;
    }


}
