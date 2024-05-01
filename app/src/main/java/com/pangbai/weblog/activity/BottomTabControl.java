package com.pangbai.weblog.activity;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.ActivityMainBinding;
import com.pangbai.weblog.databinding.LayoutTerminalBinding;
import com.pangbai.weblog.view.MainViewPagerAdapter;

public class BottomTabControl extends BottomSheetBehavior.BottomSheetCallback implements TabLayout.OnTabSelectedListener {
    TabLayout tabLayout;
    ActivityMainBinding binding;
    LayoutTerminalBinding cmdBinding;
    BottomSheetBehavior behavior;

    public BottomTabControl(ActivityMainBinding binding, LayoutTerminalBinding cmdBinding, MainViewPagerAdapter fragmentAdapter){
         this.binding=binding;
         this.tabLayout=binding.tabLayout;
         this.cmdBinding=cmdBinding;
         binding.viewpager.setAdapter(fragmentAdapter);

    }
    public void setup(){
        //Repair pager content reconstruction
        binding.viewpager.setOffscreenPageLimit(2);

        // tabLayout跟viewpager关联
        tabLayout.setupWithViewPager(binding.viewpager);

        behavior = BottomSheetBehavior.from(binding.bottomSheet);
        behavior.setHalfExpandedRatio(0.3F);
        behavior.addBottomSheetCallback(this);
        tabLayout.addOnTabSelectedListener(this);
    }
    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            binding.bottomBar.setVisibility(View.VISIBLE);
        } else {
            binding.bottomBar.setVisibility(View.GONE);
        }
        // focus on cmdview;
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            if (tabLayout.getSelectedTabPosition() == 0) {
                cmdBinding.terminal.requestFocus();
            }
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        // focus on cmdview;
        if (tab.getPosition() == 1) {
           cmdBinding.terminal.requestFocus();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
