package com.pangbai.weblog.tool;

import android.Manifest;
import android.annotation.SuppressLint;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import br.tiagohm.markdownview.Utils;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

public class Init {


    public static String filesDirPath;


    //  public static String shellPath;
    public static String libDir, binDir, nodeDir, weblogDir;

    public static String busyboxPath, shPath;
    public static String[] envp;
    final public static String sdcardPath = "/storage/emulated/0/";
    final public static String linker = "/system/bin/linker64";
    public static String keyPath;
    final public static boolean android10=Build.VERSION.SDK_INT>=29;
    boolean result;

    AlertDialog dialog;
    public static String systemShell = "/system/bin/sh";

    public Init(Activity ct) {

        File files = ct.getFilesDir();


        filesDirPath = files.getAbsolutePath();
        libDir = filesDirPath + "/usr/lib";
        binDir = filesDirPath + "/usr/bin";
        // busyboxPath =  ct.getApplicationInfo().nativeLibraryDir + "/busybox";
        busyboxPath = binDir + "/busybox";
        shPath = binDir + "/sh";
        nodeDir = binDir + "/node";
        weblogDir = filesDirPath + "/weblog";
        keyPath = filesDirPath + "/weblog/keys";
        String[] envp = {
                //"PATH=" + "/system/bin"
                "PATH=" + Init.binDir + ":/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
                "LD_PRELOAD=" + Init.libDir + "/libexec.so",
                "HOME=" + Init.filesDirPath,
                "TMPDIR=" + Init.filesDirPath + "/usr/tmp",
                "PREFIX=" + Init.filesDirPath + "/usr",
                "LD_LIBRARY_PATH=" + Init.filesDirPath + "/usr/lib",
                "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
                "TERM=xterm-256color",
                "LANG=en_US.UTF-8",
                android10?"ANDROID10=1":"ANDROID10=0",
                "ANDROID_DATA=/data",
                "ANDROID_ROOT=/system"
        };
        Init.envp = envp;


        if (!new File(binDir).exists()) {
            dialog = DialogUtils.showLoadingDialog(ct, ct.getString(R.string.load_resources));

            new Thread() {
                @Override
                public void run() {

                    try {
                        result = installEnv(ct);
                    } catch (IOException | InterruptedException e) {
                        result = false;
                        // throw new RuntimeException(e);
                    }


                    new File(filesDirPath + "/home").mkdirs();
                    dialog.dismiss();
                    util.runOnUiThread(() -> {
                        if (result)
                            DialogUtils.showConfirmationDialog(ct, "Environment installation successful", ct.getString(R.string.ask_install_hexo),
                                    () -> {
                                        dialog = DialogUtils.showLoadingDialog(ct, "Installing hexo");
                                        installHexo(ct);
                                    }, () -> {
                                        dialog.dismiss();
                                        checkPermission(ct);

                                    });
                        else
                            checkPermission(ct);



                    });


                }
            }.start();

        }


    }

    @SuppressLint("SuspiciousIndentation")
    boolean installEnv(Context context) throws IOException, InterruptedException {
      //  String name = "busybox";
        IO.copyAssetsDirToSDCard(context, "busybox", binDir);
        IO.copyAssetsDirToSDCard(context, "libexec.so", libDir);
        new File(busyboxPath).setExecutable(true);
        cmdExer.setCwd(Init.binDir);
        // cmdExer.execute(busyboxPath + " --install -s " + binDir, false, true);
        cmdExer.execute("ln -s busybox env", false, false);
        cmdExer.execute("ln -s busybox sh", false, false);
        cmdExer.setCwd(Init.filesDirPath);
        cmdExer.execute("tar -xJf - -C /", false, false);

        OutputStream outputStream = cmdExer.process.getOutputStream();
        FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(context.getAssets()));
        InputStream inputStream = FileProviderRegistry.getInstance().tryGetInputStream("env.tar.xz");
        byte[] buffer = new byte[2048];
        int bytesRead;
        while (true) {

            assert inputStream != null;
            if ((bytesRead = inputStream.read(buffer)) == -1) break;

            outputStream.write(buffer, 0, bytesRead);

        }
        inputStream.close();
        outputStream.close();
        //  int exit = cmdExer.process.waitFor();
        return cmdExer.process.waitFor()==0;


    }

    void installHexo(Activity ct) {
        ThreadUtil.thread(() -> {
            cmdExer.execute("npm config set registry http://mirrors.cloud.tencent.com/npm/", false);
            boolean result = cmdExer.execute("npm install -g hexo-cli", false) == 0;
            dialog.dismiss();
            util.runOnUiThread(() -> {
                Toast.makeText(ct, result ? "Success" : "Failed", Toast.LENGTH_SHORT).show();
                checkPermission(ct);
            });
        });

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