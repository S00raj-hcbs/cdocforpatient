package com.cybermed.cdoc_patient.PatientPortal;

import android.os.Bundle;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.me.MonitorFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;

@RunWith(AndroidJUnit4.class)
public class MonitorEventBus {

    UserInfo userInfo;
    FragmentScenario<MonitorFragment> fragmentScenario;


    @Before
    public void setUp(){
        userInfo = new UserInfo();
        userInfo.setEmail("demo6@gmail.com");
        Bundle bundle = new Bundle();
        bundle.putSerializable(USERINFOKEY, userInfo);
        fragmentScenario = launchInContainer(MonitorFragment.class, bundle);
    }

    @Test
    public void test() throws  Exception{
        Thread.sleep(50000);
    }
}
