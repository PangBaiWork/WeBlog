package com.pangbai.weblog.tool;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pangbai.weblog.R;

import java.util.ArrayList;
import java.util.List;


public class DialogUtils {

    public interface OnPositiveClickListener {
        void onPositiveButtonClick();
    }
    public interface OnNegativeClickListener {
        void onNegativeButtonClick();
    }
    public interface DialogInputListener {
        void onConfirm(String userInput);
    }
    public interface OnSelectListener {
        void select(List<String> selects);
    }
    public static AlertDialog showConfirmationDialog(Context context, String title, String message,
                                              final OnPositiveClickListener positiveClickListener,final OnNegativeClickListener negativeClickListener){
       return  showConfirmationDialog(context,title,message,context.getString(R.string.confirm),context.getString(R.string.cancle),positiveClickListener,negativeClickListener);
    }

    public static AlertDialog showConfirmationDialog(Context context, String title, String message,
                                              String positiveButtonText, String negativeButtonText,
                                              final OnPositiveClickListener positiveClickListener,final OnNegativeClickListener negativeClickListener) {
        MaterialAlertDialogBuilder builder=  new MaterialAlertDialogBuilder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, (dialogInterface, i) -> {
                    if (positiveClickListener != null) {
                        positiveClickListener.onPositiveButtonClick();
                    }
                })
                .setNegativeButton(negativeButtonText, (dialogInterface, i) -> {
                    if (negativeClickListener != null) {
                        negativeClickListener.onNegativeButtonClick();
                    }
                  //  dialogInterface.dismiss();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }
    public static AlertDialog showSelectDialog(Context context,String title,String[] choices,OnSelectListener callback) {
            return showSelectDialog(context,title,choices,new boolean[choices.length],callback);
    }

    public static AlertDialog showSelectDialog(Context context,String title,String[] choices,boolean[] choicesInitial,OnSelectListener callback){
      return   new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setPositiveButton(
                        context.getString(R.string.confirm),
                        (DialogInterface dialog, int which) -> {
                            SparseBooleanArray checkedItemPositions =
                                    ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                            List<String> result = new ArrayList<>();
                            for (int i = 0; i < choices.length; i++) {
                                if (checkedItemPositions.get(i)) {
                                    result.add(choices[i]);
                                }
                            }
                            callback.select(result);
                          //  Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
                        })
                .setNegativeButton(context.getString(R.string.cancle), null)
                .setMultiChoiceItems(choices, choicesInitial, null)
                .show();
    }

    public static AlertDialog showLoadingDialog(Context context){
        return showLoadingDialog(context,null);
    }

    public static AlertDialog showLoadingDialog(Context context,String title){
        if (title==null)title=context.getString(R.string.loading);
        return showCustomLayoutDialog(context,title,R.layout.dialog_loading);
    }


    public static AlertDialog showCustomLayoutDialog(Context context,String title, int layoutResId) {
        // Inflate custom layout
        MaterialAlertDialogBuilder builder=  new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(layoutResId, null);
        builder.setView(dialogView)
                .setTitle(title);
        AlertDialog alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(false); // Prevent dismiss on outside touch
        alertDialog.show();
        return alertDialog;
    }


    public static void showInputDialog(Context context, String title,String text,final DialogInputListener pos){
        EditText editText=  showInputDialog(context,title,new String[]{"确定","取消"},pos,null).findViewById(R.id.dialog_text_input);
        if (editText != null) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }
    public static AlertDialog showInputDialog(Context context, String title,final DialogInputListener pos){
       return showInputDialog(context,title,new String[]{context.getString(R.string.confirm),context.getString(R.string.cancle)},pos,null);
    }

    public static AlertDialog showInputDialog(Context context, String title,String[] button,final DialogInputListener pos,final DialogInputListener neg) {
        MaterialAlertDialogBuilder builder=  new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        EditText inputEditText = dialogView.findViewById(R.id.dialog_text_input);
        builder.setView(dialogView)
                .setTitle(title)
                .setPositiveButton(button[0], (dialog, which) -> {
                    if (pos==null)
                        return;
                    String userInput = inputEditText.getText().toString();
                        pos.onConfirm(userInput);
                })
                .setNegativeButton(button[1], (dialog, which) -> {
                    if (neg == null)
                        return;
                    String userInput = inputEditText.getText().toString();
                        neg.onConfirm(userInput);

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        alertDialog.show();
        inputEditText.requestFocus();
        return alertDialog;
    }


}
