package com.pangbai.weblog.project;


import com.pangbai.weblog.view.ArticleCreateFragment;


public class HexoPostCreate extends PostCreate {


    public HexoPostCreate(ArticleCreateFragment.Type type) {
        super(type);
    }


    public boolean create() {
        if (writer == null)
            return false;

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
