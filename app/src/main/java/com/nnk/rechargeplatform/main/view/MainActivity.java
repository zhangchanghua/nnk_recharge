package com.nnk.rechargeplatform.main.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.nnk.rechargeplatform.App;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.utils.BankListManager;
import com.nnk.rechargeplatform.utils.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_recharge)
    View tabRecharge;
    @BindView(R.id.tab_manage)
    View tabManage;
    @BindView(R.id.tab_mesaage)
    View tabMsg;
    @BindView(R.id.tab_profile)
    View tab_profile;
    private int currentTabId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBackView();
        tabRecharge.setSelected(true);
        swichFragment(RechargeFragment.class);
        titleView.setText(R.string.home_title_recharge);

        BankListManager.get().getBankList(this, null);
    }

    @Override
    @OnClick({R.id.tab_recharge, R.id.tab_manage, R.id.tab_mesaage, R.id.tab_profile})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (currentTabId == id) {
            return;
        }
        currentTabId = id;
        tabRecharge.setSelected(false);
        tabManage.setSelected(false);
        tabMsg.setSelected(false);
        tab_profile.setSelected(false);

        if (id == R.id.tab_recharge) {
            tabRecharge.setSelected(true);
            swichFragment(RechargeFragment.class);
            titleView.setText(R.string.home_title_recharge);
            findViewById(R.id.header).setBackgroundResource(R.drawable.header_bg);
        } else if (id == R.id.tab_manage) {
            tabManage.setSelected(true);
            swichFragment(ManageFragment.class);
            titleView.setText(R.string.home_title_manage);
            findViewById(R.id.header).setBackgroundResource(R.drawable.header_bg);
        } else if (id == R.id.tab_mesaage) {
            tabMsg.setSelected(true);
            swichFragment(MsgFragment.class);
            titleView.setText(R.string.home_title_message);
            findViewById(R.id.header).setBackgroundResource(R.drawable.header_bg);
        } else if (id == R.id.tab_profile) {
            tab_profile.setSelected(true);
            swichFragment(ProfileFragment.class);
            titleView.setText(R.string.home_title_profile);
            findViewById(R.id.header).setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private long mExitTime;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ViewUtils.showToast(this, R.string.exit_tip);
            mExitTime = System.currentTimeMillis();
        } else {
            App.getInstance().exit();
        }
    }

    protected void swichFragment(Class<? extends Fragment> cls) {
        int id = R.id.content;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        List<Fragment> frgList = fm.getFragments();
        if (frgList == null) {
            ft.add(id, getFragmentByClass(cls));
        } else {
            for (Fragment entry : frgList) {
                ft.hide(entry);
            }
            Fragment fg = fm.findFragmentByTag(cls.getName());
            if (fg != null) {
                ft.show(fg);
            } else {
                ft.add(id, getFragmentByClass(cls), cls.getName());
            }
        }
        ft.commitAllowingStateLoss();
        fm.executePendingTransactions();
    }

    Fragment getFragmentByClass(Class<? extends Fragment> cls) {
        Class<?> c = null;
        try {
            c = Class.forName(cls.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Fragment frg = null;
        try {
            frg = (Fragment) c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return frg;
    }

}
