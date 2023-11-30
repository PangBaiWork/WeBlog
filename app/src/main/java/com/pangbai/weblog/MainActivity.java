
package com.pangbai.weblog;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.pangbai.weblog.databinding.ActivityMainBinding;
import com.pangbai.weblog.tool.Init;
import com.pangbai.weblog.tool.permission;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setLayout();
        setContentView(binding.getRoot());

        new Init(this);
        new permission(this).checkPermission();
        setTerminal();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    void setTerminal() {
        String[] n = {"sh"};
        String[] envp = {
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

    void  setLayout(){
        ActionBarDrawerToggle mToggle= new ActionBarDrawerToggle(this,binding.drawLayout,binding.toolbar,0,0);
        binding.drawLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        binding. drawLayout.setScrimColor(Color.TRANSPARENT);
        binding.drawLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // 主页内容
                View contentView = binding.drawLayout.getChildAt(0);
                // 侧边栏
                // slideOffset 值默认是0~1
                contentView.setTranslationX(drawerView.getMeasuredWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });
        //状态栏中的文字颜色和图标颜色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
        //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

    }


}
