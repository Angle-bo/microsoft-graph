package com.luobo.microsoftgraph.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 用户实体
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutlookUser {
    private String id;
    private String mail;
    private String displayName;
    private String userPrincipalName;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String emailAddress) {
        this.mail = emailAddress;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }
}