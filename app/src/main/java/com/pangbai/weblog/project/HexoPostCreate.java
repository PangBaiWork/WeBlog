package com.pangbai.weblog.project;

import com.pangbai.weblog.view.ArticleCreateFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HexoPostCreate {
    ArticleCreateFragment.Type type;
    String path, title, category, description;
    String[] tags;
    PrintWriter writer;

    public HexoPostCreate(ArticleCreateFragment.Type type) {
        this.type = type;
    }

    public void setParams(String title, String path, String category, String description, String[] tags) {
        this.title = title;
        this.path = path;
        this.category = category;
        this.description = description;
        this.tags = tags;

    }

    public boolean create() {
        File file = new File(path);
        if (file.exists()) return false;
        File dir = file.getParentFile();
        if (!dir.exists()) dir.mkdirs();
        try {
            file.createNewFile();
            writer = new PrintWriter(new FileWriter(file), true);
        } catch (Exception e) {
            return false;
        }


        writeFrontmatter();
        writeTitle(title);
        writeCategories(category);
        writeTags(tags);
        writeFrontmatter();
        writeDescription(description);

        writer.close();
        return true;

    }

    void writeFrontmatter() {
        String tmp = "---";
        writer.println(tmp);
    }

    void writeTitle(String title) {
        String tmp = "title: " + title;
        writer.println(tmp);
    }


    void writeCategories(String categories) {
        String tmp = "categories: " + categories;
        writer.println(tmp);
    }

    void writeTags(String[] tags) {
        String tagsLabel = "tags:";
        writer.println(tagsLabel);
        for (String tag : tags) {
            String tmp = "- " + tag;
            writer.println(tmp);
        }
    }

    void writeDescription(String description) {
        writer.println(description);
        String tmp = "<!--more-->";
        writer.println(tmp);
    }

}
