package com.nnk.rechargeplatform.recharge.model;

import java.io.Serializable;

public class RechargePhoneM implements Serializable {
    public int orginPrice;
    public String price;
    public boolean isSelect=false;

    public RechargePhoneM(int orginPrice, String price) {
        this.orginPrice = orginPrice;
        this.price = price;
    }
}
