package com.pangbai.weblog.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.provider.DocumentsContractCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.snackbar.Snackbar;
import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.ActivityHomeBinding;
import com.pangbai.weblog.databinding.LayoutTerminalBinding;
import com.pangbai.weblog.execute.cmdExer;
import com.pangbai.weblog.global.ThemeUtil;
import com.pangbai.weblog.tool.DialogUtils;
import com.pangbai.weblog.tool.Init;
import com.pangbai.weblog.tool.ProjectManager;
import com.pangbai.weblog.tool.util;
import com.pangbai.weblog.view.FileListSelect;

import java.io.File;

import br.tiagohm.markdownview.Utils;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityHomeBinding binding;
    ActivityResultLauncher chooseFolder;
    boolean isTerminalOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // chooseFolder = registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), result -> openProject(result));
        binding.github.setOnClickListener(this);
        binding.openProject.setOnClickListener(this);
        binding.createProject.setOnClickListener(this);
        binding.pullProject.setOnClickListener(this);
        binding.openTerminal.setOnClickListener(this);
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
                AlertDialog inputdialog = DialogUtils.showInputDialog(this, getString(R.string.input_blog_title), userInput -> {
                    ProjectManager projectManager = new ProjectManager();
                    AlertDialog dialog = DialogUtils.showLoadingDialog(this);
                    new Thread() {
                        @Override
                        public void run() {
                            boolean init = projectManager.createProject(file.getAbsolutePath(), userInput, ProjectManager.Type.hexo);
                            runOnUiThread(() -> {
                                dialog.dismiss();
                                if (init) {
                                    openProject(file.getAbsolutePath());
                                } else {
                                    Snackbar.make(binding.getRoot(), "Failed, please try terminal", Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }.start();

                });
            });


        } else if (id == R.id.pull_project) {
            select(file -> {
                DialogUtils.showInputDialog(this, getString(R.string.clone_git), userInput -> {
                    AlertDialog dialog = DialogUtils.showLoadingDialog(this);
                    new Thread() {
                        @Override
                        public void run() {
                            boolean clone = cmdExer.execute("git clone " + userInput + " " + file.getAbsolutePath(), false) == 0;
                            dialog.dismiss();
                            runOnUiThread(() -> {
                                if (clone) {
                                    openProject(file.getAbsolutePath());

                                } else {
                                    Snackbar.make(binding.getRoot(), "Clone failed", Snackbar.LENGTH_SHORT);

                                }
                            });
                        }
                    }.start();
                });
            });
        } else if (id == R.id.open_project) {
            select(file -> {
                openProject(file.getAbsolutePath());
            });
        } else if (id== R.id.open_terminal) {
            openTerminal();
        }

    }

    void select(FileListSelect.FileChoose fileChoose) {
        FileListSelect list = new FileListSelect(this, fileChoose, true, null);
        list.showChooseDialog();
    }

    // Snackbar.make(v,"loading...",Snackbar.LENGTH_SHORT).show();
    void openProject(String path) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("project_path", path);
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

            isTerminalOpen=true;
        }


        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }



}
