package com.pangbai.weblog.execute;

public abstract class BlogCmd {
    public  static void   setProjectPath(String path){
        cmdExer.setCwd(path);
    }
    public abstract   boolean changeConfig(String key,String value);
    public abstract boolean initBlog(String path);
    public abstract Process Server(String port, boolean serverFlag);
}
