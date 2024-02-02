package com.pangbai.weblog.tool;

import android.app.Activity;

import android.util.Log;
import android.util.Pair;
import android.widget.Toast;


import com.pangbai.weblog.execute.NodeExer;
import com.pangbai.weblog.execute.cmdExer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Init {


    public static String filesDirPath;


    //  public static String shellPath;
    public static String libDir, binDir, nodeDir, weblogDir;

    public static String busyboxPath, shPath;
    public static String[] envp;
   final   public  static String sdcardPath="/storage/emulated/0/";
   public static String keyPath;

    public Init(Activity ct) {

        File files = ct.getFilesDir();
        libDir = ct.getApplicationInfo().nativeLibraryDir;
        busyboxPath = libDir + "/busybox";

        filesDirPath = files.getAbsolutePath();
        binDir = filesDirPath + "/usr/bin";
        shPath = binDir + "/sh";
        nodeDir = binDir + "/node";
        weblogDir = filesDirPath + "/weblog";
        keyPath=filesDirPath+"/weblog/keys";
        String[] envp = {
                //"PATH=" + "/system/bin"
                "PATH=" + Init.binDir + ":/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
                "LD_PRELOAD=" + Init.libDir + "/libexec",
                "HOME=" + Init.filesDirPath,
                "TMPDIR=" + Init.filesDirPath + "/usr/tmp",
                "PREFIX=" + Init.filesDirPath + "/usr",
                "LD_LIBRARY_PATH=" + Init.filesDirPath + "/usr/lib",
                "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
                "TERM=xterm-256color",
                "LANG=en_US.UTF-8",
                "ANDROID_DATA=/data",
                "ANDROID_ROOT=/system"
        };
        this.envp = envp;


        if (!new File(binDir).exists()) {
            Toast.makeText(ct, "Loading", Toast.LENGTH_LONG).show();
            new Thread() {
                @Override
                public void run() {
                    cmdExer.execute(libDir + "/links.sh " + libDir, false, true);
                    cmdExer.execute(busyboxPath + " tar Jxf " + libDir + "/env -C /", false, true);

                    cmdExer.execute(busyboxPath + " --install -s " + binDir, false, true);
                    cmdExer.execute("npm config set registry http://mirrors.cloud.tencent.com/npm/", false);
                    cmdExer.execute("npm install -g hexo-cli", false);
                    // mdialog.dismiss();
                    // util.ensureStoragePermissionGranted(ct);


                }
            }.start();

        } else if (!checkLink(nodeDir)) {
            Log.e("weblog", nodeDir);
            //cmdExer.execute("sh " + weblogDir + "/links.sh " + libDir, false, false);
            cmdExer.execute(libDir + "/links.sh " + libDir, false, true);
            cmdExer.execute(busyboxPath + " --install -s " + binDir, false, false);
        }


    }


    boolean checkLink(String link) {
        int result = cmdExer.execute("[ -e " + link + " ] && " + "[ -L " + link + " ]", false, true);
        return result == 0;
    }


}