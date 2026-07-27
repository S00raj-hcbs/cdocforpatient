package com.cybermed.cdoc_patient.Utility;
//
//import android.app.AlertDialog;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.ActivityInfo;
//import android.media.AudioManager;
//import android.media.SoundPool;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//
//import androidx.core.app.NotificationCompat;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewStub;
//import android.view.WindowManager;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RatingBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.cdfortis.datainterface.soap.OnPostExecute;
//import com.cdfortis.datainterface.soap.WebService;
//import com.cdfortis.datainterface.soap.WebServiceID;
//import com.cdfortis.datainterface.soap.model.Appointment;
//import com.cdfortis.datainterface.soap.model.DocInfo;
//import com.cdfortis.datainterface.soap.model.FamilyInfo;
//import com.cdfortis.datainterface.soap.model.VectorFamily;
import com.cybermed.cdoc_patient.BuildConfig;
//import com.cybermed.cdoc.R;
//import com.cybermed.cdoc.common.CommonAsyncTaskActivity;
//import com.cybermed.cdoc.common.videoui.GridVideoViewContainer;
//import com.cybermed.cdoc.common.videoui.RtlLinearLayoutManager;
//import com.cybermed.cdoc.common.videoui.SmallVideoViewAdapter;
//import com.cybermed.cdoc.common.videoui.SmallVideoViewDecoration;
//import com.cybermed.cdoc.common.videoui.UserStatusData;
//import com.cybermed.cdoc.common.videoui.VideoViewEventListener;
//import com.cybermed.cdoc.doctor.FamilyMemberAdapter;
//import com.cybermed.cdoc.view.MyAlertDialog;
//import com.isapanah.awesomespinner.AwesomeSpinner;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import io.agora.rtc.Constants;
//import io.agora.rtc.IRtcEngineEventHandler;
//import io.agora.rtc.RtcEngine;
//import io.agora.rtc.video.VideoCanvas;
//
//import static io.agora.rtc.Constants.USER_OFFLINE_DROPPED;
//
//public class VideoCallActivity extends CommonAsyncTaskActivity {
//    @BindView(R.id.btnFinish)
//    ImageButton mBtnHangup;
//    @BindView(R.id.btn_in_coming_hangup)
//    ImageButton btnInComingHangup;
//    @BindView(R.id.btn_in_coming_answer)
//    ImageButton btnInComingAnswer;
//    @BindView(R.id.endConnectionBtn)
//    ImageButton endConnectionBtn;
//    @BindView(R.id.btnMute)
//    ImageView btnMute;
//    @BindView(R.id.btnVideo)
//    ImageView mCameraBtn;
//    @BindView(R.id.txtName)
//    TextView tvName;
//    @BindView(R.id.tv_in_coming_doc_name)
//    TextView tvInComingName;
//    @BindView(R.id.in_coming_view)
//    RelativeLayout inComingView;
//    @BindView(R.id.connectingPage)
//    RelativeLayout connectingPage;
//    private static VideoCallActivity videoCallActivity;
//
//    private RtcEngine mRtcEngine;
//    private SoundPool soundPool;
//    private RatingBar ratingBarDialog;
//    private int music;
//    private int switchCam = 0;
//    private static final int END_INVITATION = 30000;
//
//    private AsyncTask mGenerateOnlineRoomNumberTask;
//    private AsyncTask mCreateCallLogRoomsTask;
//    private AsyncTask mRegisterAsRoomGuestTask;
//    private AsyncTask mMakeCall2ProviderTask;
//    private AsyncTask mMakeCall2PatientTask;
//    private AsyncTask mCreateAppointmentTask;
//    private AsyncTask markAppointmentStatusTask;
//    private AsyncTask mGetOnlineRoomNumPatient;
//    private AsyncTask mNotifyPatientAppStatusTask;
//    private AsyncTask mSetRoomChargeCCTask;
//    private AsyncTask mCheckRevStatusTask;
//    private AsyncTask mGetActiveGuestCountTask, mGetActiveGuestCountTaskRemove;
//    private AsyncTask mMarkApptPaymentMethodTask;
//    private AsyncTask NotifyProviderTask;
//    private AsyncTask NotifyPatientTask;
//    private AsyncTask getProviderOnesignalTask;
//    private AsyncTask setPatientOnesignalIndicatorTask;
//    private AsyncTask GetOnlineRoomNumberTask;
//    private AsyncTask getOnlineProviderNameTask;
//    private AsyncTask getOnlineProviderNameV2Task;
//    private AsyncTask getPatientListTask;
//    private AsyncTask mGetPatientOnlineStatusTask;
//    private AsyncTask getPatientOnesignalTask;
//
//    private EditText userInputDialogEditText;
//    @BindView(R.id.callEndedTxt)
//    TextView callEndedTxt;
//    @BindView(R.id.callCancelledTxt)
//    TextView callCancelledTxt;
//    @BindView(R.id.connectingTxt)
//    TextView connectingTxt;
//    @BindView(R.id.txt_add_provider_status)
//    TextView mCallUserStatusTxt;
//    @BindView(R.id.progress_loader)
//    ProgressBar progressSpinner;
//
//    private static final String CALLHANGUP = "Call Cancelled";
//    private static final String CALLEND = "Call Ended";
//    private static final String CALLANSWERED = "Call Answered";
//    private static final String CONSULT = "SdkConsult";
//    private String orgCode = "";
//    private String providerId;
//    private String recipientUserId;
//    private String userId;
//    private String roomNumber;
//    private String docName;
//    private String roomGuestId;
//    private String appointmentIdentifier, appointmentAccount, apptStatus;
//    private String cc_idx;
//    private String cvv_code;
//    private String weight;
//    private String height;
//    private String BPH;
//    private String BPL;
//    private String pulse;
//    private String temperature;
//    private String chiefComplaint;
//    private String allergies;
//    private String medHx;
//    private String socialHx;
//    private String phone_num;
//    private String userInputBusyProvider;
//    private String apptTime;
//    private String callerType = "";
//
//    private TimerTask timerTask;
//    private Timer timer;
//    private Timer connectTimer;
//    private static Timer incomingHangupTimer;
//    private static Timer hasCallerLeftTimer;
//    private static Timer hasRecipientLeftTimer;
//    private static Timer CheckRevStatusTimer;
//    private static Timer getProviderOnesignalTimer;
//    private static Timer getPatientOnesignalTimer;
//    private static Timer getActiveGuestCount;
//    private static Timer mRemoveAddUserStatusTimer;
//    private Handler handler;
//
//    private static final int WAITING_TIME = 30000;
//    private static final int IN_COMING = 0;
//    private static final int OUT_CALLING = 1;
//    private static final int DROPPED_CALL = 2;
//    private static final int PAT_TO_PAT = 3;
//    private int call_type;
//    private int paymentType;
//
//    private boolean isSkipped;
//    private boolean frontCam = true;
//    private boolean isRemoteJoin = false;
//    private boolean isOutGoingCall = false;
//    private boolean isInComingCallAnswered = false;
//    private boolean providerIsBusy = false;
//    private boolean hasEnteredInCall = false;
//    private boolean hasCallEnded = false;
//    private boolean btnIncomingAnswer = false;
//    private boolean btnIncomingHangup = false;
//    private boolean btnFinishClicked = false;
//    private boolean hasEndConnectionClicked = false;
//    private boolean isConnecting = false;
//    private boolean isSwitchingCamera = false;
//    private boolean callFromMyAppt = false;
//
//    @BindView(R.id.grid_video_view_container)
//    GridVideoViewContainer mGridVideoViewContainer;
//    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid
//    public int mLayoutType = LAYOUT_TYPE_DEFAULT;
//    public static final int LAYOUT_TYPE_DEFAULT = 0;
//    public static final int LAYOUT_TYPE_SMALL = 1;
//    private RelativeLayout mSmallVideoViewDock;
//    @BindView(R.id.loader_add_provider)
//    LinearLayout mCallUsersLayout;
//
//    private FamilyMemberAdapter searchPatientAdapter;
//    private RecyclerView patientList;
//    @BindView(R.id.btn_add_family)
//    ImageView mAddFamilyBtn;
//
//    private int localUid;
//    private AudioManager mAudioManager;
//
//    private AlertDialog.Builder mRatingDialog;
//    private MyAlertDialog mProviderLeftDialog;
//    private AlertDialog.Builder mProviderBusyDialog;
//    private AlertDialog mTabletProviderBusyDialog;
//
//    @BindView(R.id.btn_end_invitation)
//    ImageView mBtnEndInvitation;
//    private SwipeRefreshLayout refreshLayout;
//
//    private EditText mFamilyEmailInput;
//    private AwesomeSpinner mFamilyRelationshipSpinner;
//    private TextView mErrorRelationship, mErrorEmail;
//    private AsyncTask mGetFamilyList, mUpdateFamilyMemberTask;
//
//    private BroadcastReceiver headSetBroadCastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.hasExtra("state")) {
//                if (intent.getIntExtra("state", 0) == 0) {
//                    if (mRtcEngine != null)
//                        mRtcEngine.setEnableSpeakerphone(true);
//                } else if (intent.getIntExtra("state", 0) == 1) {
//                    if (mRtcEngine != null)
//                        mRtcEngine.setEnableSpeakerphone(false);
//                }
//            }
//        }
//    };
//
//    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
//        @Override
//        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("VIDEO2DEBUG2", String.valueOf(uid));
//                    removeAddUserStatus(0);
//                    isRemoteJoin = true;
//                    mRtcEngine.enableInEarMonitoring(true);
//
//
//                    //If getProviderOnesignal was not successful and this method was called
//                    connectingPage.setVisibility(View.GONE);
//                    if (getProviderOnesignalTimer != null)
//                        getProviderOnesignalTimer.cancel();
//                    cancelConnectTimer();
//                    /////
//
////                    tvName.setText("In session with: " + docName);
//
//                    if (getTabletMode()) {
//                        tvName.setText(getFacilityName());
//                    } else {
//                        tvName.setText("In session with: \nDr." + docName);
//                    }
//
//                    mCallUsersLayout.setVisibility(View.GONE);
//                    cancelCheckRevStatusTimer();
//                    cancelTimerTask();
//                    stopMusic();
//                    setupRemoteVideo(uid);
//
//                    //Reset push notification indicator to false
//                    SetPatientOnesignalIndicator(false);
//                    getActiveGuestCount();
//
//                }
//            });
//        }
//
//
//        @Override
//        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
////            Log.d("localvideodebug","onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
//            Log.d("videodebug", "onJoinChannelSuccess " + channel + " " + uid + " " + elapsed);
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (isFinishing()) {
//                        return;
//                    }
//
//                    SurfaceView local = mUidsList.remove(0);
//
//                    if (local == null) {
//                        return;
//                    }
//
//                    mUidsList.put(uid, local);
//                    localUid = uid;
//                }
//            });
//        }
//
//        @Override
//        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 7
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("reasondeubg", String.valueOf(reason));
//                    if (reason == USER_OFFLINE_DROPPED) {
//                        mCallUsersLayout.setVisibility(View.VISIBLE);
//                        mCallUserStatusTxt.setText(getString(R.string.consultation_connection_error));
//                        removeAddUserStatus(20000);
//                    }
//                    getGuestCount(uid);
//                }
//            });
//        }
//
//        @Override
//        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    onRemoteUserVideoMuted(uid, muted);
//                }
//            });
//        }
//    };
//
//    private void showLightScreen() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_call);
//        ButterKnife.bind(this);
//        showLightScreen();
//        setupAudioManager();
//        initCameraBtn();
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//        videoCallActivity = this;
//
//        Intent intent = getIntent();
//        userId = getLoginInfo2().getAccount();
//        call_type = intent.getIntExtra("type", IN_COMING);
//        paymentType = intent.getIntExtra("paymentType", DOCTOR_PAYMENT);
//        docName = intent.getStringExtra("docName");
//
//        SharedPreferences patPreferences = getSharedPreferences("VIDEOSHAREPREF_PAT", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = patPreferences.edit();
//
//        if (docName != null && !docName.isEmpty()) {
//            editor.putString("PROVIDER_NAME", docName);
//            editor.apply();
//        } else {
//            docName = patPreferences.getString("PROVIDER_NAME", "");
//        }
//
//        initViews();
//        initAgoraEngineAndJoinChannel();
//        registHeadSetReceiver();
//
//        if (call_type == OUT_CALLING || call_type == PAT_TO_PAT) {
//            initValues(intent);
//
//            callFromMyAppt = appointmentIdentifier != null;
//
//            initialOutCalling();
//            inComingView.setVisibility(View.GONE);
//
//        } else if (call_type == IN_COMING) {
//            getOnlineProviderNameV2();
//            roomNumber = intent.getStringExtra("roomNumber");
//            callerType = intent.getStringExtra("callType");
//            setVideoRoomNumber(roomNumber);
//            SetPatientOnesignalIndicator(true);
//            connectingPage.setVisibility(View.GONE);
//            playMusic();
//
//        } else if (call_type == DROPPED_CALL) {
//            inComingView.setVisibility(View.GONE);
//            connectingPage.setVisibility(View.GONE);
//            setPatientDeviceStatus(userId, STATUS_BUSY, getLoginInfo2().getOneSignalUserId(), true);
//
//            SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
//            roomNumber = preferences.getString("ROOM_NUMBER", "");
//            roomGuestId = preferences.getString("ROOM_GUEST_ID", "");
//            joinChannel();
//        }
//
//    }
//
//    private void setVideoRoomNumber(String roomNumber) {
//        SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("ROOM_NUMBER", roomNumber);
//        editor.apply();
//    }
//
//
//    private void initValues(Intent intent) {
//        orgCode = intent.getStringExtra("orgCode");
//        providerId = intent.getStringExtra("providerId");
//        recipientUserId = intent.getStringExtra("patientId");
//        appointmentIdentifier = intent.getStringExtra("appt_id");
//        apptStatus = intent.getStringExtra("apptStatus");
//        isSkipped = intent.getBooleanExtra("isskipped", false);
//        //Vitals and Chief Complaint
//        weight = intent.getStringExtra("weight");
//        height = intent.getStringExtra("total_height");
//        BPH = intent.getStringExtra("bPH");
//        BPL = intent.getStringExtra("bPL");
//        pulse = intent.getStringExtra("pulse");
//        temperature = intent.getStringExtra("temperature");
//        chiefComplaint = intent.getStringExtra("chief_complaint");
//        allergies = intent.getStringExtra("allergies");
//        medHx = intent.getStringExtra("medHx");
//        socialHx = intent.getStringExtra("socialHx");
//        phone_num = intent.getStringExtra("phone_num");
//        userInputBusyProvider = intent.getStringExtra("providerBusyMessage");
//        apptTime = intent.getStringExtra("apptTime");
//        cc_idx = intent.getStringExtra("cc_idx");
//        cvv_code = intent.getStringExtra("cvv_code");
//    }
//
//    private void initViews() {
//
//
//        mGridVideoViewContainer.setItemEventHandler((v, item) -> {
//            Log.d("griddebug", "onItemDoubleClick " + v + " " + item + " " + mLayoutType);
//
//            if (mUidsList.size() < 2) {
//                return;
//            }
//
//            UserStatusData user = (UserStatusData) item;
//            int uid = (user.mUid == 0) ? new Random().nextInt(61) : user.mUid;
//
//            if (mLayoutType == LAYOUT_TYPE_DEFAULT && mUidsList.size() != 1) {
//                switchToSmallVideoView(uid);
//            } else {
//                switchToDefaultVideoView();
//            }
//        });
//
//
//        if (getTabletMode()) {
//            tvName.setText("Calling " + getFacilityName());
//            tvInComingName.setText(getFacilityName());
//        } else {
//            tvName.setText("Calling Dr." + docName);
//            tvInComingName.setText(docName);
//        }
//    }
//
//    private void initCameraBtn() {
//        mCameraBtn.setOnClickListener(v -> {
//            if (!isSwitchingCamera) {
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        isSwitchingCamera = false;
//                    }
//                }, 1000);
//                isSwitchingCamera = true;
//                if (switchCam == 0) {
//                    switchCamera();
//                    frontCam = false;
//                    switchCam = 1;
//
//                    mCameraBtn.setImageResource(R.drawable.btn_switch_camera);
//                } else if (switchCam == 1) {
//                    switchCamera();
//                    frontCam = true;
//                    muteLocalVideo(true);
//                    switchCam = 2;
//                    mCameraBtn.setImageResource(R.drawable.btn_switch_mute_camera);
//
//                } else if (switchCam == 2) {
//                    muteLocalVideo(false);
//                    frontCam = true;
//                    switchCam = 0;
//                    mCameraBtn.setImageResource(R.drawable.btn_switch_camera);
//
//                }
//            }
//        });
//    }
//
//    private void setupAudioManager() {
//        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if (getTabletMode())
//            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
//    }
//
//    private void initialOutCalling() {
//        isOutGoingCall = true;
//        generateOnlineRoomNumber();
//        connectingPageTimeout();
//    }
//
//    private void initialInCalling() {
//        hasEnteredInCall = true;
//        isInComingCallAnswered = true;
//        inComingView.setVisibility(View.GONE);
//        stopMusic();
//        getOnlineRoomNumPatient(userId);
//    }
//
//    private void onEndInvitation(boolean onPressed) {
//        if (mRemoveAddUserStatusTimer != null) {
//            mRemoveAddUserStatusTimer.cancel();
//        }
//        if (onPressed) {
//            removeAddUserStatus(0);
//        } else {
//            mCallUsersLayout.setVisibility(View.VISIBLE);
//            mBtnHangup.setVisibility(View.VISIBLE);
//            mBtnEndInvitation.setVisibility(View.GONE);
//            mCallUserStatusTxt.setText(getString(R.string.consultation_family_not_pick_up));
//            removeAddUserStatus(7000);
//        }
//        setPatientOnlineRoom(recipientUserId, STATUS_ON_LINE);
//    }
//
//    @OnClick({R.id.btn_end_invitation, R.id.btnMute, R.id.btnFinish, R.id.btn_in_coming_hangup, R.id.btn_in_coming_answer
//            , R.id.endConnectionBtn, R.id.btn_add_family})
//    void click_event(View v) {
//        switch (v.getId()) {
//            case R.id.btn_end_invitation:
//                onEndInvitation(true);
//                break;
//            case R.id.btnMute:
//                if (btnMute.isSelected()) {
//                    btnMute.setSelected(false);
//                } else {
//                    btnMute.setSelected(true);
//                }
//                muteVoice(btnMute.isSelected());
//                break;
//            case R.id.btnFinish:
//                if (!btnFinishClicked) {
//                    btnFinishClicked = true;
//                    leavingRoomAsGuest(roomNumber, roomGuestId);
//                    endAgoraCall();
//                    if (isRemoteJoin) {
//                        ratingDialog();
//                    } else {
//                        if (call_type == PAT_TO_PAT && recipientUserId != null) {
//                            //Clear recipient patient room number if cancel call
//                            unAnswerHangupPatientCall();
//                        }
//                        hangUpProcess(true);
//                    }
//                }
//                break;
//            case R.id.btn_in_coming_hangup:
//                if (!btnIncomingHangup) {
//                    btnIncomingHangup = true;
//                    btnIncomingAnswer = true;
//                    hangUpProcess(false);
//                }
//                break;
//            case R.id.btn_in_coming_answer:
//                if (!btnIncomingAnswer) {
//                    btnIncomingHangup = true;
//                    btnIncomingAnswer = true;
//                    initialInCalling();
//                }
//                break;
//            case R.id.endConnectionBtn:
//                if (!hasEndConnectionClicked) {
//                    if (call_type == PAT_TO_PAT) {
//                        //Clear recipient patient room number if cancel call
//                        unAnswerHangupPatientCall();
//                    }
//                    endConnection();
//                }
//                break;
//            case R.id.btn_add_family:
//                if (mCallUsersLayout.getVisibility() != View.VISIBLE) {
//
//                    getPatientFamilyList();
//                } else {
//                    androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this).create();
//                    alertDialog.setTitle(getString(R.string.consultation_please_wait));
//                    alertDialog.setMessage(getString(R.string.consultation__please_wait_msg));
//                    alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    alertDialog.show();
//                }
//                break;
//        }
//    }
//
//    private String getFacilityName() {
//        switch (getLoginInfo2().getUserInfo().getService_code()) {
//            case "hnympc":
//                return "Heritage New York Medical";
//            case "heightsmedical":
//                return "Heights Medical";
//            case "sky":
//                return "Skylands Medical Group";
//            case "pansy":
//                return "CyberMed";
//            default:
//                return "CyberMed";
//        }
//    }
//
//    private void removeAddUserStatus(final int countDownTime) {
//        if (mRemoveAddUserStatusTimer != null) {
//            mRemoveAddUserStatusTimer.cancel();
//            mRemoveAddUserStatusTimer.purge();
//            mRemoveAddUserStatusTimer = null;
//        }
//        mRemoveAddUserStatusTimer = new Timer();
//        mRemoveAddUserStatusTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mCallUsersLayout.setVisibility(View.GONE);
//                        mBtnHangup.setVisibility(View.VISIBLE);
//                        mBtnEndInvitation.setVisibility(View.GONE);
//                        if (countDownTime == END_INVITATION) {
//                            onEndInvitation(false);
//                        }
//                        // Stuff that updates the UI
//                    }
//                });
//            }
//        }, countDownTime);
//    }
//
//    private void unAnswerHangupPatientCall() {
//
//        if (mGetPatientOnlineStatusTask == null) {
//            getPatientOnlineStatus(recipientUserId, false);
//        }
//
//    }
//
//    private void getPatientFamilyList() {
//        if (getPatientListTask == null) {
//            final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
//            LayoutInflater inflater = this.getLayoutInflater();
//            final View dialogView = inflater.inflate(R.layout.dialog_search_patient, null);
//            dialogBuilder.setView(dialogView);
//
//            dialogView.findViewById(R.id.searchInput).setVisibility(View.GONE);
//            dialogBuilder.setTitle("Please add a family member"); // change text
//            dialogBuilder.setCancelable(false);
//
//            dialogBuilder.setView(dialogView);
//            final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
//            b.setCanceledOnTouchOutside(true);
//            b.show();
//
//            Button closeBtn = dialogView.findViewById(R.id.closeBtn);
//            Button addBtn = dialogView.findViewById(R.id.addBtn);
//            closeBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    b.dismiss();
//                }
//            });
//            addBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    addFamily();
//                }
//            });
//
//            refreshLayout = dialogView.findViewById(R.id.refreshLayout);
//            patientList = dialogView.findViewById(R.id.patientList);
//            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    refreshLayout.setRefreshing(true);
//                    getPatientFamilyListAsync(b);
//                    refreshLayout.setRefreshing(false);
//                }
//            });
//
//            patientList.setLayoutManager(new LinearLayoutManager(this));
//            searchPatientAdapter = new FamilyMemberAdapter();
//            patientList.setAdapter(searchPatientAdapter);
//
//            if (getPatientListTask == null) {
//                getPatientListTask = getPatientFamilyListAsync(b);
//            }
//        }
//    }
//
//
//    private void addFamily() {
//        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_add_family, null);
//        mFamilyEmailInput = dialogView.findViewById(R.id.edit_email);
//        mFamilyRelationshipSpinner = dialogView.findViewById(R.id.spinner_relationship);
//        mErrorEmail = dialogView.findViewById(R.id.error_select_email);
//        mErrorRelationship = dialogView.findViewById(R.id.error_select_relationship);
//
//        ArrayAdapter<CharSequence> provincesAdapter = ArrayAdapter.createFromResource(this, R.array.family_relation, android.R.layout.simple_spinner_item);
//        mFamilyRelationshipSpinner.setAdapter(provincesAdapter, 0);
//        mFamilyRelationshipSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
//            @Override
//            public void onItemSelected(int position, String itemAtPosition) {
//                mErrorRelationship.setVisibility(View.GONE);
//            }
//        });
//
//        ImageView saveBtn = dialogView.findViewById(R.id.btn_add_family);
//
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (inputCheck()) {
//                    UpdateFamilyMember(mFamilyEmailInput.getText().toString(), String.valueOf(mFamilyRelationshipSpinner.getSelectedItemPosition() + 1), dialogBuilder);
//                }
//            }
//        });
//
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.show();
//    }
//
//    private boolean inputCheck() {
//
//        if (TextUtils.isEmpty(mFamilyEmailInput.getText().toString())) {
//            mFamilyEmailInput.setError(getString(R.string.family_member_email_empty));
//            mFamilyEmailInput.requestFocus();
//            return false;
//        }
//
//        if (!isEmailValid(mFamilyEmailInput.getText().toString())) {
//            mFamilyEmailInput.setError(getString(R.string.family_member_email_validation));
//            mFamilyEmailInput.requestFocus();
//            return false;
//        }
//
//
//        if (!mFamilyRelationshipSpinner.isSelected()) {
//            mErrorRelationship.setVisibility(View.VISIBLE);
//            return false;
//        }
//
//        return true;
//    }
//
//    private void UpdateFamilyMember(String email, String relationship, AlertDialog dialog) {
//        if (mUpdateFamilyMemberTask == null) {
//            mUpdateFamilyMemberTask = UpdateFamilyMemberAsyncTask(email, relationship, dialog);
//        }
//    }
//
//    private void endConnection() {
//        hasEndConnectionClicked = true;
//        progressSpinner.setVisibility(View.GONE);
//        connectingTxt.setText("Cancelling...");
//        hangUpProcess(true);
//    }
//
//
//    private void ratingDialog() {
//        if (getTabletMode() || call_type == PAT_TO_PAT || callerType.equals("patient")) {
//            hangUpProcess(false);
//        } else if (mRatingDialog == null) {
//            if (!isFinishing()) {
//                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
//                View mView = layoutInflaterAndroid.inflate(R.layout.dialog_rating, null);
//                mRatingDialog = new AlertDialog.Builder(this);
//                mRatingDialog.setView(mView);
//
//                userInputDialogEditText = mView.findViewById(R.id.userInputDialog);
//                ratingBarDialog = mView.findViewById(R.id.ratingBar);
//
//                mRatingDialog
//                        .setCancelable(false)
//                        .setPositiveButton(getString(R.string.consultation_rate_ok), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogBox, int id) {
//                                // ToDo get user input here
//
//                            }
//                        })
//
//                        .setNegativeButton(getString(R.string.consultation_rate_next_time),
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialogBox, int id) {
//                                        dialogBox.cancel();
//                                        hangUpProcess(false);
//                                    }
//                                });
//
//                final AlertDialog alertDialogAndroid = mRatingDialog.create();
//                alertDialogAndroid.show();
//
//                alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        String comments = userInputDialogEditText.getText().toString();
//
//                        if (TextUtils.isEmpty(comments)) {
//                            userInputDialogEditText.setError(getString(R.string.rating_comment));
//                            userInputDialogEditText.requestFocus();
//                            return;
//                        }
//                        String userId = getLoginInfo2().getAccount();
//                        String rating = String.valueOf(ratingBarDialog.getRating());
//                        RateDoctorAsyncTask(orgCode, providerId, userId, rating, comments);
//                        hangUpProcess(false);
//                    }
//                });
//            }
//        }
//
//    }
//
//    /**
//     * 呼叫進來未接聽掛斷
//     */
//    private void hangUpProcess(boolean isCancelledDuringOutCall) {
//        stopMusic();
//        if (isCancelledDuringOutCall) {
//            hangUpCall(true);
//        } else {
//            hangUpFunction(true);
//        }
//        clearNotification();
//        cancelGetActiveGuestCountTimer();
//        setResult(33);
//        postFinish(CALLHANGUP);
//    }
//
//
//    private void playMusic() {
//        /*mRtcEngine.setEnableSpeakerphone(!((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn());
//        mp = MediaPlayer.create(VideoCallActivity.this, R.raw.ring);
//        mp.start();*/
//        if (soundPool != null)
//            return;
//        mRtcEngine.setEnableSpeakerphone(!((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn());
//        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一個參數為同時播放數據流的最大個數，第二數據流類型，第三為聲音質量
//        music = soundPool.load(this, R.raw.ring, 1); //把你的聲音素材放到res/raw裡，第2個參數即為資源文件，第3個為音樂的優先級
//        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                if (sampleId == music) {
//                    if (soundPool != null)
//                        soundPool.play(music, 1, 1, 0, -1, 1);
//                }
//            }
//        });
//    }
//
//    private void playEndDing() {
//        stopMusic();
//        /*mRtcEngine.setEnableSpeakerphone(!((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn());
//        mp = MediaPlayer.create(VideoCallActivity.this, R.raw.ring);
//        mp.start();*/
//        if (soundPool != null)
//            return;
//
//        stopMusic();
//        if (soundPool == null) {
//            //mRtcEngine.setEnableSpeakerphone(!((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn());
//            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一個參數為同時播放數據流的最大個數，第二數據流類型，第三為聲音質量
//            music = soundPool.load(this, R.raw.end_ding, 1); //把你的聲音素材放到res/raw裡，第2個參數即為資源文件，第3個為音樂的優先級
//            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                @Override
//                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                    if (sampleId == music) {
//                        if (soundPool != null)
//                            soundPool.play(music, 1, 1, 0, -1, 1);
//                    }
//                }
//            });
//        }
//    }
//
//
//    private void stopMusic() {
//
//        if (soundPool != null) {
//            soundPool.stop(music);
//            soundPool.release();
//            soundPool = null;
//        }
//        /*
//        if (mp != null) {
//            mp.stop();
//            mp.release();
//            mp = null;
//        }*/
//        //Intent myService = new Intent(VideoCallActivity.this, MusicPlayer.class);
//        //stopService(myService);
//    }
//
//    //Video Workflow (Outgoing): 6. 30 Seconds Timer
//    private void listenProviderJoinRoom() {
//        timer = new Timer();
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 1) {
//                    stopMusic();
//                    if (call_type == OUT_CALLING) {
//                        showProviderBusyDialog();
//                    } else {
//                        hangUpProcess(false);
////                        endDuringRinging("listenprovider");
//                    }
//                }
//            }
//        };
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (!isRemoteJoin) {
//                    Message message = new Message();
//                    message.what = 1;
//                    handler.sendMessage(message);
//                }
//            }
//        };
//        timer.schedule(timerTask, WAITING_TIME);
//    }
//
//    private void connectingPageTimeout() {
//        connectTimer = new Timer();
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 1) {
//                    cancelGetProviderOnesignalTimer();
//                    cancelGetPatientOnesignalTimer();
//                    unableConnect();
//                }
//            }
//        };
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (!isRemoteJoin) {
//                    Message message = new Message();
//                    message.what = 1;
//                    handler.sendMessage(message);
//                }
//            }
//        };
//        connectTimer.schedule(timerTask, WAITING_TIME);
//    }
//
//
//    //Check whether the receiver has gotten the onesignal
//    private void getProviderOnesignalIndicator(final String roomNumber) {
//        isConnecting = true;
//        getProviderOnesignalTimer = new Timer();
//        getProviderOnesignalTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (mCheckRevStatusTask == null) {
//                    mCheckRevStatusTask = CheckRevStatusTask();
//                }
//                if (getProviderOnesignalTask == null) {
//                    getProviderOnesignalTask = getProviderOnesignalIndicatorAsyncTask(roomNumber);
//
//                }
//            }
//        }, 0, 1000);
//    }
//
//
//    //Check whether the receiver has gotten the onesignal
//    private void getPatientOnesignalIndicator() {
//        isConnecting = true;
//        getPatientOnesignalTimer = new Timer();
//        getPatientOnesignalTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (GetOnlineRoomNumberTask == null)
//                    GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(recipientUserId);
//
//                if (getPatientOnesignalTask == null) {
//                    getPatientOnesignalTask = getPatientOnesignalIndicatorAsyncTask();
//
//                }
//            }
//        }, 0, 1000);
//    }
//
//    //Check whether the receiver has accepted, declined or timeout
//    private void checkReceiverStatusTimer() {
//        isConnecting = false;
//        CheckRevStatusTimer = new Timer();
//        CheckRevStatusTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Log.d("checkrevstatus", "checking");
//                if (mCheckRevStatusTask == null) {
//                    mCheckRevStatusTask = CheckRevStatusTask();
//                }
//            }
//        }, 0, 3000);
//    }
//
//    //Video Workflow (Outgoing): 7. Check if hang up on receiving end
//    private void hasCallerLeft() {
//
//        hasCallerLeftTimer = new Timer();
//        hasCallerLeftTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (GetOnlineRoomNumberTask == null)
//                    GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(userId);
//            }
//        }, 0, 3000);
//    }
//
//    //Video Workflow (Outgoing): 7. Check if hang up on receiving end
//    private void hasRecipientLeft() {
//        hasRecipientLeftTimer = new Timer();
//        hasRecipientLeftTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (GetOnlineRoomNumberTask == null)
//                    GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(recipientUserId);
//
//            }
//        }, 0, 3000);
//    }
//
//    //Video Workflow (Outgoing): 7. Check if hang up on receiving end
//    private void incomingHangupTimerCheck() {
//
//        incomingHangupTimer = new Timer();
//        incomingHangupTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (mRatingDialog == null) {
//                    if (!hasEnteredInCall) {
//                        getOnlineProviderNameV2();
//                    }
//
//                    if (GetOnlineRoomNumberTask == null)
//                        GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(userId);
//                }
//            }
//        }, 0, 3000);
//    }
//
//    private void getActiveGuestCount() {
//        if (getActiveGuestCount == null) {
//            getActiveGuestCount = new Timer();
//            getActiveGuestCount.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    if (mGetActiveGuestCountTask == null) {
//                        mGetActiveGuestCountTask = getActiveGuestCountTask();
//
//                    }
//                }
//            }, 0, 3000);
//        }
//    }
//
//
//    private void showProviderLeftDialog() {
//        if (mProviderLeftDialog == null && !hasCallEnded) {
//            endAgoraCall();
//            leavingRoomAsGuest(roomNumber, roomGuestId);
//
//            if (!isFinishing()) {
//
//                if (getTabletMode()) {
//                    final AlertDialog alertDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
//                    alertDialog.setTitle(getString(R.string.consultation_left_title));
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    markApptStatus(ARRIVED_STATUS);
//                                    dialog.dismiss();
//                                }
//                            });
//                    alertDialog.show();
//
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (alertDialog != null && alertDialog.isShowing()) {
//                                markApptStatus(ARRIVED_STATUS);
//                                alertDialog.dismiss();
//                            }
//                        }
//                    }, 5000);
//                } else {
//                    if (mRatingDialog == null) {
//                        mProviderLeftDialog = new MyAlertDialog(this, R.style.my_alert_dialog);
//                        mProviderLeftDialog.show();
//                        mProviderLeftDialog.setDialogTitle(getString(R.string.consultation_left_title));
//                        mProviderLeftDialog.setDialogContent(getString(R.string.consultation_left_msg));
//                        mProviderLeftDialog.setCanceledOnTouchOutside(false);
//                        mProviderLeftDialog.setRightClickListener(getString(R.string.consultation_left_ok), new MyAlertDialog.RightClickListener() {
//                            @Override
//                            public void onRightClick(View view) {
//                                ratingDialog();
//                            }
//                        });
//                    }
//                }
//            }
//        }
//    }
//
//
//    private void resetTask() {
//        providerIsBusy = true;
//
//
//        if (mCheckRevStatusTask != null) {
//            mCheckRevStatusTask.cancel(true);
//            mCheckRevStatusTask = null;
//        }
//        cancelCheckRevStatusTimer();
//        leaveChannel();
//        RtcEngine.destroy();
//
//        mRtcEngine = null;
//        if (mGenerateOnlineRoomNumberTask != null) {
//            mGenerateOnlineRoomNumberTask.cancel(true);
//            mGenerateOnlineRoomNumberTask = null;
//        }
//        if (mCreateCallLogRoomsTask != null) {
//            mCreateCallLogRoomsTask.cancel(true);
//            mCreateCallLogRoomsTask = null;
//        }
//        if (mRegisterAsRoomGuestTask != null) {
//            mRegisterAsRoomGuestTask.cancel(true);
//            mRegisterAsRoomGuestTask = null;
//        }
//        if (mMakeCall2ProviderTask != null) {
//            mMakeCall2ProviderTask.cancel(true);
//            mMakeCall2ProviderTask = null;
//        }
//        if (mCreateAppointmentTask != null) {
//            mCreateAppointmentTask.cancel(true);
//            mCreateAppointmentTask = null;
//        }
//        if (markAppointmentStatusTask != null) {
//            markAppointmentStatusTask.cancel(true);
//            markAppointmentStatusTask = null;
//        }
//        if (mGetOnlineRoomNumPatient != null) {
//            mGetOnlineRoomNumPatient.cancel(true);
//            mGetOnlineRoomNumPatient = null;
//        }
//        if (mNotifyPatientAppStatusTask != null) {
//            mNotifyPatientAppStatusTask.cancel(true);
//            mNotifyPatientAppStatusTask = null;
//        }
//        stopMusic();
//        //unRegistHeadSetReceiver();
//    }
//
//    /**
//     * 30s醫生未接聽，提示用戶是否進入候診室，此時可被醫生呼叫
//     */
//    private void showProviderBusyDialog() {
//        mCallUsersLayout.setVisibility(View.GONE);
//        resetTask();
//
//        if (mProviderBusyDialog == null && mTabletProviderBusyDialog == null
//                && !hasEnteredInCall && !isRemoteJoin && !btnFinishClicked) {
//
//            cancelIncomingCallTimer();
//            //isFinishing() prevents dialog to show during asynchronous task when the activity is destroyed
//            if (!isFinishing()) {
//
//                if (getTabletMode()) {
//                    mTabletProviderBusyDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
//                    if (docName != null && docName.contains("LovingCare")) {
//                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_msg_tablet_pharmacy));
//                    } else if (getLoginInfo2().getUserInfo().getService_code().equals("hnympc")) {
//                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_heritage_msg_tablet));
//                    } else if (getLoginInfo2().getUserInfo().getService_code().equals("heightsmedical")) {
//                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_heights_msg_tablet));
//                    } else if (getLoginInfo2().getUserInfo().getService_code().equals("sky")) {
//                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_skylands_msg_tablet));
//                    } else if (getLoginInfo2().getUserInfo().getService_code().equals("pansy")) {
//                        mTabletProviderBusyDialog.setTitle(getString(R.string.consultation_busy_cybermed_msg_tablet));
//                    }
//
//                    mTabletProviderBusyDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    markApptStatus(ARRIVED_STATUS);
//                                    dialog.dismiss();
//                                }
//                            });
//                    mTabletProviderBusyDialog.show();
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mTabletProviderBusyDialog != null) {
//                                markApptStatus(ARRIVED_STATUS);
//                                if (mTabletProviderBusyDialog != null)
//                                    mTabletProviderBusyDialog.dismiss();
//                            }
//                        }
//                    }, 7000);
//                } else {
//                    if (callFromMyAppt) {
//                        AlertDialog alertDialog = new AlertDialog.Builder(VideoCallActivity.this).create();
//                        alertDialog.setTitle(getString(R.string.consultation_busy_title));
//                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        hangUpProcess(true);
//                                        dialog.dismiss();
//                                    }
//                                });
//                        alertDialog.show();
//                    } else {
//                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
//                        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_provider_busy, null);
//                        mProviderBusyDialog = new AlertDialog.Builder(this);
//                        mProviderBusyDialog.setView(mView);
//
//                        final EditText userInputDialog = mView.findViewById(R.id.userInputDialog);
//                        mProviderBusyDialog
//                                .setCancelable(false)
//                                .setNegativeButton(getString(R.string.consultation_busy_no),
//                                        new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialogBox, int id) {
//                                                hangUpProcess(true);
//                                            }
//                                        })
//                                .setPositiveButton(getString(R.string.consultation_busy_yes), new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialogBox, int id) {
//                                        userInputBusyProvider = userInputDialog.getText().toString();
//                                        markApptStatus(ARRIVED_STATUS);
//                                    }
//                                });
//
//                        AlertDialog alertDialogAndroid = mProviderBusyDialog.create();
//                        alertDialogAndroid.show();
//
//                    }
//                }
//            }
//        }
//    }
//
//    private void unableConnect() {
//        resetTask();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(getString(R.string.consultation_cannot_connect))
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        progressSpinner.setVisibility(View.GONE);
//                        connectingTxt.setText("Cancelling...");
//                        hangUpProcess(true);
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    private void showMakeAppointMentResult(String resultInfo) {
//        final MyAlertDialog dialog = new MyAlertDialog(this, R.style.my_alert_dialog);
//        dialog.show();
//        dialog.setDialogContent(resultInfo);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setRightClickListener("OK", new MyAlertDialog.RightClickListener() {
//            @Override
//            public void onRightClick(View view) {
//                hangUpProcess(true);
//            }
//        });
//    }
//
//
//    /**
//     * 主動呼出掛斷電話
//     */
//    private void hangUpCall(boolean playDing) {
//        stopMusic();
//        if (playDing) {
//            playEndDing();
//        }
//
//        if (apptStatus == null || apptStatus.equals("")) {
//            markApptStatus(CANCELLED);
//        }
//
//        cancelCall2Provider();
//
//        //a bug with some of the devices from agora < v2.0.2
//        //ending with back camera will crash the app
//        //need to set back to front camera before exit
//        if (frontCam != true) {
//            switchCamera();
//        }
//    }
//
//    /**
//     * 呼入進來已接聽的掛斷調用的方法
//     */
//    private void hangUpFunction(boolean playDing) {
//        if (playDing) {
//            playEndDing();
//        }
////        current_online_status = STATUS_ON_LINE;
//
//        toSetDeviceOnLineStatus(userId, STATUS_ON_LINE, getLoginInfo2().getOneSignalUserId(), new SetStatusResult() {
//            @Override
//            public void onLineStatusResult(int result) {
//                if (result == 1) {
//                    setPatientOnlineRoom(userId, STATUS_ON_LINE);
//                    Log.e(CONSULT, "InComing setDeviceOnlineStatus online success");
//                } else {
//                    Log.e(CONSULT, "InComing setDeviceOnlineStatus online failed");
//                }
//            }
//        });
//
//        //a bug with some of the devices from agora < v2.0.2
//        //ending with back camera will crash the app
//        //need to set back to front camera before exit
//        if (frontCam != true) {
//            switchCamera();
//
//        }
//    }
//
//    private void setPatientOnlineRoom(String userId, int status) {
//        getSetPatientOnlineRoomResult(userId, status, "", new SetPatientOnlineRoom() {
//            @Override
//            public void setPatientOnlineRoomResult(int result) {
//                if (result == 1) {
//                    Log.e(CONSULT, "InComing setPatientOnlineRoom success");
//                    if (isInComingCallAnswered || isOutGoingCall) {
//                    }
//                } else {
//                    Log.e(CONSULT, "InComing setPatientOnlineRoom failed");
//                }
//            }
//        });
//    }
//
//    private void cancelCall2Provider() {
//        getCancelCallToProviderResult(orgCode, providerId, roomNumber, new SetCancelCallToProvider() {
//            @Override
//            public void cancelCallToProviderResult(int cancelCallResult) {
//                //If 0 means already cleared
//                if (cancelCallResult == 1 || cancelCallResult == 0) {
//                    toSetDeviceOnLineStatus(userId, STATUS_ON_LINE, getLoginInfo2().getOneSignalUserId(), new SetStatusResult() {
//                        @Override
//                        public void onLineStatusResult(int result) {
//                            if (result == 1) {
//                                setPatientOnlineRoom(userId, STATUS_ON_LINE);
//                                Log.e(CONSULT, "InComing setDeviceOnlineStatus online success");
//                            } else {
//                                Log.e(CONSULT, "InComing setDeviceOnlineStatus online failed");
//                            }
//                        }
//                    });
//                    Log.e(CONSULT, "InComing cancelCall2Provider success");
//                } else {
//                    Log.e(CONSULT, "InComing cancelCall2Provider failed");
//                }
//            }
//        });
//    }
//
//    private void leavingRoomAsGuest(String roomNum, String roomGuestId) {
//        getLeavingRoomAsGuestResult(roomNum, roomGuestId, new SetLeavinRoomAsGuest() {
//            @Override
//            public void leavingRoomAsGuestResult(int result) {
//                if (result == 1) {
//                    Log.e(CONSULT, "InComing leavingRoomAsGuest success");
//                } else {
//                    Log.e(CONSULT, "InComing leavingRoomAsGuest failed");
//                }
//            }
//        });
//    }
//
//    private void GetPatientFamilyMember() {
//        if (mGetFamilyList == null) {
//            mGetFamilyList = GetPatientFamilyMemberAsyncTask();
//        }
//    }
//
//    //Video Workflow (Outgoing): 1. GenerateOnlineRoomNumber
//    private void generateOnlineRoomNumber() {
//        if (mGenerateOnlineRoomNumberTask == null) {
//            mGenerateOnlineRoomNumberTask = generrateOnlineRoomNumberTask();
//        }
//    }
//
//    //Video Workflow (Outgoing): 2. Create Call Log Rooms
//    private void createCallLogRooms(String roomNum) {
//        if (mCreateCallLogRoomsTask == null) {
//            mCreateCallLogRoomsTask = createCallLogRoomsTask(roomNum);
//        }
//    }
//
//    private void NotifyProvider(String message, String push_msg) {
//        if (NotifyProviderTask == null) {
//            NotifyProviderTask = NotifyProviderAsyncTask(message, push_msg);
//        }
//    }
//
//    private void NotifyPatient(String message) {
//        if (NotifyPatientTask == null) {
//            NotifyPatientTask = NotifyPatientAsyncTask(message);
//        }
//    }
//
//
//    private void makeCall2Provider() {
//        if (mMakeCall2ProviderTask == null) {
//            mMakeCall2ProviderTask = MakeCall2ProviderTask();
//        }
//    }
//
//
//    private void makeCall2Patient(String userId) {
//        if (mMakeCall2PatientTask == null) {
//            mMakeCall2PatientTask = MakeCall2PatientTask(userId);
//        }
//    }
//
//    //Video Workflow (Outgoing): 5. Create Appointment
//    private void createAppointment() {
//        if (mCreateAppointmentTask == null) {
//            mCreateAppointmentTask = createAppointmentTask();
//        }
//    }
//
//    private void markApptStatus(String status) {
//        if (markAppointmentStatusTask == null) {
//            markAppointmentStatusTask = markAppointmentStatusAsyncTask(status);
//        }
//    }
//
//    private void setRoomChargeCC() {
//        if (mSetRoomChargeCCTask == null) {
//            mSetRoomChargeCCTask = SetRoomChargeCCTask(roomNumber, appointmentIdentifier, orgCode, cc_idx, cvv_code);
//
//        }
//    }
//
//    private void markApptPaymentMethod(String paymentMethod) {
//        if (mMarkApptPaymentMethodTask == null) {
//            mMarkApptPaymentMethodTask = MarkApptPaymentMethodTask(appointmentIdentifier, paymentMethod);
//
//        }
//    }
//
//    //Video Workflow (Incoming): 1. Get Online RoomNum
//    private void getOnlineRoomNumPatient(String userId) {
//        if (mGetOnlineRoomNumPatient == null) {
//            mGetOnlineRoomNumPatient = getOnlineRoomNumPatientAsyncTask(userId);
//        }
//    }
//
//    //Video Workflow (Incoming): 4. Notify App Devices
//    private void notifyPatientAppStatus(String userId, String roomNum) {
//        if (mNotifyPatientAppStatusTask == null) {
//            mNotifyPatientAppStatusTask = notifyPatientAppStatusAsycnTask(userId, roomNum);
//        }
//    }
//
//
//
//    private void SetPatientOnesignalIndicator(boolean set) {
//        SimpleDateFormat sdfCompareNow = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
//        String dateTimeCompNow = sdfCompareNow.format(new Date());
//        if (setPatientOnesignalIndicatorTask == null) {
//            setPatientOnesignalIndicatorTask = SetPatientOnesignalIndicatorAsyncTask(set, dateTimeCompNow);
//        }
//    }
//
//
//    private void getOnlineProviderNameV2() {
//        if (getOnlineProviderNameV2Task == null) {
//            getOnlineProviderNameV2Task = getOnlineProviderNameV2AsyncTask();
//        }
//    }
//
//
//    private void getGuestCount(int uid) {
//        if (mGetActiveGuestCountTaskRemove == null) {
//            mGetActiveGuestCountTaskRemove = getGuestCountAsyncTask(uid);
//        }
//    }
//
//    //Video Workflow (Outgoing): 4. Register As Room Guest
//    //Video Workflow (Incoming): 3. Register as room guest
//    private void registerAsRoomGuest() {
//
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
//    }
//
//    private AsyncTask getOnlineProviderNameV2AsyncTask() {
//        return new AsyncTask<Void, Void, DocInfo>() {
//            Exception e;
//
//            @Override
//            protected DocInfo doInBackground(Void... voids) {
//                try {
//                    return WebService.getInstance().getOnlineProviderNameV2(userId);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(DocInfo doctor) {
//                getOnlineProviderNameV2Task = null;
//                if (doctor != null) {
//                    orgCode = doctor.org_code;
//                    providerId = doctor.provider_code;
//                    if (doctor.last_name != null && !doctor.last_name.isEmpty()) {
//                        docName = doctor.last_name;
//                    } else {
//                        cancelIncomingCallTimer();
//                        postFinish(CALLANSWERED);
//                    }
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask GetPatientFamilyMemberAsyncTask() {
//        return new AsyncTask<Object, Object, VectorFamily>() {
//            Exception e;
//
//            @Override
//            protected VectorFamily doInBackground(Object... params) {
//                try {
//                    return WebService.getInstance().GetFamilyList(userId);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(VectorFamily familyVector) {
//                mGetFamilyList = null;
//                if (e == null) {
//
//                    searchPatientAdapter.appendList(familyVector);
//                } else {
//
//                }
//
//            }
//        }.execute();
//    }
//
//
//
//
//
//    private AsyncTask SetPatientOnesignalIndicatorAsyncTask(final boolean set, final String delivery_date) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().SetPatientOnesignalIndicator(getLoginInfo2().getAccount(), set, delivery_date);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                setPatientOnesignalIndicatorTask = null;
//                if (e == null) {
//                    if (integer != -1) {
//                        if (set == true) {
//                            if (call_type == PAT_TO_PAT) {
//                                hasCallerLeft();
//                            } else {
//                                incomingHangupTimerCheck();
//                            }
//                            listenProviderJoinRoom();
//                        }
//                        Log.d("TIMEANALYSIS", "5. Set GETUI Indicator " + Calendar.getInstance().getTime().toString());
//                        Log.e(CONSULT, "setProviderOnesignalIndicator success");
//                    } else {
//                        Log.e(CONSULT, "setProviderOnesignalIndicator failed");
//                    }
//                }
//            }
//        }.execute();
//    }
//
//
//    private AsyncTask getProviderOnesignalIndicatorAsyncTask(final String roomNum) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().GetProviderOneSignalIndicator(orgCode, providerId, roomNum);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                getProviderOnesignalTask = null;
//                if (e == null) {
//                    if (integer == 1) {
//                        connectingPage.setVisibility(View.GONE);
//                        getProviderOnesignalTimer.cancel();
//                        cancelConnectTimer();
//                        listenProviderJoinRoom();
//                        checkReceiverStatusTimer();
//                        playMusic();
//
//                        mCallUserStatusTxt.setText("Calling: " +
//                                (getTabletMode() ? getFacilityName() : docName));
//                        mCallUsersLayout.setVisibility(View.VISIBLE);
//
//                        Log.d(CONSULT, "get provider onesignal success");
//                    } else if (integer == -1) {
//                        Log.d(CONSULT, "get provider onesignal waiting");
//                    }
//                } else {
//                    Log.d(CONSULT, "get provider onesignal Failed");
//                }
//            }
//        }.execute();
//    }
//
//
//    private AsyncTask getPatientOnesignalIndicatorAsyncTask() {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().GetPatientOneSignalIndicator(recipientUserId, roomNumber);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                getPatientOnesignalTask = null;
//                if (e == null) {
//                    if (integer == 1) {
//                        connectingPage.setVisibility(View.GONE);
//                        cancelGetPatientOnesignalTimer();
//                        listenProviderJoinRoom();
//
//                        hasRecipientLeft();
//                        cancelConnectTimer();
//                        playMusic();
//                        mCallUserStatusTxt.setText("Calling: " +
//                                (getTabletMode() ? getFacilityName() : docName));
//                        mCallUsersLayout.setVisibility(View.VISIBLE);
//                        Log.d(CONSULT, "get provider onesignal success");
//                    } else if (integer == -1) {
//                        Log.d(CONSULT, "get provider onesignal waiting");
//                    }
//                } else {
//                    Log.d(CONSULT, "get provider onesignal Failed");
//                }
//            }
//        }.execute();
//    }
//
//
//    private AsyncTask generrateOnlineRoomNumberTask() {
//        return new AsyncTask<Void, Void, String>() {
//            Exception e;
//
//            @Override
//            protected String doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().generate_OnlineRoomNumber();
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                mGenerateOnlineRoomNumberTask = null;
//                if (e == null) {
//                    if (!TextUtils.isEmpty(s)) {
//                        roomNumber = s;
//                        setVideoRoomNumber(roomNumber);
//                        if (appointmentIdentifier != null) {
//                            //Call from my appointments
//                            createCallLogRooms(roomNumber);
//                        } else {
//                            if (call_type == PAT_TO_PAT) {
//                                //Patient to Patient call
//                                createCallLogRoomsPatTask(roomNumber);
//                            } else {
//                                //Normal call to provider
//                                createAppointment();
//                            }
//                        }
//                        Log.e(CONSULT, "generrateOnlineRoomNumberTask success");
//                    }
//                } else {
//                    toastShortInfo("Generate Online Room Number Task Failed");
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask getActiveGuestCountTask() {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().GetActiveGuestsCount(roomNumber, roomGuestId);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return -1;
//            }
//
//            @Override
//            protected void onPostExecute(Integer count) {
//                super.onPostExecute(count);
//                mGetActiveGuestCountTask = null;
//                if (e == null) {
//                    if (count == 0) {
//                        if (isRemoteJoin) {
//                            if (getActiveGuestCount != null) {
//                                leavingRoomAsGuest(roomNumber, roomGuestId);
//                                cancelGetActiveGuestCountTimer();
//                            }
//                            Log.e(CONSULT, "GetActiveGuestCount LEFTTT success");
//                            showProviderLeftDialog();
//                        }
//                    }
//                    Log.e(CONSULT, "GetActiveGuestCount success");
//                } else {
//                    toastShortInfo("GetActiveGuestCount failed");
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask UpdateFamilyMemberAsyncTask(final String email, final String relationship, final AlertDialog dialog) {
//        return new AsyncTask<Object, Object, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Object... params) {
//                try {
//                    return WebService.getInstance().UpdateFamilyMember(userId, email, relationship);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                mUpdateFamilyMemberTask = null;
//                if (e == null) {
//                    if (integer == 1) {
//                        GetPatientFamilyMember();
//                        dialog.dismiss();
//                    } else {
//                        mErrorEmail.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask CheckRevStatusTask() {
//        return new AsyncTask<Void, Void, String>() {
//            Exception e;
//
//            @Override
//            protected String doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().CheckReceiverStatus(roomNumber);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                mCheckRevStatusTask = null;
//                if (e == null) {
//                    if (s.equals("")) {
//                        cancelCheckRevStatusTimer();
//                        cancelConnectTimer();
//                        cancelGetProviderOnesignalTimer();
//                        cancelGetPatientOnesignalTimer();
//
//                        if (mProviderBusyDialog == null && mTabletProviderBusyDialog == null
//                                && !hasEnteredInCall && !hasEndConnectionClicked && !isRemoteJoin) {
//                            if (!isFinishing() && !isConnecting) {
//                                showProviderBusyDialog();
//                            } else if (!isFinishing() && isConnecting && !hasEndConnectionClicked && !isRemoteJoin) {
//                                unableConnect();
//                            }
//                        }
//                    }
//                    Log.e(CONSULT, "CheckRevStatusTask success");
//                } else {
//                    toastShortInfo("CheckRevStatusTaskFailed");
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask GetOnlineRoomNumberAsyncTask(final String user_id) {
//        return new AsyncTask<Void, Void, String>() {
//            Exception e;
//
//            @Override
//            protected String doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().GetOnlineRoomNumber_Patient(user_id);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                GetOnlineRoomNumberTask = null;
//                if (s.isEmpty()) {
//
//                    if (call_type == PAT_TO_PAT) {
//                        cancelhasRecipientLeftTimer();
//                        hangUpProcess(false);
//                    } else {
//
//                        cancelIncomingCallTimer();
//
//                        if (!isInComingCallAnswered) {
//                            hangUpFunction(true);
//                        } else {
//                            hangUpFunction(false);
//                        }
//
//                        if (hasEnteredInCall == true) {
//                            showProviderLeftDialog();
//                        } else {
//                            clearNotification();
//                            cancelGetActiveGuestCountTimer();
//                            setResult(33);
//                            postFinish(CALLEND);
//                        }
//
//                    }
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask createCallLogRoomsTask(final String roomNum) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().create_Call_Log_Rooms(roomNum, appointmentIdentifier, providerId, userId, orgCode);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    if (integer == 1) {
//                        setPatientDeviceStatus(userId, STATUS_BUSY, getLoginInfo2().getOneSignalUserId(), false);
//                        //registerAsRoomGuest();
//                        Log.e(CONSULT, "Create call log room Task success");
//                    } else if (integer == -1) {
//                        endConnection();
//                        toastShortInfo("Create Call Log Room Task Error");
//                    }
//                } else {
//                    endConnection();
//                    toastShortInfo("Create Call Log Room Task Failed");
//                }
//            }
//        }.execute();
//    }
//
//
//    private AsyncTask createCallLogRoomsPatTask(final String roomNum) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().create_Call_Log_Rooms_Pat(roomNum, providerId, userId, orgCode);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    if (integer == 1) {
//                        setPatientDeviceStatus(userId, STATUS_BUSY, getLoginInfo2().getOneSignalUserId(), false);
//                        //registerAsRoomGuest();
//                        Log.e(CONSULT, "Create call log room Task success");
//                    } else if (integer == -1) {
//                        endConnection();
//                        toastShortInfo("Create Call Log Room Task Error");
//                    }
//                } else {
//                    endConnection();
//                    toastShortInfo("Create Call Log Room Task Failed");
//                }
//            }
//        }.execute();
//    }
//
//
//    private AsyncTask MakeCall2PatientTask(final String userId) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    if (BuildConfig.FLAVOR.equals("cybermedi8")) {
//                        return WebService.getInstance().MakeCall2Patient(orgCode, providerId, userId, roomNumber, 2);
//                    } else {
//                        return WebService.getInstance().MakeCall2Patient(orgCode, providerId, userId, roomNumber, 1);
//                    }
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                mMakeCall2PatientTask = null;
//                if (e == null) {
//                    if (integer >= 1) {
//                        if (!isRemoteJoin) {
//                            //Calling user
//                            joinChannel();
//                            endConnectionBtn.setVisibility(View.VISIBLE);
//                            getPatientOnesignalIndicator();
//                        } else {
//                            //Adding family
//                            mCallUsersLayout.setVisibility(View.VISIBLE);
//                            mBtnEndInvitation.setVisibility(View.VISIBLE);
//                            mBtnHangup.setVisibility(View.INVISIBLE);
//                            removeAddUserStatus(END_INVITATION);
//                        }
//                        Log.e(CONSULT, "MakeCall2PatientTask success");
//                    } else if (integer == -1) {
//                        mCallUserStatusTxt.setText(getString(R.string.consultation_family_not_pick_up));
//                        mCallUsersLayout.setVisibility(View.VISIBLE);
//                        removeAddUserStatus(7000);
//
//                        Log.e(CONSULT, "MakeCall2PatientTask error");
////                        toastShortInfo("Make Call To Provider Error");
//                    }
//                } else {
//
//                    endConnection();
//                    Log.e(CONSULT, "MakeCall2PatientTask failed");
//                    toastShortInfo("Make Call To Provider Failed");
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask MakeCall2ProviderTask() {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    if (BuildConfig.FLAVOR.equals("cybermedi8")) {
//                        return WebService.getInstance().MakeCall2Provider_Android(orgCode, providerId, userId, roomNumber, 2);
//                    } else {
//                        return WebService.getInstance().MakeCall2Provider_Android(orgCode, providerId, userId, roomNumber, 1);
//                    }
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    mMakeCall2ProviderTask = null;
////                    joinChannel();
//                    if (integer >= 1) {
//                        //加入頻道
//                        joinChannel();
//                        endConnectionBtn.setVisibility(View.VISIBLE);
//
////                        if(callFromMyAppt){
////                            switch(apptStatus){
////                                case "Not Seen":
////                                    markApptStatus(NOT_SEEN_STATUS);
////                                    break;
////                                case "Arrived":
////                                    markApptStatus(ARRIVED_STATUS);
////                                    break;
////                            }
////                        }
////                        createAppointment();
//
//
//                        if (!isSkipped) {
//                            SetApptVitalIntakeV4Task(orgCode, appointmentIdentifier, chiefComplaint, temperature, pulse, BPH, BPL, height, weight, allergies, medHx, socialHx, "", "", phone_num);
////                            SavePatVitalsTask(userId, "", orgCode, appointmentAccount, chiefComplaint, medHx, socialHx, allergies, temperature, pulse, weight, height, BPH, BPL, "");
//
//                        }
////                        String paymentMethod = "0";
//                        if (paymentType == DOCTOR_PAYMENT) {
//                            setRoomChargeCC();
////                            paymentMethod = "1";
//                        }
////                        else if (paymentType == DOCTOR_PAYPAL) {
////                            paymentMethod = "3";
////                        } else if (paymentType == DOCTOR_INSURANCE) {
////                            paymentMethod = "2";
////                        }
//
////                        markApptPaymentMethod(paymentMethod);
//
//                        getProviderOnesignalIndicator(roomNumber);
//                        Log.e(CONSULT, "MakeCall2ProviderTask success");
//                    } else if (integer == -1) {
//                        endConnection();
//                        Log.e(CONSULT, "MakeCall2ProviderTask error");
//                        toastShortInfo("Make Call To Provider Error");
//                    }
//                } else {
//                    endConnection();
//                    Log.e(CONSULT, "MakeCall2ProviderTask failed");
//                    toastShortInfo("Make Call To Provider Failed");
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask createAppointmentTask() {
//        return new AsyncTask<Void, Void, Appointment>() {
//            Exception e;
//
//            @Override
//            protected Appointment doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().create_appointment_on_EMR_Android_v2(roomNumber,
//                            userId, orgCode, providerId, apptTime);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Appointment appointment) {
//                super.onPostExecute(appointment);
//                mCreateAppointmentTask = null;
//                if (e == null) {
//                    if (appointment != null) {
//                        Log.e(CONSULT, "createAppointmentTask success");
//                        appointmentIdentifier = appointment.getProperty(1).toString();
//                        appointmentAccount = appointment.account;
//                        Log.d("APPTIDDEBUG", appointmentIdentifier);
//
////                        getProviderOnesignalIndicator(roomNumber);
//                        createCallLogRooms(roomNumber);
//                    } else {
//                        endConnection();
//                        Log.e(CONSULT, "createAppointmentTask failed");
//                    }
//                } else {
//                    endConnection();
//                    Log.e(CONSULT, "createAppointmentTask error");
//                    toastShortInfo(e.getMessage());
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask markAppointmentStatusAsyncTask(final String status) {
//        return new AsyncTask<Void, Void, Integer>() {
//
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().Mark_appointment_status(orgCode, roomNumber, status);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    if (integer == 1) {
//                        if (getTabletMode()) {
//                            hangUpProcess(true);
//                        } else if (status == CANCELLED) {
//                            hangUpProcess(true);
//                        } else {
//                            GetProviderWaitingRoomPatNumber();
//
//
////                            String waitingRoomMsg = "You are placed in the waiting room. The doctor will call you back soon";
//
////                            if (!waitingRoomCount.equals("-1")) {
////                                waitingRoomMsg = "You are placed in the waiting room. There are " + waitingRoomCount + "patient(s) ahead of you. The doctor will call you back soon";
////                            }
//
////                            showMakeAppointMentResult(waitingRoomMsg);
//
////                            if (userInputBusyProvider != null) {
////                                String push_msg = "Patient " + getLoginInfo2().getUserInfo().getFirstName() + " " +
////                                        getLoginInfo2().getUserInfo().getLastname()
////                                        + " (id: " + userId + ") was not able to reach you and entered your CDOC waiting room.\n\n"
////                                        + (isSkipped ? "" : "Reason for Appointment:\n" + chiefComplaint + "\n\n")
////                                        + (userInputBusyProvider.equals("") ? "" : "Patient Message: " + userInputBusyProvider);
////
////                                String message = "This message is from CDOC. " + push_msg;
////
////                                NotifyProvider(message, push_msg);
////                            }
//                        }
//                        Log.e(CONSULT, "markAppointmentStatusAsyncTask success");
//                    } else {
//                        hangUpProcess(true);
//                        Log.e(CONSULT, "markAppointmentStatusAsyncTask failed");
//                    }
//                } else {
//                    Log.e(CONSULT, "markAppointmentStatusAsyncTask error");
//                }
//            }
//        }.execute();
//    }
//
//    private void GetProviderWaitingRoomPatNumber() {
//
//        OnPostExecute ope = result -> {
//            String waitingRoomCount = result.toString();
//
//            String waitingRoomMsg = "You are placed in the waiting room. The doctor will call you back soon";
//
//            if (!waitingRoomCount.equals("-1")) {
//                waitingRoomMsg = "You are placed in the waiting room. There are " + waitingRoomCount + "patient(s) ahead of you. The doctor will call you back soon";
//            }
//
//            showMakeAppointMentResult(waitingRoomMsg);
//
//            if (userInputBusyProvider != null) {
//                String push_msg = "Patient " + getLoginInfo2().getUserInfo().getFirstName() + " " +
//                        getLoginInfo2().getUserInfo().getLastname()
//                        + " (id: " + userId + ") was not able to reach you and entered your CDOC waiting room.\n\n"
//                        + (isSkipped ? "" : "Reason for Appointment:\n" + chiefComplaint + "\n\n")
//                        + (userInputBusyProvider.equals("") ? "" : "Patient Message: " + userInputBusyProvider);
//
//                String message = "This message is from CDOC. " + push_msg;
//
//                NotifyProvider(message, push_msg);
//            }
//        };
//        WebService.webServiceAsyncTask(WebServiceID.getProviderWaitingRoomPatNumber_From_EMR, ope, "1", appointmentIdentifier, orgCode, providerId);
//    }
//
//
//    private AsyncTask getOnlineRoomNumPatientAsyncTask(final String userId) {
//        return new AsyncTask<Void, Void, String>() {
//
//            Exception e;
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                try {
//                    return WebService.getInstance().GetOnlineRoomNumber_Patient(userId);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                if (e == null) {
//                    if (!TextUtils.isEmpty(s)) {
//                        roomNumber = s;
//                        setPatientDeviceStatus(userId, STATUS_BUSY, getLoginInfo2().getOneSignalUserId(), false);
//                        Log.e(CONSULT, "answer getOnlineRoomNumPatient success");
//                    } else {
//                        Log.e(CONSULT, "answer getOnlineRoomNumPatient failed");
//                    }
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask SetRoomChargeCCTask(final String room_number, final String appt_id, final String org_code, final String cc_idx, final String cvv_code) {
//        return new AsyncTask<Void, Void, String>() {
//
//            Exception e;
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                try {
//
//                    return WebService.getInstance().SetRoomChargeCC(room_number, appt_id, org_code, cc_idx, cvv_code);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                if (s.equals("1")) {
//
//                } else {
//                    endConnection();
//                    Log.e(CONSULT, "setroomcc failed");
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask MarkApptPaymentMethodTask(final String appt_id, final String paymentMethod) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... voids) {
//                try {
//                    return WebService.getInstance().MarkApptPaymentMethod(orgCode, appt_id, paymentMethod);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    if (integer == 1) {
//                        Log.e(CONSULT, "answer MarkApptPaymentMethod success");
//                    } else {
//                        Log.e(CONSULT, "answer MarkApptPaymentMethod failed");
//                    }
//                }
//            }
//        }.execute();
//    }
//
//    //Video Workflow (Outgoing): 3. Set Patient Device Status
//    //Video Workflow (Incoming): 2. SetPatient DeviceStatus
//    private void setPatientDeviceStatus(final String userId, int status, String deviceId, final boolean isHangup) {
//        toSetDeviceOnLineStatus(userId, status, deviceId, new SetStatusResult() {
//            @Override
//            public void onLineStatusResult(int result) {
//                if (result == 1) {
//                    Log.e(CONSULT, "answer setPatientDeviceStatusBusy success");
//                    if (!isHangup) {
//                        registerAsRoomGuest();
//                    } else {
//                    }
//                } else {
//                    endConnection();
//                    Log.e(CONSULT, "answer setPatientDeviceStatusBusy failed");
//                }
//            }
//        });
//    }
//
//    private AsyncTask notifyPatientAppStatusAsycnTask(final String userId, final String roomNum) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... voids) {
//                try {
//                    if (BuildConfig.FLAVOR.equals("cybermedi8")) {
//                        return WebService.getInstance().notify_patient_app_devices(userId, "Call Answered", roomNum, "nil", 2);
//                    } else {
//                        return WebService.getInstance().notify_patient_app_devices(userId, "Call Answered", roomNum, "nil", 1);
//                    }
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    if (integer == 1) {
//                        Log.e(CONSULT, "answer notifyPatientAppStatus success");
//                    } else {
//                        Log.e(CONSULT, "answer notifyPatientAppStatus failed");
//                    }
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask SetApptVitalIntakeV4Task(final String org_code, final String appt_id, final String chief_complaint, final String temperature,
//                                               final String pulse, final String BPH, final String BPL, final String height, final String weight,
//                                               final String allergies, final String medHx, final String socialHx, final String LDN_Initial,
//                                               final String LDN_Refill, final String phone_num) {
//
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//
//                try {
//                    return WebService.getInstance().SetApptVitalIntakeV4(org_code, appt_id, chief_complaint, temperature, pulse, BPH, BPL, height, weight, medHx, socialHx, allergies, LDN_Initial, LDN_Refill, phone_num);
//
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//
//            }
//        }.execute();
//
//    }
//
//    private AsyncTask SavePatVitalsTask(final String userId, final String entry_user_id, final String org_code, final String account, final String chief_complaint, final String medHx, final String socialHx,
//                                        final String allergies, final String temperature, final String pulse, final String weight,
//                                        final String height, final String BPH, final String BPL, final String spo2) {
//
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//
//                try {
//                    return WebService.getInstance().SavePatVitals(userId, entry_user_id, org_code, account, chief_complaint, medHx, socialHx, allergies, temperature, pulse, weight, height, BPH, BPL, spo2);
//
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//
//            }
//        }.execute();
//
//    }
//
//    private AsyncTask NotifyProviderAsyncTask(final String message, final String push_msg) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().Notify_Provider(orgCode, providerId, message, push_msg);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    if (integer == 1) {
//
//                        NotifyPatient("Hello " + getLoginInfo2().getUserInfo().getFirstName() + ",\n\n" + "Please review your appointment details here.\n\n"
//                                + "Provider: " + docName + "\n" + "Date and Time: " + apptTime + ".\n\n"
//                                + "Please login with the CDoc app before the specified date and time to speak with the provider.\n\n"
//                                + "If you have any questions, please feel free to contact us at 732-800-0020.");
//
//                        Log.d("NotifyProvider", "sent");
//                        Log.e(CONSULT, "NotifyProviderAsyncTask success");
//
//                    } else {
//                        Log.d("NotifyProvider", "error");
//                        Log.e(CONSULT, "NotifyProviderAsyncTask failed");
//                    }
//                } else {
//                    Log.d("NotifyProvider", "error");
//                    Log.e(CONSULT, "NotifyProviderAsyncTask error");
//                }
//            }
//        }.execute();
//    }
//
//    private AsyncTask NotifyPatientAsyncTask(final String message) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().Notify_Patient(getLoginInfo2().getAccount(), message);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    if (integer == 1) {
//                        Log.d("Notify_Patient", "sent");
//                        Log.e(CONSULT, "Notify_Patient success");
//
//                    } else {
//                        Log.d("Notify_Patient", "error");
//                        Log.e(CONSULT, "Notify_Patient failed");
//                    }
//                } else {
//                    Log.d("Notify_Patient", "error");
//                    Log.e(CONSULT, "Notify_Patient error");
//                }
//            }
//        }.execute();
//    }
//
//
//    private AsyncTask getGuestCountAsyncTask(final int uid) {
//        return new AsyncTask<Void, Void, Integer>() {
//            @Override
//            protected Integer doInBackground(Void... params) {
//                return WebService.getInstance().GetActiveGuestsCount(roomNumber, roomGuestId);
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                mGetActiveGuestCountTaskRemove = null;
//                if (integer != null) {
//                    if (integer != 0) {
////                        removeRemoteVideo(uid);
//                        doRemoveRemoteUi(uid);
//                    } else {
//                        onRemoteUserLeft();
//                    }
//                }
//            }
//        }.execute();
//    }
//
//
//    private AsyncTask getPatientFamilyListAsync(final androidx.appcompat.app.AlertDialog b) {
//        return new AsyncTask<Void, Void, VectorFamily>() {
//            Exception e;
//
//            @Override
//            protected VectorFamily doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().GetFamilyList(getLoginInfo2().getAccount());
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(VectorFamily patient_family) {
//                getPatientListTask = null;
//                if (e == null) {
//                    searchPatientAdapter.appendList(patient_family);
//                    searchPatientAdapter.setClickListener(new FamilyMemberAdapter.ItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            b.dismiss();
//                            FamilyInfo searchPatient = searchPatientAdapter.getItem(position);
//                            mCallUserStatusTxt.setText("Calling " + searchPatient.first_name + " " + searchPatient.last_name);
//                            recipientUserId = searchPatient.email;
//                            if (mGetPatientOnlineStatusTask == null) {
//                                getPatientOnlineStatus(recipientUserId, true);
//                            }
//                        }
//                    });
//                }
//            }
//        }.execute();
//    }
//
//
//    private AsyncTask getPatientOnlineStatus(final String userId, final boolean isMakeCall) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().getPatientOnlineStatus(userId);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                mGetPatientOnlineStatusTask = null;
//                if (e == null) {
//                    Log.d("patientonlinestatus", String.valueOf(integer));
//                    if (integer == 1) {
//                        if (isMakeCall) {
//                            makeCall2Patient(userId);
//                        } else {
//                            setPatientOnlineRoom(recipientUserId, STATUS_ON_LINE);
//                        }
//                    } else {
//                        Toast.makeText(VideoCallActivity.this, "Patient is not online", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }.execute();
//    }
//
//
//    private void doRemoveRemoteUi(final int uid) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (isFinishing()) {
//                    return;
//                }
//
//                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;
//
//                if (mUidsList.size() == 3) {
//                    useDefaultLayout = false;
//                }
//
//                Object target = mUidsList.remove(uid);
//                Log.d("uidListdebugremove", String.valueOf(mUidsList.size()));
//
//                if (target == null) {
//                    return;
//                }
//
//                int bigBgUid = -1;
//                if (mSmallVideoViewAdapter != null) {
//                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
//                    if (bigBgUid == uid) {
//                        //Bugfix: when other side crashes and rejoins,
//                        // may crash due to getExceptedUid does not exist anymore.
//                        //Solution to reset everything
//                        mSmallVideoViewAdapter = null;
//                    }
//                }
//
//                //Issue with getting new bigBgUid if the original bigBgUid was cleared.
//                if (mUidsList.size() == 2) {
//                    List<Integer> keys = new ArrayList<>(mUidsList.keySet());
//                    if (keys.get(1) != null) {
//                        bigBgUid = keys.get(1);
//                    }
//                }
//
//                if (useDefaultLayout || uid == bigBgUid) {
//                    switchToDefaultVideoView();
//                } else {
//                    switchToSmallVideoView(bigBgUid);
//                }
//            }
//        });
//    }
//
//    public static VideoCallActivity getInstance() {
//        return videoCallActivity;
//    }
//
//    private void postFinish(String callStatus) {
//        callEndedTxt.setVisibility(View.VISIBLE);
//        callEndedTxt.setText(callStatus);
//        callCancelledTxt.setVisibility(View.VISIBLE);
//        callCancelledTxt.setText(callStatus);
//
//        SharedPreferences preferences = getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
//        editor.commit();
//
//        //Reset push notification indicator to false
//        SetPatientOnesignalIndicator(false);
//
//        playEndDing();
//        if (connectTimer != null)
//            connectTimer.cancel();
//        cancelIncomingCallTimer();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                stopMusic();
//                finish();
//            }
//        }, 2000);
//    }
//
//    public void createNotification() {
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
//
//        notification.setTicker("Call in progress");
//        notification.setSmallIcon(R.drawable.ic_call_white_36dp);
//        notification.setContentTitle("CDOC");
//        notification.setContentText("CDOC call in progress");
//        notification.setAutoCancel(false);
//        notification.setOngoing(true);
//
//        Intent intent = new Intent(this, VideoCallActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        notification.setContentIntent(pIntent);
//
//        notification.build();
//
//        NotificationManager nManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nManger.notify(0, notification.build());
//    }
//
//    public void clearNotification() {
//        NotificationManager oldNoti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        oldNoti.cancel(0);
//    }
//
//    private void cancelCheckRevStatusTimer() {
//        if (CheckRevStatusTimer != null) {
//            CheckRevStatusTimer.cancel();
//            CheckRevStatusTimer = null;
//        }
//    }
//
//    private void cancelGetProviderOnesignalTimer() {
//        if (getProviderOnesignalTimer != null) {
//            getProviderOnesignalTimer.cancel();
//            getProviderOnesignalTimer = null;
//        }
//    }
//
//    private void cancelGetPatientOnesignalTimer() {
//        if (getPatientOnesignalTimer != null) {
//            getPatientOnesignalTimer.cancel();
//            getPatientOnesignalTimer = null;
//        }
//    }
//
//    public void cancelhasCallLeftTimer() {
//        if (hasCallerLeftTimer != null) {
//            hasCallerLeftTimer.cancel();
//            hasCallerLeftTimer = null;
//        }
//    }
//
//    public void cancelhasRecipientLeftTimer() {
//        if (hasRecipientLeftTimer != null) {
//            hasRecipientLeftTimer.cancel();
//            hasRecipientLeftTimer = null;
//        }
//    }
//
//    public void cancelIncomingCallTimer() {
//        if (incomingHangupTimer != null) {
//            incomingHangupTimer.cancel();
//            incomingHangupTimer = null;
//        }
//    }
//
//    private void cancelConnectTimer() {
//        if (connectTimer != null) {
//            connectTimer.cancel();
//            connectTimer = null;
//        }
//    }
//
//    private void cancelGetActiveGuestCountTimer() {
//        if (getActiveGuestCount != null) {
//            getActiveGuestCount.cancel();
//            getActiveGuestCount = null;
//        }
//    }
//
//    private void cancelTimerTask() {
//        if (timerTask != null) {
//            timerTask.cancel();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        stopMusic();
//        cancelTimerTask();
//        unRegistHeadSetReceiver();
//        if (!providerIsBusy) {
//            leaveChannel();
//            RtcEngine.destroy();
//
//            mRtcEngine = null;
//            if (mGenerateOnlineRoomNumberTask != null) {
//                mGenerateOnlineRoomNumberTask.cancel(true);
//                mGenerateOnlineRoomNumberTask = null;
//            }
//            if (mCreateCallLogRoomsTask != null) {
//                mCreateCallLogRoomsTask.cancel(true);
//                mCreateCallLogRoomsTask = null;
//            }
//            if (mRegisterAsRoomGuestTask != null) {
//                mRegisterAsRoomGuestTask.cancel(true);
//                mRegisterAsRoomGuestTask = null;
//            }
//            if (mMakeCall2ProviderTask != null) {
//                mMakeCall2ProviderTask.cancel(true);
//                mMakeCall2ProviderTask = null;
//            }
//            if (mCreateAppointmentTask != null) {
//                mCreateAppointmentTask.cancel(true);
//                mCreateAppointmentTask = null;
//            }
//            if (markAppointmentStatusTask != null) {
//                markAppointmentStatusTask.cancel(true);
//                markAppointmentStatusTask = null;
//            }
//            if (mGetOnlineRoomNumPatient != null) {
//                mGetOnlineRoomNumPatient.cancel(true);
//                mGetOnlineRoomNumPatient = null;
//            }
//            if (mNotifyPatientAppStatusTask != null) {
//                mNotifyPatientAppStatusTask.cancel(true);
//                mNotifyPatientAppStatusTask = null;
//            }
//            cancelGetActiveGuestCountTimer();
//            cancelGetProviderOnesignalTimer();
//            cancelGetPatientOnesignalTimer();
//            cancelIncomingCallTimer();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mRtcEngine != null)
//            mRtcEngine.enableVideo();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mRtcEngine != null)
//            mRtcEngine.disableVideo();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    /////////////////////////////////////////AGORA STUFF////////////////////////////////////////////
//
//    private void registHeadSetReceiver() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
//        registerReceiver(headSetBroadCastReceiver, intentFilter);
//    }
//
//    private void unRegistHeadSetReceiver() {
//        unregisterReceiver(headSetBroadCastReceiver);
//    }
//
//
//    private void initAgoraEngineAndJoinChannel() {
//        initializeAgoraEngine();     // Tutorial Step 1
//        setupVideoProfile();         // Tutorial Step 2
//        setupLocalVideo();           // Tutorial Step 3
//    }
//
//    // Tutorial Step 1
//    private void initializeAgoraEngine() {
//        try {
//            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Tutorial Step 2
//    private void setupVideoProfile() {
//        mRtcEngine.enableVideo();
//        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_DEFAULT, false);
//
//    }
//
//    // Tutorial Step 3
//    private void setupLocalVideo() {
//
//        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
//        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
//        surfaceV.setZOrderOnTop(false);
//        surfaceV.setZOrderMediaOverlay(false);
//
//        mUidsList.put(0, surfaceV); // get first surface view
//
//        mGridVideoViewContainer.initViewContainer(getApplicationContext(), 0, mUidsList); // first is now full view
//    }
//
//    // Tutorial Step 4
//    private void joinChannel() {
//        mRtcEngine.joinChannel(null, roomNumber, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
//    }
//
//    //Step 5
//    private void setupRemoteVideo(int uid) {
//        Log.d("uidListdebug", String.valueOf(mUidsList.size()));
//        Log.d("uidlistdebugattach", String.valueOf(uid));
//
//        if (!mUidsList.containsKey(uid)) {
//            boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;
//            boolean initialSetup = false;
//            if (mUidsList.size() == 1) {
//                useDefaultLayout = false;
//                initialSetup = true;
//            } else if (mUidsList.size() == 2) {
//                useDefaultLayout = true;
//            } else if (mUidsList.size() > 4) {
//                useDefaultLayout = false;
//            }
//
//            SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
//            mUidsList.put(uid, surfaceV);
//
//
//            surfaceV.setZOrderOnTop(!useDefaultLayout);
//            surfaceV.setZOrderMediaOverlay(!useDefaultLayout);
////            surfaceV.setZOrderOnTop(false);
////            surfaceV.setZOrderMediaOverlay(false);
//
//            mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
//
//            if (useDefaultLayout) {
//                switchToDefaultVideoView();
//            } else {
//                int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
//                Log.d("uidlistdebugattach", String.valueOf(bigBgUid));
//                switchToSmallVideoView(bigBgUid);
//                if (initialSetup) {
//                    //Workaround Bug: Issue during the 1 to 1 call
//                    //The local video starts in the middle of the screen
//                    switchToDefaultVideoView();
//                    switchToSmallVideoView(bigBgUid);
//                }
//            }
//
//        }
//
////        }
//    }
//
//    private void switchToDefaultVideoView() {
//        Log.d("ggdebug", "switchtodefault");
//        if (mSmallVideoViewDock != null) {
//            mSmallVideoViewDock.setVisibility(View.GONE);
//        }
//        mGridVideoViewContainer.initViewContainer(getApplicationContext(), new Random().nextInt(61), mUidsList);
//
//        mLayoutType = LAYOUT_TYPE_DEFAULT;
//    }
//
//    private void switchToSmallVideoView(int bigBgUid) {
//        Log.d("ggdebug", "switchtosmall");
//        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
//        slice.put(bigBgUid, mUidsList.get(bigBgUid));
//        mGridVideoViewContainer.initViewContainer(getApplicationContext(), bigBgUid, slice);
//
//        bindToSmallVideoView(bigBgUid);
//
//        mLayoutType = LAYOUT_TYPE_SMALL;
//
////        requestRemoteStreamType(mUidsList.size());
//    }
//
//    private SmallVideoViewAdapter mSmallVideoViewAdapter;
//
//    private void bindToSmallVideoView(int exceptUid) {
//        if (mSmallVideoViewDock == null) {
//            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
//            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
//        }
//
//        boolean twoWayVideoCall = mUidsList.size() == 2;
//
//        RecyclerView recycler = findViewById(R.id.small_video_view_container);
//
//        boolean create = false;
//
//        if (mSmallVideoViewAdapter == null) {
//            create = true;
//            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, new Random().nextInt(61), exceptUid, mUidsList, new VideoViewEventListener() {
//                @Override
//                public void onItemDoubleClick(View v, Object item) {
//                    switchToDefaultVideoView();
//                }
//            });
//            mSmallVideoViewAdapter.setHasStableIds(true);
//        }
//        recycler.setHasFixedSize(true);
//
//        Log.d("bindToSmallVideoView ", twoWayVideoCall + " " + (exceptUid & 0xFFFFFFFFL));
//
//        if (twoWayVideoCall) {
//            recycler.setLayoutManager(new RtlLinearLayoutManager(this, RtlLinearLayoutManager.HORIZONTAL, false));
//        } else {
//            recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        }
//        recycler.addItemDecoration(new SmallVideoViewDecoration());
//        recycler.setAdapter(mSmallVideoViewAdapter);
//
//        recycler.setDrawingCacheEnabled(true);
//        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
//
//        if (!create) {
//            mSmallVideoViewAdapter.setLocalUid(new Random().nextInt(61));
//
//            //Workaround fix: Overlay issues, smaller local and remote views get covered by the bgView.
//            for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
//                if (entry.getKey() != exceptUid) {
////                    Log.d("videobug","exceptid:" + exceptUid + " key:" + entry.getKey());
//                    SurfaceView sv = entry.getValue();
//                    sv.setZOrderMediaOverlay(true);
//                    entry.setValue(sv);
//                }
//            }
//
//            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, null, null);
//        }
//        recycler.setVisibility(View.VISIBLE);
//        mSmallVideoViewDock.setVisibility(View.VISIBLE);
//    }
//
//    // Tutorial Step 7
//    private void onRemoteUserLeft() {
//        if (mRatingDialog == null && !hasCallEnded) {
//            showProviderLeftDialog();
//        }
//    }
//
//    private void onRemoteUserVideoMuted(int uid, boolean muted) {
//        SurfaceView surfaceView = mUidsList.get(uid);
//        if (surfaceView != null) {
//            surfaceView.setZOrderMediaOverlay(false);
//            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
//        }
//    }
//
//    // Tutorial Step 6
//    private void leaveChannel() {
//        if (mRtcEngine != null)
//            mRtcEngine.leaveChannel();
//    }
//
//    private void endAgoraCall() {
//        if (mRtcEngine != null) {
//            mRtcEngine.disableAudio();
//            mRtcEngine.disableVideo();
//        }
//    }
//
//
//    private void switchCamera() {
//        if (mRtcEngine != null)
//            mRtcEngine.switchCamera();
//
//    }
//
//    private void muteVoice(boolean mute) {
//        if (mRtcEngine != null)
//            mRtcEngine.muteLocalAudioStream(mute);
//    }
//
//
//    private void muteLocalVideo(boolean mute) {
//        mRtcEngine.muteLocalVideoStream(mute);
//        SurfaceView surfaceView = mUidsList.get(localUid);
//        surfaceView.setZOrderMediaOverlay(!mute);
//        surfaceView.setBackgroundResource(mute ? R.drawable.placeholder_video_mute : 0);
////        surfaceView.setVisibility(mute ? View.GONE : View.VISIBLE);
//    }
//
////////////////////////////////////End of Agora Stuff////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//}