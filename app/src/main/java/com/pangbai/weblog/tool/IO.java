package com.pangbai.weblog.tool;
import android.util.Log;

import com.pangbai.weblog.execute.cmdExer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class IO {



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
        public  static  boolean renameFile(File file,String name){
            String dir=file.isDirectory()?file.getAbsolutePath():file.getParent();

            File newFile=new File(dir + "/" + name);
            if(newFile.exists())
                return  false;
            return file.renameTo(newFile);
        }

    public static boolean deleteFolder(File folder) {
        int result = cmdExer.execute("rm -rf " + folder.getAbsolutePath(), false);
        return result == 0;
    }

        public  static  String getExtension(String fileName){
            return   fileName.substring(fileName.lastIndexOf('.') + 1).trim().toLowerCase();
        }
        public static String getMdTitle(File md){
           return  getMdAttr(md,"title:");
        }

        public static String getMdAttr(File md,String key){
            StringBuilder stringBuilder = new StringBuilder();

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(md))) {
                String line;
                int position=0;
            // only check 15 lines
                for (int i=0;i<15;i++){
                       line = bufferedReader.readLine();
                       if (line==null)
                           break;
                        position= line.indexOf(key);
                        if (position!=-1){
                            return line.substring(position+key.length()).trim();
                }       }
            }catch (Exception e){

                return null;
            }

           return null;


        }


}
