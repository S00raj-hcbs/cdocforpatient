package com.cybermed.cdoc_patient.login.onBoarding;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.cybermed.cdoc_patient.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    ArrayList<Drawable> itemsList;

    public ViewPagerAdapter(ArrayList<Drawable> itemsList) {
        this.itemsList = itemsList;
    }


    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.custom_on_board_view, container, false);
        ImageView imageView = view.findViewById(R.id.onBoardImage);
        imageView.setImageDrawable(itemsList.get(position));
        container.addView(view);
        return view;
    }
}
