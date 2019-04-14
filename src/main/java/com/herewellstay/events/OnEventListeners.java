package com.herewellstay.events;

import java.util.ArrayList;
import java.util.List;

public class OnEventListeners implements OnEventListener{
    private List<OnEventListener> listeners = new ArrayList<OnEventListener>();

    public void add (OnEventListener listener) {
        listeners.add(listener);
    }
    public void remove (OnEventListener listener) {
        listeners.remove(listener);
    }
    public void onEvent(Event e) {
        for(OnEventListener listener:listeners) {
            listener.onEvent(e);
        }
    }
}