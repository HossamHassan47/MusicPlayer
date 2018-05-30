package com.wordpress.hossamhassan47.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.activities.NowPlayingActivity;
import com.wordpress.hossamhassan47.musicplayer.model.Album;

import java.util.List;

/**
 * Adapter that used to list all Albums
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    private Context mContext;
    private List<Album> albums;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtAlbumTitle, txtNoOfSongs;
        public ImageView imgThumbnail;
        public LinearLayout layoutAlbum;

        public MyViewHolder(View view) {
            super(view);

            txtAlbumTitle = (TextView) view.findViewById(R.id.text_view_title);
            txtNoOfSongs = (TextView) view.findViewById(R.id.text_view_no_of_songs);
            imgThumbnail = (ImageView) view.findViewById(R.id.image_view_thumbnail);
            layoutAlbum = (LinearLayout) view.findViewById(R.id.linear_layout_album);
        }
    }

    public AlbumAdapter(Context mContext, List<Album> albums) {
        this.mContext = mContext;
        this.albums = albums;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Album album = albums.get(position);

        holder.txtAlbumTitle.setText(album.getTitle());
        holder.txtNoOfSongs.setText(album.getNumOfSongs() + " songs");
        holder.imgThumbnail.setImageResource(album.getThumbnail());

        holder.layoutAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NowPlayingActivity.class);
                intent.putExtra("albumTitle", album.getTitle());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }
}
