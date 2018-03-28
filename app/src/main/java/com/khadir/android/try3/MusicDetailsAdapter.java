package com.khadir.android.try3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lenovo on 18-Mar-18.
 */

public class MusicDetailsAdapter extends ArrayAdapter<MusicDetails> {

    String aa;
    Bitmap bitmap;

    static class ViewHolder {
        public TextView song_name, artist;
        public ImageView album_art;
    }

    public MusicDetailsAdapter(@NonNull Context context, ArrayList<MusicDetails> musicDetails) {
        super(context, 0, musicDetails);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.single_music_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.song_name = listItemView.findViewById(R.id.song_name);
            viewHolder.artist = listItemView.findViewById(R.id.artist);
            viewHolder.album_art = listItemView.findViewById(R.id.album_art);

            listItemView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) listItemView.getTag();

        MusicDetails currentMusicDetails = getItem(position);

//        song_name = listItemView.findViewById(R.id.song_name);
        holder.song_name.setText(currentMusicDetails.getSong_name());

//        artist = listItemView.findViewById(R.id.artist);
        holder.artist.setText(currentMusicDetails.getArtist());

//        album_art = listItemView.findViewById(R.id.album_art);
        aa = currentMusicDetails.getAlbum_art();
        if (aa != null) {
            bitmap = BitmapFactory.decodeFile(aa);
            holder.album_art.setImageBitmap(bitmap);
        } else {
            holder.album_art.setImageResource(R.drawable.ic_music_note_black_24dp);
        }

        return listItemView;
    }
}