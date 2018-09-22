package com.hao.noobchain.common;

import com.hao.noobchain.util.StringUtils;

import java.security.PublicKey;

public class TransactionOutput {

    private final String id;
    private final PublicKey recipient;
    private final double value;
    private final String parentTransactionId;

    public TransactionOutput(PublicKey recipient, double value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtils.applySha256(StringUtils.getStringFromKey(recipient) + Double.toString(value) + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return publicKey.equals(recipient);
    }

    public String getId() {
        return id;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public double getValue() {
        return value;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

}
