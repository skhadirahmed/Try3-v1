package com.khadir.android.try3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lenovo on 18-Mar-18.
 */

public class MusicDetailsAdapter extends ArrayAdapter<MusicDetails> {

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
        }

        MusicDetails currentMusicDetails = getItem(position);

        TextView song_name = listItemView.findViewById(R.id.song_name);
        song_name.setText(currentMusicDetails.getSong_name());

        TextView artist = listItemView.findViewById(R.id.artist);
        artist.setText(currentMusicDetails.getArtist());

        return listItemView;
    }
}