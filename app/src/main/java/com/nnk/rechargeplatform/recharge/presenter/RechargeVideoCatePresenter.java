package com.nnk.rechargeplatform.recharge.presenter;

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
import com.nnk.rechargeplatform.recharge.model.RechargeVideoCateM;
import com.nnk.rechargeplatform.recharge.view.RechargeVideoActivity;
import com.nnk.rechargeplatform.recharge.view.RechargeVideoCateActivity;

import java.util.ArrayList;
import java.util.List;

public class RechargeVideoCatePresenter {
    private RechargeVideoCateActivity activity;
    private CateAdater adater;

    public RechargeVideoCatePresenter(RechargeVideoCateActivity activity) {
        this.activity = activity;

    }

    public void init(RecyclerView recyclerView) {
        List<RechargeVideoCateM> list = new ArrayList<>();
        list.add(new RechargeVideoCateM(R.mipmap.video_aiqiyi, "爱奇艺"));
        list.add(new RechargeVideoCateM(R.mipmap.video_youku, "优酷土豆"));
        list.add(new RechargeVideoCateM(R.mipmap.video_tencent, "腾讯视频"));
        list.add(new RechargeVideoCateM(R.mipmap.video_thunder, "迅雷会员"));
        list.add(new RechargeVideoCateM(R.mipmap.video_mang, "芒果TV"));
        list.add(new RechargeVideoCateM(R.mipmap.video_leshi, "乐视视频"));
        list.add(new RechargeVideoCateM(R.mipmap.video_sofu, "搜狐视频"));
        list.add(new RechargeVideoCateM(R.mipmap.video_pptv, "PPTV聚力"));
        adater = new CateAdater(R.layout.item_recharge_video_category, list);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        recyclerView.setAdapter(adater);
    }

    class CateAdater extends BaseAdapter<RechargeVideoCateM> {

        public CateAdater(int layoutId, List<RechargeVideoCateM> list) {
            super(layoutId, list);
        }

        @Override
        protected void convert(BaseHolder holder, final RechargeVideoCateM item) {
            ImageView icon = holder.getView(R.id.icon);
            TextView title = holder.getView(R.id.title);
            title.setText(item.name);
            icon.setImageResource(item.iconResId);
            View rootView = holder.getView(R.id.item_root);
            rootView.setOnClickListener(new BaseOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    Intent intent = new Intent(activity, RechargeVideoActivity.class);
                    intent.putExtra("title", item.name);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
