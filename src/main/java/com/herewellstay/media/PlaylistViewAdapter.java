package com.herewellstay.media;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistViewAdapter extends RecyclerView.Adapter<PlaylistViewAdapter.ViewHolder> {
    private Playlist playlist;
    private View.OnClickListener onClickListener;
    private int lastSelected=0;

    public PlaylistViewAdapter() {
        playlist=new Playlist();
        onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_item_view, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistItem item = playlist.get(position);
        ImageView itemImageView = holder.itemView.findViewById(R.id.itemImageView);
        TextView itemTextView = holder.itemView.findViewById(R.id.itemTextView);
        itemTextView.setText(item.getName());
        try {
            String name = item.getName().trim().substring(0, 1);

            if (name.equals("\u202A") || name.equals("")){
                name = "~";
            }

            Glide.with(holder.itemView).
                    load(item.getThumbnail())
                    .error(TextDrawable.builder().buildRound(name,
                            ColorGenerator.MATERIAL.getRandomColor()))
                    .into(itemImageView);
        } catch (Exception e) {
           itemImageView.setImageDrawable(
                    TextDrawable.builder().buildRound("~",
                            ColorGenerator.MATERIAL.getRandomColor())
            );
        }
        if(position==playlist.getPosition()){
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.hwsMediaPlaylistSelectorColor));
        }
        else {
            holder.itemView.setBackgroundResource(0);
        }

    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.onClickListener = onClickListener;
    }



    protected class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playlist.setPosition(getAdapterPosition());
                    notifyDataSetChanged();
                    onClickListener.onClick(v);

                }
            });

        }
    }

    public void setPlaylist(Playlist playlist) {

        this.playlist = playlist;


        notifyDataSetChanged();
    }
}
