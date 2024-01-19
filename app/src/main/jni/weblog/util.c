#include <jni.h>
#include <stdio.h>
#include <stdbool.h>
#include <linux/fcntl.h>
#include <syscall.h>
#include <unistd.h>

JNIEXPORT jboolean JNICALL
Java_com_pangbai_weblog_tool_Jni_renameFile(JNIEnv *env, jclass clazz, jstring target,
                                            jstring newFile) {
    // TODO: implement renameFile()
    char *c_target,*c_newFile;
    bool res=0;
    c_target = (*env)->GetStringUTFChars(env, target, 0);
    c_newFile = (*env)->GetStringUTFChars(env, newFile, 0);

    chdir("/storage/emulated/0/");
    res= renameat(AT_FDCWD,c_target,AT_FDCWD,c_newFile);
    (*env)->ReleaseStringUTFChars(env, target, c_target);
    (*env)->ReleaseStringUTFChars(env, newFile, c_newFile);
    return res==0;

}