package com.cybermed.cdoc_patient.random;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Extract_WS_Method {

    @Test
    public void extractWS() throws Exception {
        String delim = "new SoapObject(NAMESPACE,";
        String property = "soapReq.addProperty(";

        File file = new File("C:\\Users\\daniel29926\\AndroidStudioProjects\\CDoctorProviderAndroid\\ProviderDataInterface\\src\\main\\java\\com\\cybermed\\providerdatainterface\\soap\\Webservice.java");
        BufferedReader br = new BufferedReader(new FileReader(file));

        PrintWriter printWriter = new PrintWriter("C:\\Users\\daniel29926\\Desktop\\Work\\dProviders\\outputWS.txt", "UTF-8");

        List<String> finalWS = new ArrayList<>();

        String wholeString = "";
        String st;
        while ((st = br.readLine()) != null)
            wholeString = wholeString + st;

        int i = 0, j = 0;

        while ((i = wholeString.indexOf(delim, i)) != -1) {
            int startIdx = wholeString.indexOf("\"", i + 1);
            int endIdx = wholeString.indexOf("\"", startIdx + 1);

            String serviceName = wholeString.substring(startIdx + 1, endIdx);

            int k = wholeString.indexOf(delim, i + 1);

            List<String> list = new ArrayList<>();

            if(k != -1){
                while((j = wholeString.indexOf(property, j)) < k){
                    int startIdxProperty = wholeString.indexOf("\"", j);
                    int endIdxProperty = wholeString.indexOf("\"", startIdxProperty + 1);
                    list.add(wholeString.substring(startIdxProperty, endIdxProperty +1));
                    j++;
                }
            }else{
                while((j = wholeString.indexOf(property, j)) != -1){
                    int startIdxProperty = wholeString.indexOf("\"", j);
                    int endIdxProperty = wholeString.indexOf("\"", startIdxProperty + 1);
                    list.add(wholeString.substring(startIdxProperty, endIdxProperty +1));
                    j++;
                }
            }

            String str = "";

            for(int idx = 0; idx < list.size(); idx++){
                if(idx == list.size()-1){
                    str += list.get(idx);
                }else{
                    str += list.get(idx) + ", ";
                }
            }

            finalWS.add(serviceName + "(" + str + "),");

            i++;
        }


        finalWS.sort(String.CASE_INSENSITIVE_ORDER);

        for(String str : finalWS){
            printWriter.println(str);
        }

        printWriter.close();

    }
}
