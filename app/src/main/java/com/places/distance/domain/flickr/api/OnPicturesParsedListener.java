package com.places.distance.domain.flickr.api;

import com.places.distance.domain.model.ImageData;

import java.util.ArrayList;

/**
 * Interface used to trigger callbacks when the image parsing is complete
 * Created by Aurelian Cotuna
 */

public interface OnPicturesParsedListener {

    void onSuccess(ArrayList<ImageData> results);
    void onError();
}
