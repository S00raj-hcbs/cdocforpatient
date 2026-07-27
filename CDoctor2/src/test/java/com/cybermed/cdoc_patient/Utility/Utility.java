package com.cybermed.cdoc_patient.Utility;

import com.cdfortis.datainterface.soap.WebServiceID;

import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.shadows.ShadowToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class Utility {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static boolean unlock = false;


    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static void toastCheck(String text) {
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(text));
    }

    public static void unlockWebservices(){
        if(!unlock){
            unlock = true;
            for (WebServiceID wsid : WebServiceID.values()){
                wsid.setDisableNullRestriction(true);
            }
        }
    }

    @Ignore
    @Test
    public void writeFile() throws IOException {
        File file = new File("C:\\Users\\daniel29926\\Desktop\\Work\\dProviders\\dProviders.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String wholeString = "";
        String st;
        while ((st = br.readLine()) != null)
            wholeString = wholeString + st + "\t";

        String [] array = wholeString.split("\t");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("C:\\Users\\daniel29926\\Desktop\\Work\\dProviders\\output.txt", "UTF-8");
            for(int i = 0; i < array.length; i=i+3){
//                writer.println(array[i] + "." + array[i+1] + "@cybermedcorp.com\t" + array[i+2]);
                String user = array[i] + "." + array[i+1] + "@cybermedcorp.com";
                String code = array[i+2];
                writer.println(sql(user, code));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }
    }

    private String sql(String user, String code){
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
                "   VALUES (\'"+ user +
                "\', 'hnympc', \'" + code + "\')\n";
        return str;
    }


}


