package com.apps.eric.photogallery;

import android.net.Uri;
import android.util.Log;
import android.widget.Gallery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 5/20/2017.
 */

public class GetPhotos {

    private int mCode = 0;

    private static final String TAG = "GetPhotos";

    private static final String API_KEY_500px = ""; // ADD YOUR OWN KEY HERE
    private static final String FETCH_RECENTS_METHOD_500px = "https://api.500px.com/v1/photos";
    private static final String SEARCH_METHOD_500px = "https://api.500px.com/v1/photos/search";
    private static final String NUM_OF_PHOTOS = "100";
    private static final String IMAGE_SIZE = "20";

    private static final String API_KEY_FLICKR = ""; // ADD YOUR OWN KEY HERE
    private static final String FETCH_RECENTS_METHOD_FLICKR = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD_FLICKR = "flickr.photos.search";
    private static final Uri FLICKR_ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY_FLICKR)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchRecentPhotos(int code) {
        String url ="";
        mCode = code;
        if (code == 0) {
            url = buildUrl500px(FETCH_RECENTS_METHOD_500px, null);
        } else if (code == 1){
            url =  buildUrlFlickr(FETCH_RECENTS_METHOD_FLICKR, null);

        }
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query, int code) {
        String url = "";
        mCode = code;
        if (code == 0) {
            url = buildUrl500px(SEARCH_METHOD_500px, query);
        } else if (code == 1){
            url = buildUrlFlickr(SEARCH_METHOD_FLICKR, query);
        }

        Log.i(TAG, url);
        return downloadGalleryItems(url);
    }

    private List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> items = new ArrayList<>();

        try{
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return items;
    }

    private String buildUrl500px(String method, String query){
        Uri ENDPOINT = Uri
                .parse(method)
                .buildUpon()
                .appendQueryParameter("consumer_key", API_KEY_500px)
                .appendQueryParameter("rpp", NUM_OF_PHOTOS)
                .appendQueryParameter("image_size", IMAGE_SIZE)
                .build();

        Uri.Builder uriBuilder = ENDPOINT.buildUpon();

        if (method.equals(SEARCH_METHOD_500px)){
            uriBuilder.appendQueryParameter("term", query);
        }

        return uriBuilder.build().toString();
    }

    private String buildUrlFlickr(String method, String query) {
        Uri.Builder uriBuilder = FLICKR_ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);
        if (method.equals(SEARCH_METHOD_FLICKR)) {
            uriBuilder.appendQueryParameter("text", query);
        }
        return uriBuilder.build().toString();
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException,
                            JSONException{
        JSONObject photosJsonObject;
        JSONArray photoJsonArray;
        if (mCode == 0){
            photoJsonArray = jsonBody.getJSONArray("photos");
        } else {
            photosJsonObject = jsonBody.getJSONObject("photos");
            photoJsonArray = photosJsonObject.getJSONArray("photo");
        }

        if (mCode == 0) {
            for (int i = 0; i < photoJsonArray.length(); i++) {
                JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

                GalleryItem item = new GalleryItem();
                item.setId(photoJsonObject.getString("name"));
                item.setCaption(photoJsonObject.getString("name"));

                if (!photoJsonObject.has("image_url")) {
                    continue;
                }
                Log.i(TAG, photoJsonObject.getString("image_url"));
                item.setUrl(photoJsonObject.getString("image_url"));
                items.add(item);
            }
        } else {
            for (int i = 0; i < photoJsonArray.length(); i++) {
                JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
                GalleryItem item = new GalleryItem();
                item.setId(photoJsonObject.getString("id"));
                item.setCaption(photoJsonObject.getString("title"));
                if (!photoJsonObject.has("url_s")) {
                    continue;
                }
                item.setUrl(photoJsonObject.getString("url_s"));
                items.add(item);
            }
        }
    }
}
