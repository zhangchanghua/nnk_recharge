package com.nnk.rechargeplatform.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.base.BaseResp;
import com.nnk.rechargeplatform.utils.JniHelper;
import com.nnk.rechargeplatform.utils.Logg;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiUtils {

    private static AtomicInteger NoOrder = new AtomicInteger();//接口递增

    private ApiUtils() {

    }

    public static void init(Context context) {
        NoOrder.set(0);
        SharedPreUtils.init(context);
    }

    //SI|sessid|appid|cmd
    public static String getPrex(Context context, String appid, String cmd) {
        String SI = ApiUtils.getSI(context);
        String sessionId = SharedPreUtils.get(context, SharedPreUtils.KEY_SESSION_ID);
        //Logg.d("SI: " + SI);
        //Logg.d("sessionId: " + sessionId);
        return SI + "|" + sessionId + "|" + appid + "|" + cmd + "|";
    }


    //生成SI
    public static String getSI(Context context) {
        String baseSIhex = SharedPreUtils.get(context, SharedPreUtils.KEY_BASE_SI);
        String incrementSIhex = SharedPreUtils.get(context, SharedPreUtils.KEY_INCREMENT_SI);
        //Logg.d("baseSIhex: " + baseSIhex);
        //Logg.d("incrementSIhex: " + incrementSIhex);
        String SI = baseSIhex;
        if (!TextUtils.isEmpty(baseSIhex) && !TextUtils.isEmpty(incrementSIhex)) {
            SI = Integer.toHexString(Utils.hexToInt(baseSIhex) + Utils.hexToInt(incrementSIhex));
        } else if (!TextUtils.isEmpty(baseSIhex)) {
            SI = baseSIhex;
        }
        return SI;
    }


    //获取基本参数
    public static Map<String, String> getCommparams(Context context, String prex, String suffix) {
        Map<String, String> params = new HashMap<>();
        NoOrder.addAndGet(1);
        String t_key = SharedPreUtils.get(context, SharedPreUtils.KEY_T_KEY);
        String suffixEncry = "";
        Logg.i("suffix: " + suffix);
        byte[] bytes = JniHelper.getInstance().BlowFishWithBase64(1, t_key, suffix);
        try {
            suffixEncry = new String(bytes, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String SI = getSI(context);
        String Orderinfo = prex + suffixEncry;
        String sign = JniHelper.getInstance().md5(Orderinfo + "|" + SI + "|" + t_key);
        params.put("Orderinfo", Orderinfo);
        params.put("Sign", sign);
        params.put("NoOrder", NoOrder + "");

        Logg.i("参数: " + params.toString());
        return params;
    }


    /**
     * 统一处理此类型返回数据
     * Orderinfo=73079|1|YB85OYljCY93t2mOzuyvnPYSEUcNml+Z
     * Sign=df81dabb7ef0542e87083f38875b4b38
     * NoOrder=2
     */
    public static CommRespData getCommDRespata(Context context, String data) {
        CommRespData cd = new CommRespData();
        if (!TextUtils.isEmpty(data)) {
            String[] responseArray = data.split("\n");
            if (responseArray.length > 0) {
                String orderInfo = responseArray[0];
                String[] orderInfoArray = orderInfo.split("\\|");
                cd.orderInfoArray = orderInfoArray;
                if (orderInfoArray.length > 0) {
                    String baseSI = orderInfoArray[0];
                    String[] baseSiArray = baseSI.split("=");
                    if (baseSiArray.length > 1) {
                        SharedPreUtils.set(context, SharedPreUtils.KEY_BASE_SI, baseSiArray[1]);
                    }
                }
                if (orderInfoArray.length > 1) {
                    String errorCode = orderInfoArray[1];
                    String localIncrementSI = SharedPreUtils.get(context, SharedPreUtils.KEY_INCREMENT_SI);
                    if (TextUtils.isEmpty(localIncrementSI)) {//本地存储为空，存储incrementSI(初始化的时候errorCode为SI增量)
                        SharedPreUtils.set(context, SharedPreUtils.KEY_INCREMENT_SI, errorCode);
                    } else if (!Constants.CODE_SUCCESS.equals(errorCode)) {//服务器返回状态码
                        cd.errorCode = errorCode;
                    }
                }
            }
            if (responseArray.length > 1) {
                cd.Sign = responseArray[1];
            }
            if (responseArray.length > 2) {
                cd.NoOrder = responseArray[2];
            }
        }
        return cd;
    }


    //获取Base64编码的json参数
    public static String getBase64JsonString(JSONObject jsonObject) {
        Logg.i("json参数: " + jsonObject.toString());
        try {
            return Base64.encodeToString(jsonObject.toString().getBytes("gbk"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    //解密服务器字段
    public static String decryptData(Context context, String originData) {
        String serverDecry = "";
        byte[] bytes = JniHelper.getInstance().BlowFishWithBase64(0, SharedPreUtils.get(context, SharedPreUtils.KEY_T_KEY), originData);
        try {
            serverDecry = new String(bytes, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logg.e("服务器解密字段: " + serverDecry);
        return serverDecry;
    }

    public static <T extends BaseResp> T parseRespObject(String deJson, Class<T> clz) {
        if (!TextUtils.isEmpty(deJson)) {
            try {
                String base64DecodeStr = new String(Base64.decode(deJson.getBytes(), Base64.DEFAULT), "gbk");
                return new Gson().fromJson(base64DecodeStr, clz);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) new BaseResp();
    }
}
