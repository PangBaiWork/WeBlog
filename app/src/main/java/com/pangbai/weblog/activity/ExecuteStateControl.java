package com.pangbai.weblog.activity;

import static com.pangbai.weblog.tool.util.runOnUiThread;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.ActivityMainBinding;
import com.pangbai.weblog.execute.cmdExer;
import com.pangbai.weblog.project.Project;
import com.pangbai.weblog.tool.DialogUtils;
import com.pangbai.weblog.tool.ThreadUtil;
import com.pangbai.weblog.tool.util;
import com.pangbai.weblog.view.ScriptsLogTextView;

import java.util.List;

public class ExecuteStateControl {
    public enum executeStatus {
        SUCCESSFUL ,
        FAILED,
        EXECUTING

    }

    ActivityMainBinding binding;
    ScriptsLogTextView logTextView;
    List<String> scripts;
    Project project;

    public ExecuteStateControl(ActivityMainBinding binding, ScriptsLogTextView logTextView, Project project) {
        this.binding = binding;
        this.logTextView = logTextView;
        this.project=project;
    }


    void runScripts(List<String> scripts){
        binding.progressbar.setIndeterminate(true);
        binding.executionStatus.setText(executeStatus.EXECUTING+scripts.toString());
        //Snackbar.make(binding.getRoot(), "Excuting", Snackbar.LENGTH_SHORT).show();
        logTextView.appendScriptText(scripts);
        ThreadUtil.thread(() -> {
            boolean cmd = cmdExer.executeScripts(scripts, project.getScriptPath(), true) == 0;
            runOnUiThread(() -> {

                binding.progressbar.setIndeterminate(false);

                logTextView.appendLogText(cmdExer.result);
                if (cmd) {
                    binding.executionStatus.setText(executeStatus.SUCCESSFUL+scripts.toString());
                   // Snackbar.make(binding.getRoot(), "Success", Snackbar.LENGTH_SHORT).show();
                } else {
                    Context context=binding.getRoot().getContext();
                    binding.executionStatus.setText(executeStatus.FAILED+scripts.toString());
                    DialogUtils.showConfirmationDialog(context, context.getString(R.string.scripts_execution_failed), cmdExer.result,context.getString(android.R.string.copy),context.getString(R.string.cancle), () -> {
                        util.copyToClipboard(context, cmdExer.result);}, null);
                }
            });
        });
    }

}
