package com.nnk.rechargeplatform.main.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseFragment;
import com.nnk.rechargeplatform.main.presenter.RechargePresenter;
import com.nnk.rechargeplatform.profile.view.AuthActivity;
import com.nnk.rechargeplatform.utils.SharedPreUtils;

import butterknife.BindView;
import butterknife.OnClick;


public class RechargeFragment extends BaseFragment {
    @BindView(R.id.not_auth_wrap)
    View notAuthWrap;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private RechargePresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recharge;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new RechargePresenter(this);
        presenter.init(recyclerView);
    }

    @Override
    @OnClick(R.id.auth)
    protected void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.auth) {
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isAuthed = SharedPreUtils.isAuthed(getActivity().getApplicationContext());
        isAuthed=true;
        recyclerView.setVisibility(isAuthed ? View.VISIBLE : View.GONE);
        notAuthWrap.setVisibility(!isAuthed ? View.VISIBLE : View.GONE);
    }
}
