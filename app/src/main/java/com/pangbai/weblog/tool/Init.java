package com.pangbai.weblog.tool;

import android.app.Activity;

import android.util.Log;
import android.util.Pair;
import android.widget.Toast;



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
    public  static  String libDir,binDir,nodeDir,weblogDir;

   public static String busyboxPath,shPath;

    public Init(Activity ct){

        File files=ct.getFilesDir();
        libDir=ct.getApplicationInfo().nativeLibraryDir;
        busyboxPath=libDir+"/busybox";

        filesDirPath=files.getAbsolutePath();
        binDir=filesDirPath+"/usr/bin";
        shPath=binDir+"/sh";
        nodeDir=binDir+"/node";
        weblogDir=filesDirPath+"/weblog";


        if (!new File( binDir).exists()){
            Toast.makeText(ct,"Loading",Toast.LENGTH_LONG).show();
            new Thread(){
                @Override
                public  void  run(){
                    cmdExer.execute( libDir+"/links.sh " +libDir,false,true);
                    cmdExer.execute(busyboxPath+" tar Jxf "+libDir+"/env -C /",false,true);

                    cmdExer.execute(busyboxPath+" --install -s "+binDir,false,true);
                   // mdialog.dismiss();
                   // util.ensureStoragePermissionGranted(ct);



                }
            }.start();

        }else if (!checkLink(nodeDir)) {
            Log.e("weblog",nodeDir);
             cmdExer.execute("sh "+weblogDir+"/links.sh "+libDir,false,false);
            cmdExer.execute(busyboxPath+" --install -s "+binDir,false,false);
        }





    }



   boolean checkLink(String link){
       int result = cmdExer.execute("[ -e " + link + " ] && "+"[ -L " + link + " ]",false,true);
       return result == 0;
    }


}