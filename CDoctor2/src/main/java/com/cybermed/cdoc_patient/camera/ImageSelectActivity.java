package com.cybermed.cdoc_patient.camera;

import static android.os.Build.VERSION.SDK_INT;
import static com.cybermed.cdoc_patient.camera.ImageUtils.CAMERA_REQUEST;
import static com.cybermed.cdoc_patient.camera.ImageUtils.PICK_IMG_REQUEST;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_IMAGE_LIST;
import static com.cybermed.cdoc_patient.util.AppConstant.REQUEST_IMAGE_SELECTION;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.camera.customcamera.view.CameraActivity;
import com.cybermed.cdoc_patient.common.BaseActivity;
import com.cybermed.cdoc_patient.databinding.ActivityImageDisplayBinding;
import com.picker.gallery.model.GalleryData;
import com.picker.gallery.view.PickerActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageSelectActivity extends BaseActivity {

    private Uri currentPhotoUri;
    private String currentPhotoPath;
    private RecyclerView selectedPhotos;
    private PhotoRecyclerViewAdapter photoRecyclerViewAdapter;
    private String appt_id;
    List<Uri> list;
    Button sendBtn;
    ActivityImageDisplayBinding binding;
    private int maxSelection = 7;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMediaLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // final View view = View.inflate(this, R.layout.activity_image_display, null);
        // setContentView(view);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_display);
        sendBtn = findViewById(R.id.sendBtn);
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
            pickMultipleMediaLauncher =
                    registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(), uris -> {
                        if (uris != null && !uris.isEmpty()) {
                            sendBtn.setEnabled(true);
                            List<Uri> uriList = new ArrayList<>();

                            for (Uri uri : uris) {
                                try {
                                    getContentResolver().takePersistableUriPermission(
                                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                uriList.add(uri);
                                Log.e("uri",""+uri);
                                //   photoRecyclerViewAdapter.addPhoto(uri);
                            }
                            sendBtn.setEnabled(true);
                            photoRecyclerViewAdapter.addPhoto(uriList);
                        }
                    });
        }
        setupRecyclerView();
        //setAddButton(view);
        addBackBtn();
        setSendBtn();


    }

    private void getBundleIntent(PhotoRecyclerViewAdapter photoRecyclerViewAdapter) {
        Bundle b = getIntent().getExtras();
        appt_id = b.getString(ImageUtils.APPT_ID_KEY);
        int value = b.getInt(ImageUtils.IMAGE_SELECT_KEY);
        if (value == PICK_IMG_REQUEST) {
            startGallerySelectIntent(photoRecyclerViewAdapter);
        } else if (value == CAMERA_REQUEST) {
            startCameraIntent();
        } else {
            onBackPressed();
        }
    }

    private void setupRecyclerView() {
        Bundle b = getIntent().getExtras();
        list = null;
        if (b.getParcelableArrayList(KEY_IMAGE_LIST) != null) {
            list = b.getParcelableArrayList(KEY_IMAGE_LIST);
            sendBtn.setEnabled(true);
        }
        selectedPhotos = findViewById(R.id.selected_photos);
        selectedPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        photoRecyclerViewAdapter = new PhotoRecyclerViewAdapter(this, (List<Uri> photos) -> {
            if (photos != null && photos.size() == 0) {
                sendBtn.setEnabled(false);
            }
        }, list);
        photoRecyclerViewAdapter.setListner(() -> {
            imageSelectionPopUp(ImageSelectActivity.this, appt_id, REQUEST_IMAGE_SELECTION, list,photoRecyclerViewAdapter);
        });
        selectedPhotos.setAdapter(photoRecyclerViewAdapter);
        getBundleIntent(photoRecyclerViewAdapter);
    }

    public void imageSelectionPopUp(Activity context, String appt_id, int requestCode, List<Uri> photos, PhotoRecyclerViewAdapter photoRecyclerViewAdapter) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_upload_image, null);

        dialogView.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startCameraIntent();
            }
        });
        dialogView.findViewById(R.id.btn_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startGallerySelectIntent(photoRecyclerViewAdapter);
            }
        });
        ImageView imgClose = dialogView.findViewById(R.id.imgclose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private void addBackBtn() {
        // Toolbar toolbar = view.findViewById(R.id.toolbar);
        // toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icon_back_row));
        binding.toolbar.txtTittle.setText("Select Images");
        binding.toolbar.backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void setSendBtn() {

        if (TextUtils.isEmpty(appt_id)) {
            sendBtn.setText("Upload");
        }
        sendBtn.setOnClickListener(v -> {
            if (photoRecyclerViewAdapter.getItemCount() == 1) {
                Toast.makeText(ImageSelectActivity.this, "Please add a picture", Toast.LENGTH_LONG).show();
            } else {
                if (TextUtils.isEmpty(appt_id)) {
                    List<Uri> photos = photoRecyclerViewAdapter.getPhotos();
                    Intent bundle = new Intent();
                    bundle.putExtra(KEY_IMAGE_LIST, (ArrayList) photos);
                    setResult(REQUEST_IMAGE_SELECTION, bundle);
                    finish();
                } else {
                    Toast.makeText(ImageSelectActivity.this, "Send started", Toast.LENGTH_LONG).show();
                    List<Uri> photos = photoRecyclerViewAdapter.getPhotos();
                    ImageUtils.sendImageRxMultipleSoap(
                            ImageSelectActivity.this, photos, appt_id);
                    onBackPressed();
                }
            }
        });

    }

/*
    //start intent to pick img from gallery
    private void startGallerySelectIntent() {
        int maxSelection = 7;
        if (photoRecyclerViewAdapter.getPhotos().size() != 0) {
            maxSelection = 7 - photoRecyclerViewAdapter.getPhotos().size();
        }
        Intent i = new Intent(this, PickerActivity.class);
        i.putExtra("IMAGES_LIMIT", maxSelection);
        i.putExtra("REQUEST_RESULT_CODE", PICK_IMG_REQUEST);
        startActivityForResult(i, 101);
    }
*/



    private void startGallerySelectIntent(PhotoRecyclerViewAdapter photoRecyclerViewAdapter) {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
            PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .setMaxItems(maxSelection-photoRecyclerViewAdapter.getPhotos().size())
                    .build();

            pickMultipleMediaLauncher.launch(request);
        } else {
            int maxSelection = 7;
            if (!photoRecyclerViewAdapter.getPhotos().isEmpty()) {
                maxSelection = 7 - photoRecyclerViewAdapter.getPhotos().size();
            }
            Intent i = new Intent(this, PickerActivity.class);
            i.putExtra("IMAGES_LIMIT", maxSelection);
            i.putExtra("REQUEST_RESULT_CODE", PICK_IMG_REQUEST);
            startActivityForResult(i, 101);
        }

    }


    //active camera intent
    private void startCameraIntent() {
        startActivityForResult(new Intent(ImageSelectActivity.this, CameraActivity.class),
                CAMERA_REQUEST);
    }

    //request for storage permission
    /*public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }*/


    //add a directory at CDOC/CDOC_Pictures
    private File createImageFile(String suffix) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "Image_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory(), "CDOC");
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File subImageDir = new File(storageDir, "CDOC_Pictures");
        if (!subImageDir.exists()) {
            subImageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                suffix,         /* suffix */
                subImageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //notify the gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 101 || requestCode == 2) {
            switch (resultCode) {
                case PICK_IMG_REQUEST:
                    if (intent != null) {
                        handleGalleryResponse(intent);
                    }
                    break;
                case CAMERA_REQUEST:
                    if (intent != null && intent.getExtras() != null) {
                        String uriString = intent.getExtras().getString("uri");
                        sendBtn.setEnabled(true);
                        photoRecyclerViewAdapter.addPhoto(Uri.parse(uriString));
                    }
                    break;
            }

        }
    }

    private void handleGalleryResponse(Intent data) {
        if (data != null && data.getExtras() != null) {
            ArrayList<GalleryData> list = data.getExtras().getParcelableArrayList("MEDIA");
            List<Uri> uriList = new ArrayList<>();

            //selected 1 image
            for (int i = 0; i < list.size(); i++) {
                Uri uri = Uri.parse(list.get(i).getPhotoUri());
                uriList.add(uri);
            }
            sendBtn.setEnabled(true);
            photoRecyclerViewAdapter.addPhoto(uriList);

        }
    }


}

