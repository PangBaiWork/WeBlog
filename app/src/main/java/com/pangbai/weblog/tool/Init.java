package com.pangbai.weblog.tool;

import android.Manifest;
import android.app.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.pangbai.weblog.R;
import com.pangbai.weblog.execute.cmdExer;

import java.io.File;

import br.tiagohm.markdownview.Utils;

public class Init {


    public static String filesDirPath;


    //  public static String shellPath;
    public static String libDir, binDir, nodeDir, weblogDir;

    public static String busyboxPath, shPath;
    public static String[] envp;
    final public static String sdcardPath = "/storage/emulated/0/";
    public static String keyPath;

    AlertDialog dialog;

    public Init(Activity ct) {

        File files = ct.getFilesDir();
        libDir = ct.getApplicationInfo().nativeLibraryDir;
        busyboxPath = libDir + "/busybox";

        filesDirPath = files.getAbsolutePath();
        binDir = filesDirPath + "/usr/bin";
        shPath = binDir + "/sh";
        nodeDir = binDir + "/node";
        weblogDir = filesDirPath + "/weblog";
        keyPath = filesDirPath + "/weblog/keys";
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
        Init.envp = envp;


        if (!new File(binDir).exists()) {
            dialog = DialogUtils.showLoadingDialog(ct, ct.getString(R.string.load_resources));

            new Thread() {
                @Override
                public void run() {
                    cmdExer.execute(libDir + "/links.sh " + libDir, false, true);
                    cmdExer.execute(busyboxPath + " tar Jxf " + libDir + "/env -C /", false, true);
                    cmdExer.execute(busyboxPath + " --install -s " + binDir, false, true);
                    new File(filesDirPath+"/home").mkdirs();
                    dialog.dismiss();
                    util.runOnUiThread(() -> {
                        DialogUtils.showConfirmationDialog(ct, "Environment installation successful", ct.getString(R.string.ask_install_hexo),
                                () -> {
                                    dialog = DialogUtils.showLoadingDialog(ct, "Installing hexo");
                                    installHexo(ct);
                                }, () -> {
                                    dialog.dismiss();
                                    checkPermission(ct);

                                });


                    });


                }
            }.start();

        } else if (!checkLink(nodeDir)) {
            Log.e("weblog", nodeDir);
            //cmdExer.execute("sh " + weblogDir + "/links.sh " + libDir, false, false);
            cmdExer.execute(libDir + "/links.sh " + libDir, false, true);
            cmdExer.execute(busyboxPath + " --install -s " + binDir, false, false);
        }


    }

    void installHexo(Activity ct) {
        ThreadUtil.thread(() -> {
            cmdExer.execute("npm Config set registry http://mirrors.cloud.tencent.com/npm/", false);
            boolean result = cmdExer.execute("npm install -g hexo-cli", false) == 0;
            dialog.dismiss();
            util.runOnUiThread(() -> {
                Toast.makeText(ct, result ? "Success" : "Failed", Toast.LENGTH_SHORT).show();
                checkPermission(ct);
            });
        });

    }


    boolean checkLink(String link) {
        int result = cmdExer.execute("[ -e " + link + " ] && " + "[ -L " + link + " ]", false, true);
        return result == 0;
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void checkPermission(Activity ct) {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                dialog = DialogUtils.showConfirmationDialog(ct, ct.getString(R.string.sdcard_permisson),
                        ct.getString(R.string.sdcard_description), () -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.setData(Uri.parse("package:" + ct.getPackageName()));
                            ct.startActivity(intent);
                        }, null);

            } else {
                //   havePermission = true;
                Log.i("swyLog", "Android 11以上，当前已有权限");
            }
        } else {
            if (ActivityCompat.checkSelfPermission(ct, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                dialog = DialogUtils.showConfirmationDialog(ct, ct.getString(R.string.sdcard_permisson),
                        ct.getString(R.string.sdcard_description), () -> {
                            ActivityCompat.requestPermissions(ct, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                        }, null);
            } else {
                // havePermission = true;
                Log.i("swyLog", "Android 6.0以上，11以下，当前已有权限");
            }
        }
    }


}