package com.pangbai.weblog.execute;


import static com.pangbai.weblog.tool.util.getByEnv;

import android.system.Os;

import com.pangbai.weblog.tool.Init;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class cmdExer {
    public static Process process;
    public static String result;
    static String cwd, lastLine;
    public static String[] envp = {
            //"PATH=" + "/system/bin"
            "PATH=" + Init.binDir + ":/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
            "LD_PRELOAD=" + Init.libDir + "/libexec",
            "HOME=" + Init.filesDirPath,
            "PREFIX=" + Init.filesDirPath + "/usr",
            "LD_LIBRARY_PATH=" + Init.filesDirPath + "/usr/lib",
            "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
            "TERM=xterm-256color",
            "LANG=en_US.UTF-8",
            "ANDROID_DATA=/data",
            "ANDROID_ROOT=/system"
    };

    public static int execute(String command, boolean su) {
        return execute(command, su, true);
    }

    public static int execute(String command, boolean su, boolean wait) {
        BufferedReader reader = null;
        String shell;
        if (su)
            shell = "su";
        else
            shell = "sh";

        ProcessBuilder processBuilder = new ProcessBuilder();
        Map<String, String> environment = processBuilder.environment();
        if (cwd!=null)
            processBuilder.directory(new File(cwd));
        String tmp[] = getByEnv(envp[0]);
        processBuilder.environment().put(tmp[0], tmp[1]);
        tmp = getByEnv(envp[1]);

        processBuilder.environment().put(tmp[0], tmp[1]);

        // 设置环境变量
        // environment.put("LD_LIBRARY_PATH", Init.filesDirPath + "/usr/lib");
        processBuilder.command(shell, "-c", command);

        try {


            process = processBuilder.start();
            if (!wait)
                return 0;
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {


                System.out.println(line);
                result += line;
                lastLine = line;
            }

            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null && wait) {
                process.destroy();
            }
        }
    }

    public static void destroy() {
        process = null;
        result = null;
        lastLine = null;
    }

    public static String getLastLine() {
        return lastLine;
    }

    public static void setCwd(String path) {
        cwd = path;
    }
}