package com.pangbai.weblog.execute;

public class HexoExer  extends BlogCmd{
    public HexoExer(String path){
        BlogCmd.setProjectPath(path);
    }
    public  boolean changeConfig(String key,String value){
        return cmdExer.execute("hexo Config "+key+" "+value,false)==0;
    }
    public  boolean initBlog(String path){
        if (path==null)path="";
        return cmdExer.execute("hexo init "+path,false)==0;
    }
    public  Process Server(String port, boolean isStatic){
        cmdExer.execute("hexo s"+(isStatic?" -s ":" ")+"-p "+port,false,false);
        return cmdExer.getProcess();
    }

    public static  boolean installComponent(String component,boolean isGlobal){
       String flag=" -g ";
      return cmdExer.execute("npm install"+(isGlobal?flag:" ")+component,false)==0;
    }




    public static boolean hexoGenerate(){
       return cmdExer.execute("hexo g",false)==0;
    }
    public static boolean hexoDeploy(){
        return cmdExer.execute("hexo d",false)==0;
    }



    public static  boolean hexoClean(){
        return cmdExer.execute("hexo clean",false)==0;
    }
}
