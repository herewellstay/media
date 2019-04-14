package com.herewellstay.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import com.herewellstay.events.Event;
import com.herewellstay.events.OnEventListener;
import com.nshmura.snappysmoothscroller.SnapType;
import com.nshmura.snappysmoothscroller.SnappyLinearLayoutManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistView extends RecyclerView {
    private static final int FLING_SCALE_DOWN_FACTOR =1;
    private final PlaylistViewAdapter adapter;
    private final SnappyLinearLayoutManager linearLayoutManager;
    private Playlist playlist;

    public PlaylistView(@NonNull Context context, AttributeSet attributes) {
        super(context, attributes);

        setHasFixedSize(true);
        linearLayoutManager = new SnappyLinearLayoutManager(context);

// Set the SnapType
        linearLayoutManager.setSnapType(SnapType.CENTER);

// Set the Interpolator
        linearLayoutManager.setSnapInterpolator(new DecelerateInterpolator());
        setLayoutManager(linearLayoutManager);
        adapter = new PlaylistViewAdapter();
        setAdapter(adapter);

    }



    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;

        adapter.setPlaylist(playlist);
        //scroll(playlist.getPosition());
        playlist.addOnEventListener(new OnEventListener() {
            @Override
            public void onEvent(Event e) {
                if (e.getName().equals(Playlist.EVENT_POSITION_CHANGE)) {
                    scroll((int) e.get("position"));
                }
            }
        });

    }

    public void scroll(int position) {
        smoothScrollToPosition(position);
        adapter.notifyDataSetChanged();
    }


    public void sync() {
        scrollToPosition(playlist.getPosition());
        adapter.notifyDataSetChanged();
    }

}
