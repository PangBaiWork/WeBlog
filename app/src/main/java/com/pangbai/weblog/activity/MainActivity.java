
package com.pangbai.weblog.activity;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
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
import com.pangbai.weblog.editor.TextMate;
import com.pangbai.weblog.execute.HexoExer;
import com.pangbai.weblog.execute.cmdExer;
import com.pangbai.weblog.preference.PrefManager;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentIO;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    LayoutTerminalBinding cmdBinding;
    MarkdownView markdown;
    Content editorText;
    File currentFile;

    Project project;
    Process livePreview;
    FilesListAdapter filesListAdapter;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefManager.init(getApplicationContext());
        project=ProjectManager.getCurrentProject();
        if (PrefManager.isFirstLaunch()||project==null||!new File(project.getProjectPath()).exists()) {
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
        binding.editor.setText("No file be Displayed\n" + getString(R.string.how_to_open_terminal));
        setEditor();
        setNavigation();
        //   setCodeText(new File(project.getProjectPath()));


    }


    void setEditor() {
        binding.editor.setCursorAnimationEnabled(false);
        binding.editSymbol.init(binding.editor);
        binding.editor.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            // keyboard listener
            if (oldBottom-bottom>50){
                binding.floatActionAdd.hide();
            }else {
                binding.floatActionAdd.show();
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

    void setCodeText(File file) {
        TextMate.setLanguage(binding.editor, file.getName());
        ThreadUtil.thread(() -> {
            try {
                editorText = ContentIO.createFrom(new FileReader(file));
                currentFile = file;
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


        //状态栏中的文字颜色和图标颜色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
        //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        binding.toolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();
            if (id == R.id.menu_run) {
                File file = new File(project.scriptPath);
                if (!file.exists()) return false;
                DialogUtils.showMultiSelectDialog(this, getString(R.string.select_scripts), file.list(), selets -> {
                    binding.progressbar.setIndeterminate(true);
                    Snackbar.make(binding.getRoot(), "Excuting", Snackbar.LENGTH_SHORT).show();

                    ThreadUtil.thread(() -> {
                        boolean cmd = cmdExer.executeScripts(selets, project.scriptPath, true) == 0;
                        runOnUiThread(() -> {
                            binding.progressbar.setIndeterminate(false);
                            if (cmd)
                                Snackbar.make(binding.getRoot(), "Success", Snackbar.LENGTH_SHORT).show();
                        });
                    });

                });

            } else if (id == R.id.menu_folder) {
                binding.drawerLayout.openDrawer(GravityCompat.END);
            } else if (id == R.id.menu_undo) {
                binding.editor.undo();
            } else if (id == R.id.menu_redo) {
                binding.editor.redo();
            } else if (id == R.id.menu_save) {
                saveFile();
            }
            return false;
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
        include_binding.recycleviewFiles.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.recycle_view)));
        include_binding.recycleviewFiles.setAdapter(filesListAdapter);
        include_binding.recycleviewFiles.setLayoutManager(new LinearLayoutManager(this));
        String path;
        path =project.getProjectPath();
        filesListAdapter.showFileList(path, null);
        filesListAdapter.setActionButton(include_binding);

    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    void setTabLayout() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        List<View> views = new ArrayList<>();

        cmdBinding = LayoutTerminalBinding.inflate(getLayoutInflater());
        cmdBinding.terminalBg.setBackgroundColor(ColorUtils.setAlphaComponent(com.google.android.material.R.attr.colorPrimary, 200));
        cmdBinding.ExtraKey.addView(cmdBinding.terminal.createKeyView());
        cmdBinding.ExtraKey.setBackgroundColor(Color.WHITE);
        cmdBinding.terminal.setTerminal(project.getProjectPath());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = 400;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;

        markdown = new MarkdownView(this);
        markdown.setLayoutParams(params);

        views.add(cmdBinding.getRoot());
        views.add(markdown);


        markdown.addStyleSheet(new Github());
        markdown.loadMarkdown("## No markdown file in Editor");

        MainViewPagerAdapter fragmentAdapter = new MainViewPagerAdapter(views);
        viewPager.setAdapter(fragmentAdapter);
        // tabLayout跟viewpager关联
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHalfExpandedRatio(0.3F);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.editSymbol.setVisibility(View.VISIBLE);
                } else {
                    binding.editSymbol.setVisibility(View.GONE);
                }
                // focus on cmdview;
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (tabLayout.getSelectedTabPosition() == 1) {
                        cmdBinding.terminal.requestFocus();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                // focus on cmdview;
                if (tab.getPosition() == 1) {
                    cmdBinding.terminal.requestFocus();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }

    void setNavigation() {
        binding.navigationView.setNavigationItemSelectedListener(this);

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
        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.project_server) {
            if (livePreview != null) {
               livePreview.destroy();
                return false;
            }
            DialogUtils.showInputDialog(this, getString(R.string.start_server) + project.blogType.name()
                    , "4000", userInput -> {
                        item.setChecked(true);
                        Snackbar.make(binding.navigationView, "Server Running", Snackbar.LENGTH_SHORT).show();
                        livePreview = project.blogCmd.Server(userInput, false);
                        checkProcess(item);
                    });


        } else if (id == R.id.project_script) {
            new FileListSelect(this, getString(R.string.scripts_list), false, project.scriptPath, file -> {
                setCodeText(file);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }).showChooseDialog();
        } else if (id == R.id.project_close) {
            if (livePreview != null) livePreview.destroy();
            util.startActivity(this, HomeActivity.class, false);
            finish();
        } else if (id == R.id.global_setting) {
            Snackbar.make(binding.navigationView,"Todo..",Snackbar.LENGTH_SHORT).show();

        } else if (id == R.id.global_env) {
            Snackbar.make(binding.navigationView,"Todo..",Snackbar.LENGTH_SHORT).show();

        }
        return false;
    }

    void checkProcess(MenuItem item) {
        ThreadUtil.thread(() -> {
            try {
                livePreview.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            livePreview.destroy();
            livePreview = null;
            runOnUiThread(() -> {
                Snackbar.make(binding.getRoot(), "The LivePreview ended", Snackbar.LENGTH_SHORT).show();
                item.setChecked(false);
            });
        });


    }


}
