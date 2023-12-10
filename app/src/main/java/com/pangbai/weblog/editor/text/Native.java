package com.pangbai.weblog.editor.text;

public class Native {
	
	static {
        System.loadLibrary("hello-jni");
    }
	public native String  stringFromJNI();


    public native String  unimplementedStringFromJNI();
	
}
