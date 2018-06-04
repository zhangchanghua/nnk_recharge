package com.nnk.rechargeplatform.profile.presenter;

import android.content.Intent;

import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.profile.model.BankListM;
import com.nnk.rechargeplatform.profile.model.BankM;
import com.nnk.rechargeplatform.profile.view.AddCardActivity;
import com.nnk.rechargeplatform.profile.view.BalanceRechargeActivity;
import com.nnk.rechargeplatform.utils.BankListManager;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.PickBankDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BalanceRechargePresenter {

    BalanceRechargeActivity activity;
    List<BankM> bankMList = new ArrayList<>();
    boolean setCurrentBank = false;

    public BalanceRechargePresenter(BalanceRechargeActivity activity) {
        this.activity = activity;
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
                        if (!setCurrentBank && !bankMList.isEmpty()) {
                            activity.setCurentBank(bankMList.get(0));
                            setCurrentBank = true;
                        }
                        activity.setBankStatus(!bankMList.isEmpty());
                    }
                } else {
                    ViewUtils.showToast(activity, resp.resultInfo);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    public void selectBank() {
        PickBankDialog dialog = new PickBankDialog(activity, new PickBankDialog.CallBack() {
            @Override
            public void onItemSelect(BankM item) {
                activity.setCurentBank(item);
            }

            @Override
            public void onAddCard() {
                activity.startActivity(new Intent(activity, AddCardActivity.class));
            }
        });
        dialog.show();
        dialog.setDatas(bankMList);
    }

    public void submit() {
        
        //activity.startActivity(new Intent(activity, BalanceRechargeSuccessActivity.class));
        //activity.finish();
    }

}
