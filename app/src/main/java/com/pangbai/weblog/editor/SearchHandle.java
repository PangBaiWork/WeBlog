package com.pangbai.weblog.editor;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.EditorSearcherBinding;
import com.pangbai.weblog.tool.DialogUtils;

import io.github.rosemoe.sora.widget.EditorSearcher;

public class SearchHandle implements View.OnClickListener {
    EditorSearcherBinding binding;
    EditorSearcher searcher;

    public SearchHandle( EditorSearcher searcher){
        this.searcher=searcher;
    }
    public void bind( EditorSearcherBinding binding){
        this.binding=binding;
        binding.searchNext.setOnClickListener(this);
        binding.searchPrevious.setOnClickListener(this);
        binding.searchReplace.setOnClickListener(this);
        binding.searchClose.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
  if (v==binding.searchPrevious){
      searcher.gotoPrevious();
  } else if (v==binding.searchNext) {
      searcher.gotoNext();
  } else if (v==binding.searchClose) {
      stopSearch();
  } else if (v==binding.searchReplace) {
      Context context=binding.getRoot().getContext();
      DialogUtils.showInputDialog(context, context.getString(R.string.search_replace),userInput -> {
        if (userInput==null)
            userInput="";
        searcher.replaceAll(userInput, () -> {
            Snackbar.make(binding.getRoot(),"Replacement successful",Snackbar.LENGTH_SHORT).show();
        });
      });

  }
    }

    public void  stopSearch(){
        binding.getRoot().setVisibility(View.GONE);
        if (searcher.hasQuery())
            searcher.stopSearch();
    }
    public boolean isSearching(){
        return searcher.hasQuery();
    }

}
