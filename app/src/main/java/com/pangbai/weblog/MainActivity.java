
package com.pangbai.weblog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pangbai.weblog.databinding.ActivityMainBinding;
import com.pangbai.weblog.tool.Init;
import com.pangbai.weblog.tool.permission;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new Init(this);
        new permission(this).checkPermission();
      //  setTerminal();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    void setTerminal() {
        String[] n = {"sh"};
        String envp[] = {
                //"PATH=" + "/system/bin"
                "PATH=" + Init.binDir + ":/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
                "HOME=" + Init.filesDirPath,
                "PREFIX=" + Init.filesDirPath + "/usr",
                "LD_LIBRARY_PATH=" + Init.filesDirPath + "/usr/lib",
                "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
                "TERM=xterm-256color",
                "LANG=en_US.UTF-8",
                "ANDROID_DATA=/data",
                "ANDROID_ROOT=/system",
                "LD_PRELOAD=" + Init.libDir + "/libexec"
        };


        String path = getApplicationInfo().nativeLibraryDir;
        //   Log.e("weblog",path);
        binding.CmdView.setProcess(path + "/busybox", getFilesDir().getAbsolutePath(), n, envp, 0);

        binding.CmdView.runProcess();
        binding.CmdView.requestFocus();


    }


}
