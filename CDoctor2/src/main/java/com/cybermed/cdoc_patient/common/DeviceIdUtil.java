package com.cybermed.cdoc_patient.common;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * Created by ft on 14/12/25.
 */
public class DeviceIdUtil {
    private static File getUUIDFile(){

        File dir = new File(Environment.getExternalStorageDirectory(),"gophar");
        dir.mkdirs();
        return new File(dir,"deviceId");
    }

    private static String readUUID(){

        try{
            String uuid=null;
            FileInputStream is =new FileInputStream(getUUIDFile());
            byte[] buff = new byte[36];
            if(is.read(buff)==buff.length) {
                uuid= new String(buff,"UTF-8");
                String reg = "[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}";
                if(!uuid.matches(reg))
                    uuid = null;
            }
            is.close();
            return uuid;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String createUUID(){
        try{
            String uuid;
            FileOutputStream os =new FileOutputStream(getUUIDFile(),false);
            uuid = UUID.randomUUID().toString();
            os.write(uuid.getBytes("UTF-8"));
            return uuid;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String getUUID(){
        String uuid = readUUID();
        if(uuid==null){
            uuid = createUUID();
        }

        return uuid;
    }

    /**
     * 获取imei串号
     * @return
     */
    private static String getImei(Context context) {
        String deviceId=null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId= telephonyManager.getDeviceId();


        } catch (Exception e) {
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(deviceId))
            return null;

        //三星note3获取的为14为字母和数字的设备好
        if(deviceId.length()<=10)
            return null;

        char f = deviceId.charAt(0);
        boolean isSame=true;
        for (int i = 0 ; i < deviceId.length() ; i ++){
            if(deviceId.charAt(i)!=f){
                isSame = false;
                break;
            }
        }
        //如果是相同数字也是无效的
        if(isSame)
            return null;
        return deviceId;
    }


    /**
     * 获取设备号
     * 可能是15 位imei号 ，
     * 也可能是14为数字和字母（如 三星note3），
     * 也可能是随机生成的uuid
     * 最后是 null
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = getImei(context);

        if(deviceId==null)
            deviceId = readUUID();

        if(deviceId==null)
            deviceId = createUUID();

        return deviceId;
    }
}
