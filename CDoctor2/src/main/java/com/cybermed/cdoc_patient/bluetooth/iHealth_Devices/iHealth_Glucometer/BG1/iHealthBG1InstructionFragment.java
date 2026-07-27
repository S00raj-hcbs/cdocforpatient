package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.BG1;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.ButterKnifeFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.SubjectInterface;
import com.cybermed.cdoc_patient.databinding.FragmentBg1InstructionsBinding;

import org.jetbrains.annotations.NotNull;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;

public class iHealthBG1InstructionFragment extends ButterKnifeFragment /*implements OnBackPressedListener*/ {


    Disposable disposable;
    FragmentBg1InstructionsBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bg1_instructions, container, false);

        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        Subject<STATE> subject = ((SubjectInterface) getActivity()).getSubject();

        disposable = subject.subscribe(state -> {
            switch(state){
                case CONNECTING:
                    connectingShow();
                    break;
                case MEASURING:
                    Toast.makeText(getContext(), "Measuring", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTED:
                    connectedShow();
                    break;
                case STRIP_IN:
                    stripInShow();
                    break;
                case RESULT:
                    Navigation.findNavController(getView()).navigate(R.id.action_iHealthBG1InstructionFragment_to_iHealthBG5ResultFragment);
                    break;
            }
        }, error -> {

        }, () -> {

        });
    }

    private void stripInShow() {
        binding.BG1Instruction.setText("1. Place lancet in lancer and prick finger\n\n2. Place blood on strip");
        binding.BG1InstructionImage.setImageResource(R.drawable.get_blood);
        binding.BG1StepImage.setImageResource(R.drawable.rpm_glucose_step_3);
        binding.btnQuit.setVisibility(View.GONE);
    }

    private void connectedShow() {
        binding.BG1Instruction.setText(getResources().getString(R.string.BG1_Insert_Strip));
        binding.BG1InstructionImage.setImageResource(R.drawable.strip_in);
        binding.BG1StepImage.setImageResource(R.drawable.rpm_glucose_step_2);
        binding.btnQuit.setVisibility(View.GONE);
    }

    private void connectingShow() {
        binding.BG1Instruction.setText(getResources().getString(R.string.BG1_Plug_In));
        binding.BG1InstructionImage.setImageResource(R.drawable.plug_in);
        binding.BG1StepImage.setImageResource(R.drawable.rpm_glucose_step_1);
        binding.btnQuit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.dispose();
    }



}