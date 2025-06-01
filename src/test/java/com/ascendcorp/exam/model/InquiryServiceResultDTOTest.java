package com.ascendcorp.exam.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class InquiryServiceResultDTOTest {

    @Test
    public void test_setters_getters_and_to_string_not_null() {
        InquiryServiceResultDTO dto = new InquiryServiceResultDTO()
                .setTranID("123")
                .setNamespace("namespace")
                .setReasonCode("reasonCode")
                .setReasonDesc("reasonDesc")
                .setBalance("5000")
                .setRefNo1("ref1")
                .setRefNo2("ref2")
                .setAmount("1000")
                .setAccountName("somchai somsri");

        assertEquals("123", dto.getTranID());
        assertEquals("namespace", dto.getNamespace());
        assertEquals("reasonCode", dto.getReasonCode());
        assertEquals("reasonDesc", dto.getReasonDesc());
        assertEquals("5000", dto.getBalance());
        assertEquals("ref1", dto.getRefNo1());
        assertEquals("ref2", dto.getRefNo2());
        assertEquals("1000", dto.getAmount());
        assertEquals("somchai somsri", dto.getAccountName());

        String s = dto.toString();
        assertNotNull(s);

    }

}
