package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.Stemoscope;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.IHEALTH_MAC_ADDR;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.FragmentStemoscopeBinding;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.util.SoundWaveNormalizer;
import com.cybermed.cdoc_patient.webapi.CallApi;
import com.stemoscope.stemolib.audio.AudioPlayManger;
import com.stemoscope.stemolib.blue.MyBluetoothManager;
import com.stemoscope.stemolib.event.BlueToothStatusEvent;
import com.stemoscope.stemolib.event.BluetoothRssiEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

//wave form chart library, see: https://medium.com/@gevorgyanweb/audiorecordview-or-simplest-and-best-audio-visualizer-for-android-4fcec59608

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StemoscopeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StemoscopeFragment extends BaseFragment implements CallApi.IApiCallBack {

    public static final int REQUEST_LOCATION = 102;
    public static final int REQUEST_LOCATION_GPS = 105;
    public final static String STEMOSCOPE_DEBUG_TAG = "stemoscope_debug_info";

    public static volatile String macAddr;

    private AudioTrack audioTrack;

    private volatile STATE state;


    private Timer timer;

    private volatile long remainSeconds = 20;

    private SoundWaveNormalizer soundWaveNormalizer;
    Activity mContext;
    FragmentStemoscopeBinding binding;

    public StemoscopeFragment() {
        // Required empty public constructor
        soundWaveNormalizer = new SoundWaveNormalizer();
    }

    public static StemoscopeFragment newInstance() {
        StemoscopeFragment fragment = new StemoscopeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stemoscope, container, false);

        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {

        mContext = getActivity();
        CallApi.setListner(this);
        soundWaveNormalizer = new SoundWaveNormalizer();
        Bundle data = getArguments();
        if (data != null) {
            macAddr = data.getString(IHEALTH_MAC_ADDR);
        }

        //check if stemo is already connected
        if (Constant.IS_STEMO_CONNECTED) {
            //show record button to user for recording
            binding.viewStemoConnected.setVisibility(View.VISIBLE);
            binding.viewNotConnected.setVisibility(View.GONE);
            binding.recordBtn.setEnabled(true);
        } else {
            binding.viewStemoConnected.setVisibility(View.GONE);
            binding.viewNotConnected.setVisibility(View.VISIBLE);
        }
        // register bluetooth event only when its not register
        if (!EventBus.getDefault().hasSubscriberForEvent(BluetoothRssiEvent.class)) {
            EventBus.getDefault().register(this);
        }
        initView();

        //#Step 1: connect stemoscope device
        connectDevice();
        //set heart point touch
        setHeartPointTouch();
    }


    private void initView() {
        //initliaze toolbar
        binding.toolbar.txtTittle.setText(getString(R.string.iot_stemoscope));
        binding.toolbar.backBtn.setOnClickListener(v -> {
            backToMainIOtPage();
        });
        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainIOtPage();
            }
        });
        //#step3: start recording
        binding.recordBtn.setOnClickListener(v -> {
            //when record button is enable, tag will be 0 and it changes to Stop button
            if (Constant.IS_STEMO_CONNECTED) {
                if (binding.recordBtn.getTag() != null && binding.recordBtn.getTag().equals("0")) {
                    //tag==1 button changes to stop recording.
                    FileDownLoadManager.getInstance().setStartSave(true);
                    binding.viewRecording.setVisibility(View.VISIBLE);
                    binding.recordBtn.setTag("1");
                    binding.recordBtn.setText(getString(R.string.stemoscope_stop_btn));
                    state = STATE.MEASURING;
                    FileDownLoadManager.getInstance().setContext(mContext);
                    //start recording show wave visulizer and save file
                    startRecoding();

                    //show 20 sec timer
                    final Handler handler = new Handler();
                    timer = new Timer(false);
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(() -> {
                                if (remainSeconds > 0) {
                                    String text = "" + remainSeconds;
                                    binding.timeCount.setText(text);
                                    remainSeconds--;
                                } else {
                                    timer.cancel();
                                    showProgress();
                                    finishRecording();
                                }
                            });
                        }
                    };

                    timer.scheduleAtFixedRate(timerTask, 0, 1000);
                } else if (state == STATE.MEASURING) {
                    timer.cancel();
                    showProgress();
                    finishRecording();
                }
            }
        });

    }

    private void backToMainIOtPage() {
        disconnect();
        if (!CDoctor2Application.getTabletMode()) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_stemoscopeFragment_to_IOT_MainPage_Fragment);
        } else
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_stemoscopeFragment_to_tabletMainFragment);
    }

    private void finishRecording() {
        FileDownLoadManager.getInstance().setStartSave(false);
        MyBluetoothManager.getInstance().stemo_end();
    }

    /**
     * connect to stemo device
     */
    private void connectDevice() {
        if (macAddr != null) {
            //call stemo_connect to connect to stemoscope
            try {
                MyBluetoothManager.getInstance().stemo_connect(macAddr, CDoctor2Application.getLoginInfo().getAccount(), receivedInt -> {
                    Log.d(STEMOSCOPE_DEBUG_TAG, "STEMOSCOPE_DEBUG_TAG: " + receivedInt);
                    switch (receivedInt) {
                        case 43:
                            //Verified successfully
                            break;
                        case 11:
                            //The App does not have internet connection
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.no_internet_connection), null);
                            });
                            break;
                        case 12:
                            //This App does not have internet access permission
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.no_internet_permission), null);
                            });

                            break;
                        case 13:
                            //Internet connection time out
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.connection_timeout), null);
                            });

                            break;

                        case 23:
                            //Other server error
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.server_error_stemo), null);
                            });

                            break;
                        case 31:
                            //The QR code has a wrong format
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.wrong_qr), null);
                            });

                            break;
                        case 41:
                            //QR code verification failed
                        case 42:
                            //QR code verification failed
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.qa_verification), null);
                            });

                            break;
                        case 51:
                            //Bluetooth in this phone is turned off
                            //TODO   Turn on Bluetooth in your phone
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.turn_off_bluetooth), () -> {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 1);
                                });
                            });

                            break;
                        case 52:
                            //The App does not have permission to use Bluetooth
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.no_permission_bluetooth), () -> {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 1);
                                });
                            });

                            break;
                        case 53:
                            //The location service in this phone is turned off
                        case 54:
                            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(gpsIntent, REQUEST_LOCATION_GPS);
                            //The location service (high accuracy) in this phone is turned off
                        case 55:
                            //The App does not have permission to use location service
                            mContext.runOnUiThread(() -> {
                                ErrorMessage.alertDialog(mContext, getString(R.string.error_dialog_title), getString(R.string.turn_on_location), null);
                            });

                            break;

                    }
                });
            } catch (SecurityException e) {
                ErrorMessage.alertDialog(mContext, "Server issue", "Try again after sometime", new ErrorMessage.OkBtnCallBack() {
                    @Override
                    public void callback() {
                        backToMainIOtPage();
                    }
                });

            }
        }
    }

    /**
     * start recording
     */
    private void startRecoding() {
        Log.d(STEMOSCOPE_DEBUG_TAG, "play sound file");
        if (audioTrack == null) {
            int bufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                    AudioTrack.MODE_STREAM);

        }
        audioTrack.play();

        AudioPlayManger.getInstance().stemo_start(data -> {
            Log.d(STEMOSCOPE_DEBUG_TAG, "sound call back");
            audioTrack.write(data, 0, data.length);
            mContext.runOnUiThread(() -> {
                if (binding.soundWaveVisualizer != null && binding.indicateText != null) {
                    binding.soundWaveVisualizer.setVisibility(View.VISIBLE);

                    int max = getLargest(data);
                    binding.soundWaveVisualizer.update(soundWaveNormalizer.normalize(max));
                }
            });


            if (FileDownLoadManager.getInstance().isStartSave()) {
                FileDownLoadManager.getInstance().writeToFile(data);
            }
        });

    }

    private int getLargest(short[] data) {
        int max = 0;
        for (short num : data) {
            max = Math.max(max, Math.abs(num));
        }
        return max;
    }

    @Subscribe
    public void receiveRssi(BluetoothRssiEvent event) {
        int i = MyBluetoothManager.getInstance().stemo_query_battery();
        String batteryPercentage = i * 20 + "%";
        //showing battery percentage

        mContext.runOnUiThread(() -> {
            binding.indicateTextDesc.setText(mContext.getString(R.string.stemoscope_battery) + batteryPercentage);
        });
    }

    /**
     * #step2 wait for the stemo to get connected
     * once device  get connected update show accordingly
     *
     * @param event tell whether stemo is connected or not
     */
    @Subscribe
    public void receiveConnectedStatus(BlueToothStatusEvent event) {
        String cStatus = event.getStatus() == 1 ? "Connected" : "Disconnected";
        Log.d(STEMOSCOPE_DEBUG_TAG, "new status: " + cStatus);

        if (event.getStatus() == 1) {
            Constant.IS_STEMO_CONNECTED = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, mContext.getString(R.string.stemoscope_connect_toast) + macAddr, Toast.LENGTH_SHORT).show();
                    updateUIConnected();
                    binding.indicateTextDesc.setText(cStatus);
                }
            });

        } else {
            Constant.IS_STEMO_CONNECTED = false;
            FileDownLoadManager.getInstance().setStartSave(false);
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUIDisconnected();
                }
            });

            MyBluetoothManager.getInstance().stemo_end();
        }


    }

    /**
     * update ui on disconnection
     */
    private void updateUIDisconnected() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.recordBtn.setEnabled(false);
                binding.recordBtn.setText(mContext.getString(R.string.stemoscope_record_btn));
                binding.recordBtn.setTag("0");
                binding.indicateText.setText(mContext.getString(R.string.stemoscope_guide));
                binding.soundWaveVisualizer.setVisibility(View.GONE);
            }
        });
        if (binding.soundWaveVisualizer != null) {
            binding.soundWaveVisualizer.recreate();
        }
        //restart the timer for beginging
        remainSeconds = 20;
        binding.timeCount.post(new Runnable() {
            @Override
            public void run() {
                binding.timeCount.setText("" + remainSeconds);
            }
        });


        Toast.makeText(mContext, mContext.getString(R.string.stemoscope_disconnect) + macAddr, Toast.LENGTH_SHORT).show();
    }

    /**
     * update ui on stemo connection
     */
    private void updateUIConnected() {
        mContext.runOnUiThread(() -> {
            binding.viewNotConnected.setVisibility(View.GONE);
            binding.viewStemoConnected.setVisibility(View.VISIBLE);
            binding.recordBtn.setEnabled(true);
        });

    }


    private void setHeartPointTouch() {
        binding.heartPointBtn1.setOnClickListener(v -> {
            if (state != STATE.MEASURING) {
                binding.heartPoint.setImageResource(R.drawable.heartpoint_1);
                FileDownLoadManager.getInstance().setSelectedImage(R.raw.heartpoint_1);
            }
        });
        binding.heartPointBtn2.setOnClickListener(v -> {
            if (state != STATE.MEASURING) {
                binding.heartPoint.setImageResource(R.drawable.heartpoint_2);
                FileDownLoadManager.getInstance().setSelectedImage(R.raw.heartpoint_2);
            }
        });
        binding.heartPointBtn3.setOnClickListener(v -> {
            if (state != STATE.MEASURING) {
                binding.heartPoint.setImageResource(R.drawable.heartpoint_3);
                FileDownLoadManager.getInstance().setSelectedImage(R.raw.heartpoint_3);
            }
        });
        binding.heartPointBtn4.setOnClickListener(v -> {
            if (state != STATE.MEASURING) {
                binding.heartPoint.setImageResource(R.drawable.heartpoint_4);
                FileDownLoadManager.getInstance().setSelectedImage(R.raw.heartpoint_4);
            }
        });
    }


    @Override
    public void onDestroy() {
        //stop times
        disconnect();
        super.onDestroy();
    }

    private void disconnect() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        //reintialize the soundwave
        if (binding.soundWaveVisualizer != null) {
            binding.soundWaveVisualizer.recreate();
        }
        if (EventBus.getDefault().hasSubscriberForEvent(BluetoothRssiEvent.class)) {
            EventBus.getDefault().unregister(this);
        }
        MyBluetoothManager.getInstance().stemo_end();
        FileDownLoadManager.getInstance().setStartSave(false);
        MyBluetoothManager.getInstance().quit_auto_scanning();
        Log.d(STEMOSCOPE_DEBUG_TAG, "on destroy");
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(bluetoothAdapter.isEnabled()) {
//            bluetoothAdapter.disable();
//        }
    }

    @Override
    public void success() {
        hideProgress();
        Toast.makeText(mContext, "Upload Succeed", Toast.LENGTH_LONG).show();
        backToMainIOtPage();
    }

    @Override
    public void failure() {
        hideProgress();
        Toast.makeText(mContext, "Upload fail, Please try again.", Toast.LENGTH_LONG).show();
        backToMainIOtPage();
    }
}