package com.cybermed.cdoc_patient.doctor.docDetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.text.Html;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.Represented_Patient;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.family.AuthRepAdapter;
import com.cybermed.cdoc_patient.login.ProceedLogin;
import com.cybermed.cdoc_patient.switchAccount.SelectRepDialog;
import com.cybermed.cdoc_patient.util.ErrorMessage;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

public class DocHelper {
    static String repUserId;

    /**
     * doc family member dialog
     * @param context context
     * @param iDocHelperCallBack callback
     */
    public static void getAuthRep(Activity context, IDocHelperCallBack iDocHelperCallBack) {
        String userId = CDoctor2Application.getLoginInfo().getOriginalAccount();
        boolean isOriginalAcct = userId.equalsIgnoreCase(CDoctor2Application.getLoginInfo().getAccount());
        OnPostExecute ope = result -> {
            List<Represented_Patient> patientList = new ArrayList<>(new SoapObjectVector<>(Represented_Patient.class, (SoapObject) result));
            if (patientList.size() == 0 || patientList.get(0).errorMsg != null) {
                iDocHelperCallBack.consultNow();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(context.getString(R.string.btn_confirm));
                CharSequence message = context.getString(R.string.select_account_msg);
                if (!isOriginalAcct) {
                    String username = CDoctor2Application.getLoginInfo().getUserInfo().getFirstName() + " " + CDoctor2Application.getLoginInfo().getUserInfo().getLastname();
                    message = Html.fromHtml(context.getString(R.string.select_account_msg_rep, username));
                }
                alertDialog.setMessage(message);
                CharSequence posButton = context.getString(R.string.family_member_title);
                if (!isOriginalAcct) {
                    posButton = context.getString(R.string.switch_account);
                }
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, posButton, (dialog, which) -> {
                    alertDialog.dismiss();
                    showSelectRepDialog(patientList, context, iDocHelperCallBack);
                });
                CharSequence negButton = context.getString(R.string.myself_btn);
                if (!isOriginalAcct) {
                    negButton = context.getString(R.string.myself_btn_rep);
                }
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, negButton, ((dialog, which) -> {
                    alertDialog.dismiss();
                    iDocHelperCallBack.consultNow();
                }));
                alertDialog.show();
            }
        };
        WebService.webServiceAsyncTask(WebServiceID.get_rep_patients, ope, userId);
    }

    /**
     *
     * @param patientList family member list
     * @param context context
     * @param iDocHelperCallBack callback
     */
    static void showSelectRepDialog(List<Represented_Patient> patientList, Activity context, IDocHelperCallBack iDocHelperCallBack) {
        String userId = CDoctor2Application.getLoginInfo().getOriginalAccount();
        //patientList.add(0, new Represented_Patient(userId));
        SelectRepDialog selectRepDialog = SelectRepDialog.newInstance(context, patientList);
        selectRepDialog.setOnPatientDeleteCallback(new AuthRepAdapter.OnPatientDeleted() {
            @Override
            public void delete(Represented_Patient represented_patient) {
                selectRepDialog.dismiss();
            }
        });
        selectRepDialog.setOnPatientSelectedCallback(represented_patient -> {
            ProceedLogin.logout(context);
            if (represented_patient != null) {
                repUserId = represented_patient.user_id;
            } else repUserId = userId;
            ProceedLogin.loginAsRep(context, userId, repUserId, CDoctor2Application.getLoginInfo().getPwd(), () -> {
                        iDocHelperCallBack.consultNow();
                        selectRepDialog.dismiss();
                    },
                    () -> ErrorMessage.alertDialog(context, context.getString(R.string.server_error), context.getString(R.string.switch_fail_message), null));
        });
        selectRepDialog.show();
    }

    public interface IDocHelperCallBack {
        void consultNow();
    }
}
