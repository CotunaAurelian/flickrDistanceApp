package com.places.distance.domain.model;

/**
 * Model data for the information received from the Flickr API, regarding photos
 * Created by Aurelian Cotuna
 */

public class ImageData {

    private String mURL;

    public ImageData(String url) {
        this.mURL = url;
    }

    public String getUrl() {
        return mURL;
    }
}
