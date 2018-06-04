package com.nnk.rechargeplatform.recharge.model;

import java.io.Serializable;

public class RechargeQQcoinM implements Serializable {
    public String title;
    public String price;
    public boolean isSelect = false;

    public RechargeQQcoinM(String title, String price) {
        this.title = title;
        this.price = price;
    }
}
