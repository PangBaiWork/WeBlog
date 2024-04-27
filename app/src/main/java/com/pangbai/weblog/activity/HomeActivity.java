package com.pangbai.weblog.activity;

import static com.pangbai.weblog.project.ProjectManager.checkIsHexo;
import static com.pangbai.weblog.project.ProjectManager.checkIsHugo;
import static com.pangbai.weblog.project.ProjectManager.checkType;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.ActivityHomeBinding;
import com.pangbai.weblog.databinding.LayoutTerminalBinding;
import com.pangbai.weblog.execute.cmdExer;
import com.pangbai.weblog.preference.PrefManager;
import com.pangbai.weblog.project.Project;
import com.pangbai.weblog.tool.DialogUtils;
import com.pangbai.weblog.tool.Init;
import com.pangbai.weblog.project.ProjectManager;
import com.pangbai.weblog.tool.ThreadUtil;
import com.pangbai.weblog.tool.util;
import com.pangbai.weblog.view.FileListSelect;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityHomeBinding binding;

    boolean isTerminalOpen = false;
    Project selectProject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new Init(this);
        // chooseFolder = registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), result -> openProject(result));
        binding.github.setOnClickListener(this);
        binding.openProject.setOnClickListener(this);
        binding.createProject.setOnClickListener(this);
        binding.pullProject.setOnClickListener(this);
        binding.openTerminal.setOnClickListener(this);
        binding.setting.setOnClickListener(this);
        binding.donate.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.github) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.project_url)));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(Intent.createChooser(intent, "GITHUB"));

        } else if (id == R.id.create_project) {

            select(file -> {
                if (Objects.requireNonNull(file.list()).length != 0) {
                    Snackbar.make(binding.getRoot(), "The folder should be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                DialogUtils.showSingleSelectDialog(this, getString(R.string.select_blog_engine),ProjectManager.getTypeArray(), select -> {
                    selectProject = new Project(file.getName(), file.getAbsolutePath(), ProjectManager.Type.valueOf(select));
                    ProjectManager projectManager = new ProjectManager(selectProject);
                    AlertDialog dialog = DialogUtils.showLoadingDialog(this);
                    ThreadUtil.thread(() -> {
                        boolean init = projectManager.createProject();
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            if (init) {
                                openProject(selectProject);
                            } else {
                                Snackbar.make(binding.getRoot(), "Failed, Please try again at the terminal", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    });


                });

            });


        } else if (id == R.id.pull_project) {

                DialogUtils.showInputDialog(this, getString(R.string.clone_git), userInput -> {

                    select(file -> {
                        if (Objects.requireNonNull(file.list()).length != 0) {
                            Snackbar.make(binding.getRoot(), "The folder should be empty", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    AlertDialog dialog = DialogUtils.showLoadingDialog(this);
                    ThreadUtil.thread(() -> {
                        boolean clone = cmdExer.execute("git clone " + userInput + " " + file.getAbsolutePath(), false) == 0;
                        if (clone) {
                            ProjectManager.Type type = checkType(file);
                            if (type == null) {
                                runOnUiThread(() -> Snackbar.make(binding.getRoot(), "Is not a hexo or hugo project", Snackbar.LENGTH_SHORT).show());
                                return;
                            }
                            selectProject = new Project(file.getName(), file.getAbsolutePath(), type);
                        }
                        dialog.dismiss();
                        runOnUiThread(() -> {
                            if (clone) {
                                openProject(selectProject);
                            } else {
                                Snackbar.make(binding.getRoot(), "Clone failed", Snackbar.LENGTH_SHORT);
                            }
                        });
                    });

                });



            });
        } else if (id == R.id.open_project) {
            select(file -> {
                ProjectManager.Type type = checkType(file);
                if (type == null) {
                    Snackbar.make(binding.getRoot(), "Is not a hexo or hugo project", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                selectProject = new Project(file.getName(), file.getAbsolutePath(), type);
                openProject(selectProject);
            });
        } else if (id == R.id.open_terminal) {
          openTerminal();
        } else if (id == R.id.setting) {
            util.startActivity(this, SettingsActivity.class,false);
        } else if (id == R.id.donate) {
           // Snackbar.make(binding.getRoot(), "Todo..", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.donate_url)));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(Intent.createChooser(intent, "GITHUB"));
        }

    }

    void select(FileListSelect.FileChoose fileChoose) {
        FileListSelect list = new FileListSelect(this, true, fileChoose);
        list.showChooseDialog();
    }


    void openProject(Project project) {
        PrefManager.putString(PrefManager.Keys.current_file,"");
        ProjectManager.saveCurrentProject(project);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("RestrictedApi")
    void openTerminal() {
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setDraggable(true);
        if (!isTerminalOpen) {
            LayoutTerminalBinding cmdBinding = LayoutTerminalBinding.inflate(getLayoutInflater());

            bottomSheet.addView(cmdBinding.getRoot());
            // cmdBinding.terminalBg.setBackgroundColor(ColorUtils.setAlphaComponent(com.google.android.material.R.attr.colorPrimary, 200));
            cmdBinding.ExtraKey.addView(cmdBinding.terminal.createKeyView());
            cmdBinding.terminal.setTerminal(Init.filesDirPath);

            //ThemeUtil.applyEdgeToEdge(getWindow());

            isTerminalOpen = true;
        }


        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}
