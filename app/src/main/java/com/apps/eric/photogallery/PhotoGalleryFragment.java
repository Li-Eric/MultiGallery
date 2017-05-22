package com.apps.eric.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 5/20/2017.
 */

public class PhotoGalleryFragment extends Fragment{

    private static final String TAG = "PhotoGalleryFragment";

    private int whichApp = 0; // 0: 500px, 1:flickr

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    protected List<Double> mRatios = new ArrayList<>();

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_photo_gallery_500px, container, false);
        GreedoLayoutManager layoutManager = new GreedoLayoutManager(new PhotoAdapter(this, mItems));
        layoutManager.setMaxRowHeight(MeasUtils.dpToPx(200, getActivity()));

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(layoutManager);

        setupAdapter();

        int spacing = MeasUtils.dpToPx(4, getActivity());
        mPhotoRecyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                MyPreferences.setStoredQuery(getActivity(), s);
                updateItems();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s){
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String query = MyPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                MyPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.flickr:
                if (whichApp != 1){
                    whichApp = 1;
                    updateItems();
                }
                return true;
            case R.id.five_hundredpx:
                if (whichApp != 0){
                    whichApp = 0;
                    updateItems();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems(){
        String query = MyPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query, whichApp).execute();
    }

    private void setupAdapter(){
        if (isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(this, mItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        private String mQuery;
        private int mCode;

        public FetchItemsTask(String query, int code){
            mQuery = query;
            mCode = code;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            if (mQuery == null){
                return new GetPhotos().fetchRecentPhotos(mCode);
            } else {
                return new GetPhotos().searchPhotos(mQuery, mCode);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items){
            mItems = items;
            new GetRatiosTask().execute();
            //setupAdapter();
        }
    }

    private class GetRatiosTask extends AsyncTask<Void, Void, List<Double>> {
        @Override
        protected List<Double> doInBackground(Void... params){
            List<Double> doubles = new ArrayList<>();
            for (int i = 0; i < mItems.size(); i++){
                try {
                    URL url = new URL(mItems.get(i).getUrl());
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    int width = image.getWidth();
                    int height = image.getHeight();
                    double ratio = (double) width/height;
                    doubles.add(ratio);
                } catch (IOException ie){
                    Log.e(TAG, "ERROR: " + ie);
                }
            }
            return doubles;
        }

        @Override
        protected void onPostExecute(List<Double> doubles){
            mRatios = doubles;
            setupAdapter();
        }
    }
}
