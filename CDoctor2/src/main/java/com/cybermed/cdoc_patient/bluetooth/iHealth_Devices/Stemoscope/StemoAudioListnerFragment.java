package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.Stemoscope;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.StemoAudioHeartBinding;
import com.cybermed.cdoc_patient.util.ErrorMessage;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

public class StemoAudioListnerFragment extends BaseFragment {
    StemoAudioHeartBinding binding;
    MediaPlayer mp;
    private Timer timer;

    private volatile long remainSeconds;
    CountDownTimer countDownTimer;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.stemo_audio_heart, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        binding.toolbar.txtTittle.setText(getString(R.string.iot_stemoscope));
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_stemoAudioFragment_to_IOT_MainPage_Fragment);
            }
        });


        mp = new MediaPlayer();
        File wavFile = new File(CDoctor2Application.application.getFilesDir().getAbsolutePath(), "stemoscope.wav");
        try {
            mp.setDataSource(wavFile.getAbsolutePath());
            mp.prepare();
            remainSeconds = mp.getDuration();
            long minutes = (mp.getDuration() / 1000) / 60;
            long seconds = ((mp.getDuration() / 1000) % 60);
            binding.txtTimer.setText(minutes + ":" + seconds);

        } catch (IOException ex) {
            ex.printStackTrace();
            ErrorMessage.alertDialog(getContext(), getContext().getString(R.string.error_dialog_title),
                    getContext().getString(R.string.no_last_record), new ErrorMessage.OkBtnCallBack() {
                        @Override
                        public void callback() {
                            Navigation.findNavController(view).navigate(R.id.action_stemoAudioFragment_to_IOT_MainPage_Fragment);
                        }
                    });
        }
        countDownTimer = new CountDownTimer(remainSeconds, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mp.isPlaying()) {
                            long minutes = (mp.getCurrentPosition() / 1000) / 60;
                            long seconds = ((mp.getCurrentPosition() / 1000) % 60);
                            binding.txtTimer.setText(minutes + ":" + seconds);
                            binding.soundWaveVisualizer.update(mp.getCurrentPosition());
                        }
                    }
                });

            }

            @Override
            public void onFinish() {
                mp.pause();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.imgPlayPause.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause));
                        binding.imgPlayPause.setTag("0");
                        countDownTimer.cancel();
                        binding.soundWaveVisualizer.recreate();
                    }
                });


            }
        };
        binding.imgPlayPause.setOnClickListener(v -> {
            if (binding.imgPlayPause.getTag().equals("0")) {
                binding.imgPlayPause.setTag("1");
                binding.imgPlayPause.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play));
                if (!mp.isPlaying()) {
                    mp.start();
                    countDownTimer.start();

                }
            } else {
                binding.imgPlayPause.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause));
                binding.imgPlayPause.setTag("0");
                mp.pause();
                countDownTimer.cancel();
                binding.soundWaveVisualizer.update(mp.getCurrentPosition());

            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
        }
    }
}
