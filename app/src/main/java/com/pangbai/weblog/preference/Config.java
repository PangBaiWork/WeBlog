package com.pangbai.weblog.preference;

import java.util.HashMap;
import java.util.Map;

public class Config {
  public static Map<String, Boolean> boolMap=new HashMap<>();

 public static void update(){
     for ( PrefManager.Keys keys:PrefManager.Keys.values()){
         if (keys.name().startsWith("bl_")){
             boolMap.put(keys.name(),PrefManager.getBoolen(keys,true));
         }
     }
 }

 public static boolean getBool(PrefManager.Keys keys){
   return Boolean.TRUE.equals(boolMap.get(keys.name()));
 }

}
