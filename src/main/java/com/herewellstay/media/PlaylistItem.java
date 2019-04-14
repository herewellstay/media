package com.herewellstay.media;

import java.io.Serializable;

public class PlaylistItem implements Serializable {
    private String name;
    private String thumbnail;
    private String path;

    public PlaylistItem(String name, String image, String path) {
        this.name = name;
        this.thumbnail = image;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPath() {
        return path;
    }
}
