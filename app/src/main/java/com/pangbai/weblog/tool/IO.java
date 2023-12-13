package com.pangbai.weblog.tool;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
                while ((line = bufferedReader.readLine()) != null) {
                   position= line.indexOf(key);
                   if (position!=-1){
                       return line.substring(position+key.length()).trim();
                   }
                }
            }catch (Exception e){

                return null;
            }

           return null;


        }


}
