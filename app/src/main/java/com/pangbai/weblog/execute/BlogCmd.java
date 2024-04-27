package com.pangbai.weblog.execute;

public abstract class BlogCmd {
    public static String checkGitUpdate(){
      cmdExer.execute("hugo version", false);
      return null;
    }
    public static String checkEnvironment() {
        boolean cmd;
        String res ;

        cmd = cmdExer.execute("hugo version", false) == 0;
        if (cmd) {
            res = cmdExer.result;
        } else {
            res = "Hugo:  Not found \n";
        }

        cmd = cmdExer.execute("\nhexo version", false) == 0;
        if (cmd) {
            res += cmdExer.result;
        } else {
            res += "Hexo:  Not found \n";
        }
        return res;


    }

    public static void setProjectPath(String path) {
        cmdExer.setCwd(path);
    }

    public abstract boolean changeConfig(String key, String value);

    public abstract boolean initBlog(String path);

    public abstract Process Server(String port, boolean serverFlag);
}
