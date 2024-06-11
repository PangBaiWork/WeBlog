
package com.pangbai.weblog.activity;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.ActivityMainBinding;
import com.pangbai.weblog.databinding.FileListBinding;
import com.pangbai.weblog.databinding.LayoutTerminalBinding;
import com.pangbai.weblog.editor.SearchHandle;
import com.pangbai.weblog.editor.TextMate;
import com.pangbai.weblog.execute.BlogCmd;
import com.pangbai.weblog.execute.HexoExer;
import com.pangbai.weblog.execute.cmdExer;
import com.pangbai.weblog.preference.PrefManager;
import com.pangbai.weblog.preference.Config;
import com.pangbai.weblog.tool.DialogUtils;
import com.pangbai.weblog.tool.IO;
import com.pangbai.weblog.tool.Init;
import com.pangbai.weblog.project.Project;
import com.pangbai.weblog.project.ProjectManager;
import com.pangbai.weblog.tool.ThreadUtil;

import com.pangbai.weblog.tool.util;
import com.pangbai.weblog.view.ArticleCreateFragment;
import com.pangbai.weblog.view.DrawerAnim;
import com.pangbai.weblog.view.FileListSelect;
import com.pangbai.weblog.view.FilesListAdapter;
import com.pangbai.weblog.view.MainViewPagerAdapter;
import com.pangbai.weblog.view.ScriptsLogTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.Utils;
import br.tiagohm.markdownview.css.styles.Github;
import io.github.rosemoe.sora.event.PublishSearchResultEvent;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentIO;
import io.github.rosemoe.sora.widget.EditorSearcher;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    LayoutTerminalBinding cmdBinding;
    MarkdownView markdown;
    ScriptsLogTextView logTextView;
    Content editorText;
    File currentFile;

    Project project;

    FilesListAdapter filesListAdapter;
    SearchHandle handle;
    ExecuteStateControl executeStateControl;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefManager.init(getApplicationContext());
        project = ProjectManager.getCurrentProject();
        if (PrefManager.isFirstOrReinstall(this) || project == null || !new File(project.getProjectPath()).exists()) {
            util.startActivity(this, HomeActivity.class, false);
            finish();
            return;
        }
        new Init(this);


        //   project = new Project("PangBai's Blog", "/storage/emulated/0/blog1", ProjectManager.Type.hexo);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HexoExer.setProjectPath(project.getProjectPath());
        setLayout();
        setRecycleView();
        setTabLayout();
        setEditor();
        executeStateControl=new ExecuteStateControl(binding,logTextView,project);


        String path = PrefManager.getString(PrefManager.Keys.current_file, "");
        File file = new File(path);

        if (file.exists()) {
            setCodeText(file);
            currentFile = file;
            filesListAdapter.setList(Objects.requireNonNull(currentFile.getParentFile()));

        } else {
            binding.editor.setText("No file be Displayed\n" + getString(R.string.how_to_open_terminal));

        }
        if (Config.getBool(PrefManager.Keys.bl_script_init)) {
            List<String> init = new ArrayList<>() {{
                add("Init.sh");
            }};
            executeStateControl.runScripts(init);
        }



    }


    void setEditor() {
        binding.editor.setCursorAnimationEnabled(false);
        binding.editSymbol.init(binding.editor);
        String fontPath=  PrefManager.getString(PrefManager.Keys.interface_font,null);
        binding.editor.setFont(fontPath);

        //binding.editor.getProps().disallowSuggestions=true;

        binding.editor.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            // keyboard listener
            boolean isUndoDisplay = Config.getBool(PrefManager.Keys.bl_interface_undo_button_display);
            View view = isUndoDisplay ? binding.floatActionAdd : binding.floatActionLayout;
            if (oldBottom - bottom > 50) {
                //keyboard up
                binding.executionStatus.setVisibility(View.GONE);
                binding.editSymbolParent.setVisibility(View.VISIBLE);
                view.setVisibility(View.INVISIBLE);
            } else {
                binding.executionStatus.setVisibility(View.VISIBLE);
                binding.editSymbolParent.setVisibility(View.GONE);
                //keyboard down
                if (Config.getBool(PrefManager.Keys.bl_editor_autosave))saveFile();
                view.setVisibility(View.VISIBLE);
            }
        });

        //initialize TEXTMATE
        try {
            TextMate.initializeLogic(binding.editor, this);
        } catch (Exception e) {
            Snackbar.make(binding.editor, "Failed to load Grammar", Snackbar.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        binding.editor.setWordwrap(true, true);
        binding.floatActionUndo.setOnClickListener(v -> binding.editor.undo());
        binding.floatActionAdd.setOnClickListener(v -> {
            DialogUtils.showInputDialog(this, getString(R.string.create_article_text),
                    userInput -> new ArticleCreateFragment(userInput, project, filesListAdapter.getCurrentDir(), file -> {
                        setCodeText(file);
                        filesListAdapter.refreshList();
                    }).show(getSupportFragmentManager(), "")).setCanceledOnTouchOutside(false);
        });





    }

    public   void setCodeText(File file) {
        handle.stopSearch();
        TextMate.setLanguage(binding.editor, file.getName());
        ThreadUtil.thread(() -> {
            try {
                editorText = ContentIO.createFrom(new FileReader(file));
                currentFile = file;
                PrefManager.putString(PrefManager.Keys.current_file, currentFile.getAbsolutePath());
                runOnUiThread(() -> {
                    binding.editor.setText(editorText, true, null);
                    if (IO.isMdFile(currentFile)) markdown.loadMarkdownFromFile(currentFile);
                });
            } catch (IOException e) {
                runOnUiThread(() -> binding.editor.setText("No file be Displayed\n   " + getString(R.string.how_to_open_terminal)));
            }
        });

    }


    void setLayout() {
        binding.toolbar.setSubtitle(project.getBlogName());
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0);
        //  mToggle.setDrawerIndicatorEnabled(true);
        binding.drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT);
        binding.drawerLayout.addDrawerListener(new DrawerAnim(binding.navigationView));

        binding.navigationView.setNavigationItemSelectedListener(new NavigationControl(this,binding,project));


        ///   SearchHandle search= (SearchHandle) ((MenuItem) binding.toolbar.findViewById(R.id.menu_search)).getActionView();
        handle = new SearchHandle(binding.editor.getSearcher());
        handle.bind(binding.searchBar);

        binding.toolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();
            if (id == R.id.menu_run) {
                File file = new File(project.getScriptPath());
                if (!file.exists()) return false;
                DialogUtils.showMultiSelectDialog(this, getString(R.string.select_scripts), file.list(), executeStateControl::runScripts);

            } else if (id == R.id.menu_folder) {
                binding.drawerLayout.openDrawer(GravityCompat.END);
            } else if (id == R.id.menu_undo) {
                binding.editor.undo();
            } else if (id == R.id.menu_redo) {
                binding.editor.redo();
            } else if (id == R.id.menu_save) {
                saveFile();
            } else if (id == R.id.menu_search) {
                DialogUtils.showInputDialog(this, getString(R.string.search_in_editor), userInput -> {
                    binding.searchBar.getRoot().setVisibility(View.GONE);
                    binding.editor.getSearcher().search(userInput, new EditorSearcher.SearchOptions(true, true));
                    binding.progressbar.setIndeterminate(true);
                    binding.editor.subscribeEvent(PublishSearchResultEvent.class, (event, unsubscribe) -> {
                        if (!handle.isSearching())
                            return;
                        binding.progressbar.setIndeterminate(false);
                        if (event.getSearcher().getMatchedPositionCount() == 0) {
                            Snackbar.make(binding.getRoot(), "Text not found", Snackbar.LENGTH_SHORT).show();
                        } else {
                            binding.searchBar.getRoot().setVisibility(View.VISIBLE);
                        }
                    });
                });
            }
            return false;
        });

    }


    void runScripts(List<String> scriptsPath){
        binding.progressbar.setIndeterminate(true);
        Snackbar.make(binding.getRoot(), "Excuting", Snackbar.LENGTH_SHORT).show();
        logTextView.appendScriptText(scriptsPath);
        ThreadUtil.thread(() -> {
            boolean cmd = cmdExer.executeScripts(scriptsPath, project.getScriptPath(), true) == 0;
            runOnUiThread(() -> {
                binding.progressbar.setIndeterminate(false);

                logTextView.appendLogText(cmdExer.result);
                if (cmd) {
                    Snackbar.make(binding.getRoot(), "Success", Snackbar.LENGTH_SHORT).show();
                } else {

                    DialogUtils.showConfirmationDialog(this, getString(R.string.scripts_execution_failed), cmdExer.result, getString(android.R.string.copy), getString(R.string.cancle), () -> {
                        util.copyToClipboard(this, cmdExer.result);}, null);
                }
            });
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void setRecycleView() {
        FileListBinding include_binding = binding.include;
        filesListAdapter = new FilesListAdapter(
                include_binding.filelistCurrentPath,
                file -> {
                    if (IO.isMdFile(file)) {
                        String subtitle = IO.getMdTitle(file);
                        binding.toolbar.setSubtitle(subtitle == null ? file.getName() : subtitle);
                    } else {
                        binding.toolbar.setSubtitle(project.getBlogName());
                    }
                    setCodeText(file);
                    binding.drawerLayout.closeDrawer(GravityCompat.END);
                }
        );
        include_binding.recycleviewFiles.setLayoutAnimation(
                new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.recycle_view)));
        include_binding.recycleviewFiles.setAdapter(filesListAdapter);
        include_binding.recycleviewFiles.setLayoutManager(new LinearLayoutManager(this));
        String path;
        path = project.getProjectPath();
        filesListAdapter.showFileList(path, null);
        filesListAdapter.setActionButton(include_binding);

    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    void setTabLayout() {
        List<View> views = new ArrayList<>();
        cmdBinding = LayoutTerminalBinding.inflate(getLayoutInflater());
        cmdBinding.terminalBg.setBackgroundColor(ColorUtils.setAlphaComponent(com.google.android.material.R.attr.colorPrimary, 200));
        cmdBinding.ExtraKey.addView(cmdBinding.terminal.createKeyView());
        cmdBinding.ExtraKey.setBackgroundColor(Color.WHITE);
        cmdBinding.terminal.setTerminal(project.getProjectPath());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = 400;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        markdown = new MarkdownView(this.getApplicationContext());
        markdown.setLayoutParams(params);

        views.add(cmdBinding.getRoot());
        views.add(markdown);
        logTextView=new ScriptsLogTextView(this);
        logTextView.setLayoutParams(params);
        views.add(logTextView);

        markdown.addStyleSheet(new Github());
        markdown.loadMarkdown("## No markdown file in Editor");

        MainViewPagerAdapter fragmentAdapter = new MainViewPagerAdapter(views);
        new BottomTabControl(binding,cmdBinding,fragmentAdapter).setup();



    }



    void saveFile() {
        if (currentFile == null) return;
        binding.progressbar.setIndeterminate(true);
        ThreadUtil.thread(() -> {
            try {
                ContentIO.writeTo(editorText, new FileOutputStream(currentFile), true);
                runOnUiThread(() -> {
                    binding.progressbar.setIndeterminate(false);
                    // Snackbar.make(binding.getRoot(), "Saved", Snackbar.LENGTH_SHORT).show();
                    if (IO.isMdFile(currentFile)) markdown.loadMarkdownFromFile(currentFile);
                });
            } catch (IOException e) {
                runOnUiThread(() -> Snackbar.make(binding.getRoot(), "Save Failed", Snackbar.LENGTH_SHORT).show());
            }
        });


    }

    /**
     * for article_create dialog
     * in case of activity layout change when inputting in dialog;
     */

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
        if (Config.getBool(PrefManager.Keys.bl_editor_autosave))saveFile();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDestroy() {
 //       if (Config.getBool(PrefManager.Keys.bl_editor_autosave))saveFile();
        //binding.editor.setEditorLanguage(null);
        if (binding!=null) {
            binding.editor.release();
            markdown.destroyWebView();
        }
        super.onDestroy();
    }
}
