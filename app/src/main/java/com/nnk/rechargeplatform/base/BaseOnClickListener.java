package com.nnk.rechargeplatform.base;

import android.view.View;

import com.nnk.rechargeplatform.utils.ViewUtils;

public class BaseOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(final View v) {
        ViewUtils.bindClickAnim(v);
    }
}
