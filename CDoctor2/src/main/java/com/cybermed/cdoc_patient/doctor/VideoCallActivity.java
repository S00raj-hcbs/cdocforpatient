package com.cybermed.cdoc_patient.doctor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.Appointment;
import com.cdfortis.datainterface.soap.model.DocInfo;
import com.cdfortis.datainterface.soap.model.FamilyInfo;
import com.cdfortis.datainterface.soap.model.VectorFamily;
import com.cybermed.cdoc_patient.BuildConfig;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.CommonAsyncTaskActivity;
import com.cybermed.cdoc_patient.common.videoui.RtlLinearLayoutManager;
import com.cybermed.cdoc_patient.common.videoui.SmallVideoViewAdapter;
import com.cybermed.cdoc_patient.common.videoui.SmallVideoViewDecoration;
import com.cybermed.cdoc_patient.common.videoui.UserStatusData;
import com.cybermed.cdoc_patient.common.videoui.VideoViewEventListener;
import com.cybermed.cdoc_patient.databinding.ActivityVideoCallBinding;
import com.cybermed.cdoc_patient.family.PatientInfoDialog;
import com.cybermed.cdoc_patient.login.signup.ValidationUtils;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.util.LocationUtil;
import com.cybermed.cdoc_patient.util.PermissionUtil;
import com.cybermed.cdoc_patient.view.MyAlertDialog;
import com.cybermed.cdoc_patient.webapi.APIs.PaymentApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.RestApiCall;
import com.cybermed.cdoc_patient.webapi.model.request.ApptPayment;
import com.cybermed.cdoc_patient.webapi.model.response.ErrorResponse;
import com.cybermed.cdoc_patient.ws.WS;
import com.google.gson.Gson;
import com.mrudultora.colorpicker.ColorPickerPopUp;
import com.onesignal.OneSignal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtc2.video.VirtualBackgroundSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cdfortis.datainterface.soap.WebServiceID.leaving_Room_as_Guest_v2;
import static com.cybermed.cdoc_patient.camera.ImageUtils.checkStoragePermission;
import static com.cybermed.cdoc_patient.camera.ImageUtils.imageSelectionPopUp;
import static com.cybermed.cdoc_patient.common.videoui.ConstantApp.KEY_ENABLE;
import static com.cybermed.cdoc_patient.main.FragmentMainActivity.MY_CAMERA_AUDIO_REQUEST_CODE;
import static io.agora.rtc2.Constants.CONNECTION_STATE_CONNECTED;
import static io.agora.rtc2.Constants.CONNECTION_STATE_CONNECTING;
import static io.agora.rtc2.Constants.CONNECTION_STATE_DISCONNECTED;
import static io.agora.rtc2.Constants.CONNECTION_STATE_FAILED;
import static io.agora.rtc2.Constants.CONNECTION_STATE_RECONNECTING;
import static io.agora.rtc2.Constants.QUALITY_BAD;
import static io.agora.rtc2.Constants.QUALITY_EXCELLENT;
import static io.agora.rtc2.Constants.QUALITY_POOR;
import static io.agora.rtc2.Constants.QUALITY_VBAD;
import static io.agora.rtc2.Constants.USER_OFFLINE_DROPPED;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15;
import static io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
import static io.agora.rtc2.video.VideoEncoderConfiguration.STANDARD_BITRATE;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_640x360;

public class VideoCallActivity extends CommonAsyncTaskActivity implements View.OnClickListener {


    private static VideoCallActivity videoCallActivity;

    boolean isVirtualBackGroundEnabled = false;
    String agoraToken;

    private RtcEngine mRtcEngine;
    private SoundPool soundPool;

    Bitmap bitmap;
    File file;
    private int music;
    private int switchCam = 0;
    private static final int END_INVITATION = 30000;

    private AsyncTask mGenerateOnlineRoomNumberTask;
    private AsyncTask mCreateCallLogRoomsTask;
    private AsyncTask mRegisterAsRoomGuestTask;
    private AsyncTask mMakeCall2ProviderTask;
    private AsyncTask mMakeCall2PatientTask;
    private AsyncTask mCreateAppointmentTask;
    private AsyncTask markAppointmentStatusTask;
    private AsyncTask mGetOnlineRoomNumPatient;
    private AsyncTask mNotifyPatientAppStatusTask;
    private AsyncTask mSetRoomChargeCCTask;
    private AsyncTask mCheckRevStatusTask;
    private AsyncTask mGetActiveGuestCountTask, mGetActiveGuestCountTaskRemove;
    private AsyncTask mMarkApptPaymentMethodTask;
    private AsyncTask NotifyProviderTask;
    private AsyncTask NotifyPatientTask;
    private AsyncTask getProviderOnesignalTask;
    private AsyncTask setPatientOnesignalIndicatorTask;
    private AsyncTask GetOnlineRoomNumberTask;
    private AsyncTask getOnlineProviderNameTask;
    private AsyncTask getOnlineProviderNameV2Task;
    private AsyncTask getPatientListTask;
    private AsyncTask mGetPatientOnlineStatusTask;
    private AsyncTask getPatientOnesignalTask;


    private static final String CONSULT = "SdkConsult";
    private String orgCode = "";
    private String providerId;
    private String recipientUserId;
    private String userId;
    private String roomNumber;
    private String docName;
    private String roomGuestId;
    private String appointmentIdentifier, appointmentAccount, apptStatus;
    private String cc_idx;
    private String cvv_code;
    private String card_id;
    private String weight;
    private String height;
    private String BPH;
    private String BPL;
    private String pulse;
    private String temperature;
    private String chiefComplaint;
    private String allergies;
    private String medHx;
    private String socialHx;
    private String phone_num;
    private String userInputBusyProvider;
    private String apptTime;
    private String callerType = "";

    private TimerTask timerTask;
    private Timer timer;
    private Timer connectTimer;
    private static Timer incomingHangupTimer;
    private static Timer hasCallerLeftTimer;
    private static Timer hasRecipientLeftTimer;
    private static Timer CheckRevStatusTimer;
    private static Timer getProviderOnesignalTimer;
    private static Timer getPatientOnesignalTimer;
    private static Timer getActiveGuestCount;
    private static Timer mRemoveAddUserStatusTimer;
    private Handler handler;

    private static final int WAITING_TIME = 30000;
    private static final int IN_COMING = 0;
    private static final int OUT_CALLING = 1;
    private static final int DROPPED_CALL = 2;
    private static final int PAT_TO_PAT = 3;
    private int call_type;
    private int paymentType;

    private boolean isSkipped;
    private boolean frontCam = true;
    private boolean isRemoteJoin = false;
    private boolean isOutGoingCall = false;
    private boolean isInComingCallAnswered = false;
    private boolean providerIsBusy = false;
    private boolean hasEnteredInCall = false;
    private boolean hasCallEnded = false;
    private boolean btnIncomingAnswer = false;
    private boolean btnIncomingHangup = false;
    private boolean btnFinishClicked = false;
    private boolean hasEndConnectionClicked = false;
    private boolean isConnecting = false;
    private boolean isSwitchingCamera = false;
    private boolean callFromMyAppt = false;
    private String cameraOffStatus = "true";
    private String mutedStatus = "true";


    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid
    public int mLayoutType = LAYOUT_TYPE_DEFAULT;
    public static final int LAYOUT_TYPE_DEFAULT = 0;
    public static final int LAYOUT_TYPE_SMALL = 1;
    private RelativeLayout mSmallVideoViewDock;


    private FamilyMemberAdapter searchPatientAdapter;
    private RecyclerView patientList;


    private int localUid;
    private AudioManager mAudioManager;

    private Dialog mRatingDialog;
    private MyAlertDialog mProviderLeftDialog;
    private AlertDialog.Builder mProviderBusyDialog;
    private AlertDialog mTabletProviderBusyDialog;

    private SwipeRefreshLayout refreshLayout;

    private EditText mFamilyEmailInput;
    //private AwesomeSpinner mFamilyRelationshipSpinner;
    private TextView mErrorRelationship, mErrorEmail;
    private AsyncTask mGetFamilyList, mUpdateFamilyMemberTask;

    ActivityVideoCallBinding binding;
    String rating = "";

    private BroadcastReceiver headSetBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    if (mRtcEngine != null)
                        mRtcEngine.setEnableSpeakerphone(true);
                } else if (intent.getIntExtra("state", 0) == 1) {
                    if (mRtcEngine != null)
                        mRtcEngine.setEnableSpeakerphone(true);
                }
            }
        }
    };

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
            Log.e("state",""+state);
            if (state == Constants.REMOTE_AUDIO_STATE_STOPPED) {
                mutedStatus="false";
            } else if (state == Constants.REMOTE_AUDIO_STATE_DECODING) {
                mutedStatus="true";
            }

            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    setupRemoteVideo(uid);
                    removeAddUserStatus(0);

                    if (isRemoteJoin)
                        return;

                    Log.d("VIDEO2DEBUG2", String.valueOf(uid));
                    isRemoteJoin = true;
                    mRtcEngine.enableInEarMonitoring(true);
                    //If getProviderOnesignal was not successful and this method was called
                    binding.connectingPage.setVisibility(View.GONE);
                    if (getProviderOnesignalTimer != null)
                        getProviderOnesignalTimer.cancel();
                    cancelConnectTimer();
                    /////

                    if (getTabletMode()) {
                        binding.txtName.setText(getFacilityName());
                        binding.textName.setText(getFacilityName()+"         ");
                    } else {
                        binding.txtName.setText(docName);
                        binding.textName.setText(docName+"         ");
                    }

                    binding.loaderAddProvider.setVisibility(View.GONE);
                    callingView(false);
                    binding.backBtnCallConnected.setVisibility(View.GONE);
                    binding.viewCallConnected.setVisibility(View.VISIBLE);
                    startCallTimer();
                    cancelCheckRevStatusTimer();
                    cancelTimerTask();
                    stopMusic();

                    //Reset push notification indicator to false
                    SetPatientOnesignalIndicator(false);
                    getActiveGuestCount();
                    mRtcEngine.adjustRecordingSignalVolume(400);
                    mRtcEngine.adjustPlaybackSignalVolume(400);
                    mRtcEngine.adjustAudioMixingVolume(100);

                    mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
                    mRtcEngine.setEnableSpeakerphone(true);
                }
            });*/
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    setupRemoteVideo(uid);
                    removeAddUserStatus(0);

                    if (isRemoteJoin)
                        return;

                    Log.d("VIDEO2DEBUG2", String.valueOf(uid));
                    isRemoteJoin = true;
                    mRtcEngine.enableInEarMonitoring(true);
                    //If getProviderOnesignal was not successful and this method was called
                    binding.connectingPage.setVisibility(View.GONE);
                    if (getProviderOnesignalTimer != null)
                        getProviderOnesignalTimer.cancel();
                    cancelConnectTimer();
                    /////

                    if (getTabletMode()) {
                        binding.txtName.setText(getFacilityName());
                        binding.textName.setText(getFacilityName()+"         ");
                    } else {
                        binding.txtName.setText(docName);
                        binding.textName.setText(docName+"         ");
                    }

                    binding.loaderAddProvider.setVisibility(View.GONE);
                    callingView(false);
                    binding.backBtnCallConnected.setVisibility(View.GONE);
                    binding.viewCallConnected.setVisibility(View.VISIBLE);
                    startCallTimer();
                    cancelCheckRevStatusTimer();
                    cancelTimerTask();
                    stopMusic();

                    //Reset push notification indicator to false
                    SetPatientOnesignalIndicator(false);
                    getActiveGuestCount();
                    mRtcEngine.adjustRecordingSignalVolume(100);
                    mRtcEngine.adjustPlaybackSignalVolume(100);
                    mRtcEngine.adjustAudioMixingVolume(100);

                    mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
                    mRtcEngine.setEnableSpeakerphone(true);
                }
            });
        }

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
//            Log.d("localvideodebug","onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
            Log.d("videodebug", "onJoinChannelSuccess " + channel + " " + uid + " " + elapsed);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }

                    SurfaceView local = mUidsList.remove(0);

                    if (local == null) {
                        return;
                    }

                    mUidsList.put(uid, local);
                    localUid = uid;
                }
            });
        }

        @Override
        public void onRemoteVideoStats(RemoteVideoStats stats) {
            super.onRemoteVideoStats(stats);
            int videoWidth = stats.width;
            int videoHeight = stats.height;

           /* if (videoWidth > 0 && videoHeight > 0) {

            } else {
                showErrorMessage("Other party camera is off");
            }*/
        }
        @Override
        public void onAudioRouteChanged(int routing) {
            super.onAudioRouteChanged(routing);

            // Handle audio route change
            if (routing == Constants.AUDIO_ROUTE_HEADSET) {
                Log.e("handset","handset");
                // Audio is now routed to a headset
            } else if (routing == Constants.AUDIO_ROUTE_EARPIECE) {
                // Audio is now routed to the earpiece
                Log.e("earpiece","earpiece");
            } else if (routing == Constants.AUDIO_ROUTE_SPEAKERPHONE) {
                // Audio is now routed to the speakerphone
                Log.e("speaker","speaker");
            }
        }

        /*@Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
            Log.e("statius",""+state);
            Log.e("reasonius",""+reason);
            Log.e("uid",""+reason);
            if (state == Constants.REMOTE_VIDEO_STATE_STOPPED) {
                cameraOffStatus="false";
               *//* binding.txtProviderStatusTxt.setText("Other party camera is off");
                binding.txtProviderStatusTxt.setVisibility(View.VISIBLE);
                showstatusAlert();*//*
            } else if (state == Constants.REMOTE_VIDEO_STATE_DECODING) {
                cameraOffStatus="true";
               // showstatusAlert();
            }
            if (reason == Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED){
                cameraOffStatus="false";
            }else if (reason == Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED){
                cameraOffStatus="true";
            }

        }*/

        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (reason == USER_OFFLINE_DROPPED) {
                        binding.loaderAddProvider.setVisibility(View.VISIBLE);
                        binding.txtAddProviderStatus.setText(getString(R.string.consultation_connection_error));
                        removeAddUserStatus(20000);
                    }
                    getGuestCount(uid);
                }
            });
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {

//            showErrorMessage("Connection state changed"
//                    + "\n New state: " + state
//                    + "\n Reason: " + reason);
            //  Log.e("Connection state changed",""+"Connection state changed" + "\n New state: " + state + "\n Reason: " + reason);

            switch (state) {
                case CONNECTION_STATE_DISCONNECTED:
                    //showErrorMessage("Internet connectivity is Poor. Please check your connection");
                    break;

                case CONNECTION_STATE_CONNECTING:

                    break;
                case CONNECTION_STATE_CONNECTED:
                    break;
                case CONNECTION_STATE_RECONNECTING:
                    showErrorMessage(getString(R.string.reconnecting));
                    break;
                case CONNECTION_STATE_FAILED:
                    showErrorMessage(getString(R.string.your_connection_is_failed));
                    break;
            }
            /*if (reason==CONNECTION_CHANGED_INTERRUPTED){
                showErrorMessage("Your connection is Poor. Please check your connection");
            }else if (reason==CONNECTION_CHANGED_BANNED_BY_SERVER)
                showErrorMessage("Your connection is unstable. Please check your connection");*/
        }
        // Implement the onAudioQuality callback
        public void onAudioQuality(int uid, int quality, short delay, short lost) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onAudioQuality",""+quality);

                    binding.txtName.setText(docName);
                    binding.textName.setText(docName+"         ");
                    if (quality==0){

                    }
                    if(videoCallActivity!=null){
                       if(quality==3){
                           showErrorMessage(getString(R.string.the_other_party_s_connection_is_poor));
                       }else if (quality>3){
                          showErrorMessage(getString(R.string.the_other_party_s_connection_is_unstable));
                       }
                        if (quality > 0 && quality < 2){
                            binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon));
                        }
                        else if (quality < 3){
                            binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon3));
                        } else if (quality <=4){
                            binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon2));
                        }
                        else if (quality < 6) {
                            binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon1));
                        }
                        else if (quality == 6) {
                            binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon4));
                        }
                        else {
                            binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon));
                        }
                    }

                    /*if (quality > 0 && quality < 2){
                        binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon));
                    }
                    else if (quality < 3){
                        binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon3));
                    } else if (quality <=4){
                        binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon2));
                    }
                    else if (quality < 6) {
                        binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon1));
                    }
                    else if (quality == 6) {
                        binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon4));
                    }
                    else {
                        binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon));
                    }*/
                }
            });
        }


        @Override
        public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
            // Use downlink network quality to update the network status

            // runOnUiThread(() -> updateNetworkStatus2(txQuality));

            // NetworkUtils.startMonitoringNetwork(VideoCallActivity.this);
            showstatusAlert();
            if(txQuality==0 || rxQuality==0){

            }
            if(videoCallActivity!=null){
                if (txQuality==QUALITY_EXCELLENT && rxQuality==QUALITY_EXCELLENT){

                } else if (txQuality==QUALITY_POOR){
                    showErrorMessage(getString(R.string.your_connection_is_poor_please_check_your_connection));
                }else if (rxQuality==QUALITY_POOR){
                    showErrorMessage(getString(R.string.the_other_party_s_connection_is_poor));
                }else if (txQuality==QUALITY_BAD){
                    showErrorMessage(getString(R.string.your_connection_is_unstable_please_check_your_connection));
                }else if (rxQuality==QUALITY_BAD){
                    showErrorMessage(getString(R.string.the_other_party_s_connection_is_unstable));
                }
                else if (txQuality==QUALITY_VBAD){
                    showErrorMessage(getString(R.string.your_connection_is_unstable_please_check_your_connection));
                }else if (rxQuality==QUALITY_VBAD){
                    showErrorMessage(getString(R.string.the_other_party_s_connection_is_unstable));
                }else {

                }
                runOnUiThread(() -> updateNetworkStatus(rxQuality));
            }

        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (muted){
                        cameraOffStatus="false";
                    }else {
                        cameraOffStatus="true";
                    }
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }


        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (speakers.length == 0) {
                        binding.audioAnimate.setVisibility(View.INVISIBLE);
                    } else
                        for (AudioVolumeInfo num : speakers) {
                            Log.e("volume ",""+num.volume);
                            Log.e("uid ",""+num.uid);
                            /*if (mRtcEngine!=null){
                                if (num.uid!=0){
                                    mRtcEngine.adjustPlaybackSignalVolume(90);
                                    mRtcEngine.adjustUserPlaybackSignalVolume(num.uid,90);
                                    mRtcEngine.adjustAudioMixingVolume(90);
                                }
                            }*/
                            if (num.uid != 0 && totalVolume > 0) {
                                binding.audioAnimate.setVisibility(View.VISIBLE);
                            } else {
                                binding.audioAnimate.setVisibility(View.INVISIBLE);
                            }
                        }
                   }
                });
            }
        };

    /**
     * calling timer
     */
    private void startCallTimer() {
        binding.txtCallTimer.setBase(SystemClock.elapsedRealtime());
        binding.txtCallTimer.start();
    }
    private void updateNetworkStatus(int quality){

        binding.txtName.setText(docName);
        binding.textName.setText(docName+"         ");
        if (quality==0){

        }
        if(videoCallActivity!=null){

            if (quality > 0 && quality < 2){
                binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon));
            }
            else if (quality < 3){
                binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon3));
            } else if (quality <=4){
                binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon2));
            }
            else if (quality < 6) {
                binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon1));
            }
            else if (quality == 6) {
                binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon4));
            }
            else {
                binding.imgRemoteuserstatus.setBackground(ContextCompat.getDrawable(videoCallActivity, R.drawable.networkicon));
            }
        }
    }
    private void showLightScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void showErrorMessage(String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(VideoCallActivity.this, message, Toast.LENGTH_SHORT);
//        View view = toast.getView();
//        view.setBackgroundColor(Color.parseColor("#000000"));
//        TextView textView = view.findViewById(android.R.id.message);
//        textView.setTextColor(Color.WHITE);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        OneSignal.getNotifications().clearAllNotifications();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //check permission so for the receiving call
        PermissionUtil.checkCameraAudioPermission(this, () -> {
            initliazeView();

        });
        OneSignal.getNotifications().clearAllNotifications();
        clickListners();
        Drawable drawable = getResources().getDrawable(R.drawable.officeimg);
        bitmap = ((BitmapDrawable) drawable).getBitmap();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        file = new File(directory, "img" + ".jpg");
        if (!file.exists()) {
            Log.d("path", file.toString());
            Log.e("path", file.getAbsolutePath());
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }else {
            file.delete();
            File directory2 = cw.getDir("imageDir", Context.MODE_PRIVATE);
            file = new File(directory2, "img" + ".jpg");
            FileOutputStream fos = null;
            Log.e("path2", file.getAbsolutePath());
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        binding.btnBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showbackgroundeffectDialog();
            }
        });
    }

    private void clickListners() {
        binding.btnEndInvitation.setOnClickListener(this);
        binding.btnMute.setOnClickListener(this);
        binding.btnFinish.setOnClickListener(this);
        binding.btnInComingHangup.setOnClickListener(this);
        binding.btnInComingAnswer.setOnClickListener(this);
        binding.endConnectionBtn.setOnClickListener(this);
        binding.btnAddFamily.setOnClickListener(this);
        binding.uploadImage.setOnClickListener(this);
        binding.txtRejoin.setOnClickListener(this);
        binding.backBtnConnecting.setOnClickListener(this);
        binding.backBtnCallConnected.setOnClickListener(this);
        binding.backBtnOutgoing.setOnClickListener(this);
        binding.backBtnIncoming.setOnClickListener(this);
    }

    public void showbackgroundeffectDialog(){
        final Dialog dialog = new Dialog(videoCallActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.background_effect_dialoge_layout);
        ImageView close_dialog = (ImageView) dialog.findViewById(R.id.close_dialog);
        LinearLayout room_linear = (LinearLayout) dialog.findViewById(R.id.room_linear);
        LinearLayout color_linear = (LinearLayout) dialog.findViewById(R.id.color_linear);
        LinearLayout blur_linear = (LinearLayout) dialog.findViewById(R.id.blur_linear);
        LinearLayout none_linear = (LinearLayout) dialog.findViewById(R.id.none_linear);
        VirtualBackgroundSource virtualBackgroundSource = new VirtualBackgroundSource();
        SegmentationProperty segmentationProperty = new SegmentationProperty(SegmentationProperty.SEG_MODEL_AI,0.5f);
        none_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   isVirtualBackGroundEnabled = false;
                VirtualBackgroundSource virtualBackgroundSource = new VirtualBackgroundSource();
                mRtcEngine.enableVirtualBackground(
                        false,
                        virtualBackgroundSource, segmentationProperty);
                dialog.dismiss();
            }
        });

        blur_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isVirtualBackGroundEnabled = true;
                virtualBackgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_BLUR;
                virtualBackgroundSource.blurDegree = virtualBackgroundSource.BLUR_DEGREE_HIGH;
                mRtcEngine.enableVirtualBackground(
                        true,
                        virtualBackgroundSource, segmentationProperty);
                dialog.dismiss();
            }
        });

        color_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  isVirtualBackGroundEnabled = true;
                virtualBackgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_COLOR;
                ColorPickerPopUp colorPickerPopUp = new ColorPickerPopUp(videoCallActivity);	// Pass the context.
                colorPickerPopUp.setShowAlpha(true)			// By default show alpha is true.
                        .setDialogTitle("Pick a Color")
                        .setOnPickColorListener(new ColorPickerPopUp.OnPickColorListener() {
                            @Override
                            public void onColorPicked(int color) {
                                // handle the use of color
                                String hexColor2 = String.format("#%06X", (0xFFFFFF & color));
                                int color2= Integer.parseInt(hexColor2.replaceFirst("#",""),16);
                                // setVirtualBackground(view);
                                virtualBackgroundSource.color = color2;
                                mRtcEngine.enableVirtualBackground(
                                        true,
                                        virtualBackgroundSource ,segmentationProperty);
                                dialog.dismiss();
                            }
                            @Override
                            public void onCancel() {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        room_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  isVirtualBackGroundEnabled = true;
                virtualBackgroundSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_IMG;
                virtualBackgroundSource.source = file.getAbsolutePath();
                mRtcEngine.enableVirtualBackground(
                        true,
                        virtualBackgroundSource, segmentationProperty);
                dialog.dismiss();
            }
        });
        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initliazeView() {
        showLightScreen();
        setupAudioManager();
        initCameraBtn();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        videoCallActivity = this;
        Intent intent = getIntent();
        userId = getLoginInfo2().getAccount();
        call_type = intent.getIntExtra("type", IN_COMING);
        paymentType = intent.getIntExtra("paymentType", DOCTOR_PAYMENT);
        docName = intent.getStringExtra("docName");
        SharedPreferences patPreferences = getSharedPreferences("VIDEOSHAREPREF_PAT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = patPreferences.edit();
        if (docName != null && !docName.isEmpty()) {
            editor.putString("PROVIDER_NAME", docName);
            editor.apply();
        } else {
            docName = patPreferences.getString("PROVIDER_NAME", "");
        }
        initViews();
        initAgoraEngineAndJoinChannel();
        registHeadSetReceiver();
        if(intent.getBooleanExtra("callFromPush",false)){
            getOnlineProviderNameV2();
            setVideoRoomNumber(roomNumber);
            roomNumber = intent.getStringExtra("roomNumber");
            callerType = intent.getStringExtra("callType");
            if(intent.getStringExtra("call_input_type").equals("RECEIVE_CALL")){
                if (!btnIncomingAnswer) {
                    btnIncomingHangup = true;
                    btnIncomingAnswer = true;
                    binding.callEndedTxt.setVisibility(View.VISIBLE);
                    binding.callEndedTxt.setText("Connecting..");
                    initialInCalling();
                }
            }
        } else if (call_type == OUT_CALLING || call_type == PAT_TO_PAT) {
            initValues(intent);
            callFromMyAppt = appointmentIdentifier != null;
            initialOutCalling();
        } else if (call_type == IN_COMING) {
            getOnlineProviderNameV2();
            roomNumber = intent.getStringExtra("roomNumber");
            callerType = intent.getStringExtra("callType");
            setVideoRoomNumber(roomNumber);
            SetPatientOnesignalIndicator(true);
            binding.connectingPage.setVisibility(View.GONE);
            binding.inComingView.setVisibility(View.VISIBLE);
            playMusic();

        } else if (call_type == DROPPED_CALL) {
            orgCode = intent.getStringExtra("orgCode");
            providerId = intent.getStringExtra("providerId");
            binding.connectingPage.setVisibility(View.GONE);
            setPatientDeviceStatus(userId, STATUS_BUSY, getLoginInfo2().getOneSignalUserId(), true);
            SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
            roomNumber = preferences.getString("ROOM_NUMBER", "");
            roomGuestId = preferences.getString("ROOM_GUEST_ID", "");
            joinChannel();
        }
    }

    private void setVideoRoomNumber(String roomNumber) {
        SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ROOM_NUMBER", roomNumber);
        editor.apply();

        if (call_type == OUT_CALLING || call_type == PAT_TO_PAT)
            WebService.webServiceAsyncTask(WebServiceID.setPatientOnlineRoom, userId, "1", roomNumber);
    }


    private void initValues(Intent intent) {
        orgCode = intent.getStringExtra("orgCode");
        providerId = intent.getStringExtra("providerId");
        recipientUserId = intent.getStringExtra("patientId");
        appointmentIdentifier = intent.getStringExtra("appt_id");
        apptStatus = intent.getStringExtra("apptStatus");
        isSkipped = intent.getBooleanExtra("isskipped", false);
        //Vitals and Chief Complaint
        weight = intent.getStringExtra("weight");
        height = intent.getStringExtra("total_height");
        BPH = intent.getStringExtra("bPH");
        BPL = intent.getStringExtra("bPL");
        pulse = intent.getStringExtra("pulse");
        temperature = intent.getStringExtra("temperature");
        chiefComplaint = intent.getStringExtra("chief_complaint");
        allergies = intent.getStringExtra("allergies");
        medHx = intent.getStringExtra("medHx");
        socialHx = intent.getStringExtra("socialHx");
        phone_num = intent.getStringExtra("phone_num");
        userInputBusyProvider = intent.getStringExtra("providerBusyMessage");
        apptTime = intent.getStringExtra("apptTime");
//        cc_idx = intent.getStringExtra("cc_idx");
//        cvv_code = intent.getStringExtra("cvv_code");
        card_id = intent.getStringExtra("card_id");
    }

    private void initViews() {
        binding.gridVideoViewContainer.setItemEventHandler((v, item) -> {
            Log.d("griddebug", "onItemDoubleClick " + v + " " + item + " " + mLayoutType);

            if (mUidsList.size() < 2) {
                return;
            }
            UserStatusData user = (UserStatusData) item;
            int uid = (user.mUid == 0) ? new Random().nextInt(61) : user.mUid;
            if (mLayoutType == LAYOUT_TYPE_DEFAULT && mUidsList.size() != 1) {
                switchToSmallVideoView(uid);
            } else {
                switchToDefaultVideoView();
            }
        });

        if (getTabletMode()) {
            binding.txtName.setText(getString(R.string.calling) + getFacilityName());
            binding.viewIncomingProfile.txtCallingName.setText(getFacilityName());
        } else {
            binding.txtName.setText(getString(R.string.calling_dr) + docName);
            binding.viewIncomingProfile.txtCallingName.setText(docName);
        }
    }

    private void initCameraBtn() {
        binding.btnVideo.setOnClickListener(v -> {
            if (!isSwitchingCamera) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSwitchingCamera = false;
                    }
                }, 1000);
                isSwitchingCamera = true;
                if (switchCam == 0) {
                    switchCamera();
                    frontCam = false;
                    switchCam = 1;
                    binding.btnVideo.setImageResource(R.drawable.call_front_cam);
                } else if (switchCam == 1) {
                    switchCamera();
                    frontCam = true;
                    muteLocalVideo(true);
                    switchCam = 2;
                    binding.btnVideo.setImageResource(R.drawable.cam_off);
                } else if (switchCam == 2) {
                    muteLocalVideo(false);
                    frontCam = true;
                    switchCam = 0;
                    binding.btnVideo.setImageResource(R.drawable.call_front_cam);
                }
            }
        });
    }

    private void setupAudioManager() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (getTabletMode())
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }

    private void initialOutCalling() {
        isOutGoingCall = true;
        generateOnlineRoomNumber();
        connectingPageTimeout();
    }

    private void initialInCalling() {
        hasEnteredInCall = true;
        isInComingCallAnswered = true;
        stopMusic();
        getOnlineRoomNumPatient(userId);
    }

    private void callingView(boolean isVisible) {
        if (isVisible) {
            binding.viewOutgoing.setVisibility(View.VISIBLE);
        } else {
            binding.callEndedTxt.setVisibility(View.GONE);
            binding.inComingView.setVisibility(View.GONE);
            binding.viewOutgoing.setVisibility(View.GONE);
        }
        binding.viewOutgoingProfile.txtCallingName.setText(getString(R.string.calling_dr) + " " + docName);
        binding.callEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endConnection();
            }
        });
    }

    private void onEndInvitation(boolean onPressed) {
        if (mRemoveAddUserStatusTimer != null) {
            mRemoveAddUserStatusTimer.cancel();
        }
        if (onPressed) {
            removeAddUserStatus(0);
        } else {
            binding.loaderAddProvider.setVisibility(View.VISIBLE);
            binding.btnFinish.setVisibility(View.VISIBLE);
            binding.btnEndInvitation.setVisibility(View.GONE);
            binding.txtAddProviderStatus.setText(getString(R.string.consultation_family_not_pick_up));
            removeAddUserStatus(7000);
        }
        setPatientOnlineRoom(recipientUserId, STATUS_ON_LINE);
    }


    private String getFacilityName() {
        String service_code = getLoginInfo2().getUserInfo().getService_code();
        if ("hnympc".equalsIgnoreCase(service_code)) {
            return "Heritage New York Medical";
        } else if ("heightsmedical".equalsIgnoreCase(service_code)) {
            return "Heights Medical";
        } else if ("sky".equalsIgnoreCase(service_code)) {
            return "Skylands Medical Group";
        } else if ("pansy".equalsIgnoreCase(service_code)) {
            return "CyberMed";
        }
        return "CyberMed";
    }

    private void removeAddUserStatus(final int countDownTime) {
        if (mRemoveAddUserStatusTimer != null) {
            mRemoveAddUserStatusTimer.cancel();
            mRemoveAddUserStatusTimer.purge();
            mRemoveAddUserStatusTimer = null;
        }
        mRemoveAddUserStatusTimer = new Timer();
        mRemoveAddUserStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.loaderAddProvider.setVisibility(View.GONE);
                        binding.btnFinish.setVisibility(View.VISIBLE);
                        binding.btnEndInvitation.setVisibility(View.GONE);
                        if (countDownTime == END_INVITATION) {
                            onEndInvitation(false);
                        }
                        // Stuff that updates the UI
                    }
                });
            }
        }, countDownTime);
    }

    private void unAnswerHangupPatientCall() {
        if (mGetPatientOnlineStatusTask == null) {
            getPatientOnlineStatus(recipientUserId, false);
        }
    }

    private void getPatientFamilyList() {
        if (getPatientListTask == null) {
            final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_search_patient, null);
            dialogBuilder.setView(dialogView);

            //dialogView.findViewById(R.id.searchInput).setVisibility(View.GONE);
            // dialogBuilder.setTitle(getString(R.string.video_add_family)); // change text
            dialogBuilder.setCancelable(false);

            dialogBuilder.setView(dialogView);
            final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
            b.setCanceledOnTouchOutside(true);
            b.show();

            ImageView closeBtn = dialogView.findViewById(R.id.closeBtn);
            Button addBtn = dialogView.findViewById(R.id.addBtn);
            LinearLayout emptyView=dialogView.findViewById(R.id.emptyView);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b.dismiss();
                }
            });
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFamily();
                }
            });

            refreshLayout = dialogView.findViewById(R.id.refreshLayout);
            patientList = dialogView.findViewById(R.id.patientList);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshLayout.setRefreshing(true);
                    getPatientFamilyListAsync(b);
                    refreshLayout.setRefreshing(false);
                }
            });

            patientList.setLayoutManager(new LinearLayoutManager(this));
            searchPatientAdapter = new FamilyMemberAdapter();
            searchPatientAdapter.setClickListener(new FamilyMemberAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (b != null)
                        b.dismiss();
                    FamilyInfo searchPatient = searchPatientAdapter.getItem(position);
                    binding.txtAddProviderStatus.setText(getString(R.string.calling) + " " + searchPatient.first_name + " " + searchPatient.last_name);
                    recipientUserId = searchPatient.user_id;
                    if (mGetPatientOnlineStatusTask == null) {
                        getPatientOnlineStatus(recipientUserId, true);
                    }
                }

                @Override
                public void setView(boolean hasList) {
                    if(hasList){
                        emptyView.setVisibility(View.GONE);
                        patientList.setVisibility(View.VISIBLE);
                    }
                }
            });
            patientList.setAdapter(searchPatientAdapter);
            if (getPatientListTask == null) {
                getPatientListTask = getPatientFamilyListAsync(b);
            }
        }
    }


    private void addFamily() {

        AlertDialog alertDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_new_family, null);

        dialogView.findViewById(R.id.btn_personal_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                addByPatientInfoDialog();
            }
        });
        dialogView.findViewById(R.id.btn_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                addByEmailDialog();
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

    private void addByPatientInfoDialog() {
        PatientInfoDialog dialog = new PatientInfoDialog(VideoCallActivity.this);
        dialog.setCallBack(new PatientInfoDialog.ButtonCallBack() {
            @Override
            public void onPatientExist(String userId, String fullname) {
                dialog.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_family_no_id, null);
                EditText email = dialogView.findViewById(R.id.email_txt);
                EditText pwdInput = dialogView.findViewById(R.id.edit_password);
                Button addBtn = dialogView.findViewById(R.id.btn_add_family);
                ImageView imgClose = dialogView.findViewById(R.id.imgClose);
                ImageView password_hide_button = dialogView.findViewById(R.id.password_hide_button);
                LinearLayout lin_email = dialogView.findViewById(R.id.lin_email2);
                CardView card_user = dialogView.findViewById(R.id.card_user);
                TextView rep_name = dialogView.findViewById(R.id.rep_name);
                TextView rep_account = dialogView.findViewById(R.id.rep_account);
                lin_email.setVisibility(View.GONE);
                card_user.setVisibility(View.VISIBLE);
                rep_name.setText(fullname);
                rep_account.setText(userId);
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                email.setText(getString(R.string.add_family_member_no_id, userId));
                addBtn.setOnClickListener(v -> {
                    addAuthRep(userId, pwdInput.getText().toString(), () -> alertDialog.dismiss());
                });
                password_hide_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pwdInput.setTypeface(Typeface.DEFAULT);
                        pwdInput.setTransformationMethod(new PasswordTransformationMethod());
                        if (view.getTag() == "0") {
                            view.setTag("1");
                            pwdInput.setTransformationMethod(null);
                            pwdInput.setSelection(pwdInput.length());
                            password_hide_button.setImageResource(R.drawable.pass_show);
                        } else {
                            view.setTag("0");
                            pwdInput.setTransformationMethod(new PasswordTransformationMethod());
                            pwdInput.setSelection(pwdInput.length());
                            password_hide_button.setImageResource(R.drawable.pass_hide);
                        }
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
            }

            @Override
            public void onPatientNotExist(String firstName, String lastName, String gender, String dob, String zip_code) {
//                AlertDialog alertDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
//                alertDialog.setTitle(getString(R.string.register_new_family_title));
//                alertDialog.setMessage(getString(R.string.register_new_family));
//
//                alertDialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.btn_ok), (dialog1, which) -> {
//                    alertDialog.dismiss();
//                    RegisterFamilyMember registerDialog = RegisterFamilyMember.newInstance(VideoCallActivity.this, (user_id, password) -> {
//                        addAuthRep(user_id, password, null);
//                        dialog.dismiss();
//                    });
//                    registerDialog.setBasicInfo(firstName, lastName, gender, dob, zip_code);
//                    registerDialog.show();
//                });
//                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.btn_cancel), (dialog2, which) -> {
//                    alertDialog.dismiss();
//                });
//                alertDialog.show();
                //Toast.makeText(VideoCallActivity.this, getString(R.string.register_new_family_title), Toast.LENGTH_LONG).show();
                ErrorMessage.alertDialog(VideoCallActivity.this, null, getString(R.string.register_new_family_title), null);

            }
        });
    }

    private void addByEmailDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_family_no_id, null);
        mFamilyEmailInput = dialogView.findViewById(R.id.email_txt);
        EditText mFamilyPwdInput = dialogView.findViewById(R.id.edit_password);
        mErrorEmail = dialogView.findViewById(R.id.error_select_email);
        mErrorRelationship = dialogView.findViewById(R.id.error_select_relationship);
        Button saveBtn = dialogView.findViewById(R.id.btn_add_family);
        saveBtn.setOnClickListener(view -> {
            mErrorEmail.setVisibility(View.GONE);
            mErrorRelationship.setVisibility(View.GONE);
            if (inputCheck()) {
                addAuthRep(mFamilyEmailInput.getText().toString().trim(), mFamilyPwdInput.getText().toString(), () -> dialogBuilder.dismiss());
            }
        });
        ImageView imgClose = dialogView.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void addAuthRep(String userId, String pwd, Runnable onAddSuccess) {
        OnPostExecute ope = result -> {
            String message = result.toString();
            if (message.equals("1")) {
                if (onAddSuccess != null) {
                    onAddSuccess.run();
                }
                ErrorMessage.alertDialog(VideoCallActivity.this, getString(R.string.success_dialog_title), getString(R.string.add_representive_success), null);
                getPatientFamilyListAsync(null);
            } else if (message.equals("Invalid user id")) {
                mFamilyEmailInput.setError(getString(R.string.family_member_email_error));
                mFamilyEmailInput.requestFocus();
            } else if (message.equals("Relation already exist")) {
                ErrorMessage.alertDialog(VideoCallActivity.this, getString(R.string.notice_title), getString(R.string.relation_already_exist), null);
            } else if (message.equals("Incorrect password")) {
                ErrorMessage.alertDialog(VideoCallActivity.this, getString(R.string.error_dialog_title), getString(R.string.incorrect_password), null);
            } else {
                Toast.makeText(VideoCallActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
            }
        };
        WebService.webServiceAsyncTask(WebServiceID.create_auth_link, ope, this.userId, userId, pwd);
    }

    private boolean inputCheck() {

        if (TextUtils.isEmpty(mFamilyEmailInput.getText().toString())) {
            mFamilyEmailInput.setError(getString(R.string.family_member_email_empty));
            mFamilyEmailInput.requestFocus();
            return false;
        }

        if (!ValidationUtils.isEmailValid(mFamilyEmailInput.getText().toString())) {
            mFamilyEmailInput.setError(getString(R.string.family_member_email_validation));
            mFamilyEmailInput.requestFocus();
            return false;
        }


//        if (!mFamilyRelationshipSpinner.isSelected()) {
//            mErrorRelationship.setVisibility(View.VISIBLE);
//            return false;
//        }

        return true;
    }

    private void UpdateFamilyMember(String email, String relationship, AlertDialog dialog) {
        if (mUpdateFamilyMemberTask == null) {
            mUpdateFamilyMemberTask = UpdateFamilyMemberAsyncTask(email, relationship, dialog);
        }
    }

    private void endConnection() {
        hasEndConnectionClicked = true;
        binding.progressLoader.setVisibility(View.GONE);
        binding.connectingTxt.setText(getString(R.string.video_cancelling));
        hangUpProcess(true);
    }


    private void ratingDialog() {
        if (getTabletMode() || call_type == PAT_TO_PAT || callerType.equals("patient")) {
            hangUpProcess(false);
        } else if (mRatingDialog == null) {
            if (!isFinishing()) {
                mRatingDialog = new Dialog(VideoCallActivity.this, R.style.Theme_Dialog);
                mRatingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mRatingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mRatingDialog.setContentView(R.layout.rating_page_dialog);
                mRatingDialog.setCancelable(false);
                mRatingDialog.setCanceledOnTouchOutside(false);
                RatingBar ratingBar = mRatingDialog.findViewById(R.id.ratingBar);

                mRatingDialog.findViewById(R.id.close_btn).setOnClickListener(v -> {
                    mRatingDialog.dismiss();
                    hangUpProcess(false);
                });

                Dialog commentDialog = new Dialog(VideoCallActivity.this, R.style.Theme_Dialog);
                commentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                commentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                commentDialog.setContentView(R.layout.dialog_review_page);
                commentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                commentDialog.setCancelable(false);
                commentDialog.setCanceledOnTouchOutside(false);
                //TextInputLayout inputLayout = commentDialog.findViewById(R.id.input_review);
                mRatingDialog.findViewById(R.id.okayBtn).setOnClickListener(v -> {
                    mRatingDialog.dismiss();
                    EditText edtComment = commentDialog.findViewById(R.id.edtComment);
                    commentDialog.findViewById(R.id.btn_later).setOnClickListener(v13 -> {

                        if (!TextUtils.isEmpty(rating))
                            RateDoctorAsyncTask(orgCode, providerId, userId, rating, null);
                        hangUpProcess(false);
                        commentDialog.dismiss();
                    });
                    commentDialog.findViewById(R.id.btn_done).setOnClickListener(v12 -> {
                        String comments = edtComment.getText().toString();
                        if (TextUtils.isEmpty(comments)) {
                            //  Toast.makeText(VideoCallActivity.this,getString(R.string.rating_comment),Toast.LENGTH_LONG);
//                            inputLayout.setErrorEnabled(true);
//                            inputLayout.setError(getString(R.string.rating_comment));
//                            inputLayout.requestFocus();
                            return;
                        }
//                        inputLayout.setError("");
//                        inputLayout.setErrorEnabled(false);
                        String userId = getLoginInfo2().getAccount();
                        RateDoctorAsyncTask(orgCode, providerId, userId, rating, comments);
                        hangUpProcess(false);
                        commentDialog.dismiss();
                    });
                    commentDialog.findViewById(R.id.close_btn).setOnClickListener(v1 -> {
                        if (!rating.equals("0"))
                            RateDoctorAsyncTask(orgCode, providerId, userId, rating, null);
                        hangUpProcess(false);
                        commentDialog.dismiss();
                    });

                    commentDialog.show();
                });
                ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
                    mRatingDialog.findViewById(R.id.okayBtn).setEnabled(rating > 0);
                    if (rating > 0)
                        this.rating = String.valueOf(rating);
                });
                mRatingDialog.show();
            }
        }
    }

    /**
     * 呼叫进来未接听挂断
     */
    private void hangUpProcess(boolean isCancelledDuringOutCall) {
        stopMusic();
        if (isCancelledDuringOutCall) {
            leavingRoomAsGuest(roomNumber, roomGuestId);
            hangUpCall(true);
        } else {
            hangUpFunction(true);
        }
        clearNotification();
        cancelGetActiveGuestCountTimer();
        setResult(33);
        postFinish(getString(R.string.call_cancelled));
    }


    private void playMusic() {
        /*mRtcEngine.setEnableSpeakerphone(!((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn());
        mp = MediaPlayer.create(VideoCallActivity.this, R.raw.ring);
        mp.start();*/
        if (soundPool != null)
            return;
        if (mRtcEngine != null)
            mRtcEngine.setEnableSpeakerphone(!((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn());
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = soundPool.load(this, R.raw.ring, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == music) {
                    if (soundPool != null)
                        soundPool.play(music, 1, 1, 0, -1, 1);
                }
            }
        });
    }

    private void playEndDing() {
        stopMusic();
        /*mRtcEngine.setEnableSpeakerphone(!((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn());
        mp = MediaPlayer.create(VideoCallActivity.this, R.raw.ring);
        mp.start();*/
        if (soundPool != null)
            return;

        stopMusic();
        if (soundPool == null) {
            //mRtcEngine.setEnableSpeakerphone(!((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn());
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            music = soundPool.load(this, R.raw.end_ding, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (sampleId == music) {
                        if (soundPool != null)
                            soundPool.play(music, 1, 1, 0, -1, 1);
                    }
                }
            });
        }
    }


    private void stopMusic() {

        if (soundPool != null) {
            soundPool.stop(music);
            soundPool.release();
            soundPool = null;
        }
        /*
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }*/
        //Intent myService = new Intent(VideoCallActivity.this, MusicPlayer.class);
        //stopService(myService);
    }

    //Video Workflow (Outgoing): 6. 30 Seconds Timer
    private void listenProviderJoinRoom() {
        timer = new Timer();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    stopMusic();
                    if (call_type == OUT_CALLING) {
                        showProviderBusyDialog();
                    } else {
                        hangUpProcess(false);
//                        endDuringRinging("listenprovider");
                    }
                }
            }
        };
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isRemoteJoin) {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        };
        timer.schedule(timerTask, WAITING_TIME);
    }

    private void connectingPageTimeout() {
        connectTimer = new Timer();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    cancelGetProviderOnesignalTimer();
                    cancelGetPatientOnesignalTimer();
                    unableConnect();
                }
            }
        };
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isRemoteJoin) {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        };
        connectTimer.schedule(timerTask, WAITING_TIME);
    }


    //Check whether the receiver has gotten the onesignal
    private void getProviderOnesignalIndicator(final String roomNumber) {
        isConnecting = true;
        getProviderOnesignalTimer = new Timer();
        getProviderOnesignalTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mCheckRevStatusTask == null) {
                    mCheckRevStatusTask = CheckRevStatusTask();
                }
                if (getProviderOnesignalTask == null) {
                    getProviderOnesignalTask = getProviderOnesignalIndicatorAsyncTask(roomNumber);

                }
            }
        }, 0, 1000);
    }


    //Check whether the receiver has gotten the onesignal
    private void getPatientOnesignalIndicator() {
        isConnecting = true;
        getPatientOnesignalTimer = new Timer();
        getPatientOnesignalTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (GetOnlineRoomNumberTask == null)
                    GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(recipientUserId);

                if (getPatientOnesignalTask == null) {
                    getPatientOnesignalTask = getPatientOnesignalIndicatorAsyncTask();

                }
            }
        }, 0, 1000);
    }

    //Check whether the receiver has accepted, declined or timeout
    private void checkReceiverStatusTimer() {
        isConnecting = false;
        CheckRevStatusTimer = new Timer();
        CheckRevStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("checkrevstatus", "checking");
                if (mCheckRevStatusTask == null) {
                    mCheckRevStatusTask = CheckRevStatusTask();
                }
            }
        }, 0, 3000);
    }

    //Video Workflow (Outgoing): 7. Check if hang up on receiving end
    private void hasCallerLeft() {

        hasCallerLeftTimer = new Timer();
        hasCallerLeftTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (GetOnlineRoomNumberTask == null)
                    GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(userId);
            }
        }, 0, 3000);
    }

    //Video Workflow (Outgoing): 7. Check if hang up on receiving end
    private void hasRecipientLeft() {
        hasRecipientLeftTimer = new Timer();
        hasRecipientLeftTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (GetOnlineRoomNumberTask == null)
                    GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(recipientUserId);

            }
        }, 0, 3000);
    }

    //Video Workflow (Outgoing): 7. Check if hang up on receiving end
    private void incomingHangupTimerCheck() {

        incomingHangupTimer = new Timer();
        incomingHangupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mRatingDialog == null) {
                    if (!hasEnteredInCall) {
                        getOnlineProviderNameV2();
                    }

                    if (GetOnlineRoomNumberTask == null)
                        GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(userId);
                }
            }
        }, 0, 3000);
    }

    private void getActiveGuestCount() {
        if (getActiveGuestCount == null) {
            getActiveGuestCount = new Timer();
            getActiveGuestCount.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mGetActiveGuestCountTask == null) {
                        mGetActiveGuestCountTask = getActiveGuestCountTask();
                    }
                }
            }, 0, 3000);
        }
    }


    private void showProviderLeftDialog() {
        if (mProviderLeftDialog == null && !hasCallEnded) {
            endAgoraCall();
            leavingRoomAsGuest(roomNumber, roomGuestId);
            if (!isFinishing()) {
                if (getTabletMode()) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
                    alertDialog.setTitle(getString(R.string.consultation_left_title));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    markApptStatus(ARRIVED_STATUS);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (alertDialog != null && alertDialog.isShowing()) {
                                markApptStatus(ARRIVED_STATUS);
                                alertDialog.dismiss();
                            }
                        }
                    }, 5000);
                } else {
                    if (mRatingDialog == null) {
                        mProviderLeftDialog = new MyAlertDialog(this);
                        mProviderLeftDialog.show();
                        mProviderLeftDialog.setDialogTitle(getString(R.string.consultation_left_title));
                        mProviderLeftDialog.setDialogContent(getString(R.string.consultation_left_msg));
                        mProviderLeftDialog.setCanceledOnTouchOutside(false);
                        mProviderLeftDialog.setRightClickListener(getString(R.string.btn_ok), new MyAlertDialog.RightClickListener() {
                            @Override
                            public void onRightClick(View view) {
                                ratingDialog();
                            }
                        });
                    }
                }
            }
        }
    }


    private void resetTask() {
        providerIsBusy = true;

        if (mCheckRevStatusTask != null) {
            mCheckRevStatusTask.cancel(true);
            mCheckRevStatusTask = null;
        }
        cancelCheckRevStatusTimer();
        leaveChannel();
        RtcEngine.destroy();

        mRtcEngine = null;
        if (mGenerateOnlineRoomNumberTask != null) {
            mGenerateOnlineRoomNumberTask.cancel(true);
            mGenerateOnlineRoomNumberTask = null;
        }
        if (mCreateCallLogRoomsTask != null) {
            mCreateCallLogRoomsTask.cancel(true);
            mCreateCallLogRoomsTask = null;
        }
        if (mRegisterAsRoomGuestTask != null) {
            mRegisterAsRoomGuestTask.cancel(true);
            mRegisterAsRoomGuestTask = null;
        }
        if (mMakeCall2ProviderTask != null) {
            mMakeCall2ProviderTask.cancel(true);
            mMakeCall2ProviderTask = null;
        }
        if (mCreateAppointmentTask != null) {
            mCreateAppointmentTask.cancel(true);
            mCreateAppointmentTask = null;
        }
        if (markAppointmentStatusTask != null) {
            markAppointmentStatusTask.cancel(true);
            markAppointmentStatusTask = null;
        }
        if (mGetOnlineRoomNumPatient != null) {
            mGetOnlineRoomNumPatient.cancel(true);
            mGetOnlineRoomNumPatient = null;
        }
        if (mNotifyPatientAppStatusTask != null) {
            mNotifyPatientAppStatusTask.cancel(true);
            mNotifyPatientAppStatusTask = null;
        }
        stopMusic();
        //unRegistHeadSetReceiver();
    }

    /**
     * 30s医生未接听，提示用户是否进入候诊室，此时可被医生呼叫
     */
    private void showProviderBusyDialog() {
        binding.loaderAddProvider.setVisibility(View.GONE);
        resetTask();

        if (mProviderBusyDialog == null && mTabletProviderBusyDialog == null
                && !hasEnteredInCall && !isRemoteJoin && !btnFinishClicked) {

            cancelIncomingCallTimer();
            //isFinishing() prevents dialog to show during asynchronous task when the activity is destroyed
            if (!isFinishing()) {

                if (getTabletMode()) {
                    mTabletProviderBusyDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
                    if (docName != null && docName.contains("LovingCare")) {
                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_msg_tablet_pharmacy));
                    } else if (getLoginInfo2().getUserInfo().getService_code().equalsIgnoreCase("hnympc")) {
                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_heritage_msg_tablet));
                    } else if (getLoginInfo2().getUserInfo().getService_code().equalsIgnoreCase("heightsmedical")) {
                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_heights_msg_tablet));
                    } else if (getLoginInfo2().getUserInfo().getService_code().equalsIgnoreCase("sky")) {
                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_skylands_msg_tablet));
                    } else if (getLoginInfo2().getUserInfo().getService_code().equalsIgnoreCase("pansy")) {
                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_cybermed_msg_tablet));
                    }else {
                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_title));
                    }

                    mTabletProviderBusyDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    markApptStatus(ARRIVED_STATUS);
                                    dialog.dismiss();
                                }
                            });
                    mTabletProviderBusyDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mTabletProviderBusyDialog != null) {
                                markApptStatus(ARRIVED_STATUS);
                                if (mTabletProviderBusyDialog != null)
                                    mTabletProviderBusyDialog.dismiss();
                            }
                        }
                    }, 7000);
                } else {
                    if (callFromMyAppt) {
                        AlertDialog alertDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
                        alertDialog.setTitle(getString(R.string.consultation_busy_title));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        hangUpProcess(true);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_provider_busy, null);
                        mProviderBusyDialog = new AlertDialog.Builder(this);
                        mProviderBusyDialog.setView(mView);

                        final EditText userInputDialog = mView.findViewById(R.id.userInputDialog);
                        mProviderBusyDialog
                                .setCancelable(false)
                                .setNegativeButton(getString(R.string.btn_no),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                hangUpProcess(true);
                                            }
                                        })
                                .setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        userInputBusyProvider = userInputDialog.getText().toString();
                                        markApptStatus(ARRIVED_STATUS);
                                    }
                                });

                        AlertDialog alertDialogAndroid = mProviderBusyDialog.create();
                        alertDialogAndroid.show();

                    }
                }
            }
        }
    }

    private void unableConnect() {
        resetTask();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.consultation_cannot_connect))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        binding.progressLoader.setVisibility(View.GONE);
                        binding.connectingTxt.setText(getString(R.string.video_cancelling));
                        hangUpProcess(true);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showMakeAppointMentResult(String resultInfo) {
        final MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setDialogContent(resultInfo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setRightClickListener(getString(R.string.btn_ok), new MyAlertDialog.RightClickListener() {
            @Override
            public void onRightClick(View view) {
                hangUpProcess(true);
            }
        });
    }


    /**
     * 主动呼出挂断电话
     */
    private void hangUpCall(boolean playDing) {
        stopMusic();
        if (playDing) {
            playEndDing();
        }

        if (apptStatus == null || apptStatus.equals("")) {
            markApptStatus(CANCELLED);
        }

        cancelCall2Provider();

        //a bug with some of the devices from agora < v2.0.2
        //ending with back camera will crash the app
        //need to set back to front camera before exit
        if (frontCam != true) {
            switchCamera();
        }
    }

    /**
     * 呼入进来已接听的挂断调用的方法
     */
    private void hangUpFunction(boolean playDing) {
        if (playDing) {
            playEndDing();
        }

        OnPostExecute ope = result -> {
            if (result.toString().equals("1")) {
                setPatientOnlineRoom(userId, STATUS_ON_LINE);
            }
        };

        WS.setPatientDeviceStatus(STATUS_ON_LINE, ope);

        //a bug with some of the devices from agora < v2.0.2
        //ending with back camera will crash the app
        //need to set back to front camera before exit
        if (frontCam != true) {
            switchCamera();
        }
    }

    private void setPatientOnlineRoom(String userId, int status) {
        getSetPatientOnlineRoomResult(userId, status, "", new SetPatientOnlineRoom() {
            @Override
            public void setPatientOnlineRoomResult(int result) {
                if (result == 1) {
                    Log.e(CONSULT, "InComing setPatientOnlineRoom success");
                    if (isInComingCallAnswered || isOutGoingCall) {
                    }
                } else {
                    Log.e(CONSULT, "InComing setPatientOnlineRoom failed");
                }
            }
        });
    }

    private void cancelCall2Provider() {
        getCancelCallToProviderResult(orgCode, providerId, roomNumber, new SetCancelCallToProvider() {
            @Override
            public void cancelCallToProviderResult(int cancelCallResult) {
                //If 0 means already cleared
                if (cancelCallResult == 1 || cancelCallResult == 0) {
                    OnPostExecute ope = result -> {
                        if (result.toString().equals("1")) {
                            setPatientOnlineRoom(userId, STATUS_ON_LINE);
                        }
                    };
                    WS.setPatientDeviceStatus(STATUS_ON_LINE, ope);

                    Log.e(CONSULT, "InComing cancelCall2Provider success");
                } else {
                    Log.e(CONSULT, "InComing cancelCall2Provider failed");
                }
            }
        });
    }

    private void leavingRoomAsGuest(String roomNum, String roomGuestId) {
        WebService.webServiceAsyncTask(leaving_Room_as_Guest_v2, roomNum, roomGuestId, CDoctor2Application.getLoginInfo().getOneSignalUserId());
    }

    private void GetPatientFamilyMember() {
        if (mGetFamilyList == null) {
            mGetFamilyList = GetPatientFamilyMemberAsyncTask();
        }
    }

    //Video Workflow (Outgoing): 1. GenerateOnlineRoomNumber
    private void generateOnlineRoomNumber() {
        if (mGenerateOnlineRoomNumberTask == null) {
            mGenerateOnlineRoomNumberTask = generateOnlineRoomNumberTask();
        }
    }

    //Video Workflow (Outgoing): 2. Create Call Log Rooms
    private void createCallLogRooms(String roomNum) {
        if (mCreateCallLogRoomsTask == null) {
            mCreateCallLogRoomsTask = createCallLogRoomsTask(roomNum);
        }
    }

    private void NotifyProvider(String message, String push_msg) {
        if (NotifyProviderTask == null) {
            NotifyProviderTask = NotifyProviderAsyncTask(message, push_msg);
        }
    }

    private void NotifyPatient(String message) {
        if (NotifyPatientTask == null) {
            NotifyPatientTask = NotifyPatientAsyncTask(message);
        }
    }
    private void makeCall2Provider() {
        if (mMakeCall2ProviderTask == null) {
            mMakeCall2ProviderTask = MakeCall2ProviderTask();
        }
    }
    private void makeCall2Patient(String userId) {
        if (mMakeCall2PatientTask == null) {
            mMakeCall2PatientTask = MakeCall2PatientTask(userId);
        }
    }

    //Video Workflow (Outgoing): 5. Create Appointment
    private void createAppointment() {
        if (mCreateAppointmentTask == null) {
            mCreateAppointmentTask = createAppointmentTask();
        }
    }

    private void markApptStatus(String status) {
        if (markAppointmentStatusTask == null) {
            markAppointmentStatusTask = markAppointmentStatusAsyncTask(status);
        }
    }

    private void setRoomChargeCC() {
        if (mSetRoomChargeCCTask == null) {
            mSetRoomChargeCCTask = SetRoomChargeCCTask(roomNumber, appointmentIdentifier, orgCode, cc_idx, cvv_code);

        }
    }

    private void setApptPaymentCard() {
        Runnable setApptCard = () -> {
            PaymentApi paymentApi = RestApiCall.getApiService(PaymentApi.class);

            ApptPayment payment = new ApptPayment(orgCode, appointmentIdentifier, card_id);
            Call<Void> setApptPaymentCardCall = paymentApi.setApptPaymentCard(payment);

            setApptPaymentCardCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {

                    } else {
                        if (response.code() == 500) {
                            ErrorMessage.alertDialog(VideoCallActivity.this, getString(R.string.server_error), "Error happened on server side", null);
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                ErrorResponse error = new Gson().fromJson(errorBody, ErrorResponse.class);
                                ErrorMessage.alertDialog(VideoCallActivity.this, getString(R.string.error), error.getError(), null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        endConnection();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ErrorMessage.alertDialog(VideoCallActivity.this, getString(R.string.server_error), "Cannot connect to the server", null);
                    endConnection();
                }
            });
        };

        AuthManager.acquireNewTokenAsync(this, setApptCard);
    }

    private void markApptPaymentMethod(String paymentMethod) {
        if (mMarkApptPaymentMethodTask == null) {
            mMarkApptPaymentMethodTask = MarkApptPaymentMethodTask(appointmentIdentifier, paymentMethod);

        }
    }

    //Video Workflow (Incoming): 1. Get Online RoomNum
    private void getOnlineRoomNumPatient(String userId) {
        if (mGetOnlineRoomNumPatient == null) {
            mGetOnlineRoomNumPatient = getOnlineRoomNumPatientAsyncTask(userId);
        }
    }

    //Video Workflow (Incoming): 4. Notify App Devices
    private void notifyPatientAppStatus(String userId, String roomNum) {
        if (mNotifyPatientAppStatusTask == null) {
            mNotifyPatientAppStatusTask = notifyPatientAppStatusAsycnTask(userId, roomNum);
        }
    }


    private void SetPatientOnesignalIndicator(boolean set) {
        SimpleDateFormat sdfCompareNow = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US);
        String dateTimeCompNow = sdfCompareNow.format(new Date());
        if (setPatientOnesignalIndicatorTask == null) {
            setPatientOnesignalIndicatorTask = SetPatientOnesignalIndicatorAsyncTask(set, dateTimeCompNow);
        }
    }


    private void getOnlineProviderNameV2() {
        if (getOnlineProviderNameV2Task == null) {
            getOnlineProviderNameV2Task = getOnlineProviderNameV2AsyncTask();
        }
    }


    private void getGuestCount(int uid) {
        if (mGetActiveGuestCountTaskRemove == null) {
            mGetActiveGuestCountTaskRemove = getGuestCountAsyncTask(uid);
        }
    }

    //Video Workflow (Outgoing): 4. Register As Room Guest
    //Video Workflow (Incoming): 3. Register as room guest
    private void registerAsRoomGuest() {
        //TODO: old code
//        getRegisterAsRoomGuest(roomNumber, orgCode, userId, new SetRegistAsRoomGuest() {
//            @Override
//            public void registAsroomGuestResult(int result) {
//                Log.e(CONSULT, "registerAsRoomGuestTask success");
//                roomGuestId = String.valueOf(result);
//                if (roomGuestId.equals("-1")) {
//                    endConnection();
//                } else {
//                    SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString("ROOM_GUEST_ID", roomGuestId);
//                    editor.apply();
//
//                    if (call_type == OUT_CALLING) {
//                        makeCall2Provider();
//                    } else if (call_type == IN_COMING) {
//                        joinChannel();
//                    } else if (call_type == PAT_TO_PAT) {
//                        makeCall2Patient(recipientUserId);
//                    }
//                }
//            }
//        });


        OnPostExecute ope = result -> {
            Log.e(CONSULT, "registerAsRoomGuestTask success");
            roomGuestId = String.valueOf(result);
            if (roomGuestId.equals("-1")) {
                endConnection();
            } else {
                SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ROOM_GUEST_ID", roomGuestId);
                editor.apply();

                if (call_type == OUT_CALLING) {
                    makeCall2Provider();
                } else if (call_type == IN_COMING) {
                    joinChannel();
                } else if (call_type == PAT_TO_PAT) {
                    makeCall2Patient(recipientUserId);
                }
            }
        };

        String latitude = "";
        String longitude = "";
        LocationUtil locationTraker = new LocationUtil(this);
        Location location = locationTraker.getLocation();
        if (location != null) {
            longitude = String.valueOf(location.getLongitude());
            latitude = String.valueOf(location.getLatitude());
        }
        WebService.webServiceAsyncTask(WebServiceID.register_as_Room_Guest_v3, ope, roomNumber, "patient", orgCode, userId, latitude, longitude, CDoctor2Application.getLoginInfo().getOneSignalUserId());


    }

    private AsyncTask getOnlineProviderNameV2AsyncTask() {
        return new AsyncTask<Void, Void, DocInfo>() {
            Exception e;

            @Override
            protected DocInfo doInBackground(Void... voids) {
                try {
                    return WebService.getInstance().getOnlineProviderNameV2(userId);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(DocInfo doctor) {
                getOnlineProviderNameV2Task = null;
                if (doctor != null) {
                    orgCode = doctor.org_code;
                    providerId = doctor.provider_code;
                    if (doctor.last_name != null && !doctor.last_name.isEmpty()) {
                        docName = doctor.last_name;
                    } else {
                        cancelIncomingCallTimer();
                        postFinish(getString(R.string.call_answered));
                    }
                }
            }
        }.execute();
    }

    private AsyncTask GetPatientFamilyMemberAsyncTask() {
        return new AsyncTask<Object, Object, VectorFamily>() {
            Exception e;

            @Override
            protected VectorFamily doInBackground(Object... params) {
                try {
                    return WebService.getInstance().GetFamilyList(userId);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(VectorFamily familyVector) {
                mGetFamilyList = null;
                if (e == null) {
                    searchPatientAdapter.appendList(familyVector);
                } else {

                }

            }
        }.execute();
    }


    private AsyncTask SetPatientOnesignalIndicatorAsyncTask(final boolean set, final String delivery_date) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().SetPatientOnesignalIndicator(getLoginInfo2().getAccount(), set, delivery_date);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                setPatientOnesignalIndicatorTask = null;
                if (e == null) {
                    if (integer != -1) {
                        if (set == true) {
                            if (call_type == PAT_TO_PAT) {
                                hasCallerLeft();
                            } else {
                                incomingHangupTimerCheck();
                            }
                            listenProviderJoinRoom();
                        }
                        Log.d("TIMEANALYSIS", "5. Set GETUI Indicator " + Calendar.getInstance().getTime().toString());
                        Log.e(CONSULT, "setProviderOnesignalIndicator success");
                        //binding.linearAudiotrouble.setVisibility(View.VISIBLE);
                    } else {
                        Log.e(CONSULT, "setProviderOnesignalIndicator failed");
                    }
                }
            }
        }.execute();
    }


    private AsyncTask getProviderOnesignalIndicatorAsyncTask(final String roomNum) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().GetProviderOneSignalIndicator(orgCode, providerId, roomNum);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                getProviderOnesignalTask = null;
                if (e == null) {
                    if (integer == 1) {
                        binding.connectingPage.setVisibility(View.GONE);
                        callingView(true);
                        getProviderOnesignalTimer.cancel();
                        cancelConnectTimer();
                        listenProviderJoinRoom();
                        checkReceiverStatusTimer();
                        playMusic();

                        binding.txtAddProviderStatus.setText(getString(R.string.calling_people, (getTabletMode() ? getFacilityName() : docName)));
                        binding.loaderAddProvider.setVisibility(View.VISIBLE);

                        Log.d(CONSULT, "get provider onesignal success");
                    } else if (integer == -1) {
                        Log.d(CONSULT, "get provider onesignal waiting");
                    }
                } else {
                    Log.d(CONSULT, "get provider onesignal Failed");
                }
            }
        }.execute();
    }


    private AsyncTask getPatientOnesignalIndicatorAsyncTask() {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().GetPatientOneSignalIndicator(recipientUserId, roomNumber);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                getPatientOnesignalTask = null;
                if (e == null) {
                    if (integer == 1) {
                        binding.connectingPage.setVisibility(View.GONE);
                        cancelGetPatientOnesignalTimer();
                        listenProviderJoinRoom();

                        hasRecipientLeft();
                        cancelConnectTimer();
                        playMusic();
                        binding.txtAddProviderStatus.setText(getString(R.string.calling_people, (getTabletMode() ? getFacilityName() : docName)));
                        binding.loaderAddProvider.setVisibility(View.VISIBLE);
                        Log.d(CONSULT, "get provider onesignal success");
                    } else if (integer == -1) {
                        Log.d(CONSULT, "get provider onesignal waiting");
                    }
                } else {
                    Log.d(CONSULT, "get provider onesignal Failed");
                }
            }
        }.execute();
    }


    private AsyncTask generateOnlineRoomNumberTask() {
        return new AsyncTask<Void, Void, String>() {
            Exception e;

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return WebService.getInstance().generate_OnlineRoomNumber();
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mGenerateOnlineRoomNumberTask = null;
                if (e == null) {
                    if (!TextUtils.isEmpty(s)) {
                        roomNumber = s;
                        setVideoRoomNumber(roomNumber);
                        if (appointmentIdentifier != null) {
                            //Call from my appointments
                            createCallLogRooms(roomNumber);
                        } else {
                            if (call_type == PAT_TO_PAT) {
                                //Patient to Patient call
                                createCallLogRoomsPatTask(roomNumber);
                            } else {
                                //Normal call to provider
                                createAppointment();
                            }
                        }
                        Log.e(CONSULT, "generrateOnlineRoomNumberTask success");
                    }
                } else {
                    toastShortInfo("Generate Online Room Number Task Failed");
                }
            }
        }.execute();
    }

    private AsyncTask getActiveGuestCountTask() {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().GetActiveGuestsCount(roomNumber, roomGuestId);
                } catch (Exception e) {
                    this.e = e;
                }
                return -1;
            }

            @Override
            protected void onPostExecute(Integer count) {
                super.onPostExecute(count);
                mGetActiveGuestCountTask = null;
                if (e == null) {
                    if (count == 0) {
                        if (isRemoteJoin) {
                            leavingRoomAsGuest(roomNumber, roomGuestId);
                            cancelGetActiveGuestCountTimer();

                            Log.e(CONSULT, "GetActiveGuestCount LEFTTT success");
                            showProviderLeftDialog();
                        }
                    }
                    Log.e(CONSULT, "GetActiveGuestCount success");
                } else {
                    toastShortInfo("GetActiveGuestCount failed");
                }
            }
        }.execute();
    }

    private AsyncTask UpdateFamilyMemberAsyncTask(final String email, final String relationship, final AlertDialog dialog) {
        return new AsyncTask<Object, Object, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Object... params) {
                try {
                    return WebService.getInstance().UpdateFamilyMember(userId, email, relationship);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                mUpdateFamilyMemberTask = null;
                if (e == null) {
                    if (integer == 1) {
                        GetPatientFamilyMember();
                        dialog.dismiss();
                    } else {
                        mErrorEmail.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute();
    }

    private AsyncTask CheckRevStatusTask() {
        return new AsyncTask<Void, Void, String>() {
            Exception e;

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return WebService.getInstance().CheckReceiverStatus(roomNumber);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mCheckRevStatusTask = null;
                if (e == null) {
                    if (s.equals("")) {
                        cancelCheckRevStatusTimer();
                        cancelConnectTimer();
                        cancelGetProviderOnesignalTimer();
                        cancelGetPatientOnesignalTimer();

                        if (mProviderBusyDialog == null && mTabletProviderBusyDialog == null
                                && !hasEnteredInCall && !hasEndConnectionClicked && !isRemoteJoin) {
                            if (!isFinishing() && !isConnecting) {
                                showProviderBusyDialog();
                            } else if (!isFinishing() && isConnecting && !hasEndConnectionClicked && !isRemoteJoin) {
                                unableConnect();
                            }
                        }
                    }
                    Log.e(CONSULT, "CheckRevStatusTask success");
                } else {
                    toastShortInfo("CheckRevStatusTaskFailed");
                }
            }
        }.execute();
    }

    private AsyncTask GetOnlineRoomNumberAsyncTask(final String user_id) {
        return new AsyncTask<Void, Void, String>() {
            Exception e;

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return WebService.getInstance().GetOnlineRoomNumber_Patient(user_id);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                GetOnlineRoomNumberTask = null;
                if (s.isEmpty()) {

                    if (call_type == PAT_TO_PAT) {
                        cancelhasRecipientLeftTimer();
                        hangUpProcess(false);
                    } else {

                        cancelIncomingCallTimer();

                        if (!isInComingCallAnswered) {
                            hangUpFunction(true);
                        } else {
                            hangUpFunction(false);
                        }

                        if (hasEnteredInCall == true) {
                            showProviderLeftDialog();
                        } else {
                            clearNotification();
                            cancelGetActiveGuestCountTimer();
                            setResult(33);
                            postFinish(getString(R.string.call_ended));
                        }

                    }
                }
            }
        }.execute();
    }

    private AsyncTask createCallLogRoomsTask(final String roomNum) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().create_Call_Log_Rooms(roomNum, appointmentIdentifier, providerId, userId, orgCode);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {
                        setPatientDeviceStatus(userId, STATUS_BUSY, getLoginInfo2().getOneSignalUserId(), false);
                        //registerAsRoomGuest();
                        Log.e(CONSULT, "Create call log room Task success");
                    } else if (integer == -1) {
                        endConnection();
                        toastShortInfo("Create Call Log Room Task Error");
                    }
                } else {
                    endConnection();
                    toastShortInfo("Create Call Log Room Task Failed");
                }
            }
        }.execute();
    }


    private AsyncTask createCallLogRoomsPatTask(final String roomNum) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().create_Call_Log_Rooms_Pat(roomNum, providerId, userId, orgCode);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {
                        setPatientDeviceStatus(userId, STATUS_BUSY, getLoginInfo2().getOneSignalUserId(), false);
                        //registerAsRoomGuest();
                        Log.e(CONSULT, "Create call log room Task success");
                    } else if (integer == -1) {
                        endConnection();
                        toastShortInfo("Create Call Log Room Task Error");
                    }
                } else {
                    endConnection();
                    toastShortInfo("Create Call Log Room Task Failed");
                }
            }
        }.execute();
    }


    private AsyncTask MakeCall2PatientTask(final String userId) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    if (BuildConfig.FLAVOR.equals("cybermedi8")) {
                        return WebService.getInstance().MakeCall2Patient(orgCode, providerId, userId, roomNumber, 2);
                    } else {
                        return WebService.getInstance().MakeCall2Patient(orgCode, providerId, userId, roomNumber, 2);
                    }
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                mMakeCall2PatientTask = null;
                if (e == null) {
                    if (integer >= 1) {
                        if (!isRemoteJoin) {
                            //Calling user
                            joinChannel();
                            binding.endConnectionBtn.setVisibility(View.VISIBLE);
                            getPatientOnesignalIndicator();
                        } else {
                            //Adding family
                            binding.loaderAddProvider.setVisibility(View.VISIBLE);
                            binding.btnEndInvitation.setVisibility(View.VISIBLE);
                            binding.btnFinish.setVisibility(View.INVISIBLE);
                            removeAddUserStatus(END_INVITATION);
                        }
                        Log.e(CONSULT, "MakeCall2PatientTask success");
                    } else if (integer == -1) {
                        binding.txtAddProviderStatus.setText(getString(R.string.consultation_family_not_pick_up));
                        binding.loaderAddProvider.setVisibility(View.VISIBLE);
                        removeAddUserStatus(7000);

                        Log.e(CONSULT, "MakeCall2PatientTask error");
//                        toastShortInfo("Make Call To Provider Error");
                    }
                } else {

                    endConnection();
                    Log.e(CONSULT, "MakeCall2PatientTask failed");
                    toastShortInfo("Make Call To Provider Failed");
                }
            }
        }.execute();
    }

    private AsyncTask MakeCall2ProviderTask() {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    if (BuildConfig.FLAVOR.equals("cybermedi8")) {
                        return WebService.getInstance().MakeCall2Provider_Android(orgCode, providerId, userId, roomNumber, 2);
                    } else {
                        return WebService.getInstance().MakeCall2Provider_Android(orgCode, providerId, userId, roomNumber, 2);
                    }
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    mMakeCall2ProviderTask = null;
//                    joinChannel();
                    if (integer >= 1) {
                        //加入频道
                        joinChannel();
                        binding.endConnectionBtn.setVisibility(View.VISIBLE);

//                        if(callFromMyAppt){
//                            switch(apptStatus){
//                                case "Not Seen":
//                                    markApptStatus(NOT_SEEN_STATUS);
//                                    break;
//                                case "Arrived":
//                                    markApptStatus(ARRIVED_STATUS);
//                                    break;
//                            }
//                        }
//                        createAppointment();


                        if (!isSkipped) {
                            SetApptVitalIntakeV4Task(orgCode, appointmentIdentifier, chiefComplaint, temperature, pulse, BPH, BPL, height, weight, allergies, medHx, socialHx, "", "", phone_num);
//                            SavePatVitalsTask(userId, "", orgCode, appointmentAccount, chiefComplaint, medHx, socialHx, allergies, temperature, pulse, weight, height, BPH, BPL, "");

                        }
//                        String paymentMethod = "0";
                        if (paymentType == DOCTOR_PAYMENT) {
                            //setRoomChargeCC();
                            setApptPaymentCard();
//                            paymentMethod = "1";
                        }
//                        else if (paymentType == DOCTOR_PAYPAL) {
//                            paymentMethod = "3";
//                        } else if (paymentType == DOCTOR_INSURANCE) {
//                            paymentMethod = "2";
//                        }

//                        markApptPaymentMethod(paymentMethod);

                        getProviderOnesignalIndicator(roomNumber);
                        Log.e(CONSULT, "MakeCall2ProviderTask success");
                    } else if (integer == -1) {
                        endConnection();
                        Log.e(CONSULT, "MakeCall2ProviderTask error");
                        toastShortInfo("Make Call To Provider Error");
                    }
                } else {
                    endConnection();
                    Log.e(CONSULT, "MakeCall2ProviderTask failed");
                    toastShortInfo("Make Call To Provider Failed");
                }
            }
        }.execute();
    }

    private AsyncTask createAppointmentTask() {
        return new AsyncTask<Void, Void, Appointment>() {
            Exception e;

            @Override
            protected Appointment doInBackground(Void... params) {
                try {
                    return WebService.getInstance().create_appointment_on_EMR_Android_v2(roomNumber,
                            userId, orgCode, providerId, apptTime);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Appointment appointment) {
                super.onPostExecute(appointment);
                mCreateAppointmentTask = null;
                if (e == null) {
                    if (appointment != null) {
                        Log.e(CONSULT, "createAppointmentTask success");
                        appointmentIdentifier = appointment.getProperty(1).toString();
                        appointmentAccount = appointment.account;
                        Log.d("APPTIDDEBUG", appointmentIdentifier);

//                        getProviderOnesignalIndicator(roomNumber);
                        createCallLogRooms(roomNumber);
                    } else {
                        endConnection();
                        Log.e(CONSULT, "createAppointmentTask failed");
                    }
                } else {
                    endConnection();
                    Log.e(CONSULT, "createAppointmentTask error");
                    toastShortInfo(e.getMessage());
                }
            }
        }.execute();
    }

    private AsyncTask markAppointmentStatusAsyncTask(final String status) {
        return new AsyncTask<Void, Void, Integer>() {

            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().Mark_appointment_status(orgCode, roomNumber, status);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {
                        if (getTabletMode()) {
                            hangUpProcess(true);
                        } else if (status == CANCELLED) {
                            hangUpProcess(true);
                        } else {
                            GetProviderWaitingRoomPatNumber();


//                            String waitingRoomMsg = "You are placed in the waiting room. The doctor will call you back soon";

//                            if (!waitingRoomCount.equals("-1")) {
//                                waitingRoomMsg = "You are placed in the waiting room. There are " + waitingRoomCount + "patient(s) ahead of you. The doctor will call you back soon";
//                            }

//                            showMakeAppointMentResult(waitingRoomMsg);

//                            if (userInputBusyProvider != null) {
//                                String push_msg = "Patient " + getLoginInfo2().getUserInfo().getFirstName() + " " +
//                                        getLoginInfo2().getUserInfo().getLastname()
//                                        + " (id: " + userId + ") was not able to reach you and entered your CDOC waiting room.\n\n"
//                                        + (isSkipped ? "" : "Reason for Appointment:\n" + chiefComplaint + "\n\n")
//                                        + (userInputBusyProvider.equals("") ? "" : "Patient Message: " + userInputBusyProvider);
//
//                                String message = "This message is from CDOC. " + push_msg;
//
//                                NotifyProvider(message, push_msg);
//                            }
                        }
                        Log.e(CONSULT, "markAppointmentStatusAsyncTask success");
                    } else {
                        hangUpProcess(true);
                        Log.e(CONSULT, "markAppointmentStatusAsyncTask failed");
                    }
                } else {
                    Log.e(CONSULT, "markAppointmentStatusAsyncTask error");
                }
            }
        }.execute();
    }

    private void GetProviderWaitingRoomPatNumber() {

        OnPostExecute ope = result -> {
            String waitingRoomCount = result.toString();

            String waitingRoomMsg = getString(R.string.video_in_waiting_room);

            if (!waitingRoomCount.equals("-1")) {
                waitingRoomMsg = getString(R.string.video_in_waiting_room_with_other_pat, Integer.valueOf(waitingRoomCount));
            }

            showMakeAppointMentResult(waitingRoomMsg);

            if (userInputBusyProvider != null) {
                String push_msg = "Patient " + getLoginInfo2().getUserInfo().getFirstName() + " " +
                        getLoginInfo2().getUserInfo().getLastname()
                        + " (id: " + userId + ") was not able to reach you and entered your CDOC waiting room.\n\n"
                        + (isSkipped ? "" : "Reason for Appointment:\n" + chiefComplaint + "\n\n")
                        + (userInputBusyProvider.equals("") ? "" : "Patient Message: " + userInputBusyProvider);

                String message = "This message is from CDOC. " + push_msg;

                NotifyProvider(message, push_msg);
            }
        };
        WebService.webServiceAsyncTask(WebServiceID.getProviderWaitingRoomPatNumber_From_EMR, ope, "1", appointmentIdentifier, orgCode, providerId);
    }


    private AsyncTask getOnlineRoomNumPatientAsyncTask(final String userId) {
        return new AsyncTask<Void, Void, String>() {

            Exception e;

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return WebService.getInstance().GetOnlineRoomNumber_Patient(userId);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (e == null) {
                    if (!TextUtils.isEmpty(s)) {
                        roomNumber = s;
                        setPatientDeviceStatus(userId, STATUS_BUSY, getLoginInfo2().getOneSignalUserId(), false);
                        Log.e(CONSULT, "answer getOnlineRoomNumPatient success");
                    } else {
                        Log.e(CONSULT, "answer getOnlineRoomNumPatient failed");
                    }
                }
            }
        }.execute();
    }

    private AsyncTask SetRoomChargeCCTask(final String room_number, final String appt_id, final String org_code, final String cc_idx, final String cvv_code) {
        return new AsyncTask<Void, Void, String>() {

            Exception e;

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    return WebService.getInstance().SetRoomChargeCC(room_number, appt_id, org_code, cc_idx, cvv_code);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equals("1")) {

                } else {
                    endConnection();
                    Log.e(CONSULT, "setroomcc failed");
                }
            }
        }.execute();
    }

    private AsyncTask MarkApptPaymentMethodTask(final String appt_id, final String paymentMethod) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    return WebService.getInstance().MarkApptPaymentMethod(orgCode, appt_id, paymentMethod);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {
                        Log.e(CONSULT, "answer MarkApptPaymentMethod success");
                    } else {
                        Log.e(CONSULT, "answer MarkApptPaymentMethod failed");
                    }
                }
            }
        }.execute();
    }

    //Video Workflow (Outgoing): 3. Set Patient Device Status
    //Video Workflow (Incoming): 2. SetPatient DeviceStatus
    private void setPatientDeviceStatus(final String userId, int status, String deviceId, final boolean isHangup) {

        OnPostExecute ope = result -> {
            if (result.toString().equals("1")) {
                if (!isHangup) {
                    // binding.linearAudiotrouble.setVisibility(View.VISIBLE);
                    registerAsRoomGuest();
                }
            } else {
                endConnection();
            }
        };

        WS.setPatientDeviceStatus(status, ope);
    }

    private AsyncTask notifyPatientAppStatusAsycnTask(final String userId, final String roomNum) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    if (BuildConfig.FLAVOR.equals("cybermedi8")) {
                        return WebService.getInstance().notify_patient_app_devices(userId, "Call Answered", roomNum, "nil", 2);
                    } else {
                        return WebService.getInstance().notify_patient_app_devices(userId, "Call Answered", roomNum, "nil", 2);
                    }
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {
                        Log.e(CONSULT, "answer notifyPatientAppStatus success");
                    } else {
                        Log.e(CONSULT, "answer notifyPatientAppStatus failed");
                    }
                }
            }
        }.execute();
    }

    private AsyncTask SetApptVitalIntakeV4Task(final String org_code, final String appt_id, final String chief_complaint, final String temperature,
                                               final String pulse, final String BPH, final String BPL, final String height, final String weight,
                                               final String allergies, final String medHx, final String socialHx, final String LDN_Initial,
                                               final String LDN_Refill, final String phone_num) {

        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... params) {

                try {
                    return WebService.getInstance().SetApptVitalIntakeV4(org_code, appt_id, chief_complaint, temperature, pulse, BPH, BPL, height, weight, medHx, socialHx, allergies, LDN_Initial, LDN_Refill, phone_num);

                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

            }
        }.execute();

    }

    private AsyncTask SavePatVitalsTask(final String userId, final String entry_user_id, final String org_code, final String account, final String chief_complaint, final String medHx, final String socialHx,
                                        final String allergies, final String temperature, final String pulse, final String weight,
                                        final String height, final String BPH, final String BPL, final String spo2) {

        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... params) {

                try {
                    return WebService.getInstance().SavePatVitals(userId, entry_user_id, org_code, account, chief_complaint, medHx, socialHx, allergies, temperature, pulse, weight, height, BPH, BPL, spo2);

                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

            }
        }.execute();

    }

    private AsyncTask NotifyProviderAsyncTask(final String message, final String push_msg) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().Notify_Provider(orgCode, providerId, message, push_msg);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {

                        NotifyPatient("Hello " + getLoginInfo2().getUserInfo().getFirstName() + ",\n\n" + "Please review your appointment details here.\n\n"
                                + "Provider: " + docName + "\n" + "Date and Time: " + apptTime + ".\n\n"
                                + "Please login with the CDoc app before the specified date and time to speak with the provider.\n\n"
                                + "If you have any questions, please feel free to contact us at 732-800-0020.");

                        Log.d("NotifyProvider", "sent");
                        Log.e(CONSULT, "NotifyProviderAsyncTask success");

                    } else {
                        Log.d("NotifyProvider", "error");
                        Log.e(CONSULT, "NotifyProviderAsyncTask failed");
                    }
                } else {
                    Log.d("NotifyProvider", "error");
                    Log.e(CONSULT, "NotifyProviderAsyncTask error");
                }
            }
        }.execute();
    }

    private AsyncTask NotifyPatientAsyncTask(final String message) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().Notify_Patient(getLoginInfo2().getAccount(), message);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {
                        Log.d("Notify_Patient", "sent");
                        Log.e(CONSULT, "Notify_Patient success");

                    } else {
                        Log.d("Notify_Patient", "error");
                        Log.e(CONSULT, "Notify_Patient failed");
                    }
                } else {
                    Log.d("Notify_Patient", "error");
                    Log.e(CONSULT, "Notify_Patient error");
                }
            }
        }.execute();
    }


    private AsyncTask getGuestCountAsyncTask(final int uid) {
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return WebService.getInstance().GetActiveGuestsCount(roomNumber, roomGuestId);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                mGetActiveGuestCountTaskRemove = null;
                if (integer != null) {
                    if (integer != 0) {
//                        removeRemoteVideo(uid);
                        doRemoveRemoteUi(uid);
                    } else {
                        onRemoteUserLeft();
                    }
                }
            }
        }.execute();
    }


    private AsyncTask getPatientFamilyListAsync(final androidx.appcompat.app.AlertDialog b) {
        return new AsyncTask<Void, Void, VectorFamily>() {
            Exception e;

            @Override
            protected VectorFamily doInBackground(Void... params) {
                try {
                    return WebService.getInstance().GetFamilyList(getLoginInfo2().getAccount());
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(VectorFamily patient_family) {
                getPatientListTask = null;
                if (e == null) {
                    searchPatientAdapter.appendList(patient_family);

                }
            }
        }.execute();
    }


    private AsyncTask getPatientOnlineStatus(final String userId, final boolean isMakeCall) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().getPatientOnlineStatus(userId);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                mGetPatientOnlineStatusTask = null;
                if (e == null) {
                    Log.d("patientonlinestatus", String.valueOf(integer));
                    if (integer == 1) {
                        if (isMakeCall) {
                            makeCall2Patient(userId);
                        } else {
                            setPatientOnlineRoom(recipientUserId, STATUS_ON_LINE);
                        }
                    } else {
                        Toast.makeText(VideoCallActivity.this, R.string.patient_is_not_online, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }.execute();
    }


    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;

                if (mUidsList.size() == 3) {
                    useDefaultLayout = false;
                }

                Object target = mUidsList.remove(uid);
                Log.d("uidListdebugremove", String.valueOf(mUidsList.size()));

                if (target == null) {
                    return;
                }

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                    if (bigBgUid == uid) {
                        //Bugfix: when other side crashes and rejoins,
                        // may crash due to getExceptedUid does not exist anymore.
                        //Solution to reset everything
                        mSmallVideoViewAdapter = null;
                    }
                }

                //Issue with getting new bigBgUid if the original bigBgUid was cleared.
                if (mUidsList.size() == 2) {
                    List<Integer> keys = new ArrayList<>(mUidsList.keySet());
                    if (keys.get(1) != null) {
                        bigBgUid = keys.get(1);
                    }
                }

                if (useDefaultLayout || uid == bigBgUid) {
                    switchToDefaultVideoView();
                } else {
                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }

    public static VideoCallActivity getInstance() {
        return videoCallActivity;
    }

    private void postFinish(String callStatus) {
        binding.callEndedTxt.setVisibility(View.VISIBLE);
        binding.callEndedTxt.setText(callStatus);
        binding.callCancelledTxt.setVisibility(View.VISIBLE);
        binding.callCancelledTxt.setText(callStatus);

        SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        //Reset push notification indicator to false
        SetPatientOnesignalIndicator(false);

        playEndDing();
        if (connectTimer != null)
            connectTimer.cancel();
        cancelIncomingCallTimer();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopMusic();
                finish();
                if (isTaskRoot()) {
                    CDoctor2Application.application.shutDown();
                }
            }
        }, 2000);
    }



    public void clearNotification() {
        NotificationManager oldNoti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        oldNoti.cancel(0);
    }

    private void cancelCheckRevStatusTimer() {
        if (CheckRevStatusTimer != null) {
            CheckRevStatusTimer.cancel();
            CheckRevStatusTimer = null;
        }
    }

    private void cancelGetProviderOnesignalTimer() {
        if (getProviderOnesignalTimer != null) {
            getProviderOnesignalTimer.cancel();
            getProviderOnesignalTimer = null;
        }
    }

    private void cancelGetPatientOnesignalTimer() {
        if (getPatientOnesignalTimer != null) {
            getPatientOnesignalTimer.cancel();
            getPatientOnesignalTimer = null;
        }
    }

    public void cancelhasCallLeftTimer() {
        if (hasCallerLeftTimer != null) {
            hasCallerLeftTimer.cancel();
            hasCallerLeftTimer = null;
        }
    }

    public void cancelhasRecipientLeftTimer() {
        if (hasRecipientLeftTimer != null) {
            hasRecipientLeftTimer.cancel();
            hasRecipientLeftTimer = null;
        }
    }

    public void cancelIncomingCallTimer() {
        if (incomingHangupTimer != null) {
            incomingHangupTimer.cancel();
            incomingHangupTimer = null;
        }
    }

    private void cancelConnectTimer() {
        if (connectTimer != null) {
            connectTimer.cancel();
            connectTimer = null;
        }
    }

    private void cancelGetActiveGuestCountTimer() {
        if (getActiveGuestCount != null) {
            getActiveGuestCount.cancel();
            getActiveGuestCount = null;
        }
    }

    private void cancelTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        videoCallActivity = null;

        stopMusic();
        cancelTimerTask();
        unRegistHeadSetReceiver();
        if (!providerIsBusy) {
            leaveChannel();
            RtcEngine.destroy();

            mRtcEngine = null;
            if (mGenerateOnlineRoomNumberTask != null) {
                mGenerateOnlineRoomNumberTask.cancel(true);
                mGenerateOnlineRoomNumberTask = null;
            }
            if (mCreateCallLogRoomsTask != null) {
                mCreateCallLogRoomsTask.cancel(true);
                mCreateCallLogRoomsTask = null;
            }
            if (mRegisterAsRoomGuestTask != null) {
                mRegisterAsRoomGuestTask.cancel(true);
                mRegisterAsRoomGuestTask = null;
            }
            if (mMakeCall2ProviderTask != null) {
                mMakeCall2ProviderTask.cancel(true);
                mMakeCall2ProviderTask = null;
            }
            if (mCreateAppointmentTask != null) {
                mCreateAppointmentTask.cancel(true);
                mCreateAppointmentTask = null;
            }
            if (markAppointmentStatusTask != null) {
                markAppointmentStatusTask.cancel(true);
                markAppointmentStatusTask = null;
            }
            if (mGetOnlineRoomNumPatient != null) {
                mGetOnlineRoomNumPatient.cancel(true);
                mGetOnlineRoomNumPatient = null;
            }
            if (mNotifyPatientAppStatusTask != null) {
                mNotifyPatientAppStatusTask.cancel(true);
                mNotifyPatientAppStatusTask = null;
            }
            cancelGetActiveGuestCountTimer();
            cancelGetProviderOnesignalTimer();
            cancelGetPatientOnesignalTimer();
            cancelIncomingCallTimer();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRtcEngine != null)
            mRtcEngine.enableVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRtcEngine != null)
            mRtcEngine.disableVideo();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                return false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////AGORA STUFF////////////////////////////////////////////

    private void registHeadSetReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headSetBroadCastReceiver, intentFilter);
    }

    private void unRegistHeadSetReceiver() {
        if (headSetBroadCastReceiver != null)
            unregisterReceiver(headSetBroadCastReceiver);
    }


    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1

    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            //mRtcEngine = RtcEngine.create(getBaseContext(), "3eccfb03072c46d1815442d5c34336b9", mRtcEventHandler);
             mRtcEngine = RtcEngine.create(getBaseContext(), CDoctor2Application.application.getAgoraAppId(), mRtcEventHandler);
            mRtcEngine.enableAudioVolumeIndication(1000, 3, false);
            setupVideoProfile();         // Tutorial Step 2
            setupLocalVideo();           // Tutorial Step 3
            enableLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {
        if (mRtcEngine != null) {
            mRtcEngine.enableVideo();
            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VD_640x360, FRAME_RATE_FPS_15
                    , STANDARD_BITRATE, ORIENTATION_MODE_ADAPTIVE));
        }

    }

    // Tutorial Step 3
    private void setupLocalVideo() {

        SurfaceView surfaceV =  new SurfaceView (getBaseContext());
        if (mRtcEngine != null)
            mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_FIT, 0));
        surfaceV.setZOrderOnTop(false);
        surfaceV.setZOrderMediaOverlay(false);

        mUidsList.put(0, surfaceV); // get first surface view

        binding.gridVideoViewContainer.initViewContainer(getApplicationContext(), 0, mUidsList); // first is now full view
    }


    // Tutorial Step 4
    private void joinChannel() {
        // if you do not specify the uid, we will generate the uid for you
        getAgoraToken(roomNumber);
       // mRtcEngine.joinChannel(null, roomNumber, "Extra Optional Data", 0);
    }

    private void showstatusAlert() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (cameraOffStatus.equals("true") && mutedStatus.equals("true")){
                    binding.txtProviderStatusTxt.setText("");
                    binding.txtProviderStatusTxt.setVisibility(View.INVISIBLE);
                }
                else if (cameraOffStatus.equals("false")){

                    if (mutedStatus.equals("false")){
                        binding.txtProviderStatusTxt.setText(R.string.other_party_camera_and_audio_are_off);
                        binding.txtProviderStatusTxt.setVisibility(View.VISIBLE);
                    }else {
                        binding.txtProviderStatusTxt.setText(R.string.other_party_camera_is_off);
                        binding.txtProviderStatusTxt.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (mutedStatus.equals("false")){
                        binding.txtProviderStatusTxt.setText(R.string.other_party_audio_is_off);
                        binding.txtProviderStatusTxt.setVisibility(View.VISIBLE);
                    }

                }
            }
        });



    }


    private void getAgoraToken(String roomNumber) {
        VideoCallManager callManager = new VideoCallManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                if (TextUtils.isEmpty(agoraToken)) {
                    agoraToken = ((ResponseToken) data).getAgoraToken();
                    if (TextUtils.isEmpty(agoraToken)) {
                        finish();
                        Toast.makeText(VideoCallActivity.this, R.string.unable_to_connect_call_please_try_again_later, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (mRtcEngine != null) {
                        ChannelMediaOptions options = new ChannelMediaOptions();
// Set the user role to BROADCASTER or AUDIENCE according to the scenario
                        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
// In the live broadcast scenario, set the channel profile to COMMUNICATION (live broadcast scenario)
                        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
                        mRtcEngine.setParameters("{\"che.audio.enable.ns\": true}");
                        mRtcEngine.joinChannel(agoraToken, roomNumber,0, options);
                        mRtcEngine.setAudioProfile(4, 3);

                    }
                }
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                Log.e("Agora App Id", errorResponse);
            }
        }, this);
        RequestToken requestToken = new RequestToken("appID_Live", roomNumber, "0");
        callManager.getAppToken(requestToken);
    }

    //Step 5
    private void setupRemoteVideo(int uid) {
        Log.d("uidListdebug", String.valueOf(mUidsList.size()));
        Log.d("uidlistdebugattach", String.valueOf(uid));

        if (!mUidsList.containsKey(uid)) {
            boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;
            boolean initialSetup = false;
            if (mUidsList.size() == 1) {
                useDefaultLayout = false;
                initialSetup = true;
            } else if (mUidsList.size() == 2) {
                useDefaultLayout = true;
            } else if (mUidsList.size() > 4) {
                useDefaultLayout = false;
            }

            SurfaceView surfaceV = /*RtcEngine.CreateRendererView(getApplicationContext());*/ new SurfaceView (getBaseContext());
            mUidsList.put(uid, surfaceV);


            surfaceV.setZOrderOnTop(!useDefaultLayout);
            surfaceV.setZOrderMediaOverlay(true);
//            surfaceV.setZOrderOnTop(false);
//            surfaceV.setZOrderMediaOverlay(false);

            mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_FIT, uid));

            if (useDefaultLayout) {
                switchToDefaultVideoView();
            } else {
                int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
                Log.d("uidlistdebugattach", String.valueOf(bigBgUid));
                switchToSmallVideoView(bigBgUid);
                if (initialSetup) {
                    //Workaround Bug: Issue during the 1 to 1 call
                    //The local video starts in the middle of the screen
                    switchToDefaultVideoView();
                    switchToSmallVideoView(bigBgUid);
                }
            }

        }

//        }
    }

    private void switchToDefaultVideoView() {
        Log.d("ggdebug", "switchtodefault");
        if (mSmallVideoViewDock != null) {
            mSmallVideoViewDock.setVisibility(View.GONE);
        }
        binding.gridVideoViewContainer.initViewContainer(getApplicationContext(), new Random().nextInt(61), mUidsList);

        mLayoutType = LAYOUT_TYPE_DEFAULT;
    }

    private void switchToSmallVideoView(int bigBgUid) {
        Log.d("ggdebug", "switchtosmall");
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(bigBgUid, mUidsList.get(bigBgUid));
        binding.gridVideoViewContainer.initViewContainer(getApplicationContext(), bigBgUid, slice);

        bindToSmallVideoView(bigBgUid);

        mLayoutType = LAYOUT_TYPE_SMALL;

//        requestRemoteStreamType(mUidsList.size());
    }

    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private void bindToSmallVideoView(int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        boolean twoWayVideoCall = mUidsList.size() == 2;

        RecyclerView recycler = findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, new Random().nextInt(61), exceptUid, mUidsList, new VideoViewEventListener() {
                @Override
                public void onItemDoubleClick(View v, Object item) {
                    switchToDefaultVideoView();
                }
            });
            mSmallVideoViewAdapter.setHasStableIds(true);
        }
        recycler.setHasFixedSize(true);

        Log.d("bindToSmallVideoView ", twoWayVideoCall + " " + (exceptUid & 0xFFFFFFFFL));

        if (twoWayVideoCall) {
            recycler.setLayoutManager(new RtlLinearLayoutManager(this, RtlLinearLayoutManager.HORIZONTAL, false));
        } else {
            recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        recycler.addItemDecoration(new SmallVideoViewDecoration());
        recycler.setAdapter(mSmallVideoViewAdapter);

        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        if (!create) {
            mSmallVideoViewAdapter.setLocalUid(new Random().nextInt(61));

            //Workaround fix: Overlay issues, smaller local and remote views get covered by the bgView.
            for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
                if (entry.getKey() != exceptUid) {
//                    Log.d("videobug","exceptid:" + exceptUid + " key:" + entry.getKey());
                    SurfaceView sv = entry.getValue();
                    sv.setZOrderMediaOverlay(true);
                    entry.setValue(sv);
                }
            }

            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, null, null);
        }
        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }

    // Tutorial Step 7
    private void onRemoteUserLeft() {
        if (mRatingDialog == null && !hasCallEnded) {
            showProviderLeftDialog();
        }
    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        SurfaceView surfaceView = mUidsList.get(uid);
        if (surfaceView != null) {

            surfaceView.setZOrderMediaOverlay(false);
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    // Tutorial Step 6
    private void leaveChannel() {
        if (mRtcEngine != null)
            mRtcEngine.leaveChannel();
    }

    private void endAgoraCall() {
        if (mRtcEngine != null) {
            mRtcEngine.disableAudio();
            mRtcEngine.disableVideo();
        }
    }


    private void switchCamera() {
        if (mRtcEngine != null)
            mRtcEngine.switchCamera();

    }

    private void muteVoice(boolean mute) {
        if (mRtcEngine != null)
            mRtcEngine.muteLocalAudioStream(mute);
    }


    private void muteLocalVideo(boolean mute) {
        mRtcEngine.muteLocalVideoStream(mute);
        SurfaceView surfaceView = mUidsList.get(localUid);
        surfaceView.setZOrderMediaOverlay(!mute);
        surfaceView.setBackgroundResource(mute ? R.drawable.placeholder_video_mute : 0);
//        surfaceView.setVisibility(mute ? View.GONE : View.VISIBLE);

    }

    private void enableLog() {
        if (PreferenceManager.getDefaultSharedPreferences(
                this).getBoolean(KEY_ENABLE, false)) {
            if (mRtcEngine != null) {
                File file = new File(getApplicationContext().getFilesDir(), "cdoc.txt");
                if (file.exists()) {
                    mRtcEngine.setLogFile(file.getAbsolutePath());
                } else {
                    try {
                        file.createNewFile();
                        mRtcEngine.setLogFile(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_end_invitation:
                onEndInvitation(true);
                break;
            case R.id.btnMute:
                if (binding.btnMute.isSelected()) {
                    binding.btnMute.setSelected(false);
                } else {
                    binding.btnMute.setSelected(true);
                }
                muteVoice(binding.btnMute.isSelected());
                break;

            case R.id.btnFinish:
                if (!btnFinishClicked) {
                    btnFinishClicked = true;
                    binding.txtCallTimer.stop();
                    leavingRoomAsGuest(roomNumber, roomGuestId);
                    endAgoraCall();
                    if (isRemoteJoin) {
                        ratingDialog();
                    } else {
                        if (call_type == PAT_TO_PAT && recipientUserId != null) {
                            //Clear recipient patient room number if cancel call
                            unAnswerHangupPatientCall();
                        }
                        hangUpProcess(true);
                    }
                }
                break;
            case R.id.btn_in_coming_hangup:
                if (!btnIncomingHangup) {
                    btnIncomingHangup = true;
                    btnIncomingAnswer = true;
                    hangUpProcess(false);
                }
                break;
            case R.id.btn_in_coming_answer:
                if (!btnIncomingAnswer) {
                    btnIncomingHangup = true;
                    btnIncomingAnswer = true;
                    binding.callEndedTxt.setVisibility(View.VISIBLE);
                    binding.callEndedTxt.setText(getString(R.string.connecting_msg));
                    initialInCalling();
                }
                break;
            case R.id.back_btn_connecting:
            case R.id.back_btn_outgoing:
            case R.id.back_btn_callConnected:
            case R.id.back_btn_incoming:
            case R.id.endConnectionBtn:
                if (!hasEndConnectionClicked) {
                    if (call_type == PAT_TO_PAT) {
                        //Clear recipient patient room number if cancel call
                        unAnswerHangupPatientCall();
                    }
                    endConnection();
                }
                break;
            case R.id.btn_add_family:
                if (binding.loaderAddProvider.getVisibility() != View.VISIBLE) {
                    getPatientFamilyList();
                } else {
                    androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this).create();
                    alertDialog.setTitle(getString(R.string.consultation_please_wait));
                    alertDialog.setMessage(getString(R.string.consultation__please_wait_msg));
                    alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                break;
            case R.id.upload_image:
                if (appointmentIdentifier == null || appointmentIdentifier.isEmpty()) {
                    OnPostExecute ope = result -> {
                        appointmentIdentifier = result.toString();
                        openImageSelection();
                    };
                    WebService.webServiceAsyncTask(WebServiceID.get_appt_id_by_room_number, ope, roomNumber);
                }else {
                  openImageSelection();
                }

                break;
            case R.id.txt_rejoin:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                finishAffinity();
                                Intent intent = new Intent(VideoCallActivity.this, FragmentMainActivity.class);
                                intent.putExtra("callRejoin", true);
                                intent.putExtra("orgCode", orgCode);
                                intent.putExtra("providerId", providerId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);

                                //Yes button clicked
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //removeDroppedCallInfo();
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(VideoCallActivity.this);
                builder.setMessage(getString(R.string.home_rejoin_call)).setPositiveButton(getString(R.string.btn_join), dialogClickListener)
                        .setNegativeButton(getString(R.string.btn_no), dialogClickListener).show();
                break;
        }
    }

    private void openImageSelection() {
       /* if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){
                imageSelectionPopUp(this, appointmentIdentifier, 0, null);
            }else{
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }else {

        }*/
        Runnable success = () -> imageSelectionPopUp(this, appointmentIdentifier, 0, null);
        Runnable failure = () -> {
        };
        checkStoragePermission(this, success, failure);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_CAMERA_MIC || requestCode == MY_CAMERA_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //toastShortInfo("Permission enabled, please call again");
                initliazeView();
            } else {
                ErrorMessage.alertDialog(this, null, getString(R.string.please_enable_permission), new ErrorMessage.OkBtnCallBack() {
                    @Override
                    public void callback() {
                        finish();
                    }
                });
                //Toast.makeText(getContext(), "Please enable the permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

//////////////////////////////////End of Agora Stuff////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
}