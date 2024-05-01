package com.pangbai.weblog.activity;

import static com.pangbai.weblog.tool.util.runOnUiThread;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.ActivityMainBinding;
import com.pangbai.weblog.execute.BlogCmd;
import com.pangbai.weblog.project.Project;
import com.pangbai.weblog.tool.DialogUtils;
import com.pangbai.weblog.tool.ThreadUtil;
import com.pangbai.weblog.tool.util;
import com.pangbai.weblog.view.FileListSelect;

public class NavigationControl implements NavigationView.OnNavigationItemSelectedListener {
    Process livePreview;
    ActivityMainBinding binding;
    Project project;
    Context context;
    MainActivity mainActivity;
   public NavigationControl(MainActivity mainActivity,ActivityMainBinding binding, Project project){
       this.binding=binding;
       this.project=project;
       this.context=binding.getRoot().getContext();
       this.mainActivity=mainActivity;

   }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.project_server) {
            if (livePreview != null) {
                livePreview.destroy();
                return false;
            }
            DialogUtils.showInputDialog(context, context.getString(R.string.start_server) + project.blogType.name()
                    , "4000", userInput -> {
                        item.setChecked(true);
                        Snackbar.make(binding.navigationView, "Server Running", Snackbar.LENGTH_SHORT).show();
                        livePreview = project.blogCmd.Server(userInput, false);
                        checkProcess(item);
                    });


        } else if (id == R.id.project_script) {
            new FileListSelect(context, context.getString(R.string.scripts_list), false, project.getScriptPath(), file -> {
               mainActivity. setCodeText(file);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }).showChooseDialog();
        } else if (id == R.id.project_close) {
            if (livePreview != null) livePreview.destroy();
            util.startActivity(context, HomeActivity.class, false);
            mainActivity.finish();
        } else if (id == R.id.global_setting) {
            util.startActivity(context, SettingsActivity.class, false);
        } else if (id == R.id.global_env) {
            Snackbar.make(binding.navigationView, "Checking", Snackbar.LENGTH_SHORT).show();
            ThreadUtil.thread(() -> {
                String res = BlogCmd.checkEnvironment();
                runOnUiThread(() -> {
                    DialogUtils.showConfirmationDialog(context, context.getString(R.string.environment), res, null, null);
                });
            });
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
