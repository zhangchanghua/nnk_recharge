package com.nnk.rechargeplatform.recharge.model;

import java.io.Serializable;

public class RechargeFlowM implements Serializable {
    public String size;
    public String price;
    public boolean isSelect = false;

    public RechargeFlowM(String size, String price) {
        this.size = size;
        this.price = price;
    }
}
