package com.pangbai.weblog.view;

import static java.lang.Character.toUpperCase;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.pangbai.weblog.R;
import com.pangbai.weblog.databinding.ArticleCreateBinding;
import com.pangbai.weblog.global.ThemeUtil;
import com.pangbai.weblog.preference.PrefManager;
import com.pangbai.weblog.project.HexoPostCreate;
import com.pangbai.weblog.project.Project;
import com.pangbai.weblog.project.ProjectManager;
import com.pangbai.weblog.tool.util;

import java.io.File;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class ArticleCreateFragment extends BottomSheetDialogFragment implements View.OnClickListener {


    public enum Type {
        POST,
        PAGE
    }

    String title, path, cwd;
    Project project;
    ArticleCreateBinding binding;
    BottomSheetBehavior behavior;
    TagManager tagManager;
    Set<String> categories;
    OnDismissListener callback;

    public ArticleCreateFragment(String title, Project project, String cwd, OnDismissListener callback) {

        this.title = title;
        this.project = project;
        this.cwd = cwd;
        this.callback = callback;

    }

    @SuppressLint({"RestrictedApi"})
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
        behavior = BottomSheetBehavior.from(bottomSheetInternal);
        behavior.setPeekHeight((int) util.dip2px(requireContext(), 300));

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


        //for description edittext
        binding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> binding.articleDescription.clearFocus());
        binding.articleDescription.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.requestFocus();
                //  ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(v,0);
            }
        });


        return bottomSheetDialog;
    }


    void setLayout() {


        binding.articalType.setText(Type.POST.name());
        binding.articalType.setSimpleItems(new String[]{Type.POST.name(), Type.PAGE.name()});
        binding.articalType.setOnItemClickListener((parent, view, position, id) -> {
            Log.e("select", "s");
            binding.articlePath.setText(generatePath(project.getProjectPath(), binding.articleTitle.getText().toString()));

        });

        binding.articalCreate.setOnClickListener(this);

        binding.articleTitle.setText(title);
        binding.articleTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                binding.articlePath.setText(generatePath(project.getProjectPath(), binding.articleTitle.getText().toString()));
        });

        binding.articlePath.setText(generatePath(project.getProjectPath(), title));

        categories = PrefManager.getStringArray(PrefManager.Keys.category, new TreeSet<>());

        binding.articalCategory.setSimpleItems(categories.stream().toArray(String[]::new));


        // Keyboard.
        tagManager = new TagManager(binding.articleTagGroup);
        String[] array=getContext().getResources().getStringArray(R.array.default_tag_array);
        Set<String> set=new TreeSet<>();
        Collections.addAll(set,array);
        tagManager.initChipGroup(PrefManager.getStringArray(PrefManager.Keys.tags,set ));
        binding.articleTagAdd.setOnClickListener(v -> {
            tagManager.addTag(binding.articleTagInput.getText().toString());
            binding.articleTagInput.setText(null);
        });

    }

    String generatePath(String projectPath, String name) {
        //base path
        if (project.blogType == ProjectManager.Type.hexo) {
            if (binding.articalType.getText().toString().equals(Type.POST.name())) {
                projectPath += "/source/_posts";
                if (cwd.contains(projectPath)) projectPath = cwd;
            } else if (!name.isBlank()) {
                return projectPath + "/source/" + name + "/index.md";
            }
        } else if (project.blogType == ProjectManager.Type.hugo) {
            if (binding.articalType.getText().toString().equals(Type.POST.name())) {
                projectPath += "/content/post";
                if (cwd.contains(projectPath)) projectPath = cwd;
            } else if (!name.isBlank()) {
               projectPath+="/content/"+name;
                // return projectPath + "/content/" + name + "/index.md";
            }
        }


        //name generation
        String tmp = "";
        if (name.isBlank()) {
            //nothing
        } else if (Charset.forName("US-ASCII").newEncoder().canEncode(name)) {
            String[] arr = name.split(" ");
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].isBlank())
                    continue;
                char[] chars = arr[i].toCharArray();
                chars[0] = toUpperCase(chars[0]);
                tmp += String.valueOf(chars);
            }
            tmp += ".md";

        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5"); // 选择所需的算法（这里使用md5）
                byte[] hashBytes = md.digest(name.getBytes()); // 将输入转换为字节数组并计算Hash值
                for (byte b : hashBytes) {
                    tmp += Integer.toString((b & 0xff) + 0x100, 16).substring(1); // 将每个字节转换成十六进制表示形式
                }
                tmp += ".md";
            } catch (Exception e) {
            }

        }


        return projectPath + "/" + tmp;
    }


    @Override
    public void onClick(View v) {
        Type type = Type.valueOf(binding.articalType.getText().toString());
        HexoPostCreate postCreate = new HexoPostCreate(type);
        String title = binding.articleTitle.getText().toString();
        if (title.isBlank()) {
            Snackbar.make(binding.getRoot(), "Title should not be blank", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String path = binding.articlePath.getText().toString();
        String category = binding.articalCategory.getText().toString();
        String description = binding.articleDescription.getText().toString();
        String[] tags = tagManager.getSelectTags();
        postCreate.setParams(title, path, category, description, tags);
        boolean created = postCreate.create();
        dismiss();
        if (created) {
            callback.openFile(new File(path));
            dismiss();
        } else {
            Toast.makeText(getActivity(), "Failed to create Artical", Toast.LENGTH_LONG).show();
        }
        categories.add(category);
        PrefManager.putStringArray(PrefManager.Keys.category, categories);
        PrefManager.putStringArray(PrefManager.Keys.tags, tagManager.getAllTags());

    }


    public interface OnDismissListener {
        void openFile(File file);
    }
}
