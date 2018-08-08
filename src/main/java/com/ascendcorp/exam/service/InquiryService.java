package com.ascendcorp.exam.service;

import com.ascendcorp.exam.model.InquiryServiceResultDTO;
import com.ascendcorp.exam.model.TransferResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.Date;

public class InquiryService {

    @Autowired
    private BankProxyGateway bankService;

    final static Logger log = Logger.getLogger(InquiryService.class);

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
                                           String lastName)
    {
        InquiryServiceResultDTO respDTO = null;
        log.info("### begin inquiry bank account ###");
        try
        {
            log.info("step 1.prepare data");
            if(transactionId == null) {
                log.info("Transaction id is required!");
                throw new NullPointerException("Transaction id is required!");
            }
            if(tranDateTime == null) {
                log.info("Transaction DateTime is required!");
                throw new NullPointerException("Transaction DateTime is required!");
            }
            if(channel == null) {
                log.info("Channel is required!");
                throw new NullPointerException("Channel is required!");
            }
            if(bankCode == null || bankCode.equalsIgnoreCase("")) {
                log.info("Bank Code is required!");
                throw new NullPointerException("Bank Code is required!");
            }
            if(bankNumber == null || bankNumber.equalsIgnoreCase("")) {
                log.info("Bank Number is required!");
                throw new NullPointerException("Bank Number is required!");
            }
            if(amount <= 0) {
                log.info("Amount must more than zero!");
                throw new NullPointerException("Amount must more than zero!");
            }

            TransferResponse response = bankService.requestTransfer(transactionId, tranDateTime, channel,
                    bankCode, bankNumber, amount, reference1, reference2);

            log.info("2.4 begin get Inquiry Response");
            if(response != null) //New
            {
                log.info("step 3.begin populate Response Values");
                respDTO = new InquiryServiceResultDTO();

                respDTO.setRef_no1(response.getReferenceCode1());
                respDTO.setRef_no2(response.getReferenceCode2());
                respDTO.setAmount(response.getBalance());
                respDTO.setTranID(response.getBankTransactionID());
                log.info("respInfo is >> respcode : "+response.getResponseCode()+" , respDesc : "+response.getDescription());
                //wanna user customer bank account name
                if(response.getResponseCode().equalsIgnoreCase("approved"))
                {
                    respDTO.setReasonCode("200");
                    respDTO.setReasonDesc(response.getDescription());
                    respDTO.setAccountName(response.getDescription());
                }else if(response.getResponseCode().equalsIgnoreCase("invalid_data"))
                {
                    String replyDesc = response.getDescription();
                    if(replyDesc != null)
                    {
                        log.info("case incorrect data will split data len 3");
                        String respDesc[] = replyDesc.split(":");
                        if(respDesc != null && respDesc.length >= 3)
                        {
                            respDTO.setReasonCode(respDesc[1]);
                            respDTO.setReasonDesc(respDesc[2]);
                        }else
                        {
                            respDTO.setReasonCode("400");
                            respDTO.setReasonDesc("General Invalid Data");
                        }
                    }else
                    {
                        respDTO.setReasonCode("400");
                        respDTO.setReasonDesc("General Invalid Data");
                    }
                }else if(response.getResponseCode().equalsIgnoreCase("transaction_error"))
                {
                    String replyDesc = response.getDescription();
                    log.info("Case New Inquiry Error Code Format From [24/09/2012] Bank 99:001:error desc");
                    if(replyDesc != null)
                    {
                        String respDesc[] = replyDesc.split(":");
                        if(respDesc != null && respDesc.length >= 2)
                        {
                            log.info("Case Inquiry Error Code Format Now Will Get From [0] and [1] first");
                            String subIdx1 = respDesc[0];
                            String subIdx2 = respDesc[1];
                            log.info("index[0] : "+subIdx1 + " index[1] is >> "+subIdx2);
                            if("98".equalsIgnoreCase(subIdx1))
                            {
                                respDTO.setReasonCode(subIdx1);
                                respDTO.setReasonDesc(subIdx2);
                            }else
                            {
                                log.info("case error is not 98 code");
                                if(respDesc.length >= 3)
                                {
                                    String subIdx3 = respDesc[2];
                                    log.info("index[0] : "+subIdx3);
                                    respDTO.setReasonCode(subIdx2);
                                    respDTO.setReasonDesc(subIdx3);
                                }else
                                {
                                    respDTO.setReasonCode(subIdx1);
                                    respDTO.setReasonDesc(subIdx2);
                                }
                            }
                        }else
                        {
                            respDTO.setReasonCode("500");
                            respDTO.setReasonDesc("General Transaction Error");
                        }
                    }else
                    {
                        respDTO.setReasonCode("500");
                        respDTO.setReasonDesc("General Transaction Error");
                    }
                }else if(response.getResponseCode().equalsIgnoreCase("unknown"))
                {
                    String replyDesc = response.getDescription();
                    if(replyDesc != null)
                    {
                        String respDesc[] = replyDesc.split(":");
                        if(respDesc != null && respDesc.length >= 2)
                        {
                            respDTO.setReasonCode(respDesc[0]);
                            respDTO.setReasonDesc(respDesc[1]);
                            if(respDTO.getReasonDesc() == null || respDTO.getReasonDesc().trim().length() == 0)
                            {
                                respDTO.setReasonDesc("General Invalid Data");
                            }
                        }else
                        {
                            respDTO.setReasonCode("501");
                            respDTO.setReasonDesc("General Invalid Data");
                        }
                    }else
                    {
                        respDTO.setReasonCode("501");
                        respDTO.setReasonDesc("General Invalid Data");
                    }
                }else
                    throw new Exception("Unsupport Error Reason Code");
                log.info(respDTO.toString());
            }else
                throw new Exception("Unable to inquiry from service.");
        }catch(NullPointerException ne)
        {
            if(respDTO == null)
            {
                respDTO = new InquiryServiceResultDTO();
                respDTO.setReasonCode("500");
                respDTO.setReasonDesc("General Invalid Data");
            }
        }catch(WebServiceException r)
        {
            String faultString = r.getMessage();
            log.info("inquiry with AxisFault Exception >> "+faultString);
            if(respDTO == null)
            {
                respDTO = new InquiryServiceResultDTO();
                if(faultString != null && (faultString.indexOf("java.net.SocketTimeoutException") > -1
                        || faultString.indexOf("Connection timed out") > -1 ))
                {

                    respDTO.setReasonCode("503");
                    respDTO.setReasonDesc("Error timeout");
                }else
                {
                    respDTO.setReasonCode("504");
                    respDTO.setReasonDesc("Internal Application Error");
                }
            }
        }
        catch(Exception e)
        {
            log.error("inquiry exception",e);
            if(respDTO == null || (respDTO != null && respDTO.getReasonCode() == null))
            {
                respDTO = new InquiryServiceResultDTO();
                respDTO.setReasonCode("504");
                respDTO.setReasonDesc("Internal Application Error");
            }
        }
        log.info("### finish inquiry bank account ###");
        return respDTO;
    }


}
