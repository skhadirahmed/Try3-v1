package com.khadir.android.try3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lenovo on 18-Mar-18.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "playlist";//i am changing table name to left table name
    public static final String RIGHT_TABLE_NAME = "right_playlist";//i am changing table name to left table name
    public static final String COL_ID = "id";
    public static final String COL_SONG_NAME = "song_name";
    public static final String COL_ARTIST = "artist";
    public static final String COL_DATA = "data";
    public static final String COL_ALBUM_ART = "album_art";
    public static final String COL_DIRECTION = "direction";
    public static final String DIRECTION_LEFT = "left";
    public static final String DIRECTION_RIGHT = "right";

    public static final String CREATE_QUERY = "create table " + TABLE_NAME + "(" + COL_ID + " integer primary key autoincrement," + COL_DIRECTION + " text," + COL_SONG_NAME + " text," + COL_ARTIST + " text," + COL_DATA + " text," + COL_ALBUM_ART + " text" + ");";
//    public static final String RIGHT_CREATE_QUERY = "create table " + RIGHT_TABLE_NAME + "(" + COL_ID + " integer primary key autoincrement," + COL_SONG_NAME + " text," + COL_ARTIST + " text," + COL_DATA + " text," + COL_ALBUM_ART + " text" + ");";

    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    public DataBaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
        Log.v("DataBaseHelper", "create query is \n" + CREATE_QUERY);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertIntoDataBase(ContentValues values) {
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    public Cursor getAllData(String direction) {
        String selection = COL_DIRECTION + "=?";
        String selection_args[] = {direction};
        return sqLiteDatabase.query(TABLE_NAME, null, selection, selection_args, null, null, null);
//        return sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + ";", null);
    }

    public String getData(String song_name, String direction) {//i added a new parameter to find direction left or right
        String query = "select " + COL_DATA + " from " + TABLE_NAME + " where " + COL_SONG_NAME + " =?";
        Log.v("getData", "complete query is " + query);
        String projection[] = {COL_DATA};
        String selection = COL_SONG_NAME + "=? AND " + COL_DIRECTION + "=?";
        String selection_args[] = {song_name, direction};
        String data = "";
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, projection, selection, selection_args, null, null, null);

//        String[] args = { "first string", "second@string.com" };
//        Cursor cursor = db.query("TABLE_NAME", null, "name=? AND email=?", args, null);

//        Cursor cursor = sqLiteDatabase.rawQuery(query, selection_args);
        if (cursor != null) {
            cursor.moveToFirst();
            data = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA));
            Log.v("getData", "data form the cursor is " + data);
        }
        cursor.close();
        return data;
    }

    public String getColAlbumArt(String song_name, String direction) {
        String query = "select " + COL_ALBUM_ART + " from " + TABLE_NAME + " where " + COL_SONG_NAME + " =?";
        String path = "";

        String projection[] = {COL_ALBUM_ART};
        String selection = COL_SONG_NAME + "=? AND " + COL_DIRECTION + "=?";
        String selection_args[] = {song_name, direction};

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, projection, selection, selection_args, null, null, null);

//        Cursor cursor = sqLiteDatabase.rawQuery(query, selection_args);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndexOrThrow(COL_ALBUM_ART));
            Log.v("getColAlbumArt", "path form the cursor is " + path);
        }
        cursor.close();

        return path;
    }

    public String[] getDataArray(String direction) {
        String data_all[] = new String[100];//currently i support only upto 100 songs in a playlist
        String query = "select " + COL_DATA + " from " + TABLE_NAME;

        String projection[] = {COL_DATA};
        String selection = COL_DIRECTION + "=?";
        String selection_args[] = {direction};

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, projection, selection, selection_args, null, null, null);

//        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        int i = 0;
        String data;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                data = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA));
                Log.v("getDataArray", "data number " + i + " is " + data);
                data_all[i++] = data;
            } while (cursor.moveToNext());
            cursor.close();
        }

        return data_all;
    }

    public String[] getDataArrayAlbumArt(String direction) {
        String data_all_album_art[] = new String[100];//currently i support only upto 100 songs in a playlist
        String query = "select " + COL_ALBUM_ART + " from " + TABLE_NAME;

        String projection[] = {COL_ALBUM_ART};
        String selection = COL_DIRECTION + "=?";
        String selection_args[] = {direction};

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, projection, selection, selection_args, null, null, null);
//        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        int i = 0;
        String data;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                data = cursor.getString(cursor.getColumnIndexOrThrow(COL_ALBUM_ART));
                data_all_album_art[i++] = data;
            } while (cursor.moveToNext());
            cursor.close();

        }
        return data_all_album_art;
    }

    public int getCount(String direction) {
        int count = 0;
        String projection[] = {COL_ID};
        String selection = COL_DIRECTION + "=?";
        String selection_args[] = {direction};

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, projection, selection, selection_args, null, null, null);
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }
}