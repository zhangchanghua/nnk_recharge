package com.nnk.rechargeplatform.recharge.presenter;

import android.annotation.SuppressLint;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseAdapter;
import com.nnk.rechargeplatform.base.BaseHolder;
import com.nnk.rechargeplatform.base.BaseOnClickListener;
import com.nnk.rechargeplatform.recharge.model.RechargeFlowM;
import com.nnk.rechargeplatform.recharge.view.RechargeFlowActivity;
import com.nnk.rechargeplatform.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RechargeFlowPresenter {
    private RechargeFlowActivity activity;
    private PriceAdater adater;

    public RechargeFlowPresenter(RechargeFlowActivity activity) {
        this.activity = activity;

    }

    public void init(RecyclerView recyclerView) {
        List<RechargeFlowM> list = new ArrayList<>();
        list.add(new RechargeFlowM("150M", Utils.getFloatFormat().format(10.00)));
        list.add(new RechargeFlowM("500M", Utils.getFloatFormat().format(29.00)));
        list.add(new RechargeFlowM("1G", Utils.getFloatFormat().format(49.00)));
        list.add(new RechargeFlowM("3G", Utils.getFloatFormat().format(99.00)));
        list.add(new RechargeFlowM("5G", Utils.getFloatFormat().format(199.00)));
        list.add(new RechargeFlowM("10G", Utils.getFloatFormat().format(299.00)));
        adater = new PriceAdater(R.layout.item_recharge_flow, list);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        recyclerView.setAdapter(adater);
    }

    class PriceAdater extends BaseAdapter<RechargeFlowM> {

        public PriceAdater(int layoutId, List<RechargeFlowM> list) {
            super(layoutId, list);
        }

        @SuppressLint("StringFormatMatches")
        @Override
        protected void convert(BaseHolder holder, final RechargeFlowM item) {
            TextView title = holder.getView(R.id.title);
            TextView subTitle = holder.getView(R.id.sub_title);
            title.setText(item.size);
            subTitle.setText(activity.getString(R.string.recharge_money, item.price));
            View rootView = holder.getView(R.id.item_root);
            rootView.setSelected(item.isSelect);
            rootView.setOnClickListener(new BaseOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    for (RechargeFlowM entry : mList) {
                        entry.isSelect = item.price == entry.price;
                    }
                    adater.notifyDataSetChanged();
                }
            });
        }
    }
}
