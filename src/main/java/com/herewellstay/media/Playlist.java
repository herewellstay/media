package com.herewellstay.media;

import com.herewellstay.events.Event;
import com.herewellstay.events.OnEventListener;
import com.herewellstay.events.OnEventListeners;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {
    public static final String EVENT_POSITION_CHANGE = "position_change";
    private List<PlaylistItem> items;
    private int position;
    private OnEventListeners onEventListeners;

    public Playlist(List<PlaylistItem> items) {
        this.items = items;
        this.position = 0;
        this.onEventListeners =new OnEventListeners();
    }
    public Playlist() {
        this(new ArrayList<>());
    }

    public int size(){
        return items.size();
    }
    public PlaylistItem get(int index){
        return items.get(index);
    }

    public void add(PlaylistItem item) {
        items.add(item);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {

        this.position = position;
        Event e = new Event();
        e.setName(EVENT_POSITION_CHANGE);
        e.put("position", position);
        onEventListeners.onEvent(e);
    }

    public void previous() {

        if(position==0){
            position=size()-1;
        }
        position--;
        setPosition(position);
    }

    public boolean isEmpty() {
        return size()==0;
    }

    public void next() {
        if(position==size()-1){
            position=0;
        }
        position++;
        setPosition(position);
    }

    public PlaylistItem current(){
        return get(getPosition());
    }
    public void addOnEventListener(OnEventListener listener) {
        this.onEventListeners .add(listener);
    }



}
