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

    private static final String TAG = "GetPhotos";

    private static final String API_KEY = "";
    private static final String FETCH_RECENTS_METHOD = "https://api.500px.com/v1/photos";
    private static final String SEARCH_METHOD = "https://api.500px.com/v1/photos/search";
    private static final String NUM_OF_PHOTOS = "100";
    private static final String IMAGE_SIZE = "20";



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

    public List<GalleryItem> fetchRecentPhotos() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
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

    private String buildUrl(String method, String query){
        Uri ENDPOINT = Uri
                .parse(method)
                .buildUpon()
                .appendQueryParameter("consumer_key", API_KEY)
                .appendQueryParameter("rpp", NUM_OF_PHOTOS)
                .appendQueryParameter("image_size", IMAGE_SIZE)
                .build();

        Uri.Builder uriBuilder = ENDPOINT.buildUpon();

        if (method.equals(SEARCH_METHOD)){
            uriBuilder.appendQueryParameter("term", query);
        }

        return uriBuilder.build().toString();
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException,
                            JSONException{
        JSONArray photoJsonArray = jsonBody.getJSONArray("photos");

        for (int i = 0; i < photoJsonArray.length(); i++){
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("name"));
            item.setCaption(photoJsonObject.getString("name"));

            if (!photoJsonObject.has("image_url")){
                continue;
            }
            Log.i(TAG, photoJsonObject.getString("image_url"));
            item.setUrl(photoJsonObject.getString("image_url"));
            items.add(item);
        }
    }
}
