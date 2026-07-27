package com.cybermed.cdoc_patient.main;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.DocInfo;

import org.junit.Before;
import org.junit.Test;
import org.ksoap2.serialization.SoapObject;

import static com.cdfortis.datainterface.soap.WebServiceID.count_online_provider;
import static com.cdfortis.datainterface.soap.WebServiceID.generate_OnlineRoomNumber;
import static com.cdfortis.datainterface.soap.WebServiceID.random_choose_online_provider;
import static org.junit.Assert.*;

public class TabletMainFragmentTest {
    private String user_id;
    private String online_room;

    @Before
    public void setUp(){
        user_id = "demo1@gmail.com";
    }

    @Test
    public void OnlineProviderCount_WS(){
        WebService.webServiceAsyncTask(count_online_provider , user_id);
        try{
            Object result = count_online_provider.getAsyncTask().get();

            Integer integer = Integer.valueOf(result.toString());

            if (integer <  0) {
                fail("count_online_provider ERROR");
            }

        }catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void random_choose_online_provider_WS(){
        WebService.webServiceAsyncTask(random_choose_online_provider , user_id);

        try{
            Object result = random_choose_online_provider.getAsyncTask().get();

            DocInfo docInfo = new DocInfo((SoapObject) result);

            if (docInfo.getOrg_code().equals("")) {
                fail("random_choose_online_provider ERROR");
            }

        }catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void bookAppointmentTest(){

        OnPostExecute ope = result -> {
            online_room = result.toString();
        };

        WebService.webServiceAsyncTask(generate_OnlineRoomNumber, ope);

        try {
            Thread.sleep(3000);
        }catch (Exception e){}

    }
}