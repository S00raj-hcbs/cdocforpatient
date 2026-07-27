package com.cybermed.cdoc_patient.doctor.doctorFilter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.CustomSpinnerTextviewBinding;
import com.cybermed.cdoc_patient.family.RegisterFamilyMember;
import com.cybermed.cdoc_patient.login.signup.SignUp3Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * spinner view for filter
 */
public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.FilterHolder> {


    public enum Source {Filters, Appointment, SignUp, Familysignup}

    private final List<SpinnerModel> items;
    private final List<Integer> selected;
    private FilterHelper helper;
    private final Source source;
    private SignUp3Fragment fragment;
    private RegisterFamilyMember Dialog;

    public SpinnerAdapter(List<SpinnerModel> items, Source source) {
        this.items = items;
        this.source = source;
        this.selected = new ArrayList<>();
    }

    public void setHelper(FilterHelper helper) {
        this.helper = helper;
    }

    public void setFragment(SignUp3Fragment fragment) {
        this.fragment = fragment;
    }
    public void setDialogue(RegisterFamilyMember Dialog) {
        this.Dialog = Dialog;
    }
    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_spinner_textview, parent, false);
        CustomSpinnerTextviewBinding binding = CustomSpinnerTextviewBinding.bind(itemView);
        return new FilterHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, int position) {
        SpinnerModel model = items.get(position);
        holder.binding.getRoot().setOnClickListener(v -> {
            switch (source) {
                case Filters:
                    helper.updateCurrentState(position);
                    break;
                case Appointment:
                    boolean state = model.isSelectedFilter();
                    if (state) {
                        model.setSelectedFilter(false);
                        selected.remove((Integer) position);
                    } else {
                        model.setSelectedFilter(true);
                        selected.add(position);
                    }
                    notifyItemChanged(position);
                    break;
                case SignUp:
                    fragment.setState(model.getFilterText());
                    break;
                case Familysignup:
                    Dialog.setState(model.getFilterText());
            }
        });
        holder.binding.setModel(model);
    }

    public void setSelected(List<Integer> selectedPositions) {
        // Clear the previous selections
        for (int pos : selected) {
            items.get(pos).setSelectedFilter(false);
        }
        selected.clear();

        // Set the new selections
        for (int pos : selectedPositions) {
            if (pos >= 0 && pos < items.size()) {
                items.get(pos).setSelectedFilter(true);
                selected.add(pos);
            }
        }

        // Notify the adapter to refresh the view
        notifyDataSetChanged();
    }

    @Override

    public int getItemCount() {
        return items.size();
    }

    public List<Integer> getSelected() {
        return selected;
    }

    static class FilterHolder extends RecyclerView.ViewHolder {
        CustomSpinnerTextviewBinding binding;

        public FilterHolder(@NonNull CustomSpinnerTextviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
