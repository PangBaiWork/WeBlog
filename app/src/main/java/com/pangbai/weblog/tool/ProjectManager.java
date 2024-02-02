package com.pangbai.weblog.tool;

import com.pangbai.weblog.execute.NodeExer;

import java.lang.reflect.Type;

public class ProjectManager {
    public enum Type {
        hexo,
        hugo
    }

    public class Project {
        String projectPath;
        String blogName;
        Type blogType;


    }

    public boolean createProject(String projectPath, String blogName, Type type) {
        if (type == Type.hexo) {
            NodeExer.setProjectPath(projectPath);
            boolean init = NodeExer.initBlog(null);
            if (!init) return false;
            NodeExer.changeConfig("title", blogName);
        } else if (type==Type.hugo) {
            
        }
        return true;
    }
}
