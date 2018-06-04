package com.nnk.rechargeplatform.recharge.model;

import java.io.Serializable;

public class RechargeQQpointM implements Serializable {
    public String title;
    public String price;
    public boolean isSelect = false;

    public RechargeQQpointM(String title, String price) {
        this.title = title;
        this.price = price;
    }
}
