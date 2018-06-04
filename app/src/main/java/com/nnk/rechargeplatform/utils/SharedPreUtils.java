package com.nnk.rechargeplatform.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreUtils {

    private static final String NNK = "nnk";
    //初始化
    public static final String INIT_KEY = "Berbon";//初始化key
    public static final String KEY_HARDWARE_ID = "hardware_id";//初始化上传的硬件id
    public static final String KEY_T_KEY = "t_key";//服务器公钥和本地私钥生成的秘钥
    public static final String KEY_BASE_SI = "base_si";
    public static final String KEY_INCREMENT_SI = "increment_si";
    public static final String KEY_SESSION_ID = "session_id";

    //登录相关
    public static final String KEY_AUTO_LOGIN = "auto_login_key";
    public static final String KEY_IS_AUTH = "is_auth";//0未激活，1已激活，2表示通过实名认证
    public static final String KEY_UNAME = "uname";//姓名(isAuth = 0时无值)
    public static final String KEY_ID_CARD_NO = "id_card_no";//身份证号(isAuth = 0时无值)
    public static final String KEY_PAY_ACCOUNT = "pay_account";//支付帐号(isAuth = 0时无值)
    public static final String KEY_IS_SET_PAY_PASS = "is_set_pay_pass";//是否需要设置支付密码,1需要设置,0不需要设置
    public static final String KEY_PHONE = "phone";//绑定登录的手机号
    public static final String KEY_USER_CODE = "user_code";//登录账号对应的倍棒数字账号，登录成功后有值
    public static final String KEY_QQ = "qq";//qq

    //配置相关
    public static final String KEY_BANK_LIST = "bank_list_json";//银行卡列表json

    public static void set(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(NNK, Context.MODE_PRIVATE);
        preferences.edit().putString(key, value).apply();
    }

    public static String get(Context context, String key) {
        return context.getSharedPreferences(NNK, Context.MODE_PRIVATE).getString(key, "");
    }

    public static void init(Context context) {
        SharedPreUtils.set(context, SharedPreUtils.KEY_T_KEY, INIT_KEY);
        SharedPreUtils.set(context, SharedPreUtils.KEY_BASE_SI, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_INCREMENT_SI, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_SESSION_ID, "");
    }

    public static void logout(Context context) {
        SharedPreUtils.set(context, SharedPreUtils.KEY_AUTO_LOGIN, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_IS_AUTH, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_UNAME, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_ID_CARD_NO, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_PAY_ACCOUNT, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_IS_SET_PAY_PASS, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_PHONE, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_USER_CODE, "");
        SharedPreUtils.set(context, SharedPreUtils.KEY_QQ, "");
    }

    //是否已认证
    public static boolean isAuthed(Context context) {
        return "2".equals(get(context, KEY_IS_AUTH));
    }
}
