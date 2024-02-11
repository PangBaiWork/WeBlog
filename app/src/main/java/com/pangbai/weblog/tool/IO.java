package com.pangbai.weblog.tool;

import android.content.Context;
import android.system.Os;
import android.util.Log;

import com.pangbai.weblog.execute.cmdExer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class IO {
    public static void copyAssetsDirToSDCard(Context context, String assetsDirName, String sdCardPath) {
        //   Log.d(TAG, "copyAssetsDirToSDCard() called with: context = [" + context + "], assetsDirName = [" + assetsDirName + "], sdCardPath = [" + sdCardPath + "]");
        try {
            String[] list = context.getAssets().list(assetsDirName);
            if (list.length == 0) {
                InputStream inputStream = context.getAssets().open(assetsDirName);
                // finalByte= inputStream.available();
                byte[] mByte = new byte[1024];
                int bt = 0;
                File file = new File(sdCardPath + File.separator + assetsDirName.substring(assetsDirName.lastIndexOf('/')));

                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    return;
                }
                FileOutputStream fos = new FileOutputStream(file);
                while ((bt = inputStream.read(mByte)) != -1) {
                    fos.write(mByte, 0, bt);
                    // dealtByte+=bt;
                }
                fos.flush();
                inputStream.close();
                fos.close();
            } else {
                String subDirName = assetsDirName;
                if (assetsDirName.contains("/")) {
                    subDirName = assetsDirName.substring(assetsDirName.lastIndexOf('/') + 1);
                }


                //  path/a   to  /sdcard/path/b  will be /sdcard/path/b/a;
                //  sdCardPath = sdCardPath + File.separator + subDirName;


                File file = new File(sdCardPath);
                if (!file.exists())
                    file.mkdirs();
                for (String s : list) {
                    copyAssetsDirToSDCard(context, assetsDirName + File.separator + s, sdCardPath);
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFileToString(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    public static boolean createFileOrDir(String path, boolean isDir) {
        File newFile = new File(path);
        if (newFile.exists())
            return false;
        if (isDir) {
            newFile.mkdir();
        } else {
            try {
                newFile.createNewFile();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static boolean renameOrMoveFile(File file, String name) {
        File newFile;
        if (name.startsWith("/"))
            newFile = new File(name);
        else
            newFile = new File(file.getParent() + "/" + name);

        if (newFile.exists())
            return false;
        Log.e("mv", "mv " + file.getAbsolutePath() + " " + newFile.getAbsolutePath());
        // return   Jni.renameFile( file.getAbsolutePath(),newFile.getAbsolutePath());
        try {

            Os.rename(file.getAbsolutePath(), newFile.getAbsolutePath());
        } catch (Exception e) {
            return false;
        }
        return true;
        // return   cmdExer.execute("mv " + file.getAbsolutePath()+" " + newFile.getAbsolutePath(), false)==0;
        // return file.renameTo(newFile);
    }

    public static boolean copyFileOrFolder(File source, String target) {
        File newFile = new File(target);
        if (newFile.exists() || target.contains(source.getAbsolutePath()))
            return false;

        int result = cmdExer.execute("cp -r " + source.getAbsolutePath() + " " + target, false);
        return result == 0;
    }

    public static boolean deleteFileOrFolder(File folder) {
        int result = cmdExer.execute("rm -rf " + folder.getAbsolutePath(), false);
        return result == 0;
    }

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1).trim().toLowerCase();
    }

    public static String getMdTitle(File md) {
        return getMdAttr(md, "title:");
    }
    public static boolean isMdFile(File file){
        return file.getName().endsWith(".md");
    }

    public static String getMdAttr(File md, String key) {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(md))) {
            String line;
            int position = 0;
            // only check 15 linescomponent
            for (int i = 0; i < 15; i++) {
                line = bufferedReader.readLine();
                if (line == null)
                    break;
                position = line.indexOf(key);
                if (position != -1) {
                    return line.substring(position + key.length()).trim();
                }
            }
        } catch (Exception e) {

            return null;
        }

        return null;


    }


}
