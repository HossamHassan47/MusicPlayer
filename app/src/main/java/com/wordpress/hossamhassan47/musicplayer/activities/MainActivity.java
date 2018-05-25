package com.wordpress.hossamhassan47.musicplayer.activities;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.wordpress.hossamhassan47.musicplayer.R;
import com.wordpress.hossamhassan47.musicplayer.adapters.PlaylistAdapter;
import com.wordpress.hossamhassan47.musicplayer.fragments.AddPlaylistFragment;
import com.wordpress.hossamhassan47.musicplayer.fragments.NoticeDialogListener;
import com.wordpress.hossamhassan47.musicplayer.model.Playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NoticeDialogListener {

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private List<Playlist> playlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AddPlaylistFragment fragment = new AddPlaylistFragment();
//
//                fragment.show(getSupportFragmentManager(), "dialog_AddPlaylistFragment");
//            }
//        });

        // Set title
        setTitle(getResources().getString(R.string.title_playlists));

        // Get Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Playlist array list
        playlists = new ArrayList<>();

        // Playlist adapter
        adapter = new PlaylistAdapter(this, playlists);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        preparePlaylists();
    }

    /**
     * Adding few playlists for testing
     */
    private void preparePlaylists() {
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            Playlist a = new Playlist("Playlist " + (i + 1), random.nextInt(100), R.drawable.ic_playlist);
            playlists.add(a);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Playlist a = new Playlist("Playlist New", 0, R.drawable.ic_playlist);
        playlists.add(a);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_playlist) {
            AddPlaylistFragment fragment = new AddPlaylistFragment();

            fragment.show(getSupportFragmentManager(), "dialog_AddPlaylistFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
