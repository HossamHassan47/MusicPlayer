package com.wordpress.hossamhassan47.musicplayer.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.activities.MainActivity;
import com.wordpress.hossamhassan47.musicplayer.fragments.AddPlaylistFragment;
import com.wordpress.hossamhassan47.musicplayer.helper.Utilities;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private Context mContext;
    private List<Song> songList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSongTitle, txtDuration;
        public LinearLayout layoutSong;

        public MyViewHolder(View view) {
            super(view);

            txtSongTitle = (TextView) view.findViewById(R.id.text_view_song_title);
            txtDuration = (TextView) view.findViewById(R.id.text_view_song_duration);
            layoutSong = (LinearLayout) view.findViewById(R.id.linear_layout_song);
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

    private Utilities utils = new Utilities();

    @Override
    public void onBindViewHolder(final SongAdapter.MyViewHolder holder, int position) {
        Song song = songList.get(position);

        holder.txtSongTitle.setText(song.getSongTitle());
        holder.txtDuration.setText("" + utils.milliSecondsToTimer(Long.valueOf(song.getDuration())));

        holder.layoutSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Play", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
