package com.nnk.rechargeplatform.profile.presenter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.base.BaseAdapter;
import com.nnk.rechargeplatform.base.BaseHolder;
import com.nnk.rechargeplatform.profile.model.BankListM;
import com.nnk.rechargeplatform.profile.model.BankM;
import com.nnk.rechargeplatform.profile.view.BankCardsActivity;
import com.nnk.rechargeplatform.profile.view.UnbindCardActivity;
import com.nnk.rechargeplatform.utils.BankListManager;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.GlideCircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BankCardsPresenter {
    BankCardsActivity activity;
    List<BankM> bankMList;
    BankAdapter adapter;

    public BankCardsPresenter(BankCardsActivity activity) {
        this.activity = activity;
    }

    public void init(RecyclerView recyclerView) {
        bankMList = new ArrayList<>();
        recyclerView.setAdapter(adapter = new BankAdapter(R.layout.item_bank, bankMList));
    }

    public void getBankList() {
        String pre = ApiUtils.getPrex(activity, "1021", Constants.CMD_MY_ACCOUNT);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", SharedPreUtils.get(activity, SharedPreUtils.KEY_USER_CODE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                BankListM resp = ApiUtils.parseRespObject(dataDe, BankListM.class);
                if (Constants.CODE_SUCCESS.equals(resp.resultCode)) {//登录成功
                    if (resp.bankCard != null) {
                        bankMList.clear();
                        bankMList.addAll(resp.bankCard);
                        for (BankM item : bankMList) {
                            item.bankName = BankListManager.get().getBankNameById(item.bankId);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    ViewUtils.showToast(activity, resp.resultInfo);
                }
                activity.setNullViewState(bankMList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                //ViewUtils.showToast(activity, R.string.profile_modify_phone_failed);
            }
        });
    }

    class BankAdapter extends BaseAdapter<BankM> {

        public BankAdapter(int layoutId, List<BankM> list) {
            super(layoutId, list);
        }

        @Override
        protected void convert(BaseHolder holder, final BankM item) {
            ((TextView) holder.getView(R.id.title)).setText(item.bankName);
            ((TextView) holder.getView(R.id.sub_title)).setText(BankListManager.get().getTypeNameById(activity,item.bankId));
            ((TextView) holder.getView(R.id.card_no)).setText("**** **** **** " + item.idNum);
            Glide.with(activity).load(BankListManager.get().getBankIconById(activity, item.bankId))
                    .apply(new RequestOptions().transform( GlideCircleTransform.get())).into((ImageView) holder.getView(R.id.icon));
            ((View) holder.getView(R.id.root)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UnbindCardActivity.class);
                    intent.putExtra("bankId", item.bankId);
                    intent.putExtra("accountName", item.accountName);
                    intent.putExtra("bankName", item.bankName);
                    intent.putExtra("idNum", item.idNum);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
