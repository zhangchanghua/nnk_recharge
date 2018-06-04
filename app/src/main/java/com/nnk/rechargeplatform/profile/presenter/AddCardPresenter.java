package com.nnk.rechargeplatform.profile.presenter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.base.BaseResp;
import com.nnk.rechargeplatform.profile.model.BankAllItemM;
import com.nnk.rechargeplatform.profile.model.CardTypeM;
import com.nnk.rechargeplatform.profile.view.AddCardActivity;
import com.nnk.rechargeplatform.utils.BankListManager;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.Utils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.TimerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.bluemoon.cardocr.lib.CaptureActivity;
import cn.com.bluemoon.cardocr.lib.bean.BankInfo;
import cn.com.bluemoon.cardocr.lib.common.CardType;

public class AddCardPresenter {
    AddCardActivity activity;
    private static final int MY_SCAN_REQUEST_CODE = 0;
    private String bankId;
    private String bankCard;
    private String cardType;

    public AddCardPresenter(AddCardActivity activity) {
        this.activity = activity;
    }

    public void pickBank(final TextView bank) {
        ViewUtils.hideKeyboard(activity);
        BankListManager.get().getBankList(activity, new BankListManager.BankListCallBack() {
            @Override
            public void callBack(final List<BankAllItemM> bankList) {
                final List<String> bankNameList = new ArrayList<>();
                for (BankAllItemM item : bankList) {
                    bankNameList.add(item.bankName);
                }
                ViewUtils.showSingleItemPicker(activity, bankNameList, new OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        bank.setText(bankNameList.get(options1));
                    }
                });
            }
        });
    }

    public void scanCard() {
        CaptureActivity.startAction(activity, CardType.TYPE_BANK, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_SCAN_REQUEST_CODE) {
        }
        if (resultCode == activity.RESULT_OK && requestCode == 1) {
            BankInfo info = (BankInfo) data.getSerializableExtra(CaptureActivity.BUNDLE_DATA);
            activity.setCardScanNumber(info.getCardNumber());
            activity.setCardScanName(info.getBankName());
        }
    }

    //获取银行卡类型
    public void getCardType(final String surname, final String idCardNo, final String bankCardNum, final String mob, final TimerView timerView) {
        if (!Utils.isPhoneNumber(mob)) {
            ViewUtils.showToast(activity, R.string.tip_phone_invalid);
            return;
        }
        ViewUtils.hideKeyboard(activity);
        String pre = ApiUtils.getPrex(activity, "1021", Constants.CMD_GET_BANK_BY_BIN);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bankCardNo", bankCardNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                String[] dataDeArray = dataDe.split("\\|");
                CardTypeM resp = ApiUtils.parseRespObject(dataDe, CardTypeM.class);
                if (Constants.CODE_SUCCESS.equals(resp.resultCode)) {
                    bankId = resp.bankId;
                    bankCard = bankCardNum;
                    cardType = resp.type + "";
                    getSms(surname, idCardNo, mob, timerView);
                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(activity, msg);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    //获取验证码
    public void getSms(String surname, String idCardNo, String mob, final TimerView timerView) {
        String pre = ApiUtils.getPrex(activity, "1021", Constants.CMD_QUICK_SIGN);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("realName", surname);
            jsonObject.put("identityNo", idCardNo);
            jsonObject.put("bankId", bankId);
            jsonObject.put("cardType", cardType);
            jsonObject.put("mobileNo", mob);
            jsonObject.put("bankCard", bankCard);
            jsonObject.put("expireDate", "");//信用卡过期日期
            jsonObject.put("cvv", bankCard);//信用卡号
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                String[] dataDeArray = dataDe.split("\\|");
                BaseResp resp = ApiUtils.parseRespObject(dataDe, BaseResp.class);
                if (Constants.CODE_SUCCESS1.equals(resp.resultCode)) {//成功
                    ViewUtils.showToast(activity, R.string.sms_sended);
                    timerView.start();
                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(activity, msg);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    public void addCard(String surname, String idCardNo, String mob, String code) {
        String pre = ApiUtils.getPrex(activity, "1021", Constants.CMD_QUICK_SIGN_CHECK);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("realName", surname);
            jsonObject.put("identityNo", idCardNo);
            jsonObject.put("mobileNo", mob);
            jsonObject.put("bankCard", bankCard);
            jsonObject.put("expireDate", "");//信用卡过期日期
            jsonObject.put("cvv", bankCard);//信用卡号
            jsonObject.put("bankId", bankId);
            jsonObject.put("cardType", cardType);
            jsonObject.put("userSessionId", SharedPreUtils.get(activity, SharedPreUtils.KEY_SESSION_ID));
            jsonObject.put("signNo", "");
            jsonObject.put("sendSignNo", "");
            jsonObject.put("realBankType", cardType);
            jsonObject.put("verifyCode", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                String[] dataDeArray = dataDe.split("\\|");
                BaseResp resp = ApiUtils.parseRespObject(dataDe, BaseResp.class);
                if (Constants.CODE_SUCCESS1.equals(resp.resultCode)) {//成功
                    ViewUtils.showToast(activity, R.string.profile_bank_cards_add_success);
                    activity.finish();
                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(activity, msg);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
}
