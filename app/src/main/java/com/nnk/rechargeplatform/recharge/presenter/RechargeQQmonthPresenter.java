package com.nnk.rechargeplatform.recharge.presenter;

import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.nnk.rechargeplatform.recharge.view.RechargeQQmonthFragment;
import com.nnk.rechargeplatform.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class RechargeQQmonthPresenter {
    private RechargeQQmonthFragment fragment;

    public RechargeQQmonthPresenter(RechargeQQmonthFragment fragment) {
        this.fragment = fragment;

    }

    public void init() {

    }

    public void selectMonthType(final TextView textView) {
        final List<String> typeList = new ArrayList<>();
        typeList.add("QQ会员");
        typeList.add("QQ黄钻");
        typeList.add("QQ超级会员");
        typeList.add("QQ蓝钻");
        typeList.add("QQ绿钻");
        typeList.add("QQ飞车紫钻");
        typeList.add("QQ红钻");
        typeList.add("QQ黑钻");
        typeList.add("QQ炫舞紫钻");
        ViewUtils.showSingleItemPicker(fragment.getContext(), typeList, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                textView.setText(typeList.get(options1));
            }
        });
    }


    public void selectMonthCount(final TextView textView) {
        final List<String> monthList = new ArrayList<>();
        monthList.add("1个月");
        monthList.add("2个月");
        monthList.add("3个月");
        monthList.add("6个月");
        monthList.add("12个月");
        ViewUtils.showSingleItemPicker(fragment.getContext(), monthList, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                textView.setText(monthList.get(options1));
            }
        });
    }


}
