package com.khadir.android.try3;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AllMusic extends AppCompatActivity {

    private ListView listView;
    public Cursor cursor;
    public String song_name, artist;
    String player = "", data = "";
    Intent intent;
    String path = "";
    DataBaseHelper dataBaseHelper;
    ContentValues contentValues;
    boolean selected_a_song = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);
        dataBaseHelper = new DataBaseHelper(this);
        contentValues = new ContentValues();
        intent = getIntent();
        player = intent.getStringExtra("player");
        Log.v("AllMusic", "player is " + player);
        listView = findViewById(R.id.listview);
//        final ArrayList<MusicDetails> musicDetails = new ArrayList<>();
        final ArrayList<MusicDetails> musicDetails = MainActivity.musicDetails;
        //TODO Query all the songs using getContentResolver().query()

//        Uri uri_for_album_art = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
//        Uri uri_for_songs = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//        String projection[] = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM};
//
//        String p[] = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.ALBUM};
//        String selection = MediaStore.Audio.Albums.ALBUM + "=?";
//
//        cursor = getContentResolver().query(uri_for_songs, projection, null, null, MediaStore.Audio.Media.ALBUM_KEY);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        do {
//            String song_album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
//            Log.v("AllMusic", "song album is  " + song_album);
//
//            Cursor album_art_cursor = getContentResolver().query(uri_for_album_art, p, selection, new String[]{String.valueOf(song_album)}, null);
//
//            if (album_art_cursor != null && album_art_cursor.moveToFirst()) {
//                Log.v("AllMusic", "album art cursor is not null");
//                path = album_art_cursor.getString(album_art_cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//                Log.v("AllMusic", "path to album art is " + path);
//                album_art_cursor.close();
//            }
//
//            song_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
//            Log.v("song_name", "" + song_name);
//            artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
//            Log.v("artist", "" + artist);
//            data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//            Log.v("String from data", data);
//            musicDetails.add(new MusicDetails(song_name, artist, data, path));
//        } while (cursor.moveToNext());

        //TODO iterate through the returned Cursor object and populate the musicDetails ArrayList

        MusicDetailsAdapter musicDetailsAdapter = new MusicDetailsAdapter(this, musicDetails);

        listView.setAdapter(musicDetailsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicDetails musicDetails1 = musicDetails.get(position);
                selected_a_song = true;
//                Toast.makeText(AllMusic.this, "song name is " + musicDetails1.getSong_name(), Toast.LENGTH_SHORT).show();
                if (player.equals("left")) {
                    //todo instead of sending back the selected music data and inserting in the database from LeftPlaylist.java insert from here and just get data from db in LeftPlaylist
                    contentValues.put(DataBaseHelper.COL_SONG_NAME, musicDetails1.getSong_name());
                    contentValues.put(DataBaseHelper.COL_ARTIST, musicDetails1.getArtist());
                    contentValues.put(DataBaseHelper.COL_DATA, musicDetails1.getData());
                    contentValues.put(DataBaseHelper.COL_ALBUM_ART, musicDetails1.getAlbum_art());

                    long inserted_row_id = dataBaseHelper.insertIntoDataBase(contentValues);
                    Log.v("AllMusic", "Inserted row id is " + inserted_row_id);

//                    intent = new Intent(AllMusic.this, LeftPlaylist.class);

                    intent.putExtra("selected_a_song", selected_a_song);
                } else if (player.equals("right")) {
                    intent = new Intent(AllMusic.this, RightPlaylist.class);
                }

                //TODO send selected song details to MyPlaylist.java to store them in the SQLite database
                //TODO also implement a class to play the playlist by quering the playlist databse
                // TODO (we will have two databases one for each person with options to delete,update,etc)
//                intent.putExtra("song_name", musicDetails1.getSong_name());
//                intent.putExtra("artist", musicDetails1.getArtist());
//                intent.putExtra("data", musicDetails1.getData());
//                setResult(RESULT_OK, intent);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}
