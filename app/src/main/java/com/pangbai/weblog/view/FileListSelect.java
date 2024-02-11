package com.pangbai.weblog.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pangbai.weblog.R;

import com.pangbai.weblog.databinding.FileListBinding;
import com.pangbai.weblog.tool.IO;
import com.pangbai.weblog.tool.Init;

import java.io.File;

public class FileListSelect {
    Boolean isDIr=false;
    Context context;
    FileChoose callback;
    File root=new File(Init.sdcardPath);
    String title;
    AlertDialog alertDialog;
    public FileListSelect(Context context,String title,boolean isDIr, String root, FileChoose callback){
        this.context=context;
        this.title=title;
        this.callback=callback;
        this.isDIr=isDIr;
        if (root!=null)  this.root=new File(root);
    }

    public FileListSelect(Context context,boolean isDIr,  FileChoose callback){
        this.context=context;
        this.callback=callback;
        this.isDIr=isDIr;

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public  AlertDialog showChooseDialog( ) {
        MaterialAlertDialogBuilder builder=  new MaterialAlertDialogBuilder(context);
        FileListBinding binding=FileListBinding.inflate( LayoutInflater.from(context));
       // binding.getRoot().setBackgroundResource(0);
        FilesListAdapter.OnFilesListClickCallBack mOnclick=null;

        if (!isDIr) mOnclick= file -> {
            if (file.isFile()){
                callback.callback(file);
                alertDialog.dismiss();
            }
        };

        FilesListAdapter mAdapter = new FilesListAdapter(binding.filelistCurrentPath, mOnclick);
        binding.recycleviewFiles.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(context, R.anim.recycle_view)));
        binding.recycleviewFiles.setAdapter(mAdapter);
        binding.recycleviewFiles.setLayoutManager(new LinearLayoutManager(context));
        mAdapter.showFileList(root.getAbsolutePath(),isDIr? mAdapter.filterDir : null);
        mAdapter.setActionButton(binding);

        if (title==null) title=context.getString(isDIr?R.string.select_folder:R.string.select_file);

        builder.setView(binding.getRoot())
                .setTitle(title)
                .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                  callback.callback(mAdapter.mCurrentfile);
                  dialog.dismiss();
                })
                .setNegativeButton(context.getString(R.string.cancle), null);
        builder.setBackground(context.getDrawable(R.drawable.drawer_round_bg));
         alertDialog = builder.create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        alertDialog.show();


        return alertDialog;
    }

    public interface FileChoose {
        void callback(File file);
    }

}
