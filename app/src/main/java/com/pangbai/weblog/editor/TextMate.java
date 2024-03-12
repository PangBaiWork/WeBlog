package com.pangbai.weblog.editor;

import android.content.Context;

import com.pangbai.weblog.tool.IO;

import org.eclipse.tm4e.core.registry.IThemeSource;

import java.io.File;
import java.io.FilenameFilter;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class TextMate {
    public static void  initializeLogic(CodeEditor editor,Context context) {
        FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(context.getAssets()));
        try {
            GrammarRegistry.getInstance().loadGrammars("textmate/languages.json");
            EditorColorScheme colorScheme = editor.getColorScheme();
            if (!(colorScheme instanceof TextMateColorScheme)) {
                colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance());
                editor.setColorScheme(colorScheme);
            }

            IThemeSource themeSource = IThemeSource.fromInputStream(
                    FileProviderRegistry.getInstance().tryGetInputStream("textmate/quietlight.json"),"textmate/quietlight.json" ,null);
            ThemeRegistry.getInstance().setTheme(new ThemeModel(themeSource));
            editor.setColorScheme(TextMateColorScheme.create(ThemeRegistry.getInstance()));
          //  TextMate.setLanguage(editor,"main.md");

           /* Language language = TextMateLanguage.create("text.html.markdown",
                    false);

            editor.setEditorLanguage(language);*/

        } catch (Exception e) {
            editor.setText(e.toString());

        }


   /*
       GrammarRegistry.getInstance().loadGrammars("TextMate/languages/languages.json");
     //   ThemeRegistry.getInstance().loadTheme("TextMate//languages.json");
        IThemeSource themeSource = IThemeSource.fromInputStream(
                FileProviderRegistry.getInstance().tryGetInputStream("TextMate/themes/dracula.json"),"TextMate/themes/dracula.json" ,null);
        ThemeRegistry.getInstance().setTheme(new ThemeModel(themeSource));
        editor.setColorScheme(TextMateColorScheme.create(ThemeRegistry.getInstance()));
        editor.setEditorLanguage(getSmaliLanguage(context));*/




    }

    public static void setLanguage(CodeEditor editor,String filename){
        Language language;
       String ScopeName = findLanguageScopeName(filename);
       if (ScopeName!=null)
        language = TextMateLanguage.create(ScopeName, true);
       else
           language=new EmptyLanguage();


        editor.setEditorLanguage(language);
    }


   public static  String  findLanguageScopeName(String fileName){
        String lang;
       switch (IO.getExtension(fileName)) {
           case "styl":
           case "css":
               lang = "source.css";
               break;
           case "htm":
           case "html":
               lang = "text.html.basic";
               break;
           case "kt":
               lang = "source.kotlin";
               break;
           case "md":
               lang = "text.html.markdown";
               break;
           case "java":
               lang = "source.java";
               break;
           case "ejs":
           case "njk":
           case "js":
           case "cjs":
           case "mjs":
               lang = "source.js";
               break;
           case "json":
               lang = "source.json";
               break;
           case "php":
               lang = "source.php";
               break;
           case "xml":
               lang = "text.xml";
               break;
           case "yml":
               lang = "source.yaml";
               break;
           case "sh":
           case "bash":
               lang ="source.shell";
               break;
           case "c":
           case "h":
               lang ="source.c";
               break;
           case "txt":
           default:
               lang = null;
       }
       return lang;
   }


    }
