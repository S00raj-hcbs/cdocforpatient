package com.cybermed.cdoc_patient.Utility;


import com.cdfortis.datainterface.soap.WebService;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.cdfortis.datainterface.soap.WebServiceID.CreateNewUser_Android_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.Mark_Provider_as_favorite;
import static com.cdfortis.datainterface.soap.WebServiceID.create_appointment_on_EMR_Android_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.register_patient_IoT_device;
import static com.cybermed.cdoc_patient.Utility.Utility.unlockWebservices;

@RunWith(AndroidJUnit4.class)
public class Register_Heritage_Patient {

    @Before
    public void unlock() throws Exception {
        unlockWebservices();
    }

    Map<String, String> providerCodeMap = createMap();

    private Map<String, String> createMap() {
        Map<String, String> myMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        myMap.put("Kelly Daro", "004");
        myMap.put("Akinsika Olubunmi", "105");
        myMap.put("Abigail Azubuike", "106");
        myMap.put("Prince Bartholomew", "107");
        myMap.put("Yolanda Beck", "108");
        myMap.put("Teresa Bertozzi", "109");
        myMap.put("Danna Gordon", "111");
        myMap.put("Omolara Goyea", "112");
        myMap.put("David Holtzman", "113");
        myMap.put("Nancy Klotz", "114");
        myMap.put("Karen Larkin", "115");
        myMap.put("Pearl Lee", "116");
        myMap.put("Nathalie Monfiston", "117");
        myMap.put("Joyce Murray", "118");
        myMap.put("Fatiha Ouklilane", "119");
        myMap.put("Nancy Reyes", "120");
        myMap.put("Wendy Rosete", "121");
        myMap.put("Stanley Saji", "122");
        myMap.put("Marc Schnall", "123");
        myMap.put("Diane Schwartz", "124");
        myMap.put("Anne Songco", "125");
        myMap.put("Djuana Stovell", "126");
        myMap.put("Tiffany Henderson", "127");
        myMap.put("Chairma Cadet", "128");
        myMap.put("Riditi Ahamed", "129");
        myMap.put("Debby Kelly", "130");
        return myMap;
    }


    // if it doesn't work, check the VM Option in Edit Configuration. Make sure you have
    // -ea -noverify
    //@Ignore
    @Test
    public void Register_Patient_On_CDoc() throws Exception {
        //change your source File
        File file = new File("C:\\Users\\joshu\\Desktop\\Patients.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String wholeString = "";
        String st;
        while ((st = br.readLine()) != null)
            wholeString = wholeString + st + "\t";

        String[] patientArray = wholeString.split("\t");

        for (int i = 0; i < patientArray.length; i = i + 12) {
            String email = patientArray[i];
            String firstName = patientArray[i + 1];
            String lastName = patientArray[i + 2];
            String gender = patientArray[i + 3];
            String phoneTemp = patientArray[i + 4];
            String phone ;
            try{
                phone = fixPhone(phoneTemp);
            }catch (Exception e){
                phone = "1234567890";
            }
            String dob = patientArray[i + 5];
            String address = patientArray[i + 6];
            String city = patientArray[i + 7];
            String state = patientArray[i + 8];
            String zip = patientArray[i + 9];
            String providerCode = providerCodeMap.get(patientArray[i + 10].trim());
            String mac_address = patientArray[i + 11];

            WebService.webServiceAsyncTask(CreateNewUser_Android_v2,
                    email, "password", firstName, "", lastName, gender, dob, address, "", city, state, zip, phone, "hnympc", "telehealth");
            Object obj = CreateNewUser_Android_v2.getAsyncTask().get();

            if (obj.toString().equals("1")) {
                WebService.webServiceAsyncTask(create_appointment_on_EMR_Android_v2, "", email, "hnympc", "125", "01/25/2019 08:45 AM");
                Object result = create_appointment_on_EMR_Android_v2.getAsyncTask().get();

                if (providerCode == null) {
                    System.out.println(email + " This email favorite is not set");
                } else {
                    WebService.webServiceAsyncTask(Mark_Provider_as_favorite, email, "hnympc", providerCode, "1");
                    Object favResult = Mark_Provider_as_favorite.getAsyncTask().get();
                    if (!favResult.toString().equals("1"))
                        System.out.println(email + " issue occured with this email setting favorite");
                }

                //Register Mac Address
                WebService.webServiceAsyncTask(register_patient_IoT_device, email, "Tablet", mac_address);
                Object regResult = register_patient_IoT_device.getAsyncTask().get();

                if (!regResult.toString().equals("1")) {
                    System.out.println(String.format("Patient : %s with %s has problems please take a look", email, mac_address));
                }

            } else {
                throw new Exception(email);
            }
        }
    }

    private String fixPhone(String phoneTemp){
        StringBuilder ret = new StringBuilder();

        for(int i = 0; ret.length() < 10; i++){
            char c = phoneTemp.charAt(i);

            if(Character.isDigit(c)){
                ret.append(c);
            }
        }

        return ret.toString();
    }

    @Ignore
    @Test
    public void writeFile() throws IOException {
        File file = new File("C:\\Users\\joshu\\Desktop\\\\dProviders.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String wholeString = "";
        String st;
        while ((st = br.readLine()) != null)
            wholeString = wholeString + st + "\t";

        String[] array = wholeString.split("\t");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("C:\\Users\\joshu\\Desktop\\\\output.txt", "UTF-8");
            for (int i = 0; i < array.length; i = i + 3) {
//                writer.println(array[i] + "." + array[i+1] + "@cybermedcorp.com\t" + array[i+2]);
                String user = array[i] + "." + array[i + 1] + "@cybermedcorp.com";
                String code = array[i + 2];
                writer.println(sql(user, code));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    private String sql(String user, String code) {
        String str = "IF NOT EXISTS\n" +
                "   (\n" +
                "\tSELECT user_id\n" +
                "\tFROM [CYBERMED].[dbo].[Patient_favorites]\n" +
                "\tWHERE user_id = \'" +
                user +
                "\' AND org_code = 'hnympc'  \n" +
                "AND provider_code = \'" + code + "\'\n" +
                "   )\n" +
                "   INSERT INTO [CYBERMED].[dbo].[Patient_favorites]\n" +
                "   VALUES (\'" + user +
                "\', 'hnympc', \'" + code + "\')\n";
        return str;
    }

    @Ignore
    @Test
    public void testt() throws Exception {
        File file = new File("C:\\Users\\joshu\\Desktop\\temp.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String wholeString = "";
        String st;
        while ((st = br.readLine()) != null)
            wholeString = wholeString + st + "\t";

        String[] array = wholeString.split("\t");


        PrintWriter writer = null;
        try {
            writer = new PrintWriter("C:\\Users\\joshu\\Desktop\\test.txt", "UTF-8");
            for (int i = 0; i < array.length; i = i + 3) {
                String output = String.format("myMap.put( \"%s\" , \"%s\" );", array[i + 1] + " " + array[i + 2], array[i]);
                writer.println(output);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }
}
