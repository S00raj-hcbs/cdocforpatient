package com.cybermed.cdoc_patient.random;


import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.VitalInfo;
import com.cybermed.cdoc_patient.webapi.APIs.AuthApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.RestApiCall;
import com.cybermed.cdoc_patient.webapi.model.request.ApiCredentials;
import com.cybermed.cdoc_patient.webapi.model.response.ApiToken;
import com.cybermed.cdoc_patient.webapi.model.response.ErrorResponse;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cdfortis.datainterface.soap.WebServiceID.CreateNewUser_Android_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.HelloWorld;
import static com.cdfortis.datainterface.soap.WebServiceID.create_appointment_on_EMR_Android_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.get_Pat_Vitals_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.retrieve_patient_routine_default_message;
import static com.cdfortis.datainterface.soap.WebServiceID.save_patient_routine_default_message;


@RunWith(AndroidJUnit4.class)
public class WS_Call {
    Context instrumentationContext;

    @Before
    public void setUp() {
        //instrumentationContext = InstrumentationRegistry.getInstrumentation().getContext();
        //WebService.getInstance().switchToQaSite();
    }

    @Test
    public void test1() throws Exception {
//        while(true){
//            Thread.sleep(100);
//        }
        OnPostExecute ope = result -> {
            VitalInfo PV = new VitalInfo((SoapObject) result);
          Log.d("", "a");
        };
         WebService.webServiceAsyncTask(get_Pat_Vitals_v2, ope, "demo7@gmail.com");
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void test2() throws Exception {

        new Thread(() -> {
            try {
                new Object();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(Long.MAX_VALUE);
    }


    @Test
    public void test20() throws Exception {
        Log.d("test", "test begin");
        OnPostExecute ope = result -> {
          String res = result.toString();
          Log.d("test", res);
        };
        //WebService.getInstance().switchToQaSite();

        WebService.webServiceAsyncTask(HelloWorld, ope);
      
        Thread.sleep(300000);
    }

    @Test
    public void test3() throws Exception {

        //WebService.getInstance().switchToQaSite();
        create_appointment_on_EMR_Android_v2.setDisableNullRestriction(true);
        WebService.webServiceAsyncTask(create_appointment_on_EMR_Android_v2, "", "jayvini227@icloud.com", "1760455497", "2459", "01/25/2019 08:45 AM");
//        WebService.webServiceAsyncTask(create_appointment_on_EMR_Android_v2, "", "cdoctest2@cdoc.com", "cdoc", "242", "01/25/2019 08:45 AM");
//        WebService.webServiceAsyncTask(create_appointment_on_EMR_Android_v2, "", "cdoctest3@cdoc.com", "cdoc", "242", "01/25/2019 08:45 AM");

        Thread.sleep(3000);
    }

    @Test
    public void save_patient_routine_default_message() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String now = dateFormat.format(new Date());

        WebService.webServiceAsyncTask(save_patient_routine_default_message,
                "demo1@gmail.com", now, now, now, now, now);


        Thread.sleep(3000);
    }

    @Test
    public void retrieve_patient_routine_default_message() throws Exception {

        WebService.webServiceAsyncTask(retrieve_patient_routine_default_message,
                "demo1@gmail.com");

        Object o = retrieve_patient_routine_default_message.getAsyncTask().get();
        o.toString();


        Thread.sleep(3000);
    }

    @Test
    public void test_CM_QA_AND_CM_QA_PORTAL() throws Exception {


        CreateNewUser_Android_v2.setDisableNullRestriction(true);
        WebService.webServiceAsyncTask(CreateNewUser_Android_v2,
                "cdoctest@cdoc.com", "password", "CDoc", "", "Test1", "M", "11/10/1967", "180 Tices", "", "New Brunswick", "NJ", "08901", "123-235-7957",
                "cgpc", "remote_monitoring");

        WebService.webServiceAsyncTask(CreateNewUser_Android_v2,
                "cdoctest2@cdoc.com", "password", "CDoc", "", "Test2", "M", "5/17/1967", "180 Tices", "", "New Brunswick", "NJ", "08901", "123-235-7957",
                "cgpc", "remote_monitoring");

        WebService.webServiceAsyncTask(CreateNewUser_Android_v2,
                "cdoctest3@cdoc.com", "password", "CDoc", "", "Test3", "M", "12/1/1967", "180 Tices", "", "New Brunswick", "NJ", "08901", "123-235-7957",
                "cgpc", "remote_monitoring");

        Thread.sleep(3000);

    }

    @Test
    public void testAPI() throws Exception {
        AuthApi authApi = RestApiCall.createNonAuthService(AuthApi.class);

        ApiCredentials credentials = AuthManager.createKeyBasedCredential();
        Call<ApiToken> apiTokenCall = authApi.getToken(credentials.getGrant_type(), credentials.getApp_id(), credentials.getApi_key());

        apiTokenCall.enqueue(new Callback<ApiToken>() {
            @Override
            public void onResponse(Call<ApiToken> call, Response<ApiToken> response) {
                if(!response.isSuccessful()) {
                    if(response.code() == 500) {

                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            ErrorResponse error = new Gson().fromJson(errorBody, ErrorResponse.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ApiToken> call, Throwable t) {
                Log.d("error", "error");
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }

}


