package com.nnk.rechargeplatform.main.presenter;

import com.nnk.rechargeplatform.main.view.MsgFragment;

public class MsgPresenter {
    MsgFragment fragment;
    private int currentPageIndex;

    public MsgPresenter(MsgFragment fragment) {
        this.fragment = fragment;
    }

    public void getData() {
        fragment.stopRefresh();
    }

    public void resetPageIndex() {
        currentPageIndex = 0;
    }

    public void increasePageIndex() {
        currentPageIndex++;
    }

}
