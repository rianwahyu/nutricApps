package com.rigadev.nutricapps.model;

public class PaymentMethodModel {
    String idPaymentMethod, name, type, account;

    public PaymentMethodModel(String idPaymentMethod, String name, String type, String account) {
        this.idPaymentMethod = idPaymentMethod;
        this.name = name;
        this.type = type;
        this.account = account;
    }

    public PaymentMethodModel() {
    }

    public String getIdPaymentMethod() {
        return idPaymentMethod;
    }

    public void setIdPaymentMethod(String idPaymentMethod) {
        this.idPaymentMethod = idPaymentMethod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
