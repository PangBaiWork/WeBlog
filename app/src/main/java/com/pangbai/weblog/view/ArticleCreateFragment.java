package com.pangbai.weblog.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.internal.ViewUtils;
import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.ActivityMainBinding;
import com.pangbai.weblog.databinding.ArticleCreateBinding;
import com.pangbai.weblog.global.ThemeUtil;
import com.pangbai.weblog.tool.util;

public  class  ArticleCreateFragment extends BottomSheetDialogFragment {
    String title;
    ArticleCreateBinding binding;
    BottomSheetBehavior behavior;
    public ArticleCreateFragment(String title){
        this.title=title;
    }
    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Set up BottomSheetDialog
        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(getContext(), R.style.ThemeOverlay_Catalog_BottomSheetDialog_Scrollable);
        ThemeUtil.applyEdgeToEdge(bottomSheetDialog.getWindow());

      //  new WindowPreferencesManager(requireContext()).applyEdgeToEdgePreference(bottomSheetDialog.getWindow());
        binding = ArticleCreateBinding.inflate(getLayoutInflater());
       // bottomSheetDialog.setContentView(R.layout.article_create);
        bottomSheetDialog.setContentView(binding.getRoot());
        setLayout();
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
       behavior= BottomSheetBehavior.from(bottomSheetInternal);
               behavior.setPeekHeight((int) util.dip2px(requireContext(),300));

        View bottomSheetContent = bottomSheetInternal.findViewById(R.id.bottom_drawer_2);

        ViewUtils.doOnApplyWindowInsets(bottomSheetContent, (v, insets, initialPadding) -> {
            // Add the inset in the inner NestedScrollView instead to make the edge-to-edge behavior
            // consistent - i.e., the extra padding will only show at the bottom of all content, i.e.,
            // only when you can no longer scroll down to show more content.
            ViewCompat.setPaddingRelative(bottomSheetContent,
                    initialPadding.start,
                    initialPadding.top,
                    initialPadding.end,
                    initialPadding.bottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });
        return bottomSheetDialog;
    }





   void setLayout( ){
        binding.articleTitle.setText(title);
        TagManager tagManager=new TagManager(binding.articleTagGroup);
        tagManager.initChipGroup();
        binding.articleTagAdd.setOnClickListener(v ->{
           tagManager.addTag(binding.articleTagInput.getText().toString());
        binding.articleTagInput.setText(null);});
      //  binding.articleTitle.setOnFocusChangeListener(focus);
   }



}
