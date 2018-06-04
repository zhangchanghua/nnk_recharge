package com.nnk.rechargeplatform.recharge.presenter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseAdapter;
import com.nnk.rechargeplatform.base.BaseHolder;
import com.nnk.rechargeplatform.base.BaseOnClickListener;
import com.nnk.rechargeplatform.recharge.model.RechargeVideoM;
import com.nnk.rechargeplatform.recharge.view.RechargeVideoActivity;
import com.nnk.rechargeplatform.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RechargeVideoPresenter {
    private RechargeVideoActivity activity;
    private PriceAdater adater;

    public RechargeVideoPresenter(RechargeVideoActivity activity) {
        this.activity = activity;

    }

    public void init(RecyclerView recyclerView) {
        List<RechargeVideoM> list = new ArrayList<>();
        list.add(new RechargeVideoM("月卡", Utils.getFloatFormat().format(10.00)));
        list.add(new RechargeVideoM("季卡", Utils.getFloatFormat().format(29.00)));
        list.add(new RechargeVideoM("半年卡", Utils.getFloatFormat().format(49.00)));
        list.add(new RechargeVideoM("年卡", Utils.getFloatFormat().format(99.00)));

        adater = new PriceAdater(R.layout.item_recharge_video, list);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        recyclerView.setAdapter(adater);
    }

    class PriceAdater extends BaseAdapter<RechargeVideoM> {

        public PriceAdater(int layoutId, List<RechargeVideoM> list) {
            super(layoutId, list);
        }

        @Override
        protected void convert(BaseHolder holder, final RechargeVideoM item) {
            TextView title = holder.getView(R.id.title);
            TextView subTitle = holder.getView(R.id.sub_title);
            title.setText(item.name);
            subTitle.setText(activity.getString(R.string.recharge_money, item.price));
            View rootView = holder.getView(R.id.item_root);
            rootView.setSelected(item.isSelect);
            rootView.setOnClickListener(new BaseOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    for (RechargeVideoM entry : mList) {
                        entry.isSelect = item.price == entry.price;
                    }
                    adater.notifyDataSetChanged();
                }
            });
        }
    }
}
