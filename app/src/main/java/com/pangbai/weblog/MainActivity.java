
package com.pangbai.weblog;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.contentcapture.ContentCaptureContext;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import com.pangbai.terminal.view.SuperTerminalView;
import com.pangbai.weblog.databinding.ActivityMainBinding;
import com.pangbai.weblog.editor.TextMate;
import com.pangbai.weblog.global.ThemeUtil;
import com.pangbai.weblog.tool.DialogUtils;
import com.pangbai.weblog.tool.IO;
import com.pangbai.weblog.tool.Init;
import com.pangbai.weblog.tool.permission;

import com.pangbai.weblog.view.ArticleCreateFragment;
import com.pangbai.weblog.view.FilesListAdapter;
import com.pangbai.weblog.view.MainViewPagerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentIO;
import io.github.rosemoe.sora.widget.SymbolInputView;



public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    SuperTerminalView cmdView;
    MarkdownView markdown;
    Content editorText;
    File currentFile;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setLayout();
        setContentView(binding.getRoot());

        new Init(this);
        new permission(this).checkPermission();


        setRecycleView();
        // binding.markdownView.addStyleSheet(new Github());
        // binding.markdownView.loadMarkdown("**MarkdownView**");
        // binding.markdownView.loadMarkdownFromAsset("markdown1.md");
        //  binding.markdownView.loadMarkdownFromFile(new File("/storage/emulated/0/blog/source/_posts/IT/re2.md"));
        // binding.markdownView.loadMarkdownFromUrl("url");
        setTabLayout();
        setTerminal();
        setEditor();
        setCodeText(new File("/storage/emulated/0/blog/source/_posts/IT/re2.md"));

    }



    void setTerminal() {
        String[] n = {"sh"};
        String[] envp = {
                //"PATH=" + "/system/bin"
                "PATH=" + Init.binDir + ":/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
                "HOME=" + Init.filesDirPath,
                "PREFIX=" + Init.filesDirPath + "/usr",
                "LD_LIBRARY_PATH=" + Init.filesDirPath + "/usr/lib",
                "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
                "TERM=xterm-256color",
                "LANG=en_US.UTF-8",
                "ANDROID_DATA=/data",
                "ANDROID_ROOT=/system",
                "LD_PRELOAD=" + Init.libDir + "/libexec"
        };


        String path = getApplicationInfo().nativeLibraryDir;
        //   Log.e("weblog",path);
        cmdView.setProcess(path + "/busybox", getFilesDir().getAbsolutePath(), n, envp, 0);
        cmdView.runProcess();
        cmdView.requestFocus();


    }

    void setEditor() {
        binding.editor.setCursorAnimationEnabled(false);

        binding.editSymbol.init(binding.editor);

     //  binding.editSymbol.addSymbols(SymbolInputView.s,sym,offset);
        try {
           TextMate.initializeLogic(binding.editor, this);
        } catch (Exception e) {
            e.printStackTrace();
            //   Log.e("editor",e)
        }
        binding.editor.setWordwrap(true,true);


        binding.floatActionUndo.setOnClickListener(v -> binding.editor.undo());
        binding.floatActionAdd.setOnClickListener(v -> {
            DialogUtils.showInputDialog(this,getString(R.string.create_article_text),
                    userInput -> new ArticleCreateFragment(userInput).show(getSupportFragmentManager(), ""));

        });


    }

    void setCodeText(File file) {
        /*TextMateLanguage language= (TextMateLanguage) binding.editor.getEditorLanguage();
        language.updateLanguage(TextMate.findLanguageScopeName(file.getName()));*/
        TextMate.setLanguage(binding.editor, file.getName());
        new Thread() {
            @Override
            public void run() {
                try {
                    editorText=ContentIO.createFrom(new FileReader(file));
                    currentFile=file;
                    runOnUiThread(() -> {
                        binding.editor.setText(editorText);

                    });


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

    }


    void setLayout() {

        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0);


        //  mToggle.setDrawerIndicatorEnabled(true);
        binding.drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT);
     /*   binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (drawerView == binding.navigationView) {
                    View contentView = binding.drawerLayout.getChildAt(0);
                    // 侧边栏
                    // slideOffset 值默认是0~1
                    contentView.setTranslationX(drawerView.getMeasuredWidth() * slideOffset);

                } else {
                    // do nothing on all other views
                }
                // 主页内容
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        */

        //状态栏中的文字颜色和图标颜色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
        //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_folder) {
                binding.drawerLayout.openDrawer(GravityCompat.END);
            }else if (id==R.id.menu_undo){
                binding.editor.undo();
            }else if (id==R.id.menu_redo){
                binding.editor.redo();
            }else if (id==R.id.menu_save){

                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                ContentIO.writeTo(editorText,new FileOutputStream(currentFile),true);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }.start();


            }
            return false;
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    void setRecycleView() {

        FilesListAdapter mAdapter = new FilesListAdapter(
                binding.drawerCurrentPath,
                file -> {
                    // binding.markdownView.loadMarkdownFromFile(file);
                    if (file.getName().endsWith(".md")) {
                        String subtitle = IO.getMdTitle(file);
                        binding.toolbar.setSubtitle(subtitle == null ? file.getName() : subtitle);
                    } else {
                        binding.toolbar.setSubtitle("Blog Name");
                    }
                    setCodeText(file);

                    binding.drawerLayout.closeDrawer(GravityCompat.END);

                }
              );
        binding.recycleviewFiles.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.recycle_view)));
        binding.recycleviewFiles.setAdapter(mAdapter);
        binding.recycleviewFiles.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setList(new File("/storage/emulated/0/blog/source/_posts/IT/"));
        binding.drawerFloatActionHome.setOnClickListener(v -> {
            mAdapter.setList(new File("/storage/emulated/0/blog/source/_posts/IT/"));
         //   binding.recycleFiles.scheduleLayoutAnimation();

        });
        binding.drawerCurrentPath.setOnClickListener(v -> {
            DialogUtils.showInputDialog(this,getString(R.string.jump_to),userInput -> mAdapter.setList(new File(userInput)));
        });
        binding.drawerFloatActionAdd.setOnClickListener(v -> {
            String prefix=  mAdapter.getCurrentDir()+"/";
            DialogUtils.showInputDialog(this,getString(R.string.create_file) ,
                    new String[]{getString(R.string.folder),getString(R.string.file)},
                    userInput->{
                      if (IO.createFileOrDir(prefix+userInput,true)){
                          mAdapter.setList(new File(prefix+userInput));
                      }else {
                          Snackbar.make(binding.recycleviewFiles,getString(R.string.notice_action_failed),Snackbar.LENGTH_SHORT).show();
                      }
                    },
                    userInput -> {
                        if (IO.createFileOrDir(prefix+userInput,false)){
                            mAdapter.setList(mAdapter.mCurrentfile);
                        }else {
                            Snackbar.make(binding.recycleviewFiles,getString(R.string.notice_action_failed),Snackbar.LENGTH_SHORT).show();
                        }
                    });

        });

        binding.drawerFloatActionParents.setOnClickListener(v -> {
            mAdapter.setList(Objects.requireNonNull(mAdapter.mCurrentfile.getParentFile()));
        //    binding.recycleFiles.scheduleLayoutAnimation();
        });

    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    void setTabLayout() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPager viewPager = findViewById(R.id.viewpager);
        List<View> views = new ArrayList<>();

        cmdView = new SuperTerminalView(this, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = 400;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        cmdView.setLayoutParams(params);
        cmdView.setBackgroundColor(ColorUtils.setAlphaComponent(com.google.android.material.R.attr.colorPrimary, 200));

        markdown = new MarkdownView(this);
        markdown.setLayoutParams(params);



        views.add(markdown);
        views.add(cmdView);
        //views.add(articalList);
        markdown.addStyleSheet(new Github());
        markdown.loadMarkdownFromFile(new File("/storage/emulated/0/blog/source/_posts/IT/re2.md"));
        // views.add(getLayoutInflater().inflate(R.layout.third_page,null));
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
                if (newState==BottomSheetBehavior.STATE_COLLAPSED){
                    Log.e("visible","true");
                    binding.editSymbol.setVisibility(View.VISIBLE);
                }else{
                    Log.e("visible","false");
                    binding.editSymbol.setVisibility(View.GONE);
                }
                // focus on cmdview;
                if (newState==BottomSheetBehavior.STATE_EXPANDED){
                    if (tabLayout.getSelectedTabPosition()==1){
                        cmdView.requestFocus();
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
                if (tab.getPosition()==1){
                    cmdView.requestFocus();
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

    /**
     * for article_create dialog
     * in case of activity layout change when inputting in dialog;
     */

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
        super.onWindowFocusChanged(hasFocus);
    }



}
