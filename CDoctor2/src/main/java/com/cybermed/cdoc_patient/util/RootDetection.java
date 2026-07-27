package com.cybermed.cdoc_patient.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class RootDetection {
    private static String[] binaryPaths = {
            "/data/local/",
            "/data/local/bin/",
            "/data/local/xbin/",
            "/sbin/",
            "/su/bin/",
            "/system/bin/",
            "/system/bin/.ext/",
            "/system/bin/failsafe/",
            "/system/sd/xbin/",
            "/system/usr/we-need-root/",
            "/system/xbin/",
            "/system/app/Superuser.apk",
            "/cache",
            "/data",
            "/dev"
    };

    /**
     * @return true if device is rooted
     */
    public static boolean checkRootedDevice() {
        //test device test keys
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys") ||
                checkForBinary("su") || checkForBinary("busybox") || checkSuExists();
    }

    /**
     * @param filename - check for this existence of this
     *                 file("su","busybox")
     * @return true if exists
     */
    private static boolean checkForBinary(String filename) {
        for (String path : binaryPaths) {
            File f = new File(path, filename);
            boolean fileExists = f.exists();
            if (fileExists) {
                return true;
            }
        }
        return false;
    }

    /**
     * A variation on the checking for SU, this attempts a 'which su'
     * different file system check for the su binary
     *
     * @return true if su exists
     */
    private static boolean checkSuExists() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]
                    {"/system /xbin/which", "su"});
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = in.readLine();
            process.destroy();
            return line != null;
        } catch (Exception e) {
            if (process != null) {
                process.destroy();
            }
            return false;
        }
    }
}
