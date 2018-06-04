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
import com.nnk.rechargeplatform.recharge.model.RechargeQQpointM;
import com.nnk.rechargeplatform.recharge.view.RechargeQQpointFragment;

import java.util.ArrayList;
import java.util.List;

public class RechargeQQpointPresenter {
    private RechargeQQpointFragment fragment;
    private PriceAdater adater;

    public RechargeQQpointPresenter(RechargeQQpointFragment fragment) {
        this.fragment = fragment;

    }

    public void init(RecyclerView recyclerView) {
        List<RechargeQQpointM> list = new ArrayList<>();
        list.add(new RechargeQQpointM("10个", "售价 10.00元"));
        list.add(new RechargeQQpointM("20个", "售价 20.00元"));
        list.add(new RechargeQQpointM("50个", "售价 50.00元"));
        list.add(new RechargeQQpointM("100个", "售价 100.00元"));
        list.add(new RechargeQQpointM("其它", ""));

        adater = new PriceAdater(R.layout.item_recharge_qq_coin, list);
        recyclerView.setLayoutManager(new GridLayoutManager(fragment.getContext(), 3));
        recyclerView.setAdapter(adater);
    }

    class PriceAdater extends BaseAdapter<RechargeQQpointM> {

        public PriceAdater(int layoutId, List<RechargeQQpointM> list) {
            super(layoutId, list);
        }

        @SuppressLint("StringFormatMatches")
        @Override
        protected void convert(BaseHolder holder, final RechargeQQpointM item) {
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
                    for (RechargeQQpointM entry : mList) {
                        entry.isSelect = item.price == entry.price;
                    }
                    fragment.togglePriceInput("".equals(item.price) ? View.VISIBLE : View.GONE);
                    adater.notifyDataSetChanged();
                }
            });
        }
    }
}
