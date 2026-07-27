package com.cybermed.cdoc_patient.doctor.doctorFilter;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.DialogFragment;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.DoctorDialogFragmentBinding;

/**
 * filter dialog
 */
public class DoctorDialogFilter extends DialogFragment {
    DoctorDialogFragmentBinding binding;
    FilterHelper filterHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.doctor_dialog_fragment, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {

        Window window = requireDialog().getWindow();
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, true);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

            WindowInsetsControllerCompat controller =
                    WindowCompat.getInsetsController(window, window.getDecorView());
            controller.setAppearanceLightStatusBars(true); // Use dark icons on light bg
            controller.setAppearanceLightNavigationBars(true);
        }

        // Apply insets manually to root view
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        }

        binding = DoctorDialogFragmentBinding.bind(view);
        filterHelper = new FilterHelper(binding, getContext());
        filterHelper.filterDialog();

        binding.backBtn.setOnClickListener(v -> this.dismiss());
        binding.btnApply.setOnClickListener(v -> {
            filterHelper.apply();
            this.dismiss();
        });
        binding.btnReset.setOnClickListener(v -> filterHelper.resetFilter());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }


}