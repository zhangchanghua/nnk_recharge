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
import com.nnk.rechargeplatform.recharge.model.RechargeOilM;
import com.nnk.rechargeplatform.recharge.view.RechargeOilFragment;

import java.util.ArrayList;
import java.util.List;

public class RechargeOilPresenter {
    private RechargeOilFragment fragment;
    private PriceAdater adater;

    public RechargeOilPresenter(RechargeOilFragment fragment) {
        this.fragment = fragment;

    }

    public void init(RecyclerView recyclerView) {
        List<RechargeOilM> list = new ArrayList<>();
        list.add(new RechargeOilM("100元", "售价 100.00元"));
        list.add(new RechargeOilM("200元", "售价 200.00元"));
        list.add(new RechargeOilM("500元", "售价 500.00元"));
        list.add(new RechargeOilM("1000元", "售价 1000.00元"));
        list.add(new RechargeOilM("其它", ""));

        adater = new PriceAdater(R.layout.item_recharge_oil, list);
        recyclerView.setLayoutManager(new GridLayoutManager(fragment.getContext(), 3));
        recyclerView.setAdapter(adater);
    }

    class PriceAdater extends BaseAdapter<RechargeOilM> {

        public PriceAdater(int layoutId, List<RechargeOilM> list) {
            super(layoutId, list);
        }

        @SuppressLint("StringFormatMatches")
        @Override
        protected void convert(BaseHolder holder, final RechargeOilM item) {
            TextView title = holder.getView(R.id.title);
            TextView subTitle = holder.getView(R.id.sub_title);
            title.setText(item.title);
            subTitle.setText(item.price);
            subTitle.setVisibility("".equals(item.price) ? View.GONE : View.VISIBLE);
            View rootView = holder.getView(R.id.item_root);
            rootView.setSelected(item.isSelect);
            rootView.setOnClickListener(new BaseOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    for (RechargeOilM entry : mList) {
                        entry.isSelect = item.price == entry.price;
                    }
                    fragment.togglePriceInput("".equals(item.price) ? View.VISIBLE : View.GONE);
                    adater.notifyDataSetChanged();
                }
            });
        }
    }
}
