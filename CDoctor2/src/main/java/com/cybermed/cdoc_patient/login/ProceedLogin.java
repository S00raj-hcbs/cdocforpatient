package com.cybermed.cdoc_patient.login;

import android.app.Activity;
import android.app.ProgressDialog;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.Patient_Demographic;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseActivity;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.ws.WS;

import org.ksoap2.serialization.SoapObject;

import static com.cdfortis.datainterface.soap.WebServiceID.get_PatientDemographic_Android;
import static com.cdfortis.datainterface.soap.WebServiceID.verifyPatientLogin_Android;
import static com.cybermed.cdoc_patient.common.BaseActivity.STATUS_ON_LINE;

public class ProceedLogin {

    private static ProgressDialog loadingLogin;

    public static void verifyCredential(Activity activity, String email, String password, Runnable onVerifySuccess, Runnable onVerifyFailed) {
        loadingLogin = ProgressDialog.show(activity, "",
                activity.getString(R.string.verifying_process), true);
        CDoctor2Application cDoctor2Application = (CDoctor2Application)(activity.getApplication());

        OnPostExecute ope = result -> {
            loadingLogin.dismiss();
            if (result.toString().equals("1")) {
                onVerifySuccess.run();
            } else {
                onVerifyFailed.run();
            }
        };
        WebService.webServiceAsyncTask(verifyPatientLogin_Android, ope, email, password, cDoctor2Application.getLoginInfo().getOneSignalUserId());
    }

    public static void login(Activity activity, String email, String password, Runnable onLoginSuccess, Runnable onLoginFailed) {
        getUserInfo(activity, email, password, onLoginSuccess, onLoginFailed);
    }

    public static void loginAsRep(Activity activity, String originId, String userId, String password, Runnable onLoginSuccess, Runnable onLoginFailed) {
        CDoctor2Application cDoctor2Application = (CDoctor2Application)(activity.getApplication());
        cDoctor2Application.setLogin(true);
        if(originId.equalsIgnoreCase(userId)) {
            cDoctor2Application.setAuthRep(originId, false);
        } else {
            cDoctor2Application.setAuthRep(originId, true);
        }
        login(activity, userId, password, onLoginSuccess, onLoginFailed);
    }

    private static void getUserInfo(Activity activity, String email,String password, Runnable onLoginSuccess, Runnable onLoginFailed) {
        CDoctor2Application cDoctor2Application = (CDoctor2Application)(activity.getApplication());
        OnPostExecute ope = result -> {
            Patient_Demographic patientInfo = new Patient_Demographic((SoapObject) result);

            UserInfo userInfo = new UserInfo();
            userInfo.deserialize(patientInfo);
            cDoctor2Application.processUserLogin2(email, password, userInfo);
            setOnLineStatus(onLoginSuccess, onLoginFailed);
        };

        WebService.webServiceAsyncTask(get_PatientDemographic_Android, ope, email);
    }

    private static void setOnLineStatus(Runnable onLoginSuccess, Runnable onLoginFailed) {
        OnPostExecute ope = result -> {
            if(loadingLogin != null) {
                loadingLogin.dismiss();
            }
            if(result.toString().equals("1")) {
                onLoginSuccess.run();
            } else {
                onLoginFailed.run();
            }
        };

        WS.setPatientDeviceStatus(STATUS_ON_LINE, ope);
    }

    public static void logout(Activity activity) {
        WS.setPatientDeviceStatus(BaseActivity.STATUS_OFF_LINE);
        CDoctor2Application cDoctor2Application = (CDoctor2Application) activity.getApplication();
        cDoctor2Application.setLogin(false);
    }
}
