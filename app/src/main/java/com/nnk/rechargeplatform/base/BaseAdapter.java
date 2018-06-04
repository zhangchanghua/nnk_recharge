package com.nnk.rechargeplatform.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder> {
    protected List<T> mList;
    private int layoutId;

    public BaseAdapter(int layoutId, List<T> list) {
        this.layoutId = layoutId;
        this.mList = list;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        BaseHolder holder = new BaseHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        if (mList == null) return;
        T item = mList.get(position);
        convert(holder, item);
    }

    protected abstract void convert(BaseHolder holder, T item);

    //获取记录数据
    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }
}
