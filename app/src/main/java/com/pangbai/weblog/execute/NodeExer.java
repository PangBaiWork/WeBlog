package com.pangbai.weblog.execute;

public class NodeExer {

   public  static void   setProjectPath(String path){
       cmdExer.setCwd(path);
   }
    public static  boolean installComponent(String component,boolean isGlobal){
       String flag=" -g ";
      return cmdExer.execute("npm install"+(isGlobal?flag:" ")+component,false)==0;
    }

    public static boolean changeConfig(String key,String value){
       return cmdExer.execute("hexo config "+key+" "+value,false)==0;
    }

    public static boolean initBlog(String path){
       if (path==null)path="";
        return cmdExer.execute("hexo init "+path,false)==0;
    }
    public static boolean hexoGenerate(){
       return cmdExer.execute("hexo g",false)==0;
    }
    public static boolean hexoDeploy(){
        return cmdExer.execute("hexo d",false)==0;
    }

    public static boolean hexoServer(boolean isStatic){
        return cmdExer.execute("hexo s "+(isStatic?"-s":null),false)==0;
    }

    public static  boolean hexoClean(){
        return cmdExer.execute("hexo clean",false)==0;
    }
}
