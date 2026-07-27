package com.cybermed.cdoc_patient.family;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.Represented_Patient;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.view.MyAlertDialog;

import java.util.ArrayList;
import java.util.List;

import static com.cdfortis.datainterface.soap.WebServiceID.delete_auth_link;

public class AuthRepAdapter extends RecyclerView.Adapter<AuthRepAdapter.ViewHolder> {

    private Context context;
    private List<Represented_Patient> rep_patients;
    private OnPatientSelected onPatientSelected;
    private OnPatientDeleted onPatientDeleted;

    public AuthRepAdapter(Context context) {
        this.context = context;
        this.rep_patients = new ArrayList<>();
    }

    public void appendList(List<Represented_Patient> rep_patients) {
        this.rep_patients.clear();
        this.rep_patients.addAll(rep_patients);
        notifyDataSetChanged();
    }

    public void setOnPatientSelected(OnPatientSelected onPatientSelected) {
        this.onPatientSelected = onPatientSelected;
    }

    public void setOnPatientDeleted(OnPatientDeleted onPatientDeleted) {
        this.onPatientDeleted = onPatientDeleted;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_auth_rep, parent, false);
        return new AuthRepAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthRepAdapter.ViewHolder holder, int position) {
        if (!rep_patients.get(position).user_id.equals(CDoctor2Application.getLoginInfo().getOriginalAccount())) {
            holder.patientAccount.setText(rep_patients.get(position).user_id);
            String name = rep_patients.get(position).first_name + " " + rep_patients.get(position).last_name;
            holder.name.setText(name);
            holder.name.setVisibility(View.VISIBLE);
            holder.deleteView.setVisibility(View.VISIBLE);
        }

        if (rep_patients.get(position).user_id.equals(CDoctor2Application.getLoginInfo().getAccount())) {
          //  holder.onlineButton.setVisibility(View.VISIBLE);
            holder.deleteView.setVisibility(View.GONE);
        } else {
           // holder.onlineButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rep_patients.size();
    }

    public interface OnPatientSelected {
        void select(Represented_Patient represented_patient);
    }

    public interface OnPatientDeleted {
        void delete(Represented_Patient represented_patient);
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
                    onPatientDeleted.delete(rep_patients.get(position));
                });
            } else if (resStr.equals("0")) {
                ErrorMessage.alertDialog(context, context.getString(R.string.error_dialog_title), context.getString(R.string.delete_relation_not_exist), null);
            } else {
                ErrorMessage.alertDialog(context, context.getString(R.string.server_error), context.getString(R.string.delete_relation_failed), null);
            }
        };
        WebService.webServiceAsyncTask(delete_auth_link, ope, userId, rep_patients.get(position).user_id);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView patientAccount;
        TextView name;
        //ImageView onlineButton;
        //SwipeRevealLayout swipeRevealLayout;
        RelativeLayout frontView;
        ImageView deleteView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientAccount = itemView.findViewById(R.id.rep_account);
            name = itemView.findViewById(R.id.rep_name);
          //  onlineButton = itemView.findViewById(R.id.icon_online_status);
            // swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            frontView = itemView.findViewById(R.id.front_layout);
            deleteView = itemView.findViewById(R.id.delete_layout);
            deleteView.setOnClickListener(v -> {
                //viewBinderHelper.closeLayout(String.valueOf(position));
                String name = rep_patients.get(getAdapterPosition()).first_name + " " + rep_patients.get(getAdapterPosition()).last_name;
                String deleteConfirmMsg = context.getString(R.string.delete_confirm_message, name);
                CharSequence formatMsg = Html.fromHtml(deleteConfirmMsg);
                MyAlertDialog dialog = new MyAlertDialog(context);
                dialog.show();
                dialog.setDialogTitle(context.getString(R.string.btn_confirm));
                dialog.setDialogContent(formatMsg.toString());
                dialog.setLeftClickListener(context.getString(R.string.btn_cancel), view -> dialog.dismiss());
                dialog.setRightClickListener(context.getString(R.string.btn_ok), view -> {
                    dialog.dismiss();
                    deleteRepresent(getAdapterPosition());
                });




            });
            frontView.setOnClickListener(v -> {
                if (!rep_patients.get(getAdapterPosition()).user_id.
                        equals(CDoctor2Application.getLoginInfo().getAccount())) {
                    if (onPatientSelected != null) {
                        onPatientSelected.select(rep_patients.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
