package com.ascendcorp.exam.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class InquiryServiceResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tranID;
    private String namespace;
    private String reasonCode;
    private String reasonDesc;
    private String balance;

    @JsonProperty("ref_no1")
    private String refNo1;

    @JsonProperty("ref_no2")
    private String refNo2;

    private String amount;
    private String accountName;

    @Override
    public String toString() {
        return new StringBuilder("InquiryServiceResultDTO [")
                .append("tranID=").append(tranID)
                .append(", namespace=").append(namespace)
                .append(", reasonCode=").append(reasonCode)
                .append(", reasonDesc=").append(reasonDesc)
                .append(", balance=").append(balance)
                .append(", refNo1=").append(refNo1)
                .append(", refNo2=").append(refNo2)
                .append(", amount=").append(amount)
                .append(", accountName=").append(accountName)
                .append("]").toString();
    }
}
