package com.nnk.rechargeplatform.main.presenter;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseAdapter;
import com.nnk.rechargeplatform.base.BaseHolder;
import com.nnk.rechargeplatform.base.BaseOnClickListener;
import com.nnk.rechargeplatform.main.model.RechargeCategory;
import com.nnk.rechargeplatform.main.view.RechargeFragment;
import com.nnk.rechargeplatform.recharge.view.RechargeFlowActivity;
import com.nnk.rechargeplatform.recharge.view.RechargeOilActivity;
import com.nnk.rechargeplatform.recharge.view.RechargePhoneActivity;
import com.nnk.rechargeplatform.recharge.view.RechargeQQActivity;
import com.nnk.rechargeplatform.recharge.view.RechargeTelActivity;
import com.nnk.rechargeplatform.recharge.view.RechargeVideoCateActivity;

import java.util.ArrayList;
import java.util.List;

public class RechargePresenter {
    private RechargeFragment fragment;
    private CategoryAdapter adapter;


    public RechargePresenter(RechargeFragment fragment) {
        this.fragment = fragment;
    }

    public void init(RecyclerView recyclerView) {
        List<RechargeCategory> itemList = new ArrayList<>();
        itemList.add(new RechargeCategory(0, R.mipmap.recharge_phone, fragment.getString(R.string.recharge_mobile)));
        itemList.add(new RechargeCategory(1, R.mipmap.recharge_flow, fragment.getString(R.string.recharge_flow)));
        itemList.add(new RechargeCategory(2, R.mipmap.recharge_video, fragment.getString(R.string.recharge_video_vip)));
        itemList.add(new RechargeCategory(3, R.mipmap.recharge_oil, fragment.getString(R.string.recharge_oil)));
        itemList.add(new RechargeCategory(4, R.mipmap.recharge_qq, fragment.getString(R.string.recharge_qq)));
        itemList.add(new RechargeCategory(5, R.mipmap.recharge_tel, fragment.getString(R.string.recharge_tel)));
        adapter = new CategoryAdapter(R.layout.item_recharge_category, itemList);
        recyclerView.setLayoutManager(new GridLayoutManager(fragment.getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    class CategoryAdapter extends BaseAdapter<RechargeCategory> {

        public CategoryAdapter(int layoutId, List<RechargeCategory> list) {
            super(layoutId, list);
        }

        @Override
        protected void convert(BaseHolder holder, final RechargeCategory item) {
            ((ImageView) holder.getView(R.id.icon)).setImageResource(item.iconRes);
            ((TextView) holder.getView(R.id.title)).setText(item.name);
            ((View) holder.getView(R.id.item_root)).setOnClickListener(new BaseOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    if (item.id == 0) {
                        fragment.startActivity(new Intent(fragment.getContext(), RechargePhoneActivity.class));
                    } else if (item.id == 1) {
                        fragment.startActivity(new Intent(fragment.getContext(), RechargeFlowActivity.class));
                    } else if (item.id == 2) {
                        fragment.startActivity(new Intent(fragment.getContext(), RechargeVideoCateActivity.class));
                    } else if (item.id == 3) {
                        fragment.startActivity(new Intent(fragment.getContext(), RechargeOilActivity.class));
                    } else if (item.id == 4) {
                        fragment.startActivity(new Intent(fragment.getContext(), RechargeQQActivity.class));
                    } else if (item.id == 5) {
                        fragment.startActivity(new Intent(fragment.getContext(), RechargeTelActivity.class));
                    }
                }
            });
        }
    }

}
