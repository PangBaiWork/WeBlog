package com.pangbai.weblog.project;

import com.pangbai.weblog.project.ProjectManager;

public class Project {
    String projectPath;
    String blogName;
  public   ProjectManager.Type blogType;
  public  String scriptPath;

    public Project( String name,String path, ProjectManager.Type type) {
        this.projectPath = path;
        this.blogName = name;
        this.blogType = type;
        scriptPath=projectPath+"/.scripts";
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }
}
