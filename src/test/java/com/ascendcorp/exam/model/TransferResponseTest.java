package com.ascendcorp.exam.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TransferResponseTest {

    @Test
    public void test_setters_getters() {
        TransferResponse response = new TransferResponse()
                .setResponseCode("00")
                .setDescription("Success")
                .setReferenceCode1("ref1")
                .setReferenceCode2("ref2")
                .setAmount("1000")
                .setBankTransactionID("txn123");

        assertEquals("00", response.getResponseCode());
        assertEquals("Success", response.getDescription());
        assertEquals("ref1", response.getReferenceCode1());
        assertEquals("ref2", response.getReferenceCode2());
        assertEquals("1000", response.getAmount());
        assertEquals("txn123", response.getBankTransactionID());
    }
}
