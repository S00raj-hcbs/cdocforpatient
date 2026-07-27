package com.cybermed.cdoc_patient.PatientPortal;

import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.ImmunizationRecord;
import com.cdfortis.datainterface.soap.model.LabReportRecord;
import com.cdfortis.datainterface.soap.model.MedicationRecord;
import com.cdfortis.datainterface.soap.model.ReferralRecord;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cdfortis.datainterface.soap.model.VisitRecord;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ksoap2.serialization.SoapObject;

import java.util.Vector;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_medical_records;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_visit_record;
import static com.cybermed.cdoc_patient.Utility.Utility.unlockWebservices;

@RunWith(AndroidJUnit4.class)
public class PatientPortalWSTest {

    Vector<VisitRecord> visitRecords;

    @Before
    public void setUp() throws Exception {
        unlockWebservices();
        WebService.webServiceAsyncTask(get_patient_visit_record, "WILLIAM@NOWHERE.COM");
        Object obj = get_patient_visit_record.getAsyncTask().get();
        if (obj instanceof SoapObject)
            visitRecords = new SoapObjectVector<>(VisitRecord.class, (SoapObject) obj);
    }

    @Test
    public void MediactionTest() throws Exception {
        WebService.webServiceAsyncTask(get_patient_medical_records, "1",
                visitRecords.get(0).org_code, visitRecords.get(0).account);
        Object obj = get_patient_medical_records.getAsyncTask().get();
        Vector<MedicationRecord> medicationRecords = new SoapObjectVector<>(MedicationRecord.class, (SoapObject) obj);
        assertEquals(2, medicationRecords.size());
    }

    @Test
    public void ImmunizationTest() throws Exception {
        WebService.webServiceAsyncTask(get_patient_medical_records, "2",
                visitRecords.get(0).org_code, visitRecords.get(0).account);
        Object obj = get_patient_medical_records.getAsyncTask().get();
        Vector<ImmunizationRecord> ImmunizationRecord = new SoapObjectVector<>(ImmunizationRecord.class, (SoapObject) obj);
        assertEquals(2, ImmunizationRecord.size());
    }

    @Test
    public void LabReportTest() throws Exception {
        WebService.webServiceAsyncTask(get_patient_medical_records, "3",
                "pansy", "1001");
        Object obj = get_patient_medical_records.getAsyncTask().get();
        Vector<LabReportRecord> LabReportRecord = new SoapObjectVector<>(LabReportRecord.class, (SoapObject) obj);
        assertEquals(2, LabReportRecord.size());
    }

    @Test
    public void NewLabReportTest() throws Exception {
        WebService.webServiceAsyncTask(get_patient_medical_records, "3",
                "pansy", "1001");
        Object obj = get_patient_medical_records.getAsyncTask().get();

//        Vector<LabReportRecord> LabReportRecord = new SoapObjectVector<>(LabReportRecord.class, (SoapObject) obj);
//        assertEquals(2, LabReportRecord.size());

    }

    @Test
    public void ReferralTest() throws Exception {
        WebService.webServiceAsyncTask(get_patient_medical_records, "4",
                visitRecords.get(0).org_code, visitRecords.get(0).account);
        Object obj = get_patient_medical_records.getAsyncTask().get();
        Vector<ReferralRecord> ReferralRecord = new SoapObjectVector<>(ReferralRecord.class, (SoapObject) obj);
        assertEquals(2, ReferralRecord.size());
    }


}
