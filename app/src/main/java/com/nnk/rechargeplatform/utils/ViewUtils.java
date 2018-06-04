package com.nnk.rechargeplatform.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.nnk.rechargeplatform.R;

import java.util.List;

public class ViewUtils {

    public static void showToast(Context context, int resId) {
        showToast(context, context.getResources().getString(resId));
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, int resId) {
        showToast(context, context.getResources().getString(resId));
    }

    public static void showToastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    //显示单条的picker
    public static void showSingleItemPicker(Context context, List<String> list, OnOptionsSelectListener l) {
        OptionsPickerView pickerView = new OptionsPickerBuilder(context, l).setCancelColor(context.getResources().getColor(R.color.gray))
                .setLineSpacingMultiplier(1.7f)
                .setContentTextSize(22)
                .setTextColorCenter(context.getResources().getColor(R.color.colorAccent))
                .build();
        pickerView.setPicker(list);
        pickerView.show();
    }

    //view增加点击效果
    public static void bindClickAnim(final View v) {
        if (v != null) {
            v.animate().scaleX(0.95f).scaleY(0.95f).alpha(0.5f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    v.animate().setListener(null);
                    v.animate().scaleX(1).scaleY(1).alpha(1).setDuration(100).start();
                }
            }).start();
        }
    }
}
