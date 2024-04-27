package com.pangbai.weblog.project;

import android.content.Context;

import com.pangbai.weblog.preference.PrefManager;
import com.pangbai.weblog.tool.IO;

import java.io.File;
import java.util.stream.Stream;

public class ProjectManager {
    public enum Type {
        hexo,
        hugo
    }

    public static String[] getTypeArray() {
        return Stream.of(Type.values()).map(Type::name).toArray(String[]::new);
    }


    Project project;

    public ProjectManager(Project project) {
        this.project = project;
    }



    public boolean createProject() {
        boolean init = project.blogCmd.initBlog(project.projectPath);
        if (!init) return false;
        project.blogCmd.changeConfig("title", project.blogName);
        return true;
    }

   /* public boolean createScript(Context context) {
        if (new File(project.scriptPath).exists()) return true;
        IO.copyAssetsDirToSDCard(context, "scripts/" + project.blogType.name(), project.scriptPath);
        return true;
    }*/

    public static void saveCurrentProject(Project project) {
        String[] infor = new String[]{project.blogName, project.getProjectPath(), project.blogType.name()};
        PrefManager.putStringArray(PrefManager.Keys.current_project, infor);
    }

    public static Project getCurrentProject() {
        String[] infor = new String[]{};
        infor = PrefManager.getStringArray(PrefManager.Keys.current_project, infor);
        if (infor == null) return null;
        String name = infor[0];
        String path = infor[1];
        String type = infor[2];
        return new Project(name, path, Type.valueOf(type));
    }

    public static Type checkType(File file) {
        if (checkIsHexo(file)) return Type.hexo;
        if (checkIsHugo(file)) return Type.hugo;
        return null;
    }

    public static boolean checkIsHexo(File file) {
        String[] root = file.list();
        if (root == null) return false;
        for (String s : root) {
            if (s.contains("package.json") || s.endsWith(".yml") || s.startsWith("node"))
                return true;
        }
        return false;
    }

    public static boolean checkIsHugo(File file) {
        String[] root = file.list();
        if (root == null) return false;
        for (String s : root) {
            if (s.contains("hugo") || s.endsWith(".toml") || s.contains("content")) return true;
        }
        return false;
    }
}
