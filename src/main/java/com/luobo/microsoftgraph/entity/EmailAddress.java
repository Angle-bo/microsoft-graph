package com.luobo.microsoftgraph.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 电子邮件地址类型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailAddress {
    private String name;
    private String address;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}