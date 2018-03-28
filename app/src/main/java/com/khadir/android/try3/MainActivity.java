package com.khadir.android.try3;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, MediaPlayer.OnCompletionListener {

    static ArrayList<MusicDetails> musicDetails = new ArrayList<>();

    static ImageView Lplay, Lstop, Rplay, Rstop;
    static CardView LalbumArt, RalbumArt;
    SeekBar LeftSeekBar;
    private static int PLAY = 0;
    private static int PAUSE = 1;
    private static int LEFT_PLAY_PAUSE = 0;//play 0,pause 1
    private static int RIGHT_PLAY_PAUSE = 0;//play 0,pause 1
    public static int LEFT_REQUEST_CODE = 903;
    public static int RIGHT_REQUEST_CODE = 905;
    public static MediaPlayer LeftMediaPlayer = new MediaPlayer();
    public static MediaPlayer RightMediaPlayer = new MediaPlayer();
    String data_all[], data_all_album_art[] = new String[100];
    boolean isReady;
    Handler seekHandler;
    Runnable runnable;
    int song_playing_number = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        seekHandler = new Handler();
        sharedPreferences = getSharedPreferences("data_all", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        runnable = new Runnable() {
            @Override
            public void run() {
                seekUpdation();
            }
        };
//        if (LeftMediaPlayer.isPlaying()) {
//            int mFileDuration = LeftMediaPlayer.getDuration();
//            LeftSeekBar.setMax(mFileDuration); // where mFileDuration is mMediaPlayer.getDuration();
//        }

        Lplay = findViewById(R.id.LeftPlay);
        Lstop = findViewById(R.id.LeftStop);
        LalbumArt = findViewById(R.id.LeftAlbumArt);
        LeftSeekBar = findViewById(R.id.LeftSeekBar);
        Rplay = findViewById(R.id.RightPlay);
        Rstop = findViewById(R.id.RightStop);
        RalbumArt = findViewById(R.id.RightAlbumArt);
        LeftMediaPlayer.setVolume(1, 0);
        LeftMediaPlayer.setOnCompletionListener(this);
        RightMediaPlayer.setVolume(0, 1);
        if (LeftMediaPlayer.isPlaying()) {
            Lplay.setImageResource(R.drawable.ic_pause);
            LEFT_PLAY_PAUSE = PAUSE;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("please wait while your data is loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIcon(R.drawable.ic_audiotrack);
        progressDialog.show();

        //i am calling get ready
        isReady = getReady();
        for (int i = 0; i < musicDetails.size(); i++) {
            Log.v("music details", "songname " + musicDetails.get(i).getSong_name());
            Log.v("music details", "artist " + musicDetails.get(i).getArtist());
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        data_all = dataBaseHelper.getDataArray();
        data_all_album_art = dataBaseHelper.getDataArrayAlbumArt();

        if (data_all[0] != null && data_all_album_art[0] != null) {
            try {
                String d = data_all[song_playing_number];
                LeftMediaPlayer.setDataSource(d);
                Drawable drawable = Drawable.createFromPath(data_all_album_art[0]);
                LalbumArt.setBackground(drawable);
                LeftMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void seekUpdation() {
        LeftSeekBar.setProgress(LeftMediaPlayer.getCurrentPosition());
        seekHandler.postDelayed(runnable, 1000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_single_mode) {
//            intent = new Intent(this, SingleMode.class);
//            startActivity(intent);
            Toast.makeText(this, "launch single mode", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_language) {

        } else if (id == R.id.nav_theme) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contact) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LeftPlay:
                if (LEFT_PLAY_PAUSE == PLAY) {
                    Lplay.setImageResource(R.drawable.ic_pause);
//                    if (LeftMediaPlayer.isPlaying()) {
//                        LeftMediaPlayer.start();
//                    }
                    if (!LeftMediaPlayer.isPlaying()) {

                        LeftMediaPlayer.start();
//                        LeftMediaPlayer.seekTo(200000);
//                        Log.v("LeftPlay", "trying to play playlist");
//                        for (int i = 0; i < 100; i++) {
//                            String data = sharedPreferences.getString("" + i, "default");
//                            Log.v("MainActivity", "shared preferences value at " + i + " is " + data);
//                        }
                    } else if (LeftMediaPlayer.isPlaying()) {
                        LeftMediaPlayer.start();
                    }
                    LEFT_PLAY_PAUSE = 1;
                } else {
                    Lplay.setImageResource(R.drawable.ic_play_arrow);
                    LeftMediaPlayer.pause();

                    Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
                    LEFT_PLAY_PAUSE = 0;
                }
                break;
            case R.id.LeftStop:
                LeftMediaPlayer.stop();
                LeftMediaPlayer.reset();
                Lplay.setImageResource(R.drawable.ic_play_arrow);
                Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
                break;
            case R.id.LeftAlbumArt:
                Toast.makeText(this, "Clicked on Left Album Art", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LeftPlaylist.class);
//                intent.putStringArrayListExtra("data_of_all_songs",new ArrayList<String>());
                startActivityForResult(intent, LEFT_REQUEST_CODE);
                break;
            case R.id.RightPlay:
                if (RIGHT_PLAY_PAUSE == 0) {
                    Rplay.setImageResource(R.drawable.ic_pause);
                    Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
                    RIGHT_PLAY_PAUSE = 1;
                } else {
                    Rplay.setImageResource(R.drawable.ic_play_arrow);
                    Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
                    RIGHT_PLAY_PAUSE = 0;
                }
                break;
            case R.id.RightStop:
                Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
                break;
            case R.id.RightAlbumArt:
                Intent intent1 = new Intent(this, RightPlaylist.class);
                startActivity(intent1);
                Toast.makeText(this, "Clicked on Right Album Art", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPlaylist() throws IOException {
        if (LeftMediaPlayer.isPlaying()) {
            Toast.makeText(this, "already playing a song", Toast.LENGTH_SHORT).show();
        } else {
            LeftMediaPlayer.reset();
//            LeftMediaPlayer.setDataSource(data_all[song_playing_number]);
            LeftMediaPlayer.prepare();
            LeftMediaPlayer.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LEFT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //successfully returned from left selection of playlist
                //todo instead of returning a data_all array just query again for the data
                //todo implement methods to get required parameters from playlist database like getSongName,getArtist and getData which is already implemented
//                data_all = data.getStringArrayExtra("data_all");
//                for(int i=0;i<data_all.length;i++)
//                    Log.v("onActivityResult","data_all["+i+"] is "+data_all[i]);
                Toast.makeText(this, "inside onActivityResult of MainActivity", Toast.LENGTH_SHORT).show();
                DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
                data_all = dataBaseHelper.getDataArray();
                for (int i = 0; i < data_all.length; i++) {
                    Log.v("MainActivity", "data form data_all at number " + i + " is " + data_all[i]);
                    editor.putString("" + i, data_all[i]);
                    editor.apply();
                }
            }
        }

        if (requestCode == RIGHT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //successfully returned from right selection of playlist
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReady) {
            progressDialog.dismiss();
        }
        if (LeftMediaPlayer.isPlaying()) {
            Lplay.setImageResource(R.drawable.ic_pause);
            LEFT_PLAY_PAUSE = PAUSE;

        } else {
            Lplay.setImageResource(R.drawable.ic_play_arrow);
            LEFT_PLAY_PAUSE = PLAY;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        song_playing_number++;
        mp.reset();
        try {
            mp.setDataSource(data_all[song_playing_number]);
            Drawable drawable = Drawable.createFromPath(data_all_album_art[song_playing_number]);
            LalbumArt.setBackground(drawable);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
//        mp.seekTo(200000);
    }


//    private Handler mHandler = new Handler();
////Make sure you update Seekbar on UI thread
//    MainActivity.this.runOnUiThread(new Runnable() {
//
//        @Override
//        public void run() {
//            if(LeftMediaPlayer != null){
//                int mCurrentPosition = LeftMediaPlayer.getCurrentPosition() / 1000;
//                LeftSeekBar.setProgress(mCurrentPosition);
//            }
//            mHandler.postDelayed(this, 1000);
//        }
//    });

    public boolean getReady() {
        Cursor cursor;
        String path = "", song_name, artist, data;
        Uri uri_for_album_art = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Uri uri_for_songs = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String projection[] = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM};

        String p[] = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.ALBUM};
        String selection = MediaStore.Audio.Albums.ALBUM + "=?";

        cursor = getContentResolver().query(uri_for_songs, projection, null, null, MediaStore.Audio.Media.ALBUM_KEY);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        do {
            String song_album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            Log.v("AllMusic", "song album is  " + song_album);

            Cursor album_art_cursor = getContentResolver().query(uri_for_album_art, p, selection, new String[]{String.valueOf(song_album)}, null);

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
        return true;
    }
}
