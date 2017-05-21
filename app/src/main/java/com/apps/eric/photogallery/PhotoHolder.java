package com.apps.eric.photogallery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Eric on 5/21/2017.
 */
class PhotoHolder extends RecyclerView.ViewHolder {
    private PhotoGalleryFragment mPhotoGalleryFragment;
    private ImageView mItemImageView;

    public PhotoHolder(PhotoGalleryFragment photoGalleryFragment, View itemView) {
        super(itemView);
        mPhotoGalleryFragment = photoGalleryFragment;

        mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
    }

    public void bindGalleryItem(GalleryItem galleryItem) {
        Picasso.with(mPhotoGalleryFragment.getActivity()).load(galleryItem.getUrl()).placeholder(R.drawable.loading)
                .into(mItemImageView);
    }
}
