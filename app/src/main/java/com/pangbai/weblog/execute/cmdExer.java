package com.pangbai.weblog.execute;


import static com.pangbai.weblog.tool.util.getByEnv;

import android.os.Build;
import android.system.Os;
import android.util.Log;

import com.pangbai.weblog.tool.Init;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class cmdExer {
    public static Process process;
    public static String result;
    static String cwd = Init.sdcardPath, lastLine;

    public static int executeScripts(List<String> name, String path, boolean wait) {
        String cmd = "";
        path += "/";
        int size = name.size();
        for (int i = 0; i < size; i++) {
            cmd += "sh " + path + name.get(i);
            if (i != size - 1) cmd += " && ";
        }
        return execute(cmd, false, wait);
    }


    public static int execute(String command, boolean su) {
        return execute(command, su, true);
    }


    public static int execute(String command, boolean su, boolean wait) {
        result = "";
        BufferedReader reader = null;
        String shell;
        if (su)
            shell = "su";
        else
            shell = "sh";

        ProcessBuilder processBuilder = new ProcessBuilder();
       // Map<String, String> environment = processBuilder.environment();
        if (cwd != null)
            processBuilder.directory(new File(cwd));
        for (String env : Init.envp) {
            String[] tmp = getByEnv(env);
            processBuilder.environment().put(tmp[0], tmp[1]);
        }

        // 设置环境变量
        // environment.put("LD_LIBRARY_PATH", Init.filesDirPath + "/usr/lib");
      if (Init.android10) {
          processBuilder.command(Init.linker, Init.busyboxPath, shell, "-c", command);
      }else {
          processBuilder.command(Init.busyboxPath,shell, "-c", command);
      }
       processBuilder.redirectErrorStream(true);

        try {

             process = processBuilder.start();
            if (!wait)
                return 0;
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {

                result += line + "\n";
                lastLine = line;
                Log.e("exer", line);
            }

            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            result += e.getMessage();
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

    public static Process getProcess() {
        return process;
    }
}