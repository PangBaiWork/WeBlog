package com.pangbai.weblog.view;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.pangbai.weblog.R;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class filesListAdapter extends RecyclerView.Adapter<Holder> {
    //  ArrayList <File> filesList;
    public File mCurrentfile;
    public File[] childFiles;
    private OnFilesListClick mOnclick;
    ViewGroup parent;


    public filesListAdapter(OnFilesListClick onclick){
        mOnclick=onclick;
    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent=parent;
        View view = View.inflate(parent.getContext(), R.layout.list_files_item, null);
        Holder holder = new Holder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO: Implement this method
                // Toast.makeText(parent.getContext(),"1"+holder.getAdapterPosition(),Toast.LENGTH_LONG).show();
                File targetFile=childFiles[holder.getAdapterPosition()];
                if( targetFile.isDirectory()){
                    if (targetFile.list().length!=0){
                    setList(targetFile);
                    parent.scheduleLayoutAnimation();
                    }else {
                        Snackbar.make(parent,"No File Found",Snackbar.LENGTH_SHORT).show();
                       // Toast.makeText(parent.getContext(),,Toast.LENGTH_SHORT).show();
                    }
                }else if(targetFile.exists()) {
                    mOnclick.onClick(targetFile);
                }
            }

        });

        return holder;

    }


    @Override
    public void onBindViewHolder(@NonNull Holder mholder, int position) {
        int res = R.drawable.ic_file;
        if (childFiles[position].isDirectory()) {
            res = R.drawable.ic_folder;
        } else if (childFiles[position].getName().endsWith(".md")) {
            res = R.drawable.ic_file_markdown;
        }
        mholder.mItemIcon.setBackgroundResource(res);
        mholder.mItemName.setText(childFiles[position].getName());


    }

    @Override
    public int getItemCount() {
        return childFiles == null ? 0 : childFiles.length;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(File files) {
        if (!files.exists() || !files.canRead()){
            Snackbar.make(parent,"Can Not Open This Folder",Snackbar.LENGTH_SHORT).show();
            return;
        }

        this.mCurrentfile = files;
        this.childFiles = files.listFiles();
        Arrays.sort(childFiles, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                if (file1.isDirectory() && !file2.isDirectory()) {
                    return -1; // 文件夹排在前面
                } else if (!file1.isDirectory() && file2.isDirectory()) {
                    return 1; // 文件夹排在前面
                } else {
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            }
        });

        notifyDataSetChanged();




    }

    public   interface OnFilesListClick {
        public	void onClick(File file);
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


