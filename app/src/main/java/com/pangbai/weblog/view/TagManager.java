package com.pangbai.weblog.view;

import android.content.Context;
import android.view.LayoutInflater;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.pangbai.weblog.R;
import com.pangbai.weblog.tool.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.rosemoe.sora.text.Content;

public class TagManager {
    ChipGroup chipGroup;
    Context context;

    public TagManager(ChipGroup chipGroup) {
        this.chipGroup = chipGroup;
        this.context = chipGroup.getContext();
    }

    public void initChipGroup(Set<String> array) {
        chipGroup.removeAllViews();
        addTags(array);
    }

    public void addTag(String tag) {
        if (tag == null || tag.isBlank())
            return;
        Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.tags_chips, chipGroup, false);
        chip.setText(tag);
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
        chipGroup.addView(chip);
    }

    public void addTags(Set<String> tags) {
        for (String tag : tags) {
            addTag(tag);
        }
    }


    public Set<String> getAllTags(){
     int tagCount=  chipGroup.getChildCount();
     //  String []array=new String[tagCount];
       Set<String> array=new TreeSet<>();
        for (int i=0;i<tagCount;i++){
            Chip chip= (Chip) chipGroup.getChildAt(i);
            array.add( chip.getText().toString());
        }
        return array;
    }
    public String[] getSelectTags(){
        int tagCount=  chipGroup.getChildCount();
        String []array=new String[chipGroup.getCheckedChipIds().size()];
        int n=0;
        for (int i=0;i<tagCount;i++){
            Chip chip= (Chip) chipGroup.getChildAt(i);
           if (chip.isChecked()) {
               array[n] = chip.getText().toString();
                n++;
           }
        }
    return array;
    }

}
