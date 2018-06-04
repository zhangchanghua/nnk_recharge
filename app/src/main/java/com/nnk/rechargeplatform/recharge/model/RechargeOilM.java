package com.nnk.rechargeplatform.recharge.model;

import java.io.Serializable;

public class RechargeOilM implements Serializable {
    public String title;
    public String price;
    public boolean isSelect = false;

    public RechargeOilM(String title, String price) {
        this.title = title;
        this.price = price;
    }
}
