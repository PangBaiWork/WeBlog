package com.pangbai.weblog.execute;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class cmdExer {
    public static Process process;
    public static String lastLine, result;

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

}