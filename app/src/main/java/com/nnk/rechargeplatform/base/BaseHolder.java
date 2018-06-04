package com.nnk.rechargeplatform.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;

public class BaseHolder extends RecyclerView.ViewHolder {
    private HashMap<Integer, View> mViews = new HashMap<>();

    public BaseHolder(View itemView) {
        super(itemView);
    }

    public <T> T getView(Integer viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

}