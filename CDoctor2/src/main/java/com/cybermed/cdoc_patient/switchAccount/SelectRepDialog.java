package com.cybermed.cdoc_patient.switchAccount;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cdfortis.datainterface.soap.model.Represented_Patient;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.FragmentFamilyBinding;
import com.cybermed.cdoc_patient.family.AuthRepAdapter;

import java.util.List;

public class SelectRepDialog extends Dialog {

    List<Represented_Patient> represented_patients;
    private AuthRepAdapter patientAccountsRecyclerViewAdapter;
    AuthRepAdapter.OnPatientSelected callBack;
    FragmentFamilyBinding binding;

    public interface OnPatientSelected {
        void select(AuthRepAdapter.OnPatientSelected represented_patient);
    }

    public interface OnPatientDeleted {
        void delete(AuthRepAdapter.OnPatientSelected represented_patient);
    }

    public SelectRepDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static SelectRepDialog newInstance(Activity activity, List<Represented_Patient> represented_patients) {
        SelectRepDialog dialog = new SelectRepDialog(activity, R.style.AppTheme_NoActionBar);
        dialog.setCanceledOnTouchOutside(false);
        dialog.patientAccountsRecyclerViewAdapter = new AuthRepAdapter(activity);
        dialog.patientAccountsRecyclerViewAdapter.appendList(represented_patients);
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //reuse the view of family member fragment
        binding = FragmentFamilyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!CDoctor2Application.getLoginInfo().getOriginalAccount()
                .equals(CDoctor2Application.getLoginInfo().getAccount())) {
            binding.btnCurrent.setVisibility(View.VISIBLE);
            binding.btnCurrent.setText(R.string.main_acc_call);
        } else binding.btnCurrent.setVisibility(View.GONE);
        binding.btnAddFamilyDialog.setVisibility(View.INVISIBLE);
        binding.backBtn.setOnClickListener(v -> {
           dismiss();
          //  callBack.select(null);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.recyclerView.setAdapter(patientAccountsRecyclerViewAdapter);
        binding.btnCurrent.setOnClickListener(v -> callBack.select(null));
    }

    public void setOnPatientSelectedCallback(AuthRepAdapter.OnPatientSelected onPatientSelectedCallback) {
        callBack = onPatientSelectedCallback;
        patientAccountsRecyclerViewAdapter.setOnPatientSelected(onPatientSelectedCallback);
    }

    public void setOnPatientDeleteCallback(AuthRepAdapter.OnPatientDeleted onPatientDeletedCallback) {
        patientAccountsRecyclerViewAdapter.setOnPatientDeleted(onPatientDeletedCallback);
    }
}
