package com.cybermed.cdoc_patient.doctor.doctorFilter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.DoctorDialogFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * filter helper
 */
public class FilterHelper {
    private final DoctorDialogFragmentBinding binding;
    private final Context context;
    //private int specialtySpinnerPositionStore, languageSpinnerPositionStore, stateSpinnerPositionStore;
    private Dialog stateDialog, languageDialog, specialtyDialog;
    private String specialtyFilter, languageFilter, stateFilter;
    private List<SpinnerModel> stateList, specialtyList, languageList;
    private int selectedFilter;
    private FilterCommunicator filterCommunicator;

    public FilterHelper(DoctorDialogFragmentBinding binding, Context context) {
        this.binding = binding;
        this.context = context;
        specialtyDialog = getDialog(R.array.specialities, R.string.select_speciality_title);
        languageDialog = getDialog(R.array.languages, R.string.select_language_title);
        stateDialog = getDialog(R.array.state_name, R.string.select_state_title);
    }

    /**
     * filter dialog listner
     */
    public void filterDialog() {
        filterCommunicator = FilterCommunicator.getInstance();
        stateFilter = filterCommunicator.getState();
        languageFilter = filterCommunicator.getLanguage();
        specialtyFilter = filterCommunicator.getSpecialty();
        setModels();

        if (specialtyFilter.equals(""))
            binding.specialtySpinner.setText("All");
        else
            binding.specialtySpinner.setText(specialtyFilter);

        binding.specialtySpinner.setOnClickListener(v -> {
            specialtyDialog = getDialog(R.array.specialities, R.string.select_speciality_title);
            specialtyDialog.show();
            selectedFilter = 1;
        });

        if (languageFilter.equals(""))
            binding.languageSpinner.setText("All");
        else
            binding.languageSpinner.setText(languageFilter);
        binding.languageSpinner.setOnClickListener(v -> {
            languageDialog = getDialog(R.array.languages, R.string.select_language_title);
            languageDialog.show();
            selectedFilter = 2;
        });

        if (stateFilter.equals(""))
            binding.stateSpinner.setText("All");
        else
            binding.stateSpinner.setText(stateFilter);
        binding.stateSpinner.setOnClickListener(v -> {
            stateDialog = getDialog(R.array.state_name, R.string.select_state_title);
            stateDialog.show();
            selectedFilter = 3;
        });
    }

    /**
     * @param position selected position
     */
    public void updateCurrentState(int position) {
        if (selectedFilter == 1) {
            // specialtySpinnerPositionStore = position;
            SpinnerModel model =
                    specialtyList.get(position);
            specialtyDialog.dismiss();
            if (specialtyFilter.equals("All"))
                specialtyFilter = "";
                //binding.specialtySpinner.setText(R.string.select_speciality_title);
            else
                specialtyFilter = model.getFilterText();
            binding.specialtySpinner.setText(model.getFilterText());

        } else if (selectedFilter == 2) {
            //   languageSpinnerPositionStore = position;
            SpinnerModel model =
                    languageList.get(position);

            languageDialog.dismiss();
            if (languageFilter.equals("All"))
                languageFilter = "";
                // binding.languageSpinner.setText(R.string.select_language_title);
            else
                languageFilter = model.getFilterText();
            binding.languageSpinner.setText(model.getFilterText());


        } else {

            SpinnerModel model =
                    stateList.get(position);

            stateDialog.dismiss();
            if (stateFilter.equals("All"))
                stateFilter = "";
                // binding.stateSpinner.setText(R.string.select_state_title);
            else
                stateFilter = model.getFilterText();
            binding.stateSpinner.setText(model.getFilterText());

        }
    }

    /**
     * apply filter
     */
    public void apply() {
        filterCommunicator.setLanguage(languageFilter);
        filterCommunicator.setSpecialty(specialtyFilter);
        filterCommunicator.setState(stateFilter);
    }

    /**
     * set model
     */
    private void setModels() {
        stateList = new ArrayList<>();
        String[] filters = context.getResources().getStringArray(R.array.state_name);

        for (String filter : filters) {
            stateList.add(new SpinnerModel(filter, false));
        }

        specialtyList = new ArrayList<>();
        filters = context.getResources().getStringArray(R.array.specialities);
        for (String filter : filters) {
            specialtyList.add(new SpinnerModel(filter, false));
        }

        languageList = new ArrayList<>();
        filters = context.getResources().getStringArray(R.array.languages);
        for (String filter : filters) {
            languageList.add(new SpinnerModel(filter, false));
        }
    }

    /**
     * @param id     id
     * @param _title tittle of dialog
     * @return dialog
     */
    private Dialog getDialog(int id, int _title) {
        List<SpinnerModel> ListModel;
        if (id == R.array.state_name)
            ListModel = stateList;
        else if (id == R.array.specialities)
            ListModel = specialtyList;
        else
            ListModel = languageList;

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_spinner_drop_down_list_view, null);
        RecyclerView filters = mView.findViewById(R.id.listItems);
        TextView title = mView.findViewById(R.id.title);
        title.setText(_title);

        //in this case there is no okay button
        mView.findViewById(R.id.okayBtn).setVisibility(View.GONE);
        mView.findViewById(R.id.closeBtn).setOnClickListener(v -> {
            if (selectedFilter == 1)
                specialtyDialog.dismiss();
            else if (selectedFilter == 2)
                languageDialog.dismiss();
            else
                stateDialog.dismiss();
        });

        SpinnerAdapter adapter = new SpinnerAdapter(ListModel, SpinnerAdapter.Source.Filters);
        adapter.setHelper(this);
        filters.setAdapter(adapter);
        filters.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        filters.setHasFixedSize(true);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(mView);
        return alertDialogBuilder.create();
    }

    /**
     * reset filter
     */
    public void resetFilter() {
        if (specialtyDialog != null) {
            selectedFilter = 1;
            updateCurrentState(0);
        }
        if (languageDialog != null) {
            selectedFilter = 2;
            updateCurrentState(0);
        }
        if (stateDialog != null) {
            selectedFilter = 3;
            updateCurrentState(0);
        }
        specialtyFilter = languageFilter = stateFilter = "";
        apply();
    }
}
