package com.herewellstay.events;

import java.io.Serializable;

public interface OnEventListener extends Serializable {
    void onEvent(Event e);
}
