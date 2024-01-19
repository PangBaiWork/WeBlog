package com.pangbai.weblog.tool;



public class Jni {

    static {
        System.loadLibrary("weblog");
    }
    public static native boolean renameFile(String target, String newFile);

}
