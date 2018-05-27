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
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.activities.MainActivity;
import com.wordpress.hossamhassan47.musicplayer.fragments.AddPlaylistFragment;
import com.wordpress.hossamhassan47.musicplayer.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private Context mContext;
    private List<Song> songList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, path;
        public ImageView overflow;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.text_view_song_title);
            path = (TextView) view.findViewById(R.id.text_view_song_path);
            overflow = (ImageView) view.findViewById(R.id.image_view_song_overflow);
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
    public void onBindViewHolder(final SongAdapter.MyViewHolder holder, int position) {
        Song song = songList.get(position);

        holder.title.setText(song.getSongTitle());
        holder.path.setText(song.getSongPath());

        // loading album cover using Glide library
        //Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });

//        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());

        popup.setOnMenuItemClickListener(new SongAdapter.MyMenuItemClickListener());

        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_rename:
                    Toast.makeText(mContext, "Rename", Toast.LENGTH_SHORT).show();
                    AddPlaylistFragment fragment = new AddPlaylistFragment();

                    fragment.show(((MainActivity)mContext).getSupportFragmentManager(), "dialog_AddPlaylistFragment");
                    return true;
                case R.id.action_delete:
                    Toast.makeText(mContext, "Delete", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
