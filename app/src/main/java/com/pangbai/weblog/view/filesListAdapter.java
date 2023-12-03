package com.pangbai.weblog.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pangbai.weblog.R;

import java.io.File;

public class filesListAdapter extends RecyclerView.Adapter<Holder> {
    //  ArrayList <File> filesList;
    public File mCurrentfile;
    public File[] childFiles;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_files_item, null);
        Holder holder = new Holder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull Holder mholder, int position) {
        int res = R.drawable.ic_file;
        if (childFiles[position].isDirectory()) {
            res = R.drawable.ic_folder;
        } else if (childFiles[position].getName().endsWith(".md")) {
            res = R.drawable.ic_markdown;
        }
        mholder.mItemIcon.setBackgroundResource(res);
        mholder.mItemName.setText(childFiles[position].getName());


    }

    @Override
    public int getItemCount() {
        return childFiles == null ? 0 : childFiles.length;
    }

    public void setList(File files) {
        if (!files.exists() || !files.canRead())
            return;
        this.mCurrentfile = files;
        this.childFiles = files.listFiles();
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


