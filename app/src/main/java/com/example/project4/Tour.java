package com.example.project4;

import android.net.Uri;

public class Tour {
    private String name;
    private String description;
    private String videoUri;

    public Tour(String name, String description, String videoUri) {
        this.name = name;
        this.description = description;
        this.videoUri = videoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Uri getVideoUri() {
        return Uri.parse(videoUri);
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
