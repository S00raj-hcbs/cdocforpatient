package com.cybermed.cdoc_patient.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseActivity;

/**
 * Created by Ldj on 2016/5/4.
 */
public class RegistNoteDialog extends Dialog {
    private Context mContext;

    private TextView mTxtTitle, mTxtContent;
    private Button mBtnOk;
    private Button mBtnResend;

    private View.OnClickListener mOnClickListener;

    private boolean isResendEmail;

    private BaseActivity mActivity;

    private OnResendEmailListener mResendEmailListener;

    public RegistNoteDialog(Context context, View.OnClickListener listener, boolean isResendEmail) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mActivity = (BaseActivity) mContext;
        mOnClickListener = listener;
        this.isResendEmail = isResendEmail;
    }

    public interface OnResendEmailListener {
        void resendEmail();
    }

    public void setOnResendEmailListener(OnResendEmailListener resendEmailListener) {
        mResendEmailListener = resendEmailListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_register_send_email);
//        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_register_send_email,null);
        mTxtTitle = (TextView) findViewById(R.id.title);
        mTxtContent = (TextView) findViewById(R.id.content);
        mBtnOk = (Button) findViewById(R.id.btnOk);

        mBtnResend = (Button) findViewById(R.id.btn_dialog_register_note_resend_email);
        if (isResendEmail) {

            mBtnResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mResendEmailListener != null) {
                        mResendEmailListener.resendEmail();
                    }
                }
            });
        } else {
            mBtnResend.setVisibility(View.GONE);
        }

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        if (mOnClickListener != null)
            mBtnOk.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setLayout((int) (dm.widthPixels * 0.8), getWindow().getAttributes().height);
    }

    public void setText(String title, String content, String btn) {
        mTxtTitle.setText(title);
        mTxtContent.setText(content);
        mBtnOk.setText(btn);
    }


}
