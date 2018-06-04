package com.nnk.rechargeplatform.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.profile.model.BankAllItemM;
import com.nnk.rechargeplatform.profile.model.BankAllListM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BankListManager {
    private static BankListManager instance;
    private List<BankAllItemM> bankList;
    public static final String CARD_TYPE_CREDIT = "2";//信用卡
    public static final String CARD_TYPE_DEBIT = "1";//储蓄卡

    private BankListManager() {
        bankList = new ArrayList<>();
    }

    public static BankListManager get() {
        if (instance == null) {
            instance = new BankListManager();

        }
        return instance;
    }

    //获取所有支持银行卡
    public void getBankList(Context context, BankListCallBack cb) {
        if (bankList.isEmpty()) {
            String json = SharedPreUtils.get(context, SharedPreUtils.KEY_BANK_LIST);
            if (!TextUtils.isEmpty(json)) {//本地读取
                List list = new Gson().fromJson(json, new TypeToken<List<BankAllItemM>>() {
                }.getType());
                if (list != null) {
                    bankList.addAll(list);
                }
                if (cb != null) {
                    cb.callBack(bankList);
                }
            } else {
                getBankListFromServer(context, cb);
            }
        } else {
            if (cb != null) {
                cb.callBack(bankList);
            }
        }
    }

    public interface BankListCallBack {
        void callBack(List<BankAllItemM> bankList);
    }

    //服务端获取所有支持银行卡
    private void getBankListFromServer(final Context context, final BankListCallBack cb) {
        String pre = ApiUtils.getPrex(context, "1021", Constants.CMD_QUERY_BANK_LIST);
        String suff = "";
        Map params = ApiUtils.getCommparams(context, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(context) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                BankAllListM resp = ApiUtils.parseRespObject(dataDe, BankAllListM.class);
                if (Constants.CODE_SUCCESS.equals(resp.resultCode)) {//成功
                    //List<BankAllItemM> credit = resp.credit;
                    List<BankAllItemM> debit = resp.debit;
//                    if (!credit.isEmpty()) {
//                        bankList.addAll(credit);
//                    }
                    if (!debit.isEmpty()) {
                        bankList.addAll(debit);
                    }
                    if (!bankList.isEmpty()) {
                        String json = new Gson().toJson(bankList);
                        if (!TextUtils.isEmpty(json)) {
                            SharedPreUtils.set(context, SharedPreUtils.KEY_BANK_LIST, json);
                        }
                    }
                    if (cb != null) {
                        cb.callBack(bankList);
                    }
                } else {

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });


    }

    //根据银行卡id获取名称
    public String getBankNameById(String bankId) {
        for (BankAllItemM item : bankList) {
            if (bankId.equals(item.bankId)) {
                return item.bankName;
            }
        }
        return "";
    }

    //根据银行卡id获取银行卡类型名称
    public String getTypeNameById(Context context, String bankId) {
        for (BankAllItemM item : bankList) {
            if (bankId.equals(item.bankId)) {
                return getTypeNameByType(context, item.cardType);
            }
        }
        return "未知卡类型";
    }

    //根据银行卡类型获取银行卡类型名称
    private String getTypeNameByType(Context context, String cardType) {
        if (CARD_TYPE_DEBIT.equals(cardType)) {
            return context.getString(R.string.profile_recharge_car_type0);
        } else if (CARD_TYPE_CREDIT.equals(cardType)) {
            return context.getString(R.string.profile_recharge_car_type1);
        }
        return "未知卡类型";
    }

    //根据银行卡类型获取银行卡类型名称
    public int getBankIconById(Context context, String bankId) {
        switch (bankId) {
            case "8000014":
                return R.mipmap.bank_zhongxing;
            case "800004":
                return R.mipmap.bank_jianshe;
            case "8000013":
                return R.mipmap.bank_guangda;
            case "800003":
                return R.mipmap.bank_zhonghang;
            case "800001":
                return R.mipmap.bank_gonghang;
            case "800002":
                return R.mipmap.bank_nongye;
            case "8000024":
                return R.mipmap.bank_youzheng;
            case "8000010":
                return R.mipmap.bank_fujian_xingye;
            case "8000012":
                return R.mipmap.bank_shanghai;
            case "800009":
                return R.mipmap.bank_pufa;
            case "8000011":
                return R.mipmap.bank_pingan;
            case "800007":
                return R.mipmap.bank_mingsheng;
            case "800006":
                return R.mipmap.bank_jiaotong;
            case "8000018":
                return R.mipmap.bank_huaxia;
            case "8000207":
                return R.mipmap.bank_guangfa;
            case "8000020":
                return R.mipmap.bank_beijing;
        }
        return 0;
    }

}
