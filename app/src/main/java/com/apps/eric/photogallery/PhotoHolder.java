package com.apps.eric.photogallery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by Eric on 5/21/2017.
 */
public class PhotoHolder extends RecyclerView.ViewHolder{
    private PhotoGalleryFragment mPhotoGalleryFragment;
    private ImageView mImageView;

    public PhotoHolder(PhotoGalleryFragment photoGalleryFragment, View itemView) {
        super(itemView);
        mPhotoGalleryFragment = photoGalleryFragment;

        mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
    }

    public void bindGalleryItem(GalleryItem galleryItem) {
        Picasso.with(mPhotoGalleryFragment.getActivity()).load(galleryItem.getUrl()).placeholder(R.drawable.loading)
                .into(mImageView);
    }
}
