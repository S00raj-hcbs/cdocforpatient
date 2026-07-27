package com.cybermed.cdoc_patient.login;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;

/**
 * Created by dtaka on 8/20/2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHelper extends FingerprintManager.AuthenticationCallback {
    private FingerprintHelperListener listener;
    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 500;

    private final ImageView mIcon;
    private final TextView mErrorTextView;
    private final Callback mCallback;

    private boolean mSelfCancelled;

    public FingerprintHelper(FingerprintHelperListener listener, ImageView icon,
                             TextView errorTextView, Callback callback) {
        this.listener = listener;
        mIcon = icon;
        mErrorTextView = errorTextView;
        mCallback = callback;
    }

    private CancellationSignal cancellationSignal;

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        mIcon.setImageResource(R.drawable.ic_fp_40px);

        try {
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        } catch (SecurityException ex) {
            listener.authenticationFailed("An error occurred:\n" + ex.getMessage());
        } catch (Exception ex) {
            listener.authenticationFailed("An error occurred\n" + ex.getMessage());
        }
    }

    public void cancel() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
            mSelfCancelled = true;
        }
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
    }

    public interface FingerprintHelperListener {
        public void authenticationFailed(String error);
        public void authenticationSucceeded(FingerprintManager.AuthenticationResult result);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            showError(errString);
            mIcon.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError();
                }
            }, ERROR_TIMEOUT_MILLIS);
        }
        listener.authenticationFailed("Authentication error\n" + errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError(helpString);
        listener.authenticationFailed("Authentication help\n" + helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        showError(mIcon.getResources().getString(
                R.string.login_fingerprint_not_recognized_text));
        listener.authenticationFailed("Authentication failed.");
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.success_color, null));
        mErrorTextView.setText(
                mErrorTextView.getResources().getString(R.string.login_fingerprint_success_text));
        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.onAuthenticated();
            }
        }, SUCCESS_DELAY_MILLIS);
        listener.authenticationSucceeded(result);
    }


    public interface Callback {

        void onAuthenticated();

        void onError();
    }


    private void showError(CharSequence error) {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        mErrorTextView.setText(error);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.warning_color, null));
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            mErrorTextView.setTextColor(
                    mErrorTextView.getResources().getColor(R.color.hint_color, null));
            mErrorTextView.setText(
                    mErrorTextView.getResources().getString(R.string.login_fingerprint_hint_text));
            mIcon.setImageResource(R.drawable.ic_fp_40px);
        }
    };
}