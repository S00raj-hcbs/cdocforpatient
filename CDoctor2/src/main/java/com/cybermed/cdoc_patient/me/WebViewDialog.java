package com.cybermed.cdoc_patient.me;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.cybermed.cdoc_patient.BuildConfig;
import com.cybermed.cdoc_patient.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class WebViewDialog extends Dialog {

    Context context;
    ImageView download;
    PDFView imageView;
    String base64, fileName;
    TextView txtTittle,tvPageNumber;
    Uri uri;
    File imgFile;

    public WebViewDialog(Context context, int layout) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        initView(layout);
    }

    private void initView(int layout) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getWindow()).
                setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Window window = getWindow();
            if (window != null) {
                WindowCompat.setDecorFitsSystemWindows(window, true);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
                WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
                controller.setAppearanceLightStatusBars(true);
                controller.setAppearanceLightNavigationBars(true);
                ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            }
        }
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        setCanceledOnTouchOutside(false);
        getWindow().setAttributes(lp);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (layout == R.layout.dialog_webview) {

            download = findViewById(R.id.download);
            ImageView share = findViewById(R.id.share);
            imageView = findViewById(R.id.image_view);
            txtTittle = findViewById(R.id.toolbar_title);
            tvPageNumber = findViewById(R.id.tvPageNumber);
            findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile();
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareFile();
                }
            });
        }

    }


    public void setDisplayUrl(String url, String tittle) {
        if (tittle != null) {
            txtTittle.setText(tittle);
        }
        base64 = url;
        byte[] imageAsBytes = Base64.decode(url.getBytes(), Base64.DEFAULT);
       // storetoPdfandOpen(context, base64);

        fileName = "Attachments.pdf";
        deleteFileByName( context,  fileName);
       // downloadPdfFromBase64(context,  base64,  fileName);
        uri=addWatermarkToPDF(url, "Cybermed Health");

       /* imageView.fromUri(uri).onDrawAll(new OnDrawListener() {
            @Override
            public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

            }
        }).load();*/

        imageView.fromUri(uri)
                .enableSwipe(true)
                .swipeVertical(true)
                .enableDoubletap(true)
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                        // This is where you can add custom drawings on top of the PDF
                        // Example: canvas.drawText("Sample Text", 50, 50, new Paint());
                       /* Paint paint = new Paint();
                        paint.setTextSize(50);
                        paint.setColor(android.graphics.Color.RED);
                        canvas.drawText("Cybermed Health", pageWidth / 2, pageHeight / 2, paint);*/
                    }
                }).onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        // Show page number (1-based index)
                        tvPageNumber.setText((page) + " / " + pageCount);
                    }
                })
                .load();
    }
// old code
    /*public void storetoPdfandOpen(Context context, String base) {
        String root = Environment.getExternalStorageDirectory().toString();

        Log.d("ResponseEnv", root);

        File myDir = new File(root + "/Cdoc");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);

        fileName = "Attachments-" + n + ".pdf";
        File file = new File(myDir, fileName);
        if (file.exists())
            file.delete();
        try {

            FileOutputStream out = new FileOutputStream(file);
            byte[] pdfAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
            out.write(pdfAsBytes);
            out.flush();
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
           // Toast.makeText(context, "You need to provide permission manually from app permission setting", Toast.LENGTH_LONG).show();
        }

      *//*  file = new File(Environment.getExternalStorageDirectory(), "Cdoc");
        imgFile = new File(file, fileName);*//*
        if (Build.VERSION.SDK_INT < 24) {
            uri = Uri.fromFile(file);
        } else {
            uri = Uri.parse("file://" + file); // My work-around for new SDKs, causes ActivityNotFoundException in API 10.
        }


    }*/

    public void shareFile() {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(share);
    }

    public void downloadFile() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setDataAndType(uri, "application/pdf");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(sendIntent);
       /* DownloadManager.Request request1 = new DownloadManager.Request(uri);
        request1.setDescription("Download file");   //appears the same in Notification bar while downloading
        request1.setTitle("Cdoc");
        request1.setVisibleInDownloadsUi(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        request1.setDestinationInExternalFilesDir(context, "/Cdoc", fileName);

        DownloadManager manager1 = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Objects.requireNonNull(manager1).enqueue(request1);*/
    }

    // new code
    public void downloadPdfFromBase64(Context context, String base64Pdf, String fileName) {
        Uri fileUri = null;
        try {
            // Decode the Base64 string into bytes
            byte[] pdfBytes = Base64.decode(base64Pdf, Base64.DEFAULT);

            // Create the file in the Downloads directory
            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped storage - Android 10 and above
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    outputStream = context.getContentResolver().openOutputStream(uri);
                    fileUri = uri; // Set the URI
                } else {
                    throw new Exception("Failed to create content resolver URI");
                }

            } else {
                // For Android 9 and below, save to external storage
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File pdfFile = new File(downloadsDir, fileName);
                outputStream = new FileOutputStream(pdfFile);
                fileUri = Uri.fromFile(pdfFile); // Set the URI
            }

            // Write the PDF bytes to the file
            if (outputStream != null) {
                outputStream.write(pdfBytes);
                outputStream.close();

                // Notify the user
               // Toast.makeText(context, "PDF downloaded successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("PdfDownloader", "Error downloading PDF", e);
           // Toast.makeText(context, "Failed to download PDF", Toast.LENGTH_SHORT).show();
        }

        uri=fileUri;

    }

    public void deleteFileByName(Context context, String fileName) {
        try {
            // Check if we're on Android 10 or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Query the MediaStore to find the file by name in the Downloads folder
                Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;

                String[] projection = new String[]{MediaStore.Downloads._ID};

                String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
                String[] selectionArgs = new String[]{fileName};

                ContentResolver contentResolver = context.getContentResolver();
                try (Cursor cursor = contentResolver.query(collection, projection, selection, selectionArgs, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        // Get the file's URI
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                        Uri uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id);

                        // Delete the file
                        int deletedRows = contentResolver.delete(uri, null, null);
                        if (deletedRows > 0) {
                            Log.d("DeleteFile", "File deleted successfully: " + fileName);
                        } else {
                            Log.e("DeleteFile", "Failed to delete file: " + fileName);
                        }
                    } else {
                        Log.e("DeleteFile", "File not found: " + fileName);
                    }
                }
            } else {
                // For Android 9 and below, delete the file directly from the Downloads folder
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        Log.d("DeleteFile", "File deleted successfully: " + fileName);
                    } else {
                        Log.e("DeleteFile", "Failed to delete file: " + fileName);
                    }
                } else {
                    Log.e("DeleteFile", "File not found: " + fileName);
                }
            }
        } catch (Exception e) {
            Log.e("DeleteFile", "Error deleting file: ", e);
        }
    }

    public void getFileByName(Context context, String fileName) {
        try {
            // Check if we're on Android 10 or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Query the MediaStore to find the file by name in the Downloads folder
                Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;

                String[] projection = new String[]{MediaStore.Downloads._ID};

                String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
                String[] selectionArgs = new String[]{fileName};

                ContentResolver contentResolver = context.getContentResolver();
                try (Cursor cursor = contentResolver.query(collection, projection, selection, selectionArgs, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        // Get the file's URI
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                        Uri uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id);


                    } else {
                        Log.e("DeleteFile", "File not found: " + fileName);
                    }
                }
            } else {
                // For Android 9 and below, delete the file directly from the Downloads folder
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        Log.d("DeleteFile", "File deleted successfully: " + fileName);
                    } else {
                        Log.e("DeleteFile", "Failed to delete file: " + fileName);
                    }
                } else {
                    Log.e("DeleteFile", "File not found: " + fileName);
                }
            }
        } catch (Exception e) {
            Log.e("DeleteFile", "Error deleting file: ", e);
        }
    }

    public Uri addWatermarkToPDF(String base64PDF, String watermarkText) {
        try {
            // Step 1: Decode the Base64 PDF to bytes
            byte[] decodedBytes = Base64.decode(base64PDF, Base64.DEFAULT);

            // Step 2: Write the bytes to a temporary file
            File tempInputFile = new File(context.getExternalCacheDir(), "input.pdf");
            FileOutputStream fos = new FileOutputStream(tempInputFile);
            fos.write(decodedBytes);
            fos.close();

            // Step 3: Create a destination file for the watermarked PDF
            File tempOutputFile = new File(context.getExternalCacheDir(), "Attachments.pdf");

            // Step 4: Apply watermark using the existing logic
            PdfReader reader = new PdfReader(tempInputFile.getAbsolutePath());
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(tempOutputFile));

            int total = reader.getNumberOfPages();
            PdfContentByte under;
            for (int i = 1; i <= total; i++) {
                under = stamper.getOverContent(i);
                under.saveState();

                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.2f);  // 20% transparency
                under.setGState(gs);
                String fontPath = "res/font/roboto_bold.ttf"; // or an absolute path
                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.WINANSI, BaseFont.EMBEDDED);
                under.beginText();
                under.setFontAndSize(bf, 64);
                under.showTextAligned(PdfContentByte.ALIGN_CENTER, watermarkText, 300, 500, 45);
                under.endText();

                under.restoreState();
            }

            stamper.close();
            reader.close();

            // Step 5: Return the Uri of the watermarked PDF file
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +".fileprovider", tempOutputFile);

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to add watermark", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

 /*   public void addWatermarkToPDF(String src, String dest, String watermarkText) {
        try {
            // Read the existing PDF
            PdfReader reader = new PdfReader(src);
            deleteFileByName( context, src);
            // Create the output PDF
            File outputFile = new File(context.getExternalCacheDir(), dest);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

            // Get the PDF content
            PdfContentByte under;
            int total = reader.getNumberOfPages() + 1;

            // Loop through each page
            for (int i = 1; i < total; i++) {
                under = stamper.getUnderContent(i);

                // Set transparency
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.3f);  // 30% transparency
                under.setGState(gs);

                // Set font and size
                String fontPath = "res/font/roboto_bold.ttf"; // or an absolute path
                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.WINANSI, BaseFont.EMBEDDED);
                under.beginText();
                under.setFontAndSize(bf, 80);
                under.setTextMatrix(30, 30);  // Adjust position
                under.showTextAligned(PdfContentByte.ALIGN_CENTER, watermarkText, 300, 400, 45);  // Rotate 45 degrees
                under.endText();
            }

            // Close the stamper and reader
            stamper.close();
            reader.close();

            Toast.makeText(context, "Watermark added successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to add watermark", Toast.LENGTH_SHORT).show();
        }
    }*/
}
