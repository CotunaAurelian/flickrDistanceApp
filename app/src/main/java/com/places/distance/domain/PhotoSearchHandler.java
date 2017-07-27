package com.places.distance.domain;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.places.distance.Constants;
import com.places.distance.domain.flickr.api.FlickrOAuthClient;
import com.places.distance.domain.flickr.api.OnPicturesParsedListener;
import com.places.distance.domain.model.ImageData;

import java.util.ArrayList;

/**
 * Class that handle the Flicker photos search
 * Created by Aurelian Cotuna
 */

public class PhotoSearchHandler {

    private Context mContext;
    private FlickrOAuthClient mClient;

    private ArrayList<ImageData> mResultsCache;


    public PhotoSearchHandler(Context context, FlickrOAuthClient client) {
        this.mClient = client;
        this.mContext = context;
        this.mResultsCache = new ArrayList<>();
    }


    public void searchForLocation(final double lat, final double lon) {
        mClient.searchFor(lat, lon, 20, new OnPicturesParsedListener() {
            @Override
            public void onSuccess(ArrayList<ImageData> results) {
                mResultsCache.addAll(0, results);

                //Notify any listeners that the dataset has been updated
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.FILTER_TRACKING_IMAGESET_UPDATED));
            }

            @Override
            public void onError() {
                //No listeners need to be notified of this
            }
        });
    }

    public ArrayList getResults() {
        return mResultsCache;
    }


}
