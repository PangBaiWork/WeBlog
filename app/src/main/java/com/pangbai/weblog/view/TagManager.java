package com.pangbai.weblog.view;

import android.content.Context;
import android.view.LayoutInflater;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.pangbai.weblog.R;

import io.github.rosemoe.sora.text.Content;

public class TagManager {
    ChipGroup chipGroup;
    Context context;

    public TagManager(ChipGroup chipGroup) {
        this.chipGroup = chipGroup;
        this.context=chipGroup.getContext();
    }
    public void initChipGroup( ) {
        chipGroup.removeAllViews();
        boolean singleSelection = false;
        String[] textArray = context.getResources().getStringArray(R.array.cat_textfield_exposed_dropdown_content);
       addTags(textArray);
    }

    public void  addTag(String tag){
        if (tag==null||tag.isBlank())
            return;
        Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.tags_chips, chipGroup, false);
        chip.setText(tag);
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
        chipGroup.addView(chip);
    }

    public void  addTags(String tags[]){
        for (String tag:tags){
            addTag(tag);
        }
    }
}
