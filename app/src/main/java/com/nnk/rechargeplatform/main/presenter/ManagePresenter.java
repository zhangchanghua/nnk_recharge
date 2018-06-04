package com.nnk.rechargeplatform.main.presenter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.main.view.ManageFragment;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.ViewUtils;

import java.util.Map;

public class ManagePresenter {
    private static final int TYPE_MONEY = 0;//资金流水
    private static final int TYPE_TRADE = 1;//交易记录
    private static final int TYPE_OPERATION = 2;//经营状况
    private static final int TYPE_COMMISSION = 3;//佣金管理
    ManageFragment fragment;
    Adater adater;


    public ManagePresenter(ManageFragment fragment) {
        this.fragment = fragment;
        adater = new Adater();
    }

    public void getDatas(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        recyclerView.setAdapter(adater);
    }

    public void getRechargeHistory(int pageIndex,int pageCount){
        String pre = ApiUtils.getPrex(fragment.getContext(), "0", Constants.CMD_GET_FOUNDS_CHANGE);
        String phone = SharedPreUtils.get(fragment.getContext(), SharedPreUtils.KEY_PHONE);
        String suff = phone + "|0|0|0|" + pageIndex + "|"+pageCount;
        Map params = ApiUtils.getCommparams(fragment.getContext(), pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(fragment.getContext()) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                String[] dataDeArray = dataDe.split("\\|");
                String errorCode = "-1";
                if (dataDeArray.length > 0) {
                    errorCode = dataDeArray[0];
                }
                if (Constants.CODE_SUCCESS.equals(errorCode)) {//成功

                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(fragment.getContext(), msg);
                    }
                    return;
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    class Adater extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_MONEY:
                    return new MoneyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_money, parent, false));
                case TYPE_TRADE:
                    return new TradeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_trade, parent, false));
                case TYPE_OPERATION:
                    return new OperationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_operation, parent, false));
                case TYPE_COMMISSION:
                    return new CommissionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_commission, parent, false));
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return TYPE_MONEY;
                case 1:
                    return TYPE_TRADE;
                case 2:
                    return TYPE_OPERATION;
                case 3:
                    return TYPE_COMMISSION;

            }
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    static class MoneyHolder extends RecyclerView.ViewHolder {

        public MoneyHolder(View itemView) {
            super(itemView);
        }
    }

    static class TradeHolder extends RecyclerView.ViewHolder {

        public TradeHolder(View itemView) {
            super(itemView);
        }
    }

    static class OperationHolder extends RecyclerView.ViewHolder {

        public OperationHolder(View itemView) {
            super(itemView);
        }
    }

    static class CommissionHolder extends RecyclerView.ViewHolder {

        public CommissionHolder(View itemView) {
            super(itemView);
        }
    }
}
