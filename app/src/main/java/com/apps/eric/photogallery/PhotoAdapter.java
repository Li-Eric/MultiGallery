package com.apps.eric.photogallery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import java.util.List;

/**
 * Created by Eric on 5/21/2017.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>
        implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {

    private PhotoGalleryFragment mPhotoGalleryFragment;
    private List<GalleryItem> mGalleryItems;

    public PhotoAdapter(PhotoGalleryFragment photoGalleryFragment, List<GalleryItem> galleryItems) {
        mPhotoGalleryFragment = photoGalleryFragment;
        mGalleryItems = galleryItems;
    }

    @Override
    public double aspectRatioForIndex(int index) {
        if (index >= mPhotoGalleryFragment.mRatios.size()) {
            return 1.0;
        }
        return mPhotoGalleryFragment.mRatios.get(index);
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mPhotoGalleryFragment.getActivity());
        View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
        return new PhotoHolder(mPhotoGalleryFragment, view);
    }

    @Override
    public void onBindViewHolder(PhotoHolder photoHolder, int position) {
        GalleryItem galleryItem = mGalleryItems.get(position);
        photoHolder.bindGalleryItem(galleryItem);
    }

    @Override
    public int getItemCount() {
        return mGalleryItems.size();
    }
}
