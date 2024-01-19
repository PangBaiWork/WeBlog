package com.pangbai.weblog.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.pangbai.weblog.tool.DialogUtils;
import com.pangbai.weblog.R;
import com.pangbai.weblog.tool.IO;
import com.pangbai.weblog.tool.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class FilesListAdapter extends RecyclerView.Adapter<Holder> {
    //  ArrayList <File> filesList;
    public File mCurrentfile;
    public File[] childFiles;
    private final OnFilesListClickCallBack mOnclick;

    ViewGroup parent;
    TextView pathView;
    Context context;



    public FilesListAdapter(TextView pathView,OnFilesListClickCallBack mOnclick) {
        this.pathView=pathView;
        this.mOnclick = mOnclick;

    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_files_item, parent, false);
        Holder holder = new Holder(view);
        // For list Click & LongClick
        holder.itemView.setOnClickListener(arg0 -> {
            File targetFile = childFiles[holder.getAdapterPosition()];
            if (targetFile.getName().equals("db.json"))
                return;
            if (targetFile.isDirectory()) {
                    setList(targetFile);
            } else if (targetFile.exists()) {
                mOnclick.onClick(targetFile);
            }else {
                Snackbar.make(parent, "No File Found", Snackbar.LENGTH_SHORT).show();
            }
        });


        holder.itemView.setOnLongClickListener(v -> {
            File targetFile = childFiles[holder.getAdapterPosition()];
            PopupMenu pop = new PopupMenu(v.getContext(), v);
            pop.inflate(R.menu.file_action);
            pop.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.file_action_delete) {
                    new Thread() {
                        @Override
                        public void run() {
                            // use shell to delete for better speed
                            IO.deleteFolder(targetFile);
                            util.runOnUiThread(() -> {
                                setList(Objects.requireNonNull(targetFile.getParentFile()));
                            });
                        }
                    }.start();
                } else if (id == R.id.file_action_rename) {

                    DialogUtils.showInputDialog(context,
                            context.getResources().getString(R.string.file_action_rename), targetFile.getAbsolutePath(),
                            userInput -> {
                                Snackbar.make(parent, "moving files", Snackbar.LENGTH_SHORT).show();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        boolean move = IO.renameOrMoveFile(targetFile, userInput);
                                        util.runOnUiThread(() -> {
                                            if (move) {
                                                Snackbar.make(parent, "rename success", Snackbar.LENGTH_SHORT).show();
                                                setList(Objects.requireNonNull(targetFile.getParentFile()));
                                            }else{
                                                Snackbar.make(parent, "rename failed", Snackbar.LENGTH_SHORT).show();}
                                        });
                                    }
                                }.start();

                            });

                }
                return false;
            });
            pop.show();
            return true;
        });

        return holder;

    }


    @Override
    public void onBindViewHolder(@NonNull Holder mholder, int position) {
        int res = R.drawable.ic_file;
        String name = childFiles[position].getName();
        if (childFiles[position].isDirectory()) {
            res = R.drawable.ic_folder;
        } else if (name.endsWith(".md")) {
            res = R.drawable.ic_file_markdown;
            String title;
            if ((title = IO.getMdTitle(childFiles[position])) != null)
                name = name + " | " + title;

        }

        mholder.mItemIcon.setBackgroundResource(res);
        mholder.mItemName.setText(name);


    }

    @Override
    public int getItemCount() {
        return childFiles == null ? 0 : childFiles.length;
    }
    public String getCurrentDir(){
        return mCurrentfile.getAbsolutePath();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(File files) {
        if (!files.exists() || !files.canRead()) {

            Snackbar.make(parent, "Can Not Open This Folder", Snackbar.LENGTH_SHORT).show();

            return;
        }

        this.mCurrentfile = files;
        this.childFiles = files.listFiles();
        Arrays.sort(childFiles, (file1, file2) -> {
            if (file1.isDirectory() && !file2.isDirectory()) {
                return -1; // 文件夹排在前面
            } else if (!file1.isDirectory() && file2.isDirectory()) {
                return 1; // 文件夹排在前面
            } else {
                return file1.getName().compareToIgnoreCase(file2.getName());
            }
        });
        pathView.setText(files.getAbsolutePath());

        notifyDataSetChanged();
        if (parent != null)
            parent.scheduleLayoutAnimation();


    }


    public interface OnFilesListClickCallBack {
        public void onClick(File file);
    }



}


class Holder extends RecyclerView.ViewHolder {
    TextView mItemName;
    LinearLayout mItemIcon;


    public Holder(@NonNull View itemView) {
        super(itemView);
        mItemName = itemView.findViewById(R.id.list_item_name);
        mItemIcon = itemView.findViewById(R.id.list_item_icon);
    }
}


