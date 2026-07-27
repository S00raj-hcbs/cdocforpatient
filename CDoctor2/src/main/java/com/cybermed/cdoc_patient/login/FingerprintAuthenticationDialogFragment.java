/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.cybermed.cdoc_patient.login;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybermed.cdoc_patient.R;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
public class FingerprintAuthenticationDialogFragment extends DialogFragment implements FingerprintHelper.Callback {

    private static final int DIALOG_FRAGMENT_REGISTER = 0;
    private static final int DIALOG_FRAGMENT_LOGIN = 1;
    private Button mCancelButton;
    private Button mRegisterButton;
    private View mFingerprintContent;

    private Stage mStage = Stage.FINGERPRINT;

    private LoginActivity mActivity;
    private FingerprintHelper mFingerprintHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String title = bundle.getString("fingerprintTitle");
        int type = bundle.getInt("fingerprintType",DIALOG_FRAGMENT_REGISTER);
        getDialog().setTitle(title);


        View v = inflater.inflate(R.layout.dialog_fingerprint_container, container, false);

        mFingerprintContent = v.findViewById(R.id.fingerprint_container);
        mFingerprintHelper = new FingerprintHelper(mActivity,
                (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status), this);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFingerprintHelper.cancel();
                dismiss();
            }
        });
        mRegisterButton = (Button) v.findViewById(R.id.register_button);
        if(type == DIALOG_FRAGMENT_REGISTER) {
            mRegisterButton.setVisibility(View.GONE);
        } else {
            mRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (mActivity.checkLoginInfo()) {
                        mActivity.attemptRegister();
                    }
                }
            });
        }
        updateStage();

        return v;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject){
        mFingerprintHelper.startAuth(manager, cryptoObject);
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (LoginActivity) getActivity();
    }



    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                mCancelButton.setText(R.string.cancel);
                mRegisterButton.setText("Register");
                mFingerprintContent.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onAuthenticated() {
        dismiss();
    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
    }

    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    public enum Stage {
        FINGERPRINT,
        PASSWORD
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("FingerPrintDebug","DialogPaused");
        mFingerprintHelper.cancel();
    }
}
