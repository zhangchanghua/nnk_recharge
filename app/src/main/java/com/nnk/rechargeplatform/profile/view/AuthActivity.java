package com.nnk.rechargeplatform.profile.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.base.BaseOnClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthActivity extends BaseActivity {
    @BindView(R.id.type0_title)
    TextView type0Title;
    @BindView(R.id.type0_index)
    View type0Index;
    @BindView(R.id.type1_title)
    TextView type1Title;
    @BindView(R.id.type1_index)
    View type1Index;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_auth;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swichFragment(AuthShopFragment.class, 0);
        titleView.setText(R.string.profile_auth);
        setRightText(R.string.profile_rights);
        setRightClickLister(new BaseOnClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

            }
        });
    }

    @OnClick({R.id.shop, R.id.person})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.shop) {
            type0Title.setTextColor(getResources().getColor(R.color.colorAccent));
            type1Title.setTextColor(getResources().getColor(R.color.gray));
            type0Index.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            type1Index.setBackgroundColor(getResources().getColor(R.color.gray));
            swichFragment(AuthShopFragment.class, 0);
            titleView.setText(R.string.profile_auth);
        } else if (id == R.id.person) {
            type0Title.setTextColor(getResources().getColor(R.color.gray));
            type1Title.setTextColor(getResources().getColor(R.color.colorAccent));
            type0Index.setBackgroundColor(getResources().getColor(R.color.gray));
            type1Index.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            swichFragment(AuthPersonFragment.class, 1);
            titleView.setText(R.string.profile_auth_upgrate);
        }
    }

    private void swichFragment(Class<? extends Fragment> cls, int type) {
        int id = R.id.content;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (type == 0) {
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }

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
