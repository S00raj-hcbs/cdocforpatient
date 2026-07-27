package com.cybermed.cdoc_patient.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.cybermed.cdoc_patient.BR;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.Tablet_Mode.WelcomeActivityTablet;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTActivity_MainPage;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.CommonAsyncTaskActivity;

import com.cybermed.cdoc_patient.databinding.ActivityLoginBinding;
import com.cybermed.cdoc_patient.login.signup.SignUpActivity;
import com.cybermed.cdoc_patient.login.signup.ValidationUtils;
import com.cybermed.cdoc_patient.login.viewmodel.LoginVM;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.view.MedicalDisclaimerDialog;
import com.cybermed.cdoc_patient.view.MyAlertDialog;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import com.cybermed.cdoc_patient.webapi.model.response.ResOrgLogo;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static android.Manifest.permission.READ_CONTACTS;
import static android.os.Build.VERSION.SDK_INT;

public class LoginActivity extends CommonAsyncTaskActivity implements View.OnClickListener, FingerprintHelper.FingerprintHelperListener {
    ActivityLoginBinding binding;
    private String passWord;
    private String userId;
    private boolean isLogin = false;
    private boolean newOrgCode = false;

    private AsyncTask userLoginAsyncTask;
    private AsyncTask getUserInfoTask;
    private AsyncTask recoverUserPwdTask;

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String DIALOG_FRAGMENT_TAG = "fingerprintFragment";
    private static final int DIALOG_FRAGMENT_REGISTER = 0;
    private static final int DIALOG_FRAGMENT_LOGIN = 1;

    private UserLoginTask mAuthTask;

    private static final String PREFERENCES_FINGERPRINT_ENABLED = "fingerprintenabled";
    private static final String KEY_ALIAS = "sitepoint";
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String PREFERENCES_KEY_EMAIL = "email";
    private static final String PREFERENCES_KEY_PASS = "pass";
    private static final String PREFERENCES_KEY_IV = "iv";
    public static final String TABLETMOODE = "tabletmode";

    private KeyStore keyStore;
    private Cipher cipher;
    private FingerprintManager fingerprintManager;
    private FingerprintManager.CryptoObject cryptoObject;
    private SharedPreferences sharedPreferences;
    private FingerprintHelper fingerprintHelper;

    private boolean encrypting;
    private FingerprintAuthenticationDialogFragment fragment;
    private ProgressDialog loadingLogin;

    private SharedPreferences prefs;
    LoginVM loginVM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tabletMode = prefs.getBoolean(TABLETMOODE, false);
        loginVM = new ViewModelProvider(this).get(LoginVM.class);
        binding.setLifecycleOwner(this);
        binding.setVariable(BR.vm, loginVM);
        Log.e("tab",""+tabletMode);
        //Reset Tablet Mode settings after update app (If updating cause app to lose the manifest settings)
        if (tabletMode) {
            switchTabletMode();
            Intent mStartActivity = new Intent(LoginActivity.this, WelcomeActivityTablet.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(LoginActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
            System.exit(0);
        }


        initLang();
        checkDataConnectionAndVersion(1);
        initView();
        initEvent();
        initData();
        initFingerPrint();
        registerObserver();
        getOrgLogo();
        initVersionName();

        SharedPreferences prefs =
                getSharedPreferences("app_prefs", MODE_PRIVATE);

        boolean isDisclaimerShown =
                prefs.getBoolean("important_notice", false);

        if (!isDisclaimerShown) {
            String title="Important Notice";
            String description= "This application requires a compatible external wearable or medical device to collect and display health data.\n" +
                    "\n" +
                    "The application does not function independently without a supported device.";


            MedicalDisclaimerDialog.show(this,title,description,"Continue",false,()->{
                prefs.edit()
                        .putBoolean("important_notice", true)
                        .apply();
            },()->{

            });

        }

    }

    private void registerObserver() {
        loginVM.getApiResponse().observe(this, liveAction -> {
            switch (liveAction.getLiveActionEvent()) {
                case RECOVER_USER_PASSWORD:
                    break;

            }
        });
    }

    private void switchTabletMode() {
        PackageManager pm = this.getApplicationContext().getPackageManager();
        ComponentName compName = new ComponentName(this.getPackageName(), this.getPackageName() + ".login.WelcomeActivity");
        pm.setComponentEnabledSetting(compName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        ComponentName compName2 = new ComponentName(this.getPackageName(), this.getPackageName() + ".login.WelcomeActivityTablet");
        pm.setComponentEnabledSetting(compName2, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    private void initLang() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getResources().getConfiguration();

        if (!prefs.getBoolean("firstTime", false)) {
            //First time run
            String currentLanguage = config.locale.getLanguage();
            Locale locale = new Locale(currentLanguage);
            Locale.setDefault(locale);
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        } else {
            //not first time run
            String lang = prefs.getString("LANG", "");
            if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }
        }
    }

    private void initView() {
        binding.editUserPwd.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                /*if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()){
                        initiateLogin();
                    }else {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                            startActivityForResult(intent, PERMISSION_Storage_MIC);
                        } catch (Exception e) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivityForResult(intent, PERMISSION_Storage_MIC);
                        }
                    }
                }else {

                }*/
                if (TextUtils.isEmpty(binding.editUserId.getText().toString())) {
                    binding.inputMail.setError(getString(R.string.enter_email_id));
                }else {
                    initiateLogin();
                }


            }
            return false;
        });
        binding.txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsDialog();
            }
        });
        binding.passwordHideButton.setOnClickListener(view -> {
            binding.editUserPwd.setTypeface(Typeface.DEFAULT);
            //binding.editUserPwd.setTransformationMethod(new PasswordTransformationMethod());
            if (view.getTag() == "0") {
                view.setTag("1");
                binding.editUserPwd.setTransformationMethod(null);
                binding.editUserPwd.setSelection(binding.editUserPwd.length());
                loginVM.getPasswordView().setValue(true);
            } else {
                view.setTag("0");
                binding.editUserPwd.setTransformationMethod(new PasswordTransformationMethod());
                binding.editUserPwd.setSelection(binding.editUserPwd.length());
                loginVM.getPasswordView().setValue(false);
            }
        });
    }

    private void initEvent() {
        binding.tvForgetPwd.setOnClickListener(this);
        binding.tvSignUp.setOnClickListener(this);
        binding.txtLogin.setOnClickListener(this);
    }

    private void initData() {
        if (!TextUtils.isEmpty(getLoginInfo2().getOriginalAccount())) {
            userId = getLoginInfo2().getOriginalAccount();
            binding.editUserId.setText(userId);
            binding.editUserId.setSelection(userId.length());
        }
    }

    private void userLogin(String email, String passWord) {
        isLogin = true;
        ProceedLogin.verifyCredential(this, email, passWord, () -> {
            isLogin = false;
            userId = email;
            ProceedLogin.loginAsRep(this, userId, userId, passWord, () -> {
                boolean IOTMode = CDoctor2Application.getLoginInfo()
                        .getUserInfo().getMobile_mode().equalsIgnoreCase("remote_monitoring");
                if (!IOTMode) {
                    Intent intent = new Intent(LoginActivity.this, FragmentMainActivity.class);
                    startActivity(intent);
                    finish();
                    if (loadingLogin != null) {
                        loadingLogin.dismiss();
                    }
                } else { //IOT Mode
                    Intent intent = new Intent(LoginActivity.this, IOTActivity_MainPage.class);
                    startActivity(intent);
                    finish();
                    if (loadingLogin != null) {
                        loadingLogin.dismiss();
                    }
                }
            }, () -> {
            });
        }, () -> {
            isLogin = false;
            binding.loginError.setVisibility(View.VISIBLE);
            // toastShortInfo(getString(R.string.login_failed));
        });
    }

    public void initFingerPrint() {
        ImageView mFingerPrintButton = (ImageView) findViewById(R.id.fingerprint_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if we're running on Android 6.0 (M) or higher
        if (SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (!fingerprintManager.isHardwareDetected()) {
                    // Device doesn't support fingerprint authentication
                    mFingerPrintButton.setVisibility(View.GONE);
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    // User hasn't enrolled any fingerprints to authenticate with
                } else {
                    // Everything is ready for fingerprint authentication
                    final boolean fingerprintEnabled = sharedPreferences.getBoolean(PREFERENCES_FINGERPRINT_ENABLED, false);
                    if (fingerprintEnabled == true) {
                        attemptFingerprintLogin();
                    }

                    mFingerPrintButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (fingerprintEnabled == true) {
                                attemptFingerprintLogin();
                            } else {
                                if (checkLoginInfo()) {
                                    attemptRegister();
                                }
                            }
                        }
                    });
                }
            } else {
                mFingerPrintButton.setVisibility(View.GONE);
            }
        } else {
            mFingerPrintButton.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mAuthTask != null)
            mAuthTask.cancel(true);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        //getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            /*Snackbar.make(mEmailView, "Contacts permissions are needed for providing email completions.", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });*/
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_CAMERA_MIC) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (binding.txtLogin.isEnabled() && checkCamMicNotificationPermissions()) {
                        loginFunction();
                    }
                }else {
                    if (binding.txtLogin.isEnabled() && checkCamMicPermissions()) {
                        loginFunction();
                    }
                }

            } else {
                Toast.makeText(this, "Please enable the permissions to login", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegister() {
        if (!testFingerPrintSettings())
            return;

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        binding.editUserId.setError(null);
        binding.editUserPwd.setError(null);

        // Store values at the time of the login attempt.
        String email = binding.editUserId.getText().toString();
        String password = binding.editUserPwd.getText().toString();
        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute((Void) null);
    }

    @Override
    public void authenticationFailed(String error) {
        Log.e("FingerPrint", error);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void authenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        cipher = result.getCryptoObject().getCipher();

        if (encrypting) {
            String textToEncrypt = binding.editUserPwd.getText().toString();
            encryptString(textToEncrypt);
            Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show();
        } else {
            String encryptedText = sharedPreferences.getString(PREFERENCES_KEY_PASS, "");
            decryptString(encryptedText);
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
        }
    }

    public void encryptString(String password) {
        try {
            byte[] bytes = cipher.doFinal(password.getBytes());
            String encryptedText = Base64.encodeToString(bytes, Base64.NO_WRAP);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREFERENCES_KEY_PASS, encryptedText);
            editor.putBoolean(PREFERENCES_FINGERPRINT_ENABLED, true);
            editor.commit();

            userLogin(binding.editUserId.getText().toString(), password);

        } catch (Exception e) {
            Log.e("FingerPrint", e.getMessage());
        }
    }

    public void decryptString(String cipherText) {
        try {
            byte[] bytes = Base64.decode(cipherText, Base64.NO_WRAP);
            String userEmail = sharedPreferences.getString(PREFERENCES_KEY_EMAIL, "");
            String decryptedPass = new String(cipher.doFinal(bytes));
            userLogin(userEmail, decryptedPass);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    private void attemptFingerprintLogin() {
        if (!testFingerPrintSettings())
            return;

        if (!usersRegistered())
            return;

        //showProgress(true);
        mAuthTask = new UserLoginTask();
        mAuthTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Boolean mRegister; // if false, authenticate instead

        @TargetApi(Build.VERSION_CODES.M)
        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            mRegister = true;

            Bundle bundle = new Bundle();
            bundle.putString("fingerprintTitle", "Add New Fingerprint Unlock");
            bundle.putInt("fingerprintType", DIALOG_FRAGMENT_REGISTER);
            fragment = new FingerprintAuthenticationDialogFragment();
            fragment.setArguments(bundle);
            boolean useFingerprintPreference = sharedPreferences.getBoolean(getString(R.string.login_use_fingerprint_to_authenticate_key), true);
            if (useFingerprintPreference) {
                fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
            } else {
                fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
            }
            fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);

        }

        @TargetApi(Build.VERSION_CODES.M)
        UserLoginTask() {
            mRegister = false;
            mEmail = null;
            mPassword = null;

            Bundle bundle = new Bundle();
            bundle.putString("fingerprintTitle", "Sign In");
            bundle.putInt("fingerprintType", DIALOG_FRAGMENT_LOGIN);

            fragment
                    = new FingerprintAuthenticationDialogFragment();
            fragment.setArguments(bundle);
            boolean useFingerprintPreference = sharedPreferences.getBoolean(getString(R.string.login_use_fingerprint_to_authenticate_key), true);
            if (useFingerprintPreference) {
                fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
            } else {
                fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
            }
            fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!getKeyStore())
                return false;

            if (!createNewKey(false))
                return false;

            // Inside doInBackground
            if (!getCipher())
                return false;

            // Inside doInBackground
            if (mRegister) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREFERENCES_KEY_EMAIL, mEmail);
                editor.commit();

                encrypting = true;

                if (!initCipher(Cipher.ENCRYPT_MODE))
                    return false;
            } else {
                encrypting = false;
                if (!initCipher(Cipher.DECRYPT_MODE))
                    return false;
            }

            if (!initCryptObject())
                return !initCryptObject();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            onCancelled();

            if (!success) {
                if (mRegister)
                    Log.e("FingerPrintDebug", "GG");
                else
                    Log.e("FingerPrintDebug", "YY");
            } else {
                fragment.startAuth(LoginActivity.this.fingerprintManager, cryptoObject);
                //fingerprintHelper.startAuth(LoginActivity.this.fingerprintManager, cryptoObject);
                Log.e("FingerPrint", "Authenticate using fingerprint!");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }

    @SuppressLint("NewApi")
    private boolean testFingerPrintSettings() {
        Log.e("FingerPrint", "Testing Fingerprint Settings");

        if (SDK_INT < Build.VERSION_CODES.M) {
            Log.e("FingerPrint", "This Android version does not support fingerprint authentication.");
            return false;
        }

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!keyguardManager.isKeyguardSecure()) {
            Log.e("FingerPrint", "User hasn't enabled Lock Screen");
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("FingerPrint", "User hasn't granted permission to use Fingerprint");
            return false;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            Log.e("FingerPrint", "User hasn't registered any fingerprints");
            return false;
        }

        Log.e("FingerPrint", "Fingerprint authentication is set.\n");

        return true;
    }

    private boolean usersRegistered() {
        if (sharedPreferences.getString(PREFERENCES_KEY_EMAIL, null) == null) {
            Log.e("FingerPrint", "No user is registered");
            return false;
        }

        return true;
    }

    private boolean getKeyStore() {
        Log.e("FingerPrint", "Getting keystore...");
        try {
            keyStore = KeyStore.getInstance(KEYSTORE);
            keyStore.load(null); // Create empty keystore
            return true;
        } catch (KeyStoreException e) {
            Log.e("FingerPrint", e.getMessage());
        } catch (CertificateException e) {
            Log.e("FingerPrint", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e("FingerPrint", e.getMessage());
        } catch (IOException e) {
            Log.e("FingerPrint", e.getMessage());
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean createNewKey(boolean forceCreate) {
        Log.e("FingerPrint", "Creating new key...");
        try {
            if (forceCreate)
                keyStore.deleteEntry(KEY_ALIAS);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);

                generator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setUserAuthenticationRequired(true)
                        .build()
                );

                generator.generateKey();
                Log.e("FingerPrint", "Key created.");
            } else
                Log.e("FingerPrint", "Key exists.");

            return true;
        } catch (Exception e) {
            Log.e("FingerPrint", e.getMessage());
        }

        return false;
    }

    private boolean getCipher() {
        Log.e("FingerPrint", "Getting cipher...");
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            return true;
        } catch (NoSuchAlgorithmException e) {
            Log.e("FingerPrint", e.getMessage());
        } catch (NoSuchPaddingException e) {
            Log.e("FingerPrint", e.getMessage());
        }

        return false;
    }

    private boolean initCipher(int mode) {
        Log.d("FingerPrintDebug", "Initializing cipher...");
        try {
            keyStore.load(null);
            SecretKey keyspec = (SecretKey) keyStore.getKey(KEY_ALIAS, null);

            if (mode == Cipher.ENCRYPT_MODE) {
                cipher.init(mode, keyspec);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREFERENCES_KEY_IV, Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP));
                editor.commit();
            } else {
                byte[] iv = Base64.decode(sharedPreferences.getString(PREFERENCES_KEY_IV, ""), Base64.NO_WRAP);
                IvParameterSpec ivspec = new IvParameterSpec(iv);
                cipher.init(mode, keyspec, ivspec);
            }

            return true;
        } catch (Exception e) {
            Log.e("FingerPrint", e.getMessage());
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean initCryptObject() {
        Log.e("FingerPrint", "Initializing crypt object...");
        try {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            return true;
        } catch (Exception ex) {
            Log.e("FingerPrint", ex.getMessage());
        }
        return false;
    }

//    private void getUserInfo(String userId, String pwd) {
//        OnPostExecute ope = result -> {
//            Patient_Demographic patientInfo = new Patient_Demographic((SoapObject) result);
//
//            UserInfo userInfo = new UserInfo();
//            userInfo.deserialize(patientInfo);
//            getCDocApplication().processUserLogin2(userId, pwd, userInfo);
//
//            OnPostExecute statusOpe = statusResult -> {
//                boolean IOTMode = CDoctor2Application.getLoginInfo()
//                        .getUserInfo().getMobile_mode().equalsIgnoreCase("remote_monitoring");
//
//                if (statusResult.toString().equals("1")) {
//                    if (!IOTMode) {
//                        Intent intent = new Intent(LoginActivity.this, FragmentMainActivity.class);
//                        startActivity(intent);
//                        finish();
//                        if (loadingLogin != null) {
//                            loadingLogin.dismiss();
//                        }
//                    } else { //IOT Mode
//                        Intent intent = new Intent(LoginActivity.this, IOTActivity_MainPage.class);
//                        startActivity(intent);
//                        finish();
//                        if (loadingLogin != null) {
//                            loadingLogin.dismiss();
//                        }
//                    }
//                }
//            };
//
//            WS.setPatientDeviceStatus(STATUS_ON_LINE, statusOpe);
//        };
//
//        WebService.webServiceAsyncTask(get_PatientDemographic_Android, ope, userId);
//
//    }

    private void recoverPassWord() {
        loginVM.recoverUserPassword(userId);
//        if (recoverUserPwdTask == null) {
//            recoverUserPwdTask = recoverUserPassWordAsyncTask();
//        }
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_language, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);

        dialogBuilder.setTitle(getResources().getString(R.string.about_change_language));
        dialogBuilder.setMessage(getResources().getString(R.string.about_select_lang));
        dialogBuilder.setPositiveButton(getString(R.string.btn_change), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                switch (langpos) {
                    case 0: //English
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        setLangRecreate("en");
                        return;
                    case 1: //Chinese
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "zh").commit();
                        setLangRecreate("zh");
                        return;
                    case 2: //Chinese
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "es").commit();
                        setLangRecreate("es");
                        return;
                    default: //By default set to english
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        setLangRecreate("en");
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.about_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }


    /*private AsyncTask recoverUserPassWordAsyncTask() {
        return new AsyncTask<Void, Void, Integer>() {

            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().RecoverUserPassword(userId);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                recoverUserPwdTask = null;
                if (e == null) {
                    if (integer == 1) {

                    } else {

                    }
                }
            }
        }.execute();
    }
     */

    private void showResetPwdDialog() {
        MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setDialogTitle(getString(R.string.login_confirm_title));
        dialog.setDialogContent(getString(R.string.login_confirm_message) + " " + userId);
        dialog.setLeftClickListener(getString(R.string.btn_cancel), new MyAlertDialog.LeftClickListener() {
            @Override
            public void onLeftClick(View view) {

            }
        });
        dialog.setRightClickListener(getString(R.string.btn_confirm), new MyAlertDialog.RightClickListener() {
            @Override
            public void onRightClick(View view) {
                recoverPassWord();
                toastShortInfo(getString(R.string.login_password_reset));
            }
        });
    }

    private void showResetPwdDialog2() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_forget_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btn_ok = dialog.findViewById(R.id.btn_ok);

        LinearLayout lin_send_email = dialog.findViewById(R.id.lin_send_email);
        TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        lin_send_email.setVisibility(View.VISIBLE);
        EditText ed_email = dialog.findViewById(R.id.edt_email);


        btn_ok.setOnClickListener(view1 -> {
            binding.editUserId.setText("");
            userId=ed_email.getText().toString();
            if (ValidationUtils.isEmailAddress(userId)) {
                dialog.dismiss();
                showResetPwdDialog();
            } else {
               // binding.inputMail.setError(getString(R.string.regist_error_email));
                Toast.makeText(LoginActivity.this,getString(R.string.regist_error_email),Toast.LENGTH_SHORT).show();
            }


        });
        btn_cancel.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forget_pwd:
                userId = binding.editUserId.getText().toString().trim();
                if (TextUtils.isEmpty(userId)) {
                    binding.inputMail.setError(getString(R.string.enter_email_id));
                    return;
                }
                if (ValidationUtils.isEmailAddress(userId)) {
                   // binding.inputMail.setError(null);
                    showResetPwdDialog();
                } else {
                    showResetPwdDialog2();
                  //  binding.inputMail.setError(getString(R.string.regist_error_email));
                }

                break;
            case R.id.tv_sign_up:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivityForResult(intent, 1111);
                break;
            case R.id.txt_login:
                binding.loginError.setVisibility(View.GONE);
                    initiateLogin();
                break;
        }

    }


    private boolean checkCamMicPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkCamMicNotificationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("NewApi")
    private void initiateLogin() {

        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkCamMicNotificationPermissions()) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
/*
                                Manifest.permission.READ_MEDIA_IMAGES,
*/
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_CAMERA_MIC);
                return;
            } else {
                loginFunction();
            }
        }else {
            if (!checkCamMicPermissions()) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION/*,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE*/},
                        PERMISSION_CAMERA_MIC);
                return;

            } else {
                loginFunction();
            }
        }

    }

    //
    private void loginFunction() {
        checkDataConnectionAndVersion(1);
        if (isLogin) {
            return;
        }
        if (checkLoginInfo()) {
            //Remove Previously Saved Default Location
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("filtered_state", "");
            editor.commit();
            userLogin(userId, passWord);
        }
    }

    public boolean checkLoginInfo() {
        userId = binding.editUserId.getText().toString().trim();
        passWord = binding.editUserPwd.getText().toString().trim();
      /*  if (!ValidationUtils.isEmailAddress(userId)) {
            binding.inputMail.setError(getString(R.string.regist_error_email));
            return false;
        }*/
        binding.inputMail.setError(null);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111 && resultCode == RESULT_OK) {
            String str = data.getStringExtra("email");
            binding.editUserId.setText(str);
            binding.editUserId.setSelection(str.length());

            newOrgCode = data.getBooleanExtra("newOrgCode", false);
        }
        if (requestCode == PERMISSION_Storage_MIC) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    initiateLogin();
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingLogin != null) {
            loadingLogin.dismiss();
        }
        if (userLoginAsyncTask != null) {
            userLoginAsyncTask.cancel(true);
            userLoginAsyncTask = null;
        }
        if (getUserInfoTask != null) {
            getUserInfoTask.cancel(true);
            getUserInfoTask = null;
        }
        if (recoverUserPwdTask != null) {
            recoverUserPwdTask.cancel(true);
            recoverUserPwdTask = null;
        }
    }

    void getOrgLogo() {
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                ResOrgLogo resOrgLogo = (ResOrgLogo) data;
                String base64 = resOrgLogo.getBase64File();
                String name = resOrgLogo.getOrgFullname();
                if (!TextUtils.isEmpty(base64)) {
                    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        ImageView logoImg = findViewById(R.id.img_logo);
                        logoImg.setImageBitmap(decodedByte);
                        //binding.imgLogo.setVisibility(View.VISIBLE);
                        binding.imgCdoc.setVisibility(View.VISIBLE);
                        binding.layoutLogo.setVisibility(View.GONE);
                        binding.linearDoc.setVisibility(View.VISIBLE);
                        binding.txtHealthcare.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(name)) {
                            binding.txtHealthcare.setText(name);
                        } else
                            binding.txtHealthcare.setText(getString(R.string.healthcare_at_ur_fingertip));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {

            }
        }, this);
        if (!TextUtils.isEmpty(CDoctor2Application.getLoginInfo().getUserInfo().getService_code())) {
            apiManager.getOrgLogo(CDoctor2Application.getLoginInfo().getUserInfo().getService_code());
        }
    }

    private void initVersionName() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            binding.txtVersion.setText(getString(R.string.version2_0) + " " + versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    void showTermsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_terms);
        Button btn_ok = dialog.findViewById(R.id.tv_left);
        btn_ok.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

}
