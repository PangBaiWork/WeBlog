package com.pangbai.weblog.project;

import com.pangbai.weblog.execute.BlogCmd;
import com.pangbai.weblog.execute.HexoExer;
import com.pangbai.weblog.execute.HugoExer;
import com.pangbai.weblog.project.ProjectManager;

public class Project {
    String projectPath;
    String blogName;
  public   ProjectManager.Type blogType;
  public  String scriptPath;
  public BlogCmd blogCmd;

    public Project( String name,String path, ProjectManager.Type type) {
        this.projectPath = path;
        this.blogName = name;
        this.blogType = type;
        if (type== ProjectManager.Type.hexo){
            blogCmd=new HexoExer(projectPath);
        } else if (type== ProjectManager.Type.hugo) {
            blogCmd=new HugoExer(projectPath);
        }
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
