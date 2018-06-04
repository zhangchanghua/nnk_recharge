package com.nnk.rechargeplatform;

public abstract class Constants {


    public static final String HOST = BuildConfig.HOST;
    public static final String HOST_FILE = BuildConfig.HOST_FILE;
    public static final String API_INDEX = "berbon/index_1_4.php/";
    public static final String API_FILE = "IdcardsUpload";

    public static final String CODE_SUCCESS = "1";//服务器返回成功
    public static final String CODE_SUCCESS1 = "1001";//服务器返回成功(绑定银行卡发送短信验证码)
    public static final String CMD_INIT = "Init";//初始化
    public static final String CMD_SETENV = "SetEnv";//上传手机信息
    public static final String CMD_CHECK_MOB = "CheckMob";//获取(校验)验证码
    public static final String CMD_REGISTER = "NewBerbonRegist";//注册
    public static final String CMD_LOGIN_SMS = "QuickLogin";//验证码登录
    public static final String CMD_LOGIN = "BerbonLogin";//密码登录
    public static final String CMD_RE_BIND = "ChangeBindMob";//换帮手机号
    public static final String CMD_RESET_PASS = "ResetLoginPwd";//重置密码
    public static final String CMD_CHANGE_PASS = "ChangeLoginPwd";//修改密码
    public static final String CMD_QUERY_BANK_LIST = "QueryBankList";//获取银行列表
    public static final String CMD_GET_BANK_BY_BIN = "GetBankByBin";//判断银行卡类型
    public static final String CMD_QUICK_SIGN = "QuickSign";//添加银行卡短信获取
    public static final String CMD_QUICK_SIGN_CHECK = "QuickSignCheck";//添加银行卡
    public static final String CMD_UNBIND_ACCOUNT = "UnbindAccount";//解绑银行卡
    public static final String CMD_MY_ACCOUNT = "MyAccount";//获取银行列表
    public static final String CMD_QUERY_AUTH_INFO = "GetVerifyInfo";//获取用户实名信息
    public static final String CMD_GET_ACC_DETAIL = "QueryUserInfo";//获取我的信息
    public static final String CMD_VERIFY = "NewBerbonVerify";//认证
    public static final String CMD_CHANGE_PAY_PASS = "ChangePayPwd";//修改支付密码
    public static final String CMD_GET_FOUNDS_CHANGE = "GetFundsChange";//收支明细
}
