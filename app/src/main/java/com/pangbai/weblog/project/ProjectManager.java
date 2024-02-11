package com.pangbai.weblog.project;

import android.content.Context;

import com.pangbai.weblog.execute.NodeExer;
import com.pangbai.weblog.preference.PrefManager;
import com.pangbai.weblog.tool.IO;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import kotlin.text.UStringsKt;

public class ProjectManager {
    public enum Type {
        hexo,
        hugo
    }

    Project project;

    public ProjectManager(Project project) {
        this.project = project;
    }


    public boolean createProject() {
        if (project.blogType == Type.hexo) {
            NodeExer.setProjectPath(project.projectPath);
            boolean init = NodeExer.initBlog(null);
            if (!init) return false;
            NodeExer.changeConfig("title", project.blogName);
        } else if (project.blogType == Type.hugo) {

        }
        return true;
    }

    public boolean createScript(Context context) {
        if (new File(project.scriptPath).exists()) return true;
        if (project.blogType == Type.hexo) {
            IO.copyAssetsDirToSDCard(context, "scripts/hexo", project.scriptPath);
        }
        return true;
    }

    public static void saveCurrentProject(Project project) {
      String[] infor=new String[]{project.blogName,project.getProjectPath(),project.blogType.name()};
        PrefManager.putStringArray(PrefManager.Keys.current_project, infor);
    }

    public static Project getCurrentProject() {
        String []infor=new String[]{};
        infor = PrefManager.getStringArray(PrefManager.Keys.current_project, infor);
        if (infor==null)return null;
        String name=infor[0];
        String path=infor[1];
        String type=infor[2];
        return new Project(name,path,Type.valueOf(type));
    }
    public static boolean checkIsHexo(File file){
        String[] root= file.list();
        if (root==null)return false;
        for (String s:root){
            if (s.contains("package.json")||s.endsWith(".yml")||s.startsWith("node"))return true;
        }
        return false;
    }
}
