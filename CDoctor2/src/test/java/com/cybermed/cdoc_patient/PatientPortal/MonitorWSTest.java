package com.cybermed.cdoc_patient.PatientPortal;

import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.Monitor_BO;
import com.cdfortis.datainterface.soap.model.Monitor_BP;
import com.cdfortis.datainterface.soap.model.Monitor_Glucose;
import com.cdfortis.datainterface.soap.model.Monitor_HR;
import com.cdfortis.datainterface.soap.model.Monitor_Weight;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cdfortis.datainterface.soap.model.VisitRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ksoap2.serialization.SoapObject;

import java.util.Vector;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_health_records;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_visit_record;
import static com.cybermed.cdoc_patient.Utility.Utility.unlockWebservices;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MonitorWSTest {

    private Vector<VisitRecord> visitRecords;

    @Before
    public void setUp() throws Exception {
        unlockWebservices();
        WebService.webServiceAsyncTask(get_patient_visit_record, "demo1@gmail.com");
        Object obj = get_patient_visit_record.getAsyncTask().get();
        visitRecords = new SoapObjectVector<>(VisitRecord.class, (SoapObject) obj);
    }

    @Test
    public void HR() throws Exception {
        WebService.webServiceAsyncTask(get_patient_health_records, "1",
                visitRecords.get(0).org_code, visitRecords.get(0).account);
        Object obj = get_patient_health_records.getAsyncTask().get();
        Vector<Monitor_HR> monitor_hrs = new SoapObjectVector<>(Monitor_HR.class, (SoapObject) obj);
        assertEquals(2, monitor_hrs.size());
    }

    @Test
    public void weight() throws Exception {
        WebService.webServiceAsyncTask(get_patient_health_records, "2",
                visitRecords.get(1).org_code, visitRecords.get(1).account);
        Object obj = get_patient_health_records.getAsyncTask().get();
        Vector<Monitor_Weight> monitor_weights = new SoapObjectVector<>(Monitor_Weight.class, (SoapObject) obj);
        assertEquals(2, monitor_weights.size());
    }

    @Test
    public void BO() throws Exception {
        WebService.webServiceAsyncTask(get_patient_health_records, "3",
                visitRecords.get(0).org_code, visitRecords.get(0).account);
        Object obj = get_patient_health_records.getAsyncTask().get();
        Vector<Monitor_BO> monitor_bos = new SoapObjectVector<>(Monitor_BO.class, (SoapObject) obj);
        assertEquals(2, monitor_bos.size());
    }

    @Test
    public void BP() throws Exception {
        WebService.webServiceAsyncTask(get_patient_health_records, "4",
                visitRecords.get(0).org_code, visitRecords.get(0).account);
        Object obj = get_patient_health_records.getAsyncTask().get();
        Vector<Monitor_BP> monitor_bps = new SoapObjectVector<>(Monitor_BP.class, (SoapObject) obj);
        assertEquals(2, monitor_bps.size());
    }

    @Test
    public void Glucose() throws Exception {
        WebService.webServiceAsyncTask(get_patient_health_records, "5",
                visitRecords.get(0).org_code, visitRecords.get(0).account);
        Object obj = get_patient_health_records.getAsyncTask().get();
        Vector<Monitor_Glucose> monitor_glucoses = new SoapObjectVector<>(Monitor_Glucose.class, (SoapObject) obj);
        assertEquals(2, monitor_glucoses.size());
    }
}
