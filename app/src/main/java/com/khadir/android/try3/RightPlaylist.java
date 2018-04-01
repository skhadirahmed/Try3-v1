package com.khadir.android.try3;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class RightPlaylist extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    String direction = DataBaseHelper.DIRECTION_RIGHT;
    Intent intent = null;
    LinearLayout linearLayout;
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    NotificationManager notificationManager;
    String data_all[] = new String[100];
    static int i = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayout l;
    String songname, martist = "";
    Notification notification = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_right_playlist);

        dataBaseHelper = new DataBaseHelper(this);
        linearLayout = findViewById(R.id.root);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sharedPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("FIRST_TIME", "true");
        editor.apply();

        intent = getIntent();
    }

    public void addSongs(View view) {
        intent = new Intent(this, AllMusic.class);
        intent.putExtra("player", "right");
        startActivityForResult(intent, MainActivity.RIGHT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.RIGHT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                boolean selected_a_song = data.getBooleanExtra("selected_a_song", false);
                if (selected_a_song) {
                    myUpdate();
                    setResult(RESULT_OK);
                    finish();
                } else if (resultCode == RESULT_CANCELED) {

                    Toast.makeText(this, "no song selected", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public void updatePlaylist(final String song_name, final String artist) {
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        final TextView s = new TextView(this);
        String short_song = song_name;
        if (song_name.length() > 20) {
            short_song = song_name.substring(0, 16) + "...";
        }
//        s.setText(song_name);
        s.setText(short_song);
        s.setTextSize(30);


        final TextView a = new TextView(this);
        a.setText(artist);
        a.setTextSize(15);

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songname = song_name;
                martist = artist;
                Toast.makeText(RightPlaylist.this, "Playing the song " + song_name, Toast.LENGTH_SHORT).show();
//                sendNotification();
                String data = dataBaseHelper.getData(song_name, direction);
                String path = dataBaseHelper.getColAlbumArt(song_name, direction);
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Drawable drawable = Drawable.createFromPath(path);
                MainActivity.RalbumArt.setBackground(drawable);
//                Log.v("LeftPlaylist", "data from getData() is " + data);
                if (MainActivity.RightMediaPlayer.isPlaying()) {
                    //Clear the media player object for the next song
                    MainActivity.RightMediaPlayer.stop();
                    MainActivity.RightMediaPlayer.reset();
                }
                try {
                    Toast.makeText(RightPlaylist.this, "preparing the medaiplayer !", Toast.LENGTH_SHORT).show();
                    MainActivity.RightMediaPlayer.setDataSource(data);
                    MainActivity.RightMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MainActivity.RightMediaPlayer.start();
                MainActivity.RightMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(RightPlaylist.this, "completed playing the song " + song_name, Toast.LENGTH_SHORT).show();
                        mp.reset();
                        notificationManager.cancelAll();
                    }
                });
            }
        });

        s.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                v.setVisibility(View.GONE);
//                a.setVisibility(View.GONE);
                Toast.makeText(RightPlaylist.this, song_name, Toast.LENGTH_SHORT).show();

                //todo delete the removed views from the playlist database
                return true;
            }
        });

        l.addView(s);
        l.addView(a);
        linearLayout.addView(l);
    }

    public void myUpdate() {
        Cursor cursor = dataBaseHelper.getAllData(direction);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String song_name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SONG_NAME));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_ARTIST));
                updatePlaylist(song_name, artist);
            } while (cursor.moveToNext());
        }
    }
    //i have to write onResume()

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("RightPlaylist", "onResume in RightPlaylist");
        myUpdate();
    }
}
