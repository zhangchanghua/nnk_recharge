package com.nnk.rechargeplatform.profile.model;

import java.io.Serializable;

//支持的银行卡
public class BankAllItemM implements Serializable {
    public String bankId;
    public String bankName;
    public String bankType;
    public String cardType;
    public String firstLetter;
    public String minAmount;
    public String swiftCode;

}
