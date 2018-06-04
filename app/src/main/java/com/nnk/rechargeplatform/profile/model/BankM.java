package com.nnk.rechargeplatform.profile.model;

import java.io.Serializable;

public class BankM implements Serializable {
    public String accountName;
    public String idNum;
    public String bankId;
    public String mob;
    public String payUserCode;
    public String quickPaySign;
    public String userFlag;

    public String bankName;//服务器没返回，需要在支持的银行卡列表查找赋值
}
