package com.nnk.rechargeplatform.profile.model;

import com.nnk.rechargeplatform.base.BaseResp;

public class AuthPersonM extends BaseResp {
    public String idCardNo;
    public String isAuth;//是否激活（设置支付密码并实名认证），如果没有激活，则进入填写身份信息界面，0未激活，1已激活，2表示通过实名认证
    public String isSetPaypwd;
    public String mob;
    public String payAccount;
}
