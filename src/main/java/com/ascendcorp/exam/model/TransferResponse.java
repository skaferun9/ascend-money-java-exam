package com.ascendcorp.exam.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TransferResponse {
    private String responseCode;
    private String description;
    private String referenceCode1;
    private String referenceCode2;
    private String amount;
    private String bankTransactionID;
}
