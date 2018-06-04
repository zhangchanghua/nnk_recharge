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
import com.nnk.rechargeplatform.recharge.model.RechargePhoneM;
import com.nnk.rechargeplatform.recharge.view.RechargePhoneActivity;
import com.nnk.rechargeplatform.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RechargePhonePresenter {
    private RechargePhoneActivity activity;
    private PriceAdater adater;

    public RechargePhonePresenter(RechargePhoneActivity activity) {
        this.activity = activity;

    }

    public void init(RecyclerView recyclerView) {
        List<RechargePhoneM> list = new ArrayList<>();
        list.add(new RechargePhoneM(10, Utils.getFloatFormat().format(10.00)));
        list.add(new RechargePhoneM(30, Utils.getFloatFormat().format(29.00)));
        list.add(new RechargePhoneM(50, Utils.getFloatFormat().format(49.00)));
        list.add(new RechargePhoneM(100, Utils.getFloatFormat().format(99.00)));
        list.add(new RechargePhoneM(200, Utils.getFloatFormat().format(199.00)));
        list.add(new RechargePhoneM(500, Utils.getFloatFormat().format(499.00)));
        adater = new PriceAdater(R.layout.item_recharge_phone, list);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        recyclerView.setAdapter(adater);
    }

    class PriceAdater extends BaseAdapter<RechargePhoneM> {

        public PriceAdater(int layoutId, List<RechargePhoneM> list) {
            super(layoutId, list);
        }

        @SuppressLint("StringFormatMatches")
        @Override
        protected void convert(BaseHolder holder, final RechargePhoneM item) {
            TextView title = holder.getView(R.id.title);
            TextView subTitle = holder.getView(R.id.sub_title);
            title.setText(activity.getString(R.string.recharge_origin_money, item.orginPrice));
            subTitle.setText(activity.getString(R.string.recharge_money, item.price));
            View rootView = holder.getView(R.id.item_root);
            rootView.setSelected(item.isSelect);
            rootView.setOnClickListener(new BaseOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    for (RechargePhoneM entry : mList) {
                        entry.isSelect = item.price == entry.price;
                    }
                    adater.notifyDataSetChanged();
                }
            });
        }
    }
}
