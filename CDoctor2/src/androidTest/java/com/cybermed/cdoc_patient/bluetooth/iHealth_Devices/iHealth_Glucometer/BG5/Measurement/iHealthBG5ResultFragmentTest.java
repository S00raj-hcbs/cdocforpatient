package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.BG5.Measurement;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;

@RunWith(AndroidJUnit4.class)
public class iHealthBG5ResultFragmentTest {

    FragmentScenario<iHealthBG5ResultFragment> fragmentScenario;


    @Before
    public void setUp() throws Exception {
        Bundle bundle = new Bundle();
        fragmentScenario = launchInContainer(iHealthBG5ResultFragment.class, bundle);
    }

    @Test
    public void layout(){
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
    }
}