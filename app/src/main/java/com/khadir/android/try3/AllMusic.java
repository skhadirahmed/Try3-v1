package com.khadir.android.try3;

import android.content.Intent;
import android.database.Cursor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);
        intent = getIntent();
        player = intent.getStringExtra("player");
        Log.v("AllMusic", "player is " + player);
        listView = findViewById(R.id.listview);
        final ArrayList<MusicDetails> musicDetails = new ArrayList<>();
        //TODO Query all the songs using getContentResolver().query()
        String projection[] = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA};
        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Audio.Media.DISPLAY_NAME);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        do {
            song_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            Log.v("song_name", "" + song_name);
            artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            Log.v("artist", "" + artist);
//            String album_art = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
//            Log.v("Album Art", "" + album_art);
            data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Log.v("String from data", data);
            musicDetails.add(new MusicDetails(song_name, artist,data));
        } while (cursor.moveToNext());
        //TODO iterate through the returned Cursor object and populate the musicDetails ArrayList

        MusicDetailsAdapter musicDetailsAdapter = new MusicDetailsAdapter(this, musicDetails);

        listView.setAdapter(musicDetailsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicDetails musicDetails1 = musicDetails.get(position);
//                Toast.makeText(AllMusic.this, "song name is " + musicDetails1.getSong_name(), Toast.LENGTH_SHORT).show();
                if (player.equals("left")) {
                    intent = new Intent(AllMusic.this, LeftPlaylist.class);
                } else if (player.equals("right")) {
                    intent = new Intent(AllMusic.this, RightPlaylist.class);
                }

                //TODO send selected song details to MyPlaylist.java to store them in the SQLite database
                //TODO also implement a class to play the playlist by quering the playlist databse
                // TODO (we will have two databases one for each person with options to delete,update,etc)
                intent.putExtra("song_name", musicDetails1.getSong_name());
                intent.putExtra("artist", musicDetails1.getArtist());
                intent.putExtra("data",musicDetails1.getData());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}
