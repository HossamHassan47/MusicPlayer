package com.wordpress.hossamhassan47.musicplayer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.helper.Utilities;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.util.List;

/**
 * Adapter that used to list selected album songs
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {
    private Context mContext;
    private List<Song> songList;
    private Utilities utils = new Utilities();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSongTitle, txtDuration;
        public LinearLayout layoutSong;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);

            txtSongTitle = (TextView) view.findViewById(R.id.text_view_song_title);
            txtDuration = (TextView) view.findViewById(R.id.text_view_song_duration);
            layoutSong = (LinearLayout) view.findViewById(R.id.linear_layout_song);
            thumbnail = (ImageView) view.findViewById(R.id.image_view_thumbnail);
        }
    }

    public SongAdapter(Context mContext, List<Song> songs) {
        this.mContext = mContext;
        this.songList = songs;
    }

    @Override
    public SongAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card, parent, false);

        return new SongAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Song song = songList.get(position);

        holder.txtSongTitle.setText(song.getSongTitle());
        holder.txtDuration.setText("" + utils.milliSecondsToTimer(Long.valueOf(song.getDuration())));

        holder.layoutSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Log.v("Adapter", "Is Playing: " + song.isPlaying());
        //Log.v("Adapter", "Is Paused: " + song.isPaused());

        if (song.isPaused()) {
            // Display paused icon
            holder.thumbnail.setImageResource(R.drawable.ic_song_paused);
        } else if (song.isPlaying()) {
            // Display playing gif image
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(holder.thumbnail);
            Glide.with(mContext).load(R.raw.gif_playing_song).into(imageViewTarget);
        } else {
            // Display default song icon
            holder.thumbnail.setImageResource(R.drawable.ic_song);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
