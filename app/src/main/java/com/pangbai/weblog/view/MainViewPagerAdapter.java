package com.pangbai.weblog.view;



import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;
/**
 * time:2023/7/15
 * author: 敬往事一杯酒
 */
public class MainViewPagerAdapter extends PagerAdapter {
    private final List<View> mList;
    public MainViewPagerAdapter(List<View> list){
        this.mList = list;
    }
    // 返回要滑动的View的个数
    @Override
    public int getCount() {
        return mList.size();
    }
    // 创建指定位置的页面图
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(mList.get(position));
        return mList.get(position);
    }
    // instantiateItem返回的值是否与当前视图一样
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    // 移除一个给定位置的页面
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mList.get(position));
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        // 返回每个 Fragment 的标签文本
        switch (position) {
            case 0:
                return "Terminal";
            case 1:
                return "Visual";
            case 2:
                return "Posts";
            default:
                return "";
        }
    }
}