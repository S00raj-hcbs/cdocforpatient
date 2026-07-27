package com.cybermed.cdoc_patient.camera;

import static android.os.Build.VERSION.SDK_INT;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybermed.cdoc_patient.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FullScreenPhotoDialog.DeletePhoto {
    private List<Uri> photos;
    private Context context;
    private FullScreenPhotoDialog.DeletePhoto activityCallback;
    ItemClickLisnter itemClickLisnter;
    private PhotoAdapterCallback callback;
    private boolean isLastItemHidden = false;
    public PhotoRecyclerViewAdapter(Context context, FullScreenPhotoDialog.DeletePhoto activityCallback, List<Uri> photos) {
        if (photos == null) {
            this.photos = new ArrayList<>();
        } else {
            this.photos = photos;
        }
        this.context = context;
        this.activityCallback = activityCallback;
    }

    public void setPhotoAdapterCallback(PhotoAdapterCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == R.layout.recyclerview_photos) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_photos, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_upload_last_item, parent, false);
        }
        return new PhotoHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == photos.size() && photos.size() != ImageUtils.MAX_PHOTOS) ? R.layout.lay_upload_last_item : R.layout.recyclerview_photos;
    }

    @Override
    public int getItemCount() {
        return ImageUtils.MAX_PHOTOS == photos.size() ? photos.size() : photos.size() + 1;
    }

    public boolean addPhoto(Uri imageUri) {
        if (photos.size() == ImageUtils.MAX_PHOTOS) {
            return false;
        }
        photos.add(imageUri);
        notifyDataSetChanged();
        return true;
    }

    @SuppressWarnings("unused")
    public boolean addPhoto(List<Uri> imageUri) {
        if (photos.size() == ImageUtils.MAX_PHOTOS) {
            return false;
        }
        photos.addAll(imageUri);
        notifyDataSetChanged();
        return true;
    }

    @SuppressWarnings("unused")
    public void setPhoto(List<Uri> imageUri) {
        photos.clear();
        photos.addAll(imageUri);
        notifyDataSetChanged();
    }


    //return a deep copy
    public List<Uri> getPhotos() {
        return new ArrayList<>(photos);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == R.layout.lay_upload_last_item) {
            if (isLastItemHidden) {
                holder.itemView.setVisibility(View.GONE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
        } else {
        PhotoHolder photoHolder = (PhotoHolder) holder;
        if (position == photos.size()) {
            /*if(photoHolder.linearItem!=null)
            photoHolder.linearItem.setOnClickListener(v -> {
                itemClickLisnter.addNewItem();
            });*/

        } else {
            if (SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
                    // Now you can use the bitmap, e.g. display in ImageView
                   // imageView.setImageBitmap(bitmap);

                Glide.with(context)
                            .load(photos.get(position))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_doc)
                                    .dontAnimate())
                            .into(photoHolder.imageView);
            }else {
                Glide.with(context)
                        .load(new File(photos.get(position).getPath()))
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_doc)
                                .dontAnimate())
                        .into(photoHolder.imageView);
            }

//            photoHolder.imageView.setOnClickListener(view -> {
//                FullScreenPhotoDialog dialog = FullScreenPhotoDialog.newInstance(context, PhotoRecyclerViewAdapter.this, position, photos);
//                dialog.show();
//            });
            photoHolder.cancelImg.setOnClickListener(v -> {
                photos.remove(position);
                deletePhoto(photos);
            });
        }
        }
        PhotoHolder photoHolder = (PhotoHolder) holder;
        if (photoHolder.linearItem != null) {
            photoHolder.linearItem.setOnClickListener(v -> {
                itemClickLisnter.addNewItem();
            });
        }
    }

    public void setLastItemHidden(boolean hide) {
        isLastItemHidden = hide;
        notifyItemChanged(photos.size()); // Notify the adapter to update the last item
    }
    @Override
    public void deletePhoto(List<Uri> photos) {
        activityCallback.deletePhoto(photos);
        notifyDataSetChanged();
        if (photos.isEmpty() && callback != null) {
            callback.onPhotoListEmpty();
        }
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView cancelImg;
        private LinearLayout linearItem;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnail_photo);
            cancelImg = itemView.findViewById(R.id.imgCancel);
            linearItem = itemView.findViewById(R.id.linear_item);
        }
    }

    public interface ItemClickLisnter {
        void addNewItem();
    }

    public interface PhotoAdapterCallback {
        void onPhotoListEmpty();
    }

    public void setListner(ItemClickLisnter itemClickLisnter) {
        this.itemClickLisnter = itemClickLisnter;
    }
}
