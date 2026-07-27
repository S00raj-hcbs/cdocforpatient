package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.Stemoscope;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.util.PcmToWavUtils;
import com.cybermed.cdoc_patient.webapi.CallApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileDownLoadManager {
    private static int sampleRateInHz = 8000; // 采样率
    private static int mSampBit = AudioFormat.ENCODING_PCM_16BIT;// 采样精度 :16bit

    private List<short[]> list = Collections.synchronizedList(new ArrayList<short[]>());

    private SaveFileThread thread;

    private boolean isThreadStart;


    private boolean startSave;

    private Context context;

    private int selectedImage;

    public boolean isStartSave() {
        return startSave;
    }

    public void setStartSave(boolean startSave) {
        this.startSave = startSave;
    }

    public int getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(int selectedImage) {
        this.selectedImage = selectedImage;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


    private FileDownLoadManager() {
        //default heartpoint position
        selectedImage = R.raw.heartpoint_3;
    }

    public static volatile FileDownLoadManager instance;


    public static FileDownLoadManager getInstance() {
        if (instance == null) {
            synchronized (FileDownLoadManager.class) {
                if (instance == null) {
                    instance = new FileDownLoadManager();
                }
            }
        }
        return instance;
    }


    public void writeToFile(short[] m_bitDateZ) {
        list.add(m_bitDateZ);
        if (!isThreadStart) {
            isThreadStart = true;
            thread = new SaveFileThread();
            thread.start();
        }
    }


    class SaveFileThread extends Thread {
        @Override
        public void run() {
            File file = new File( CDoctor2Application.application.getFilesDir().getAbsolutePath(),"stemoscope.pcm");
            FileOutputStream os = null;
            //当没有按下停止按钮  或者列表的长度不为0的时候  说明文件还没写入完成
            while (startSave || list.size() != 0) {
                if (list.size() > 0) {
                    try {
                        os = new FileOutputStream(file, true);
                        short[] src = list.get(0);
                        os.write(toByteArray(src), 0, src.length * 2);


                        list.remove(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            //到了这里  说明文件已经录制完成了
            File wavFile = savePcm2Wav(file);
            isThreadStart = false;

            CallApi.sendStemoDataAsync(context, StemoscopeFragment.macAddr, DateUtil.getCurrentTimestamp(), wavFile, "UNKNOWN", getImgFile());
        }
    }

    public static File savePcm2Wav(File file) {
        File wavFile = new File(CDoctor2Application.application.getFilesDir().getAbsolutePath(),"stemoscope.wav");
        if (file != null) {
            if (file.getName().endsWith(".pcm")) {
//                    File storageDir = new File(Environment.getExternalStorageDirectory(), "CDOC");
//                    if (!storageDir.exists()) {
//                        storageDir.mkdir();
//                    }
//                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
//                    String soundFileName = "Stemoscope_" + timeStamp + "_";
//                    File storedSoundFile = File.createTempFile(
//                            soundFileName,  /* prefix */
//                            ".wav",         /* suffix */
//                            storageDir      /* directory */
//                    );
                PcmToWavUtils.convertPcm2Wav(file.getAbsolutePath(),
                        wavFile.getAbsolutePath(), sampleRateInHz, 1, mSampBit);

                file.delete();
            }
        }
        return wavFile;
    }

    public static byte[] toByteArray(short[] src) {
        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) (src[i]);
            dest[i * 2 + 1] = (byte) (src[i] >> 8);
        }
        return dest;
    }

    public static File getImgFile() {
        InputStream is = CDoctor2Application.application.getResources().openRawResource(FileDownLoadManager.getInstance().getSelectedImage());
        File imgFile = new File(CDoctor2Application.application.getFilesDir().getAbsolutePath(),"stemoscope.png");
        try {
            FileOutputStream fos = new FileOutputStream(imgFile);
            byte[] buffer = new byte[1024];
            int len = is.read(buffer);
            while (len != -1) {
                fos.write(buffer, 0, len);
                len = is.read(buffer);
            }
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgFile;
    }

}
