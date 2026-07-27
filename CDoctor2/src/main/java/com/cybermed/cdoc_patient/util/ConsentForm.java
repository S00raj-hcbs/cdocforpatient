package com.cybermed.cdoc_patient.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.modal.GeneralConsentForm;
import com.cybermed.cdoc_patient.modal.RPMConsentForm;
import com.cybermed.cdoc_patient.modal.RPMConsentForm_Maysam;
import com.cybermed.cdoc_patient.modal.RPMConsentForm_ThirdParty;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import org.threeten.bp.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.cybermed.cdoc_patient.camera.ImageUtils.checkStoragePermission;

public class ConsentForm {

    public interface ConsentFormCallBack {
        void successWithPDF(String b64_pdf);
    }

    private int adjustedHeight, adjustedWidth, documentMargin = 0;
    private final Activity holderActivity;
    private final Paragraph consentFormParagraph;
    private final String consentFormString;
    private final ConsentFormCallBack consentFormCallBack;
    private final String title;
    private AlertDialog ad;

    public ConsentForm(Activity holderActivity, RPMConsentForm rpmConsentForm, ConsentFormCallBack consentFormCallBack) {
        this.holderActivity = holderActivity;
        this.consentFormCallBack = consentFormCallBack;
        this.title = rpmConsentForm.getTitle();
        consentFormString = RemoteMonitoringConsentFormKt.RPMConsentFormText(rpmConsentForm);
        consentFormParagraph = RemoteMonitoringConsentFormKt.RPMConsentFormParagraph(rpmConsentForm);
    }

    public ConsentForm(Activity holderActivity, RPMConsentForm_Maysam rpmConsentForm, ConsentFormCallBack consentFormCallBack) {
        this.holderActivity = holderActivity;
        this.consentFormCallBack = consentFormCallBack;
        this.title = rpmConsentForm.getTitle();
        consentFormString = RemoteMonitoringConsentFormKt.RPMConsentFormMaysamText(rpmConsentForm);
        consentFormParagraph = RemoteMonitoringConsentFormKt.RPMConsentFormMaysamParagraph(rpmConsentForm);
    }

    public ConsentForm(Activity holderActivity, RPMConsentForm_ThirdParty rpmConsentForm, String consentFormContent, ConsentFormCallBack consentFormCallBack) {
        this.holderActivity = holderActivity;
        this.consentFormCallBack = consentFormCallBack;
        this.title = rpmConsentForm.getTitle();
        consentFormParagraph = RemoteMonitoringConsentFormKt.RPMThirdPartyConsentFormParagraph(rpmConsentForm, consentFormContent);
        consentFormString = RemoteMonitoringConsentFormKt.ParagraphToText(consentFormParagraph);
    }

    // signature 2
    public ConsentForm(Activity holderActivity, GeneralConsentForm rpmConsentForm, ConsentFormCallBack consentFormCallBack) {
        this.holderActivity = holderActivity;
        this.consentFormCallBack = consentFormCallBack;
        this.title = rpmConsentForm.getTitle();
        consentFormParagraph = RemoteMonitoringConsentFormKt.GeneralConsentFormParagraph(); // String -> Paragraph (iText only recognise Par.)
        consentFormString = RemoteMonitoringConsentFormKt.ParagraphToText(consentFormParagraph); // String -> String text (show on dialog)
    }

    //check storage permission before show consent form
    public void ShowConsentForm() {
       /* if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){
                ShowConsentFormImplementation();
            }else {
                new android.app.AlertDialog.Builder(holderActivity)
                        .setTitle(holderActivity.getString(R.string.ask_storage_permission_title))
                        .setMessage(holderActivity.getString(R.string.ask_storage_permission_msg))
                        .setPositiveButton(holderActivity.getString(R.string.btn_ok), (d, w) -> {
                            d.dismiss();
                        })
                        .setCancelable(false)
                        .show();
            }
        }else {

        }*/
        checkStoragePermission(holderActivity, this::ShowConsentFormImplementation, () -> {
        });
    }

    //internal implementation of consent form
    private void ShowConsentFormImplementation() {

        //for signature --start
        DisplayMetrics displayMetrics = new DisplayMetrics();
        holderActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        adjustedHeight = (int) (displayMetrics.heightPixels * 0.9);
//        adjustedWidth = (int) (displayMetrics.widthPixels * 0.9); // resize for displaying, old
        adjustedHeight = (int) (displayMetrics.heightPixels);
        adjustedWidth = (int) (displayMetrics.widthPixels);

        View sign_pad = LayoutInflater.from(holderActivity).inflate(R.layout.dialog_signature_pad, null);

        /*copy from example*/
        SignaturePad mSignaturePad = sign_pad.findViewById(R.id.signature_pad);
        TextView mClearButton = sign_pad.findViewById(R.id.sign_clear);
        Button mSaveButton = sign_pad.findViewById(R.id.sign_save);
        ImageView mBack=sign_pad.findViewById(R.id.btn_back);
        mSaveButton.setEnabled(false);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
                Log.d("signPad", "1");
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton.setOnClickListener(view -> mSignaturePad.clear());
        mBack.setOnClickListener(view -> dismissDialog());

        mSaveButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                generatePDF_iText(sign_pad.findViewById(R.id.signature_pad_container));
            }else {
                generatePDF_iTextAndroid10(sign_pad.findViewById(R.id.signature_pad_container));
            }


        });
        /*end from example*/


        TextView tv = sign_pad.findViewById(R.id.consent_form);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(consentFormString);

        //set ratio of views
        ConstraintLayout contraintLayout = sign_pad.findViewById(R.id.custom_dialog_layout_design_user_input);

        ViewGroup.LayoutParams lp = contraintLayout.getLayoutParams();
        lp.width = adjustedWidth;
        lp.height = adjustedHeight;
        contraintLayout.setLayoutParams(lp);

        ad = new AlertDialog.Builder(holderActivity,android.R.style.Theme_Light_NoTitleBar_Fullscreen).setView(sign_pad).create();
        ad.show();

        //for signature --end
    }

    public void dismissDialog() {
        if (ad != null)
            ad.dismiss();
    }


    /*Code Copy From stackoverFlow
     * https://stackoverflow.com/questions/2801116/converting-a-view-to-bitmap-without-displaying-it-in-android
     * */
    private Bitmap loadBitmapFromView(View v) {

        //document margin is added so that it fits A4 PDF nicely
        Bitmap b = Bitmap.createBitmap(adjustedWidth + documentMargin * 2, adjustedHeight / 5, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    private void generatePDF_iText(View signature_view) {
        //create file path for saving pdf
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/CDoc Consent Form/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }


        String targetPdf =  signature_view.getContext().getFilesDir() + File.separator+ "ShowConsentForm.pdf";
        File filePath = new File(targetPdf);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            //Initialize a Document for Consent Form
            Document document = new Document();

            //store locally and base64 fpr WS use
            PdfWriter.getInstance(document, baos);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            documentMargin = (int) document.leftMargin();

            //Getting Consent Form Paragraph
            consentFormParagraph.setLeading(20.0f, 1.25f);

            //it seems like does not take effect in pdf UI, but I add it anyways
            document.addTitle(title);
            document.addCreationDate();
           // BaseFont robotoBoldFont = BaseFont.createFont("fonts/Roboto-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font robotoBoldFont = getRobotoFont(signature_view.getContext(), 36.0f, Font.BOLD, BaseColor.BLACK);
           // Paragraph titleParagraph = new Paragraph(title);
            //titleParagraph.setFont(new Font(robotoBoldFont/*robotoBoldFont, 40.0f, Font.BOLD, BaseColor.BLACK*/));
            Paragraph titleParagraph = new Paragraph(title,robotoBoldFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.setSpacingAfter(20f);
            LocalDate ld = LocalDate.now();
            //BaseFont robotoBaseFont = BaseFont.createFont("fonts/Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font robotoBaseFont = getRobotoRegularFont(signature_view.getContext(),20.0f, Font.BOLD, BaseColor.BLACK);
            Paragraph signDate = new Paragraph(ld.getMonthValue() + "/" + ld.getDayOfMonth() + "/" + ld.getYear());
            //signDate.setFont(new Font(robotoBaseFont, 20.0f, Font.BOLD, BaseColor.BLACK));
            signDate.setFont(new Font(robotoBaseFont));
            signDate.setAlignment(Element.ALIGN_CENTER);

            //Getting Consent Form Image
            Bitmap signature = loadBitmapFromView(signature_view);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            signature.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(PageSize.A4);

            //Add Paragraph and Image to Document
            document.add(titleParagraph);
            document.add(consentFormParagraph);
            document.add(image);
            document.add(signDate);
            document.close();

            //get the Base64 String of the Consent form in PDF
            byte[] bytes = baos.toByteArray();
            String encoded = Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);

            consentFormCallBack.successWithPDF(encoded);
            dismissDialog();

        } catch (Exception e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(holderActivity, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void generatePDF_iTextAndroid10(View signature_view) {
        Context context = signature_view.getContext();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // 1. Create PDF document
            Document document = new Document();

            // 2. Prepare content values for MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "ShowConsentForm.pdf");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/CDoc Consent Form");

            // 3. Get ContentResolver and insert file entry
            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record");
            }

            // 4. Write PDF to both stream (for Base64) and file
            try (OutputStream fileOut = resolver.openOutputStream(uri)) {
                PdfWriter.getInstance(document, baos);
                PdfWriter.getInstance(document, fileOut);
                document.open();
                documentMargin = (int) document.leftMargin();
                // Add your PDF content (keep your existing code)
             /*   document.addTitle("Consent Form");
                document.addCreationDate();

                // Example content (replace with your actual content)
                Paragraph title = new Paragraph("Patient Consent Form");
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);*/
                //Getting Consent Form Paragraph
                consentFormParagraph.setLeading(20.0f, 1.25f);

                //it seems like does not take effect in pdf UI, but I add it anyways
                document.addTitle(title);
                document.addCreationDate();


                Font robotoBoldFont = getRobotoFont(context, 36.0f, Font.BOLD, BaseColor.BLACK);
                Paragraph titleParagraph = new Paragraph(title,robotoBoldFont);
                titleParagraph.setAlignment(Element.ALIGN_CENTER);
                titleParagraph.setSpacingAfter(20f);


                LocalDate ld = LocalDate.now();
                Font robotoBaseFont = getRobotoRegularFont(context,20.0f, Font.BOLD, BaseColor.BLACK);
                Paragraph signDate = new Paragraph(ld.getMonthValue() + "/" + ld.getDayOfMonth() + "/" + ld.getYear());
                signDate.setFont(new Font(robotoBaseFont));
                signDate.setAlignment(Element.ALIGN_CENTER);

                // Add signature image
                Bitmap signature = loadBitmapFromView(signature_view);
                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                signature.compress(Bitmap.CompressFormat.PNG, 100, imageStream);
                Image image = Image.getInstance(imageStream.toByteArray());
                image.scaleToFit(PageSize.A4);
                document.add(titleParagraph);
                document.add(consentFormParagraph);
                document.add(image);
                document.add(signDate);
                document.close();
            }

            // 5. Handle success
            byte[] bytes = baos.toByteArray();
            String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
            consentFormCallBack.successWithPDF(encoded);

            // Show where file was saved
            //Toast.makeText(context, "PDF saved to Documents/CDoc Consent Form", Toast.LENGTH_LONG).show();
            dismissDialog();

        } catch (Exception e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(context, "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Font getRobotoFont(Context context, float size, int style, BaseColor color) {
        try {
            // Copy the font from assets to a temp file
            InputStream inputStream = context.getAssets().open("fonts/Roboto-Bold.ttf");
            File tempFont = File.createTempFile("roboto_temp", ".ttf", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFont);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Load the font from the temp file
            BaseFont robotoBase = BaseFont.createFont(tempFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(robotoBase, size, style, color);

        } catch (Exception e) {
            e.printStackTrace();
            return new Font(Font.FontFamily.HELVETICA, size, style, color); // fallback
        }
    }

    private Font getRobotoRegularFont(Context context, float size, int style, BaseColor color) {
        try {
            // Copy the font from assets to a temp file
            InputStream inputStream = context.getAssets().open("fonts/Roboto-Regular.ttf");
            File tempFont = File.createTempFile("roboto_temp", ".ttf", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFont);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Load the font from the temp file
            BaseFont robotoBase = BaseFont.createFont(tempFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(robotoBase, size, style, color);

        } catch (Exception e) {
            e.printStackTrace();
            return new Font(Font.FontFamily.HELVETICA, size, style, color); // fallback
        }
    }

}
