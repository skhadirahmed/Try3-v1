package com.khadir.android.try3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class LeftPlaylist extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    String direction = DataBaseHelper.DIRECTION_LEFT;
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
        setContentView(R.layout.activity_left_playlist);
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
        intent.putExtra("player", "left");
        startActivityForResult(intent, MainActivity.LEFT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.LEFT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(this, "onActivityResult of LeftPlaylist", Toast.LENGTH_SHORT).show();
                //show selected song on the left playlist activity
//                String song_name = data.getStringExtra("song_name");
//                String artist = data.getStringExtra("artist");
//                String song_data = data.getStringExtra("data");

//                data_all[i] = song_data;
//                Log.v("LeftPlaylist", "data_all at index " + i + " from getData() is " + data_all[i]);
//                i++;

//                Toast.makeText(this, "song selected " + song_name + "\nartist " + artist, Toast.LENGTH_SHORT).show();
//
//                ContentValues values = new ContentValues();
//                values.put(DataBaseHelper.COL_SONG_NAME, song_name);
//                values.put(DataBaseHelper.COL_ARTIST, artist);
//                values.put(DataBaseHelper.COL_DATA, song_data);
//i commented the below lines 81 and 84
//                long id = dataBaseHelper.insertIntoDataBase(values);

                //todo write a insertIntoDataBase for atleast one song to be preloaded
//                Log.v("MyPlaylist", "value of id inserted is " + id);
                //todo i added the below two lines

//                updatePlaylist(song_name, artist);//todo i commented this line
                boolean selected_a_song = data.getBooleanExtra("selected_a_song", false);
                Log.v("LeftPlaylist", "selected_a_song value is " + selected_a_song);
                if (selected_a_song) {
                    Toast.makeText(this, "song selected", Toast.LENGTH_SHORT).show();
                    myUpdate();
                    setResult(RESULT_OK);
                    intent.putExtra("data_all", data_all);
                    finish();
                } else {
                    Toast.makeText(this, "no song selected", Toast.LENGTH_SHORT).show();
                    finish();//no song selected
                }

            }
        }

//        if (requestCode == MainActivity.RIGHT_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                //show selected song in the right playlist activity
//            }
//        }
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
                Toast.makeText(LeftPlaylist.this, "Playing the song " + song_name, Toast.LENGTH_SHORT).show();
                sendNotification();
                String data = dataBaseHelper.getData(song_name, direction);
                String path = dataBaseHelper.getColAlbumArt(song_name, direction);
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Drawable drawable = Drawable.createFromPath(path);
                MainActivity.LalbumArt.setBackground(drawable);
                Log.v("LeftPlaylist", "data from getData() is " + data);
                if (MainActivity.LeftMediaPlayer.isPlaying()) {
                    //Clear the media player object for the next song
                    MainActivity.LeftMediaPlayer.stop();
                    MainActivity.LeftMediaPlayer.reset();
                }
                try {
                    Toast.makeText(LeftPlaylist.this, "preparing the medaiplayer !", Toast.LENGTH_SHORT).show();
                    MainActivity.LeftMediaPlayer.setDataSource(data);
                    MainActivity.LeftMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MainActivity.LeftMediaPlayer.start();
                MainActivity.LeftMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(LeftPlaylist.this, "completed playing the song " + song_name, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LeftPlaylist.this, song_name, Toast.LENGTH_SHORT).show();
                //todo delete the removed views from the playlist database
                return true;
            }
        });

        l.addView(s);
        l.addView(a);
        linearLayout.addView(l);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume in activity_left_playlist", Toast.LENGTH_SHORT).show();
        myUpdate();
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

    public void sendNotification() {
        builder.setContentTitle(songname);
        builder.setContentText(martist);
        builder.setSmallIcon(R.drawable.ic_audiotrack);
        builder.setPriority(2);
//                builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        Intent i = new Intent();
        PendingIntent p = PendingIntent.getActivity(LeftPlaylist.this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_audiotrack, "action1", p);
        builder.addAction(R.drawable.ic_audiotrack, "action2", p);
        builder.addAction(R.drawable.ic_audiotrack, "action3", p);
//                builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowCancelButton(true).setCancelButtonIntent(p));

        notification = builder.build();
        notificationManager.notify(1, notification);
    }
}
