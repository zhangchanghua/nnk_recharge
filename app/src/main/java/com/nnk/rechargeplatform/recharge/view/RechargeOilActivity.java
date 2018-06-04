package com.nnk.rechargeplatform.recharge.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RechargeOilActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
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
        return R.layout.activity_recharge_oil;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.recharge_oil);
        final List<RechargeOilFragment> fragmentList = new ArrayList<>();
        fragmentList.add(new RechargeOilFragment());
        fragmentList.add(new RechargeOilFragment());
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.type0)
    public void onType0(View v) {
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.type1)
    public void onType1(View v) {
        viewPager.setCurrentItem(1);
    }

    private void setSelect(int index) {
        if (index == 0) {
            type0Title.setTextColor(getResources().getColor(R.color.colorAccent));
            type1Title.setTextColor(getResources().getColor(R.color.gray));
            type0Index.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            type1Index.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            type0Title.setTextColor(getResources().getColor(R.color.gray));
            type1Title.setTextColor(getResources().getColor(R.color.colorAccent));
            type0Index.setBackgroundColor(getResources().getColor(R.color.gray));
            type1Index.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }
}
