package com.places.distance.domain.flickr.api;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.places.distance.domain.model.ImageData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Client for the Flicker API implementation, including Oauth triggers if needed
 * Created by Aurelian Cotuna
 */

public class FlickrOAuthClient extends OAuthBaseClient {
    private static final String TAG = FlickrOAuthClient.class.getName();

    public static final Class<? extends Api> REST_API_CLASS = FlickrApi.class;

    public static final String BASE_URL = "https://api.flickr.com/services/rest";

    public static final String REST_CONSUMER_KEY = "198e7ca27dcce302da61a4e208baa02c";

    public static final String REST_CONSUMER_SECRET = "0c44899acb5e8dbb";

    public static final String REST_CALLBACK_URL = "oauth://restresult";

    public FlickrOAuthClient(Context context) {
        super(context, REST_API_CLASS, BASE_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET,
                REST_CALLBACK_URL);
    }


    /**
     * Search for photos list in a certain area
     *
     * @param latitude  Longitude of the area
     * @param longitude Latitude of the area
     * @param radius    Radius in km
     */
    public void searchFor(double latitude, double longitude, double radius, final OnPicturesParsedListener listener) {

        StringBuilder builder = new StringBuilder("?method=flickr.photos.search");
        builder.append("&api_key=").append(REST_CONSUMER_KEY).
                append("&content_type=1").
                append("&lat=").append(longitude).append("&lon=").append(latitude).
                append("&radius=").append(radius).
                append("&radius_units=km&format=json&nojsoncallback=1");

        String apiUrl = getApiUrl(builder.toString());
        Log.d(TAG, "Sending API call to " + apiUrl);
        client.get(apiUrl, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                ArrayList<ImageData> results = new ArrayList<>();

                try {
                    JSONArray array = response.getJSONObject("photos").getJSONArray("photo");

                    for (int iterator = 0; iterator < array.length(); iterator++) {
                        String farm = array.getJSONObject(iterator).getString("farm");
                        String serverId = array.getJSONObject(iterator).getString("server");
                        String id = array.getJSONObject(iterator).getString("id");
                        String secret = array.getJSONObject(iterator).getString("secret");

                        String url = "https://farm" + farm + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + ".jpg";
                        Log.d(TAG, "Parsed image at : " + url);
                        results.add(new ImageData(url));
                    }

                    listener.onSuccess(results);

                } catch (Exception exception) {
                    //Catch all the parsing exceptions
                    listener.onError();
                }
            }
        });

    }

}

