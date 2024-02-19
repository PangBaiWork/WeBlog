package com.pangbai.weblog.project;

import com.pangbai.weblog.view.ArticleCreateFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public abstract class PostCreate {

        ArticleCreateFragment.Type type;
        String path, title, category, description;
        String[] tags;
        PrintWriter writer;

        public PostCreate(ArticleCreateFragment.Type type) {
            this.type = type;

        }

        public void setParams(String title, String path, String category, String description, String[] tags) {
            this.title = title;
            this.path = path;
            this.category = category;
            this.description = description;
            this.tags = tags;
            File file = new File(path);
            if (file.exists()) return ;
            File dir = file.getParentFile();
            if (!dir.exists()) dir.mkdirs();
            try {
                file.createNewFile();
                writer = new PrintWriter(new FileWriter(file), true);
            } catch (Exception e) {
            }
        }

        public  boolean create() {
           if (writer==null)return false;


            writeFrontmatter();
            writeTitle(title);
            writeCategories(category);
            writeTags(tags);
            writeFrontmatter();
            writeDescription(description);

            writer.close();
            return true;

        }

     abstract    void writeFrontmatter();

     abstract    void writeTitle(String title) ;


      abstract   void writeCategories(String categories) ;

      abstract   void writeTags(String[] tags);

      abstract   void writeDescription(String description);



}
