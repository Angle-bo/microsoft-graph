package com.luobo.microsoftgraph.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 收件人实体
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipient {
    private EmailAddress emailAddress;

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }
}