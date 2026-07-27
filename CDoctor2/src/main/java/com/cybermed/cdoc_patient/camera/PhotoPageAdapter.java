package com.cybermed.cdoc_patient.camera;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.cybermed.cdoc_patient.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class PhotoPageAdapter extends PagerAdapter {

    private Context context;
    private List<Uri> photos;

    public PhotoPageAdapter(Context context, List<Uri> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        Glide.with(context)
                .load(photos.get(position))
                .into(photoView);
        container.addView(photoView, 0);
        return photoView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((PhotoView) object);
    }
}
