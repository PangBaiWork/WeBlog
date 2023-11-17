package com.pangbai.weblog.tool;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Init {
    public static String filesDirPath;
    public static boolean  isRoot;
   public static String linuxDeployDirPath;
   public static  String fontPath;
   public  static  String keyPath;
    public  static  String tmpPath;
   public static String binDirPath;

 //  public static String shellPath;
   public static String busyboxPath;
    public Init(Activity ct){



        File files=ct.getFilesDir();

        filesDirPath=files.getAbsolutePath();
        fontPath=filesDirPath+"/weblog/terminal/font.ttf";
        keyPath=filesDirPath+"/weblog/terminal/keys";



    }


  void   createSymlinks() throws IOException {
      final List<Pair<String, String>> symlinks = new ArrayList<>(50);
      BufferedReader symlinksReader = new BufferedReader(new InputStreamReader());
      String line;
      while ((line = symlinksReader.readLine()) != null) {
          String[] parts = line.split("←");
          if (parts.length != 2)
              throw new RuntimeException("Malformed symlink line: " + line);
          String oldPath = parts[0];
          String newPath = "/data/data/com.pangbai.weblog/files/usr" + "/" + parts[1];
          symlinks.add(Pair.create(oldPath, newPath));


      }}


}