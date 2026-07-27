package com.cybermed.cdoc_patient.camera;

import static android.os.Build.VERSION.SDK_INT;
import static com.cdfortis.datainterface.soap.WebServiceID.save_patient_imagelist_V1;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_IMAGE_LIST;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
/*
import com.bumptech.glide.request.target.CustomTarget;
*/
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * This class contains some utility functions to handle images
 */

public class ImageUtils {

    public final static String IMAGE_SELECT_KEY = "image_select_key";
    public final static String APPT_ID_KEY = "appt_id_key";
    public final static int CAMERA_REQUEST = 2;
    public final static int PICK_IMG_REQUEST = 1;
    public final static int THUMB_SIZE = 200;
    public final static int COMPRESSED_SIZE = 640;

    public final static int MAX_PHOTOS = 7;

    //method hasPermissions for checking if already has permission
    public static boolean hasPermissions(Context context, String... permissions) {
        if (SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //TODO: unused
    //send images by multiple soapEnvelope
    public static void sendImageRxSingleSoap(Context context, List<Uri> uriList) {

        final String userId = CDoctor2Application.getLoginInfo().getAccount();
        final String appt_id = "27608";
        final String orgCode = CDoctor2Application.getLoginInfo().getUserInfo().getService_code();

        Object o = Observable.<Bitmap>create(emitter -> {
            for (Uri uri : uriList) {
                Glide.with(context)
                        .asBitmap()
                        .apply(new RequestOptions().override(COMPRESSED_SIZE))
                        .load(uri)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                emitter.onNext(resource);
                            }
                        });
            }
        })
                .flatMap(bitmap -> Observable.fromCallable(() -> {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    String encoded = Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
                    SoapObject soapObject = toSoapObj(encoded);
                    return soapObject;
                }).subscribeOn(Schedulers.computation()))
                .flatMap(soapObject -> Observable.fromCallable(() -> {
                    String result = WebService.getInstance().testCallingWebservice(save_patient_imagelist_V1, userId, appt_id, orgCode, soapObject).toString();
                    return null;
                }).subscribeOn(Schedulers.io()))
                .subscribe();
    }

    //compress and send multiple images with one soapEnvelope
    @SuppressLint("CheckResult")
    public static void sendImageRxMultipleSoap(Context context, List<Uri> uriList, String apptId) {

        final String userId = CDoctor2Application.getLoginInfo().getAccount();
        final String appt_id = apptId;
        final String orgCode = CDoctor2Application.getLoginInfo().getUserInfo().getService_code();

        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
            Observable.fromIterable(uriList)
                    .flatMap(uri -> Observable.fromCallable(() -> {
                        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                            if (inputStream == null) throw new Exception("Failed to open image stream");

                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        }
                    }).subscribeOn(Schedulers.io()))
                    .toList()
                    .flatMap((List<String> encodedList) -> {
                        SoapObject imageFile = toSoapObj(encodedList);
                        return Single.fromCallable(() ->
                                WebService.getInstance().testCallingWebservice(
                                        save_patient_imagelist_V1,
                                        userId,
                                        apptId,
                                        orgCode,
                                        imageFile
                                ).toString()
                        ).subscribeOn(Schedulers.io());
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                if (result.trim().equals("1")) {
                                    Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }else {
            Disposable thread = Observable.<Bitmap>create(emitter -> {
                        AtomicInteger atomicInteger = new AtomicInteger(uriList.size());
                        for (Uri uri : uriList) {
                      /*      if (SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
                    *//*Glide.with(context)
                            .asBitmap()
                            .load(uri)
                            .apply(new RequestOptions().override(640,640))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
                                 //   String encoded = encodeBitmap(bitmap);
                                    emitter.onNext(bitmap);
                                    emitter.onComplete();
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    emitter.onError(new Exception("Glide load failed"));
                                }
                            });*//*
                            }else {*/
                                Glide.with(context)
                                        .asBitmap()
                                        .load(new File(uri.getPath()))
                                        .apply(new RequestOptions().override(COMPRESSED_SIZE))
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                emitter.onNext(resource);
                                                checkEmitCompleted(atomicInteger, emitter);
                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                super.onLoadFailed(errorDrawable);
                                                checkEmitCompleted(atomicInteger, emitter);
                                            }
                                        });
                            }

                       // }
                    }).flatMap(bitmap -> Observable.fromCallable(() -> {
                                Log.e("bitmap",""+bitmap);
                                String encoded = encodeBitmap(bitmap);
                                Log.e("encode",""+encoded);
                                Toast.makeText(context, "encode Bitmap is "+encoded, Toast.LENGTH_SHORT).show();
                                return encoded;
                            }
                    ).subscribeOn(Schedulers.computation()))
                    .toList()
                    .observeOn(Schedulers.io())
                    .map((encodedList) -> {
                        SoapObject imageFile = toSoapObj(encodedList);
                        return WebService.getInstance().testCallingWebservice(save_patient_imagelist_V1, userId, appt_id, orgCode, imageFile).toString();
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(next -> {
                        if (next.trim().equals("1")) {
                            //sendingHost.onSentFailed();
                            Toast.makeText(context, "Send Complete", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Send Failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    public interface SendingHost {
        void onSentFailed();
    }



    public static String encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String encoded = Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
        return encoded;
    }

    public static void checkEmitCompleted(AtomicInteger i, Emitter emitter) {
        if (i.decrementAndGet() == 0) {
            emitter.onComplete();
        }
    }

    public static void checkStoragePermission(Activity activity, Runnable successCallBack, Runnable failureCallBack) {
        /*MultiplePermissionsListener mpl = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                //if not all permissions are granted, go back
                if (!report.areAllPermissionsGranted()) {
                    new AlertDialog.Builder(activity)
                            .setTitle(activity.getString(R.string.ask_storage_permission_title))
                            .setMessage(activity.getString(R.string.ask_storage_permission_msg))
                            .setPositiveButton(activity.getString(R.string.btn_ok), (d, w) -> {
                                d.dismiss();
                                failureCallBack.run();
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    successCallBack.run();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        };*/


        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
           /* Dexter.withActivity(activity)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES
                    ).withListener(mpl).check();*/
            successCallBack.run();
        } else {
            /*Dexter.withActivity(activity)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).withListener(mpl).check();*/
            successCallBack.run();
        }



    }

    public static void imageSelectionPopUp(Activity context, String appt_id, int requestCode, List<Uri> photos) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_upload_image, null);

        dialogView.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, ImageSelectActivity.class);
                Bundle b = new Bundle();
                b.putInt(ImageUtils.IMAGE_SELECT_KEY, ImageUtils.CAMERA_REQUEST);
                if (photos != null)
                    b.putParcelableArrayList(KEY_IMAGE_LIST, (ArrayList) photos);
                b.putString(ImageUtils.APPT_ID_KEY, appt_id);
                intent.putExtras(b);
                context.startActivityForResult(intent, requestCode);
            }
        });
        dialogView.findViewById(R.id.btn_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, ImageSelectActivity.class);
                Bundle b = new Bundle();
                b.putInt(ImageUtils.IMAGE_SELECT_KEY, ImageUtils.PICK_IMG_REQUEST);
                if (photos != null)
                    b.putParcelableArrayList(KEY_IMAGE_LIST, (ArrayList) photos);
                b.putString(ImageUtils.APPT_ID_KEY, appt_id);
                intent.putExtras(b);
                context.startActivityForResult(intent, requestCode);
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

    //TODO: unused
    public static SoapObject toSoapObj(String encoded) {
        SoapObject so = new SoapObject();
        so.addProperty("base64Binary", encoded);
        return so;
    }

    public static SoapObject toSoapObj(List<String> encodedList) {
        SoapObject so = new SoapObject();
        for (String s : encodedList)
            so.addProperty("base64Binary", s);
        return so;
    }


}
