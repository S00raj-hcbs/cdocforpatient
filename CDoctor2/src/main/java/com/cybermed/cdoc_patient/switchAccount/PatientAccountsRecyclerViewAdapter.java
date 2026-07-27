package com.cybermed.cdoc_patient.switchAccount;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.Represented_Patient;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.util.ErrorMessage;

import java.util.List;

import static com.cdfortis.datainterface.soap.WebServiceID.delete_auth_link;

public class PatientAccountsRecyclerViewAdapter extends RecyclerView.Adapter<PatientAccountsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Represented_Patient> rep_patients;
    private SelectRepDialog.OnPatientSelected onPatientSelected;
    private SelectRepDialog.OnPatientDeleted onPatientDeleted;

    public PatientAccountsRecyclerViewAdapter(Context context, List<Represented_Patient> rep_patients) {
        this.context = context;
        this.rep_patients = rep_patients;
    }

    public void setOnPatientSelectedCallback(SelectRepDialog.OnPatientSelected onPatientSelectedCallback) {
        onPatientSelected = onPatientSelectedCallback;
    }
    public void setOnPatientDeleted(SelectRepDialog.OnPatientDeleted onPatientDeleted) {
        this.onPatientDeleted = onPatientDeleted;
    }


    @NonNull
    @Override
    public PatientAccountsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_auth_rep, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientAccountsRecyclerViewAdapter.ViewHolder holder, int position) {
        if (position == 0) {
            holder.patientAccount.setText(rep_patients.get(position).user_id + context.getString(R.string.me_account));
            holder.deleteView.setVisibility(View.GONE);
        } else {
            holder.deleteView.setVisibility(View.VISIBLE);
            holder.patientAccount.setText(rep_patients.get(position).user_id);
            String name = rep_patients.get(position).first_name + " " + rep_patients.get(position).last_name;
            holder.name.setText(name);
        }
        // holder.swipeRevealLayout.setLockDrag(true);
        holder.frontView.setOnClickListener(v -> {
           // onPatientSelected.select(rep_patients.get(position));
        });

        if (rep_patients.get(position).user_id.equals(CDoctor2Application.getLoginInfo().getAccount())) {
            holder.onlineButton.setVisibility(View.VISIBLE);
        } else {
            holder.onlineButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rep_patients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView patientAccount;
        TextView name;
        ImageView onlineButton;
        //SwipeRevealLayout swipeRevealLayout;
        LinearLayout frontView;
        ImageView deleteView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientAccount = itemView.findViewById(R.id.rep_account);
            name = itemView.findViewById(R.id.rep_name);
            onlineButton = itemView.findViewById(R.id.icon_online_status);
            // swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            frontView = itemView.findViewById(R.id.front_layout);
            deleteView = itemView.findViewById(R.id.delete_layout);
            deleteView.setOnClickListener(v -> {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(context.getString(R.string.btn_confirm));
                String deleteConfirmMsg = context.getString(R.string.delete_confirm_message, name);
                CharSequence formatMsg = Html.fromHtml(deleteConfirmMsg);
                alertDialog.setMessage(formatMsg);
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, context.getText(R.string.btn_ok), (dialog, which) -> {
                    alertDialog.dismiss();
                    deleteRepresent(getAdapterPosition());
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, context.getText(R.string.btn_cancel), ((dialog, which) -> {
                    alertDialog.dismiss();
                }));
                alertDialog.show();
            });
        }

    }

    private void deleteRepresent(int position) {
        String userId = CDoctor2Application.getLoginInfo().getOriginalAccount();
        String username = rep_patients.get(position).first_name + " " + rep_patients.get(position).last_name;
        OnPostExecute ope = result -> {
            String resStr = result.toString();
            if (resStr.equals("1")) {
                String deleteSuccessMsg = context.getString(R.string.delete_success_message, username);
                CharSequence formatMsg = Html.fromHtml(deleteSuccessMsg);
                ErrorMessage.alertDialog(context, context.getString(R.string.success_dialog_title), formatMsg, () -> {
                  //  onPatientDeleted.delete(rep_patients.get(position));
                });
            } else if (resStr.equals("0")) {
                ErrorMessage.alertDialog(context, context.getString(R.string.error_dialog_title), context.getString(R.string.delete_relation_not_exist), null);
            } else {
                ErrorMessage.alertDialog(context, context.getString(R.string.server_error), context.getString(R.string.delete_relation_failed), null);
            }
        };
        WebService.webServiceAsyncTask(delete_auth_link, ope, userId, rep_patients.get(position).user_id);
    }
}
