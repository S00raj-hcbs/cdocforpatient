package com.cybermed.cdoc_patient.camera;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.util.SystemFunctionUtil;

import java.util.List;


public class FullScreenPhotoDialog extends Dialog {

    private int position;
    private ImageView deleteBtn;
    private DeletePhoto adapterCallBack;
    private List<Uri> photos;


    public interface DeletePhoto {
        void deletePhoto(List<Uri> photos);
    }


    public FullScreenPhotoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.full_image, null);
        setContentView(view);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        PagerAdapter adapter = new PhotoPageAdapter(getContext(), photos);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        addBackBtn(view);

        deleteBtn = view.findViewById(R.id.deleteButton);
        deleteBtn.setOnClickListener(v -> {
            if(SystemFunctionUtil.checkDoubleClick()) {
                int currPosition = viewPager.getCurrentItem();
                photos.remove(currPosition);
                adapterCallBack.deletePhoto(photos);
                adapter.notifyDataSetChanged();
                animateDialog(view, false);
            }
        });

        setOnShowListener(dialog -> animateDialog(view, true));

        setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_BACK) {
                animateDialog(view, false);
                return true;
            }
            return false;
        });
    }

    public static FullScreenPhotoDialog newInstance(Context context, DeletePhoto adapterCallBack, int position, List<Uri> photos) {
        FullScreenPhotoDialog dialog = new FullScreenPhotoDialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.photos = photos;
        dialog.adapterCallBack = adapterCallBack;
        dialog.position = position;
        return dialog;
    }


    private void addBackBtn(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getContext().getResources().getDrawable(R.drawable.icon_back_row));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateDialog(view, false);
            }
        });
    }


    private void animateDialog(View view, boolean open) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int w = view.getWidth();
            int h = view.getHeight();
            int endRadius = (int) Math.hypot(w, h);
            if(open) {
                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, 0, endRadius);
                view.setVisibility(View.VISIBLE);
                revealAnimator.setDuration(500);
                revealAnimator.start();
            } else {
                Animator anim = null;
                anim = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, endRadius, 0);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.INVISIBLE);
                        dismiss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                anim.setDuration(500);
                anim.start();
            }
        }
    }
}
