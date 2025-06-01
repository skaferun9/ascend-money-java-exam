package com.ascendcorp.exam.service;

import com.ascendcorp.exam.model.InquiryServiceResultDTO;
import com.ascendcorp.exam.model.TransferResponse;
import com.ascendcorp.exam.proxy.BankProxyGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerException;

import java.util.Date;

import static com.ascendcorp.exam.util.Constants.*;

public class InquiryService {

    @Autowired
    private BankProxyGateway bankProxyGateway;

    private static final Logger log = LoggerFactory.getLogger(InquiryService.class);

    public InquiryServiceResultDTO inquiry(String transactionId,
                                           Date tranDateTime,
                                           String channel,
                                           String locationCode,
                                           String bankCode,
                                           String bankNumber,
                                           double amount,
                                           String reference1,
                                           String reference2,
                                           String firstName,
                                           String lastName) {

        try {
            log.info("Validating request parameters.");
            validateInputs(transactionId, tranDateTime, channel, bankCode, bankNumber, amount);

            log.info("Calling bank web service.");
            TransferResponse response = bankProxyGateway.requestTransfer(
                    transactionId, tranDateTime, channel, bankCode, bankNumber, amount, reference1, reference2
            );

            if (response == null) {
                throw new Exception("Unable to inquiry from service.");
            }

            log.info("Response from bank proxy gateway is OK.");
            return mapBankResponse(response);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return createErrorResponse(CODE_500, GENERAL_INVALID_DATA);
        } catch (WebServerException e) {
            log.error("Web server exception occurred", e);
            return handleWebServerException(e);
        } catch (Exception e) {
            log.error("Unhandled inquiry exception", e);
            return createErrorResponse(CODE_504, INTERNAL_APPLICATION_ERROR);
        }
    }

    private void validateInputs(String transactionId, Date tranDateTime, String channel,
                                String bankCode, String bankNumber, double amount) {

        if (isNullOrEmpty(transactionId)) throw new IllegalArgumentException("Transaction id is required!");
        if (tranDateTime == null) throw new IllegalArgumentException("Transaction DateTime is required!");
        if (isNullOrEmpty(channel)) throw new IllegalArgumentException("Channel is required!");
        if (isNullOrEmpty(bankCode)) throw new IllegalArgumentException("Bank Code is required!");
        if (isNullOrEmpty(bankNumber)) throw new IllegalArgumentException("Bank Number is required!");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be more than zero!");
    }

    private InquiryServiceResultDTO mapBankResponse(TransferResponse response) {
        log.info("Checking bank response code");

        InquiryServiceResultDTO respDTO = new InquiryServiceResultDTO()
                .setRefNo1(response.getResponseCode())
                .setRefNo2(response.getReferenceCode2())
                .setAmount(response.getAmount())
                .setTranID(response.getBankTransactionID());

        String code = response.getResponseCode();
        String description = response.getDescription();

        switch (code.toLowerCase()) {
            case APPROVED:
                respDTO.setReasonCode(CODE_200);
                respDTO.setReasonDesc(description);
                respDTO.setAccountName(description);
                break;

            case INVALID_DATA:
                handleInvalidDataResponse(description, respDTO);
                break;

            case TRANSACTION_ERROR:
                handleTransactionErrorResponse(description, respDTO);
                break;

            case UNKNOWN:
                handleUnknowResponse(description, respDTO);
                break;

            default:
                throw new RuntimeException("Unsupported Error Reason Code: " + code);
        }

        return respDTO;
    }

    private void handleInvalidDataResponse(String description, InquiryServiceResultDTO respDTO) {
        if (description != null) {
            String[] respDesc = description.split(COLON);
            if (respDesc.length >= 3) {
                respDTO.setReasonCode(respDesc[1]);
                respDTO.setReasonDesc(respDesc[2]);
            } else {
                respDTO.setReasonCode(CODE_400);
                respDTO.setReasonDesc(GENERAL_INVALID_DATA);
            }
        } else {
            respDTO.setReasonCode(CODE_400);
            respDTO.setReasonDesc(GENERAL_INVALID_DATA);
        }
    }

    private void handleTransactionErrorResponse(String description, InquiryServiceResultDTO respDTO) {
        if (description != null) {
            String[] respDesc = description.split(COLON);
            if (respDesc.length >= 2) {
                String subIdx1 = respDesc[0];
                String subIdx2 = respDesc[1];
                log.debug("Parsed transaction error: [{}] [{}]", subIdx1, subIdx2);

                if (CODE_98.equalsIgnoreCase(subIdx1)) {
                    respDTO.setReasonCode(subIdx1);
                    respDTO.setReasonDesc(subIdx2);
                } else if (respDesc.length >= 3) {
                    respDTO.setReasonCode(subIdx2);
                    respDTO.setReasonDesc(respDesc[2]);
                } else {
                    respDTO.setReasonCode(subIdx1);
                    respDTO.setReasonDesc(subIdx2);
                }

            } else {
                respDTO.setReasonCode(CODE_500);
                respDTO.setReasonDesc(GENERAL_TRANSACTION_ERROR);
            }
        } else {
            respDTO.setReasonCode(CODE_500);
            respDTO.setReasonDesc(GENERAL_TRANSACTION_ERROR);
        }
    }

    private void handleUnknowResponse(String description, InquiryServiceResultDTO respDTO) {
        if (description != null) {
            String[] respDesc = description.split(COLON);
            if (respDesc.length >= 2) {
                respDTO.setReasonCode(respDesc[0]);
                respDTO.setReasonDesc((respDesc[1] == null || respDesc[1].trim().isEmpty())
                        ? GENERAL_INVALID_DATA
                        : respDesc[1]);
            } else {
                respDTO.setReasonCode(CODE_501);
                respDTO.setReasonDesc(GENERAL_INVALID_DATA);
            }
        } else {
            respDTO.setReasonCode(CODE_501);
            respDTO.setReasonDesc(GENERAL_INVALID_DATA);
        }
    }

    private InquiryServiceResultDTO handleWebServerException(WebServerException ex) {
        String errorMessage = ex.getMessage();
        if (errorMessage != null &&
                (errorMessage.contains(JAVA_NET_SOCKET_TIMEOUT_EXCEPTION) ||
                        errorMessage.contains(CONNECTION_TIMEOUT))) {
            return createErrorResponse(CODE_503, ERROR_TIMEOUT);
        }
        return createErrorResponse(CODE_504, INTERNAL_APPLICATION_ERROR);
    }

    private InquiryServiceResultDTO createErrorResponse(String code, String message) {
        return new InquiryServiceResultDTO()
                .setReasonCode(code)
                .setReasonDesc(message);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
