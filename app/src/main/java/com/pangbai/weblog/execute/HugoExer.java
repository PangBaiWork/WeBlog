package com.pangbai.weblog.execute;

public class HugoExer extends BlogCmd {
    public HugoExer(String path){
        BlogCmd.setProjectPath(path);
    }
    public  boolean initBlog(String path){
        if (path==null)path="";
        return cmdExer.execute("hugo new site "+path,false)==0;
    }
    public  Process Server(String port, boolean disableFastRender){
        cmdExer.execute("hugo server --noBuildLock -D --port="+port+(disableFastRender?" --disableFastRender":" "),false,false);
        return cmdExer.getProcess();
    }

    public  boolean changeConfig(String key,String value){
        return false;
    }
}
